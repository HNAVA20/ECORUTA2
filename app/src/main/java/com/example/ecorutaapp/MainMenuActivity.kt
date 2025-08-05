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
    }
}
