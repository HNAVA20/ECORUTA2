package com.example.wear

import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.os.Looper
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.ComponentActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.wear.listeners.DataListener
import com.example.wear.sensors.CompassManager
import com.example.wear.viewmodel.WearViewModel
import com.google.android.gms.location.*
import com.google.android.gms.wearable.MessageClient
import com.google.android.gms.wearable.MessageEvent
import com.google.android.gms.wearable.Wearable

class MainActivity : ComponentActivity(), MessageClient.OnMessageReceivedListener {

    private lateinit var listener: DataListener
    private lateinit var compassManager: CompassManager
    private lateinit var viewModel: WearViewModel
    private var currentLocation: Location? = null
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationCallback: LocationCallback

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_direction)

        // Referencias de UI
        val arrowImageView = findViewById<ImageView>(R.id.arrowImageView)
        val destinationTextView = findViewById<TextView>(R.id.destinationTextView)
        val txtStatus = findViewById<TextView>(R.id.txtStatus)
        val btnTestConexion = findViewById<Button>(R.id.btnTestConexion)

        // ViewModel y listener de datos
        viewModel = ViewModelProvider(this)[WearViewModel::class.java]
        listener = DataListener(viewModel)
        Wearable.getDataClient(this).addListener(listener)

        // Sensor de brújula
        compassManager = CompassManager(this)
        compassManager.start()

        // Obtener ubicación actual
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        locationCallback = object : LocationCallback() {
            override fun onLocationResult(result: LocationResult) {
                currentLocation = result.lastLocation
                updateArrow(arrowImageView, destinationTextView)
            }
        }

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
            == PackageManager.PERMISSION_GRANTED
        ) {
            val request = LocationRequest.create().apply {
                interval = 1000
                fastestInterval = 500
                priority = Priority.PRIORITY_HIGH_ACCURACY
            }
            fusedLocationClient.requestLocationUpdates(request, locationCallback, Looper.getMainLooper())
        }

        // Si no hay destino recibido, usar uno por defecto
        if (viewModel.destinoLat.value == null || viewModel.destinoLng.value == null) {
            // Ejemplo: Centro de reciclaje "Centro 1" (ajusta los valores según tus datos reales)
            viewModel.setDestino(21.5058, -104.8940, "Centro 1")
        }

        // Observa cambios en destino y azimuth
        viewModel.destinoLat.observe(this, Observer { updateArrow(arrowImageView, destinationTextView) })
        viewModel.destinoLng.observe(this, Observer { updateArrow(arrowImageView, destinationTextView) })
        viewModel.destinoName.observe(this, Observer { updateArrow(arrowImageView, destinationTextView) })
        compassManager.azimuth.observe(this, Observer { updateArrow(arrowImageView, destinationTextView) })

        txtStatus.text = "Sin conexión"
        txtStatus.setTextColor(getColor(android.R.color.holo_red_light))

        // Actualiza el estado cuando se reciben datos
        viewModel.destinoLat.observe(this, Observer {
            txtStatus.text = "Conectado"
            txtStatus.setTextColor(getColor(android.R.color.holo_green_light))
        })
        viewModel.destinoLng.observe(this, Observer {
            txtStatus.text = "Conectado"
            txtStatus.setTextColor(getColor(android.R.color.holo_green_light))
        })
        viewModel.destinoName.observe(this, Observer {
            txtStatus.text = "Conectado"
            txtStatus.setTextColor(getColor(android.R.color.holo_green_light))
        })

        btnTestConexion.setOnClickListener {
            txtStatus.text = "Probando..."
            txtStatus.setTextColor(getColor(android.R.color.holo_orange_light))
            Wearable.getNodeClient(this).connectedNodes.addOnSuccessListener { nodes ->
                if (nodes.isNotEmpty()) {
                    val nodeId = nodes[0].id
                    Wearable.getMessageClient(this).sendMessage(
                        nodeId,
                        "/test_conexion",
                        "ping".toByteArray()
                    ).addOnSuccessListener {
                        txtStatus.text = "Test enviado"
                    }.addOnFailureListener {
                        txtStatus.text = "Error de conexión"
                        txtStatus.setTextColor(getColor(android.R.color.holo_red_light))
                    }
                } else {
                    txtStatus.text = "No se encontró el móvil"
                    txtStatus.setTextColor(getColor(android.R.color.holo_red_light))
                }
            }
        }

        Wearable.getMessageClient(this).addListener(this)
    }

    private fun updateArrow(arrowImageView: ImageView, destinationTextView: TextView) {
        val lat = viewModel.destinoLat.value ?: return
        val lng = viewModel.destinoLng.value ?: return
        val name = viewModel.destinoName.value ?: ""
        val azimuth = compassManager.azimuth.value ?: 0f

        val destinoLocation = Location("").apply {
            latitude = lat
            longitude = lng
        }

        val bearing = currentLocation?.bearingTo(destinoLocation) ?: 0f
        val arrowRotation = (bearing - azimuth + 360) % 360

        arrowImageView.rotation = arrowRotation
        destinationTextView.text = "Ir a: $name"
    }

    override fun onDestroy() {
        super.onDestroy()
        Wearable.getDataClient(this).removeListener(listener)
        compassManager.stop()
        fusedLocationClient.removeLocationUpdates(locationCallback)
        Wearable.getMessageClient(this).removeListener(this)
    }

    override fun onMessageReceived(event: MessageEvent) {
        val txtStatus = findViewById<TextView>(R.id.txtStatus)
        if (event.path == "/test_conexion_respuesta") {
            runOnUiThread {
                txtStatus.text = "¡Conexión exitosa!"
                txtStatus.setTextColor(getColor(android.R.color.holo_green_light))
            }
        }
    }
}
