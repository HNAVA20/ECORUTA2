package com.example.ecorutaapp

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class MainMenuActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_menu)

        findViewById<Button>(R.id.btn_habitos).setOnClickListener {
            startActivity(Intent(this, HabitosEcologicosActivity::class.java))
        }
        findViewById<Button>(R.id.btn_education).setOnClickListener {
            startActivity(Intent(this, EducacionAmbientalActivity::class.java))
        }
        findViewById<Button>(R.id.btn_centros).setOnClickListener {
            startActivity(Intent(this, CentrosReciclajeActivity::class.java))
        }
        val btnSync = findViewById<Button>(R.id.btn_sync_watch)
        btnSync.setOnClickListener {
            // Mostrar Toast y cambiar estado visual
            android.widget.Toast.makeText(this, "Sincronizando...", android.widget.Toast.LENGTH_SHORT).show()
            findViewById<android.widget.TextView>(R.id.txtSyncStatus).text = "Sincronizando..."
            // Simulación de envío de sincronización al reloj
            // Aquí deberías llamar a tu método real de sincronización
            com.example.ecorutaapp.utils.LocationSender.enviarTestSincronizacion(this) { exito ->
                if (exito) {
                    android.widget.Toast.makeText(this, "¡Conectado!", android.widget.Toast.LENGTH_SHORT).show()
                    findViewById<android.widget.TextView>(R.id.txtSyncStatus).text = "Conectado"
                } else {
                    android.widget.Toast.makeText(this, "Error de conexión", android.widget.Toast.LENGTH_SHORT).show()
                    findViewById<android.widget.TextView>(R.id.txtSyncStatus).text = "Sin conexión"
                }
            }
        }
    }
}
