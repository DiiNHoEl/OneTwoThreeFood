package com.example.onetwothreefood

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.CheckBox
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.POST

class MainActivity : AppCompatActivity() {

    // =========================
    // MODELOS DE RESPUESTA
    // =========================

    data class User(
        val id: Int,
        val nombre: String,
        val usuario: String,
        val rol: String
    )

    data class LoginRequest(
        val usuario: String,
        val contrasena: String
    )

    data class LoginResponse(
        val success: Boolean,
        val message: String,
        val user: User?
    )

    // =========================
    // API SERVICE
    // =========================

    interface ApiService {
        @POST("auth/login")
        suspend fun login(
            @Body request: LoginRequest
        ): LoginResponse
    }

    // =========================
    // RETROFIT CLIENT
    // =========================

    object RetrofitClient {
        // CAMBIA ESTA URL POR TU URL REAL
        private const val BASE_URL = "https://j443sw77-3000.usw3.devtunnels.ms/"

        val instance: ApiService by lazy {
            Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(ApiService::class.java)
        }
    }

    // =========================
    // ON CREATE
    // =========================

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val editUsuario = findViewById<EditText>(R.id.editUsuario)
        val editContrasena = findViewById<EditText>(R.id.editContrasena)
        val btnIniciarSesion = findViewById<MaterialButton>(R.id.btnIniciarSesion)
        val checkRecordar = findViewById<CheckBox>(R.id.checkRecordar)
        val progressBarLogin = findViewById<ProgressBar>(R.id.progressBarLogin)

        // SharedPreferences
        val preferencias = getSharedPreferences("datos_login", Context.MODE_PRIVATE)

        val usuarioGuardado = preferencias.getString("usuario_recordado", "")
        val contrasenaGuardada = preferencias.getString("contrasena_recordada", "")

        // Si hay datos recordados, solo llenar campos
        if (!usuarioGuardado.isNullOrEmpty() && !contrasenaGuardada.isNullOrEmpty()) {
            editUsuario.setText(usuarioGuardado)
            editContrasena.setText(contrasenaGuardada)
            checkRecordar.isChecked = true
        }

        btnIniciarSesion.setOnClickListener {
            val usuario = editUsuario.text.toString().trim()
            val contrasena = editContrasena.text.toString().trim()

            if (usuario.isEmpty() || contrasena.isEmpty()) {
                Toast.makeText(this, "Por favor, completa los campos", Toast.LENGTH_SHORT).show()
            } else {
                iniciarSesion(
                    usuario,
                    contrasena,
                    checkRecordar.isChecked,
                    progressBarLogin,
                    btnIniciarSesion
                )
            }
        }
    }

    // =========================
    // LOGIN
    // =========================

    private fun iniciarSesion(
        usuario: String,
        contrasena: String,
        recordar: Boolean,
        progressBarLogin: ProgressBar,
        btnIniciarSesion: MaterialButton
    ) {
        progressBarLogin.visibility = View.VISIBLE
        btnIniciarSesion.isEnabled = false

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = RetrofitClient.instance.login(
                    LoginRequest(usuario, contrasena)
                )

                withContext(Dispatchers.Main) {
                    progressBarLogin.visibility = View.GONE
                    btnIniciarSesion.isEnabled = true

                    if (response.success && response.user != null) {
                        Toast.makeText(
                            this@MainActivity,
                            "Bienvenido ${response.user.nombre}",
                            Toast.LENGTH_SHORT
                        ).show()

                        val preferencias = getSharedPreferences("datos_login", Context.MODE_PRIVATE)
                        val editor = preferencias.edit()

                        if (recordar) {
                            editor.putString("usuario_recordado", usuario)
                            editor.putString("contrasena_recordada", contrasena)
                        } else {
                            editor.remove("usuario_recordado")
                            editor.remove("contrasena_recordada")
                        }

                        editor.apply()

                        redirigirSegunRol(response.user.id, response.user.usuario, response.user.rol)

                    } else {
                        Toast.makeText(
                            this@MainActivity,
                            response.message,
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    progressBarLogin.visibility = View.GONE
                    btnIniciarSesion.isEnabled = true

                    Log.e("LoginError", "Error: ${e.message}")

                    Toast.makeText(
                        this@MainActivity,
                        "Error de conexión: ${e.message}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }
    }

    // =========================
    // REDIRECCIÓN SEGÚN ROL
    // =========================

    private fun redirigirSegunRol(id: Int, usuario: String, rol: String) {
        val intent = when (rol) {
            "admin" -> Intent(this, Admin::class.java)
            "mesero" -> Intent(this, Mesero::class.java)
            "cajero" -> Intent(this, Cajero::class.java)
            "cocina" -> Intent(this, Cocina::class.java)
            else -> null
        }

        if (intent != null) {
            intent.putExtra("id", id)
            intent.putExtra("usuario", usuario)
            intent.putExtra("rol", rol)
            startActivity(intent)
            finish()
        } else {
            Toast.makeText(this, "Rol no reconocido", Toast.LENGTH_SHORT).show()
        }
    }
}
