package com.example.ecorutaapp

import android.os.Bundle
import android.widget.Button
import android.widget.CheckBox
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class HabitosEcologicosActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_habitos_ecologicos)

        findViewById<Button>(R.id.btn_volver).setOnClickListener {
            finish()
        }

        findViewById<Button>(R.id.btn_guardar_habitos).setOnClickListener {
            val bolsa = findViewById<CheckBox>(R.id.cb_bolsa).isChecked
            val botella = findViewById<CheckBox>(R.id.cb_botella).isChecked
            val reciclaje = findViewById<CheckBox>(R.id.cb_reciclaje).isChecked

            Toast.makeText(this, "¡Hábitos guardados!", Toast.LENGTH_SHORT).show()
        }
    }
}
