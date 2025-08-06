package com.example.ecorutaapp

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.ecorutaapp.model.CentroReciclaje
import com.example.ecorutaapp.utils.LocationSender
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.wearable.MessageClient
import com.google.android.gms.wearable.MessageEvent
import com.google.android.gms.wearable.Wearable
import org.json.JSONArray

class MainActivity : AppCompatActivity(), OnMapReadyCallback, MessageClient.OnMessageReceivedListener {

    private lateinit var map: GoogleMap
    private lateinit var centros: List<CentroReciclaje>
    private lateinit var adapter: CentroAdapter
    private lateinit var txtRecibido: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val mapFragment =
            supportFragmentManager.findFragmentById(R.id.mapFragment) as SupportMapFragment
        mapFragment.getMapAsync(this)

        val recyclerView = findViewById<RecyclerView>(R.id.centrosRecyclerView)
        val buscador = findViewById<EditText>(R.id.buscadorEditText)
        txtRecibido = findViewById(R.id.txtRecibido)

        centros = cargarCentrosDesdeJson()
        adapter = CentroAdapter(centros) { centro ->
            moverMapaACentro(centro)
            LocationSender.enviarDestinoEnTiempoReal(this, centro.lat, centro.lng, centro.nombre)
            txtRecibido.text = "Destino enviado al reloj: ${centro.nombre}"
        }

        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

        buscador.addTextChangedListener {
            val filtro = it.toString().trim().lowercase()
            val filtrados = centros.filter { c -> c.materiales.lowercase().contains(filtro) }
            adapter.actualizarLista(filtrados)
        }

        Wearable.getMessageClient(this).addListener(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        Wearable.getMessageClient(this).removeListener(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 1)
            return
        }

        map.isMyLocationEnabled = true
        val tepic = LatLng(21.5058, -104.8940)
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(tepic, 13f))

        for (centro in centros) {
            val posicion = LatLng(centro.lat, centro.lng)
            map.addMarker(MarkerOptions().position(posicion).title(centro.nombre))
        }
    }

    private fun moverMapaACentro(centro: CentroReciclaje) {
        val posicion = LatLng(centro.lat, centro.lng)
        map.animateCamera(CameraUpdateFactory.newLatLngZoom(posicion, 15f))
    }

    private fun cargarCentrosDesdeJson(): List<CentroReciclaje> {
        val jsonString = assets.open("centros.json").bufferedReader().use { it.readText() }
        val jsonArray = JSONArray(jsonString)
        val centros = mutableListOf<CentroReciclaje>()
        for (i in 0 until jsonArray.length()) {
            val obj = jsonArray.getJSONObject(i)
            centros.add(
                CentroReciclaje(
                    obj.getString("nombre"),
                    obj.getDouble("lat"),
                    obj.getDouble("lng"),
                    obj.getString("materiales")
                )
            )
        }

        return centros
    }

    override fun onMessageReceived(event: MessageEvent) {
        if (event.path == "/acelerometro") {
            val mensaje = String(event.data)
            runOnUiThread {
                txtRecibido.text = "Desde reloj: $mensaje"
            }
        } else if (event.path == "/test_conexion") {
            runOnUiThread {
                txtRecibido.text = "¡Conexión con reloj exitosa!"
            }
            // Opcional: responder al reloj
            Wearable.getMessageClient(this).sendMessage(
                event.sourceNodeId,
                "/test_conexion_respuesta",
                "pong".toByteArray()
            )
        }
    }
}
