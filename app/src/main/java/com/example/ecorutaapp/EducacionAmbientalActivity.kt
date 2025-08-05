package com.example.ecorutaapp

import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class EducacionAmbientalActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_educacion_ambiental)

        findViewById<Button>(R.id.btn_volver).setOnClickListener {
            finish()
        }
    }
}