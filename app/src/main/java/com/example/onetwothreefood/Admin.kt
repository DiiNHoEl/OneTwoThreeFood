package com.example.onetwothreefood

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton

class Admin : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin)

        val btnAdministrarPersonal = findViewById<MaterialButton>(R.id.btnAdministrarPersonal)
        val btnAdministrarMesas = findViewById<MaterialButton>(R.id.btnAdministrarMesas)
        val btnAdministrarPlatillos = findViewById<MaterialButton>(R.id.btnAdministrarPlatillos)
        val btnCerrarSesionAdmin = findViewById<MaterialButton>(R.id.btnCerrarSesionAdmin)

        btnAdministrarPersonal.setOnClickListener {
            startActivity(Intent(this, AdminPersonal::class.java))
        }

        btnAdministrarMesas.setOnClickListener {
            startActivity(Intent(this, AdminMesas::class.java))
        }

        btnAdministrarPlatillos.setOnClickListener {
            startActivity(Intent(this, AdminPlatillos::class.java))
        }

        btnCerrarSesionAdmin.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
    }
}