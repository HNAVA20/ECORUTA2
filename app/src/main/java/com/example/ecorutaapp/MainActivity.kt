package com.example.ecorutaapp

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.bottomsheet.BottomSheetDialog
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

        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.mapFragment) as SupportMapFragment
        mapFragment.getMapAsync(this)

        val recyclerView = findViewById<RecyclerView>(R.id.centrosRecyclerView)
        val buscador = findViewById<EditText>(R.id.buscadorEditText)
        txtRecibido = findViewById(R.id.txtRecibido)

        centros = cargarCentrosDesdeJson()
        adapter = CentroAdapter(centros) { centro ->
            moverMapaACentro(centro)
        }

        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

        buscador.addTextChangedListener {
            val filtro = it.toString().trim().lowercase()
            val filtrados = centros.filter {
                it.materiales.lowercase().contains(filtro)
            }
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
            map.addMarker(
                MarkerOptions()
                    .position(posicion)
                    .title(centro.nombre)
                    .snippet("Materiales: ${centro.materiales}")
            )
        }
    }

    private fun moverMapaACentro(centro: CentroReciclaje) {
        val posicion = LatLng(centro.lat, centro.lng)
        map.animateCamera(CameraUpdateFactory.newLatLngZoom(posicion, 15f))
    }

    private fun mostrarDetallesCentro(centro: CentroReciclaje) {
        val view = layoutInflater.inflate(R.layout.bottom_centro, null)

        view.findViewById<TextView>(R.id.txtNombreCentro).text = centro.nombre
        view.findViewById<TextView>(R.id.txtUbicacion).text = "Ubicación: ${centro.lat}, ${centro.lng}"
        view.findViewById<TextView>(R.id.txtMaterialesAceptados).text = "Materiales aceptados: ${centro.materiales}"

        val bottomSheet = BottomSheetDialog(this)
        bottomSheet.setContentView(view)
        bottomSheet.show()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        if (requestCode == 1 && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            onMapReady(map)
        }
    }

    private fun cargarCentrosDesdeJson(): List<CentroReciclaje> {
        val jsonString = assets.open("centros.json").bufferedReader().use { it.readText() }
        val jsonArray = JSONArray(jsonString)
        val centros = mutableListOf<CentroReciclaje>()
        for (i in 0 until jsonArray.length()) {
            val obj = jsonArray.getJSONObject(i)
            val centro = CentroReciclaje(
                obj.getString("nombre"),
                obj.getDouble("lat"),
                obj.getDouble("lng"),
                obj.getString("materiales")
            )
            centros.add(centro)
        }
        return centros
    }

    override fun onMessageReceived(event: MessageEvent) {
        if (event.path == "/acelerometro") {
            val mensaje = String(event.data)
            runOnUiThread {
                txtRecibido.text = "Desde reloj: $mensaje"
            }
        }
    }
}

// Modelo de centro
data class CentroReciclaje(
    val nombre: String,
    val lat: Double,
    val lng: Double,
    val materiales: String
)
