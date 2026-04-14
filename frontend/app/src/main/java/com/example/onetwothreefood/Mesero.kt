package com.example.onetwothreefood

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

class Mesero : AppCompatActivity() {

    data class Mesa(
        val id: Int,
        val numero: Int,
        val capacidad: Int,
        val estado: String
    )

    data class Platillo(
        val id: Int,
        val nombre: String,
        val descripcion: String?,
        val precio: Double,
        val categoria: String,
        val disponible: Boolean
    )

    data class MesasResponse(
        val success: Boolean,
        val message: String?,
        val mesas: List<Mesa>?
    )

    data class PlatillosResponse(
        val success: Boolean,
        val message: String?,
        val platillos: List<Platillo>?
    )

    data class DetalleRequest(
        val platilloId: Int,
        val cantidad: Int
    )

    data class ComandaRequest(
        val mesaId: Int,
        val meseroId: Int,
        val detalles: List<DetalleRequest>
    )

    data class GenericResponse(
        val success: Boolean,
        val message: String
    )

    interface ApiService {

        @GET("mesas")
        suspend fun consultarMesas(): MesasResponse

        @GET("platillos")
        suspend fun consultarPlatillos(): PlatillosResponse

        @POST("comandas")
        suspend fun registrarComanda(
            @Body request: ComandaRequest
        ): GenericResponse
    }

    object RetrofitClient {
        private const val BASE_URL = "https://j443sw77-3000.usw3.devtunnels.ms/"

        val instance: ApiService by lazy {
            Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(ApiService::class.java)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mesero)

        val textUsuarioMesero = findViewById<TextView>(R.id.textUsuarioMesero)
        val editMesaId = findViewById<EditText>(R.id.editMesaId)
        val editPlatilloId = findViewById<EditText>(R.id.editPlatilloId)
        val editCantidad = findViewById<EditText>(R.id.editCantidad)

        val btnConsultarMesasMesero = findViewById<MaterialButton>(R.id.btnConsultarMesasMesero)
        val btnConsultarPlatillosMesero = findViewById<MaterialButton>(R.id.btnConsultarPlatillosMesero)
        val btnRegistrarComanda = findViewById<MaterialButton>(R.id.btnRegistrarComanda)
        val btnCerrarSesionMesero = findViewById<MaterialButton>(R.id.btnCerrarSesionMesero)

        val textResultadoMesero = findViewById<TextView>(R.id.textResultadoMesero)
        val progressBarMesero = findViewById<ProgressBar>(R.id.progressBarMesero)

        val preferencias = getSharedPreferences("datos_login", Context.MODE_PRIVATE)

        val usuarioIntent = intent.getStringExtra("usuario")
        val idIntent = intent.getIntExtra("id", 0)

        val usuarioGuardado = if (!usuarioIntent.isNullOrEmpty()) {
            usuarioIntent
        } else {
            preferencias.getString("usuario", "") ?: ""
        }

        val idMesero = if (idIntent != 0) {
            idIntent
        } else {
            preferencias.getInt("id", 0)
        }

        textUsuarioMesero.text = "Mesero: $usuarioGuardado"

        btnConsultarMesasMesero.setOnClickListener {
            consultarMesas(textResultadoMesero, progressBarMesero)
        }

        btnConsultarPlatillosMesero.setOnClickListener {
            consultarPlatillos(textResultadoMesero, progressBarMesero)
        }

        btnRegistrarComanda.setOnClickListener {
            val mesaIdTexto = editMesaId.text.toString().trim()
            val platilloIdTexto = editPlatilloId.text.toString().trim()
            val cantidadTexto = editCantidad.text.toString().trim()

            if (idMesero == 0) {
                Toast.makeText(this, "No se encontró el ID del mesero en sesión", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (mesaIdTexto.isEmpty() || platilloIdTexto.isEmpty() || cantidadTexto.isEmpty()) {
                Toast.makeText(this, "Completa mesa, platillo y cantidad", Toast.LENGTH_SHORT).show()
            } else {
                registrarComanda(
                    mesaIdTexto.toInt(),
                    idMesero,
                    platilloIdTexto.toInt(),
                    cantidadTexto.toInt(),
                    textResultadoMesero,
                    progressBarMesero
                )
            }
        }

        btnCerrarSesionMesero.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
    }

    private fun consultarMesas(
        textResultadoMesero: TextView,
        progressBarMesero: ProgressBar
    ) {
        progressBarMesero.visibility = View.VISIBLE

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = RetrofitClient.instance.consultarMesas()

                withContext(Dispatchers.Main) {
                    progressBarMesero.visibility = View.GONE

                    if (response.success && !response.mesas.isNullOrEmpty()) {
                        val resultado = StringBuilder()

                        for (mesa in response.mesas) {
                            resultado.append(
                                "ID: ${mesa.id}\n" +
                                        "Número: ${mesa.numero}\n" +
                                        "Capacidad: ${mesa.capacidad}\n" +
                                        "Estado: ${mesa.estado}\n\n"
                            )
                        }

                        textResultadoMesero.text = resultado.toString()
                        Toast.makeText(this@Mesero, "Mesas consultadas correctamente", Toast.LENGTH_SHORT).show()
                    } else {
                        textResultadoMesero.text = response.message ?: "No hay mesas registradas"
                        Toast.makeText(this@Mesero, response.message ?: "No hay mesas registradas", Toast.LENGTH_SHORT).show()
                    }
                }

            } catch (e: HttpException) {
                withContext(Dispatchers.Main) {
                    progressBarMesero.visibility = View.GONE
                    textResultadoMesero.text = "Error HTTP: ${e.code()}"
                    Toast.makeText(this@Mesero, "Error HTTP: ${e.code()}", Toast.LENGTH_SHORT).show()
                    Log.e("MeseroMesasError", "Error HTTP: ${e.message}")
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    progressBarMesero.visibility = View.GONE
                    textResultadoMesero.text = "Error: ${e.message}"
                    Toast.makeText(this@Mesero, "Error de conexión", Toast.LENGTH_SHORT).show()
                    Log.e("MeseroMesasError", "Error: ${e.message}")
                }
            }
        }
    }

    private fun consultarPlatillos(
        textResultadoMesero: TextView,
        progressBarMesero: ProgressBar
    ) {
        progressBarMesero.visibility = View.VISIBLE

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = RetrofitClient.instance.consultarPlatillos()

                withContext(Dispatchers.Main) {
                    progressBarMesero.visibility = View.GONE

                    if (response.success && !response.platillos.isNullOrEmpty()) {
                        val resultado = StringBuilder()

                        for (platillo in response.platillos) {
                            resultado.append(
                                "ID: ${platillo.id}\n" +
                                        "Nombre: ${platillo.nombre}\n" +
                                        "Descripción: ${platillo.descripcion}\n" +
                                        "Precio: ${platillo.precio}\n" +
                                        "Categoría: ${platillo.categoria}\n" +
                                        "Disponible: ${if (platillo.disponible) "Si" else "No"}\n\n"
                            )
                        }

                        textResultadoMesero.text = resultado.toString()
                        Toast.makeText(this@Mesero, "Platillos consultados correctamente", Toast.LENGTH_SHORT).show()
                    } else {
                        textResultadoMesero.text = response.message ?: "No hay platillos registrados"
                        Toast.makeText(this@Mesero, response.message ?: "No hay platillos registrados", Toast.LENGTH_SHORT).show()
                    }
                }

            } catch (e: HttpException) {
                withContext(Dispatchers.Main) {
                    progressBarMesero.visibility = View.GONE
                    textResultadoMesero.text = "Error HTTP: ${e.code()}"
                    Toast.makeText(this@Mesero, "Error HTTP: ${e.code()}", Toast.LENGTH_SHORT).show()
                    Log.e("MeseroPlatillosError", "Error HTTP: ${e.message}")
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    progressBarMesero.visibility = View.GONE
                    textResultadoMesero.text = "Error: ${e.message}"
                    Toast.makeText(this@Mesero, "Error de conexión", Toast.LENGTH_SHORT).show()
                    Log.e("MeseroPlatillosError", "Error: ${e.message}")
                }
            }
        }
    }

    private fun registrarComanda(
        mesaId: Int,
        meseroId: Int,
        platilloId: Int,
        cantidad: Int,
        textResultadoMesero: TextView,
        progressBarMesero: ProgressBar
    ) {
        progressBarMesero.visibility = View.VISIBLE

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = RetrofitClient.instance.registrarComanda(
                    ComandaRequest(
                        mesaId = mesaId,
                        meseroId = meseroId,
                        detalles = listOf(
                            DetalleRequest(
                                platilloId = platilloId,
                                cantidad = cantidad
                            )
                        )
                    )
                )

                withContext(Dispatchers.Main) {
                    progressBarMesero.visibility = View.GONE
                    textResultadoMesero.text = response.message
                    Toast.makeText(this@Mesero, response.message, Toast.LENGTH_SHORT).show()
                }

            } catch (e: HttpException) {
                withContext(Dispatchers.Main) {
                    progressBarMesero.visibility = View.GONE

                    when (e.code()) {
                        404 -> {
                            textResultadoMesero.text = "Mesa, mesero o platillo no encontrado"
                            Toast.makeText(this@Mesero, "Mesa, mesero o platillo no encontrado", Toast.LENGTH_SHORT).show()
                        }
                        409 -> {
                            textResultadoMesero.text = "La mesa ya tiene una comanda activa"
                            Toast.makeText(this@Mesero, "La mesa ya tiene una comanda activa", Toast.LENGTH_SHORT).show()
                        }
                        400 -> {
                            textResultadoMesero.text = "Datos inválidos para registrar la comanda"
                            Toast.makeText(this@Mesero, "Datos inválidos para registrar la comanda", Toast.LENGTH_SHORT).show()
                        }
                        else -> {
                            textResultadoMesero.text = "Error HTTP: ${e.code()}"
                            Toast.makeText(this@Mesero, "Error HTTP: ${e.code()}", Toast.LENGTH_SHORT).show()
                        }
                    }

                    Log.e("MeseroComandaError", "Error HTTP: ${e.message}")
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    progressBarMesero.visibility = View.GONE
                    textResultadoMesero.text = "Error: ${e.message}"
                    Toast.makeText(this@Mesero, "Error de conexión", Toast.LENGTH_SHORT).show()
                    Log.e("MeseroComandaError", "Error: ${e.message}")
                }
            }
        }
    }
}