package com.example.wear

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import com.google.android.gms.wearable.Wearable
import java.nio.charset.StandardCharsets

class MainActivity : ComponentActivity() {

    private lateinit var locationManager: LocationManager
    private lateinit var sensorManager: SensorManager
    private lateinit var txtUbicacion: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        txtUbicacion = findViewById(R.id.txtUbicacion)
        locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager

        solicitarPermisosUbicacion()
        iniciarAcelerometro()
    }

    private fun solicitarPermisosUbicacion() {
        val permisos = arrayOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )

        val permisosNoConcedidos = permisos.filter {
            ContextCompat.checkSelfPermission(this, it) != PackageManager.PERMISSION_GRANTED
        }

        if (permisosNoConcedidos.isNotEmpty()) {
            registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { resultados ->
                if (resultados.all { it.value }) {
                    iniciarGPS()
                } else {
                    Toast.makeText(this, "Permisos de ubicación denegados", Toast.LENGTH_SHORT).show()
                }
            }.launch(permisosNoConcedidos.toTypedArray())
        } else {
            iniciarGPS()
        }
    }

    private fun iniciarGPS() {
        try {
            locationManager.requestLocationUpdates(
                LocationManager.GPS_PROVIDER,
                5000L,
                5f,
                object : LocationListener {
                    override fun onLocationChanged(location: Location) {
                        val ubicacion = "Lat: ${location.latitude}, Lng: ${location.longitude}"
                        txtUbicacion.text = ubicacion
                        enviarMensajeAlTelefono(ubicacion)
                    }
                }
            )
        } catch (e: SecurityException) {
            e.printStackTrace()
            Toast.makeText(this, "Error: permisos no concedidos", Toast.LENGTH_SHORT).show()
        }
    }

    private fun iniciarAcelerometro() {
        val acelerometro = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        if (acelerometro != null) {
            sensorManager.registerListener(object : SensorEventListener {
                override fun onSensorChanged(event: SensorEvent) {
                    val x = event.values[0]
                    val y = event.values[1]
                    val z = event.values[2]
                    val datos = "Accel X=$x Y=$y Z=$z"
                    txtUbicacion.text = datos
                    enviarMensajeAlTelefono(datos)
                }

                override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
            }, acelerometro, SensorManager.SENSOR_DELAY_NORMAL)

            Toast.makeText(this, "Acelerómetro activo ✅", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "No hay acelerómetro ❌", Toast.LENGTH_SHORT).show()
        }
    }

    private fun enviarMensajeAlTelefono(mensaje: String) {
        val cliente = Wearable.getMessageClient(this)
        val nodos = Wearable.getNodeClient(this).connectedNodes
        nodos.addOnSuccessListener { lista ->
            for (nodo in lista) {
                cliente.sendMessage(nodo.id, "/acelerometro", mensaje.toByteArray(StandardCharsets.UTF_8))
            }
        }
    }
}
