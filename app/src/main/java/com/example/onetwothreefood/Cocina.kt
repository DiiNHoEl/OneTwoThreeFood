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
import retrofit2.http.PUT
import retrofit2.http.Path

class Cocina : AppCompatActivity() {

    data class Mesa(
        val id: Int,
        val numero: Int,
        val capacidad: Int,
        val estado: String
    )

    data class MeseroInfo(
        val id: Int,
        val nombre: String,
        val usuario: String,
        val rol: String
    )

    data class Platillo(
        val id: Int,
        val nombre: String,
        val descripcion: String?,
        val precio: Double,
        val categoria: String,
        val disponible: Boolean
    )

    data class Detalle(
        val id: Int,
        val cantidad: Int,
        val precioUnitario: Double,
        val subtotal: Double,
        val platillo: Platillo?
    )

    data class Comanda(
        val id: Int,
        val estado: String,
        val total: Double,
        val fecha: String,
        val mesa: Mesa?,
        val mesero: MeseroInfo?,
        val detalles: List<Detalle>?
    )

    data class ComandasResponse(
        val success: Boolean,
        val message: String?,
        val comandas: List<Comanda>?
    )

    data class EstadoRequest(
        val estado: String
    )

    data class ComandaResponse(
        val success: Boolean,
        val message: String?,
        val comanda: Comanda?
    )

    interface ApiService {

        @GET("comandas/pendientes/cocina")
        suspend fun consultarComandasCocina(): ComandasResponse

        @PUT("comandas/{id}/estado")
        suspend fun actualizarEstadoComanda(
            @Path("id") id: Int,
            @Body request: EstadoRequest
        ): ComandaResponse
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
        setContentView(R.layout.activity_cocina)

        val textUsuarioCocina = findViewById<TextView>(R.id.textUsuarioCocina)
        val editIdComandaCocina = findViewById<EditText>(R.id.editIdComandaCocina)

        val btnConsultarComandasCocina = findViewById<MaterialButton>(R.id.btnConsultarComandasCocina)
        val btnCambiarPreparando = findViewById<MaterialButton>(R.id.btnCambiarPreparando)
        val btnCambiarListo = findViewById<MaterialButton>(R.id.btnCambiarListo)
        val btnCerrarSesionCocina = findViewById<MaterialButton>(R.id.btnCerrarSesionCocina)

        val textResultadoCocina = findViewById<TextView>(R.id.textResultadoCocina)
        val progressBarCocina = findViewById<ProgressBar>(R.id.progressBarCocina)

        val preferencias = getSharedPreferences("datos_login", Context.MODE_PRIVATE)

        val usuarioIntent = intent.getStringExtra("usuario")
        val usuarioGuardado = if (!usuarioIntent.isNullOrEmpty()) {
            usuarioIntent
        } else {
            preferencias.getString("usuario_recordado", "") ?: ""
        }

        textUsuarioCocina.text = "Usuario de cocina: $usuarioGuardado"

        btnConsultarComandasCocina.setOnClickListener {
            consultarComandasCocina(textResultadoCocina, progressBarCocina)
        }

        btnCambiarPreparando.setOnClickListener {
            val idTexto = editIdComandaCocina.text.toString().trim()

            if (idTexto.isEmpty()) {
                Toast.makeText(this, "Ingresa el ID de la comanda", Toast.LENGTH_SHORT).show()
            } else {
                actualizarEstadoComanda(
                    idTexto.toInt(),
                    "preparando",
                    textResultadoCocina,
                    progressBarCocina
                )
            }
        }

        btnCambiarListo.setOnClickListener {
            val idTexto = editIdComandaCocina.text.toString().trim()

            if (idTexto.isEmpty()) {
                Toast.makeText(this, "Ingresa el ID de la comanda", Toast.LENGTH_SHORT).show()
            } else {
                actualizarEstadoComanda(
                    idTexto.toInt(),
                    "listo",
                    textResultadoCocina,
                    progressBarCocina
                )
            }
        }

        btnCerrarSesionCocina.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
    }

    private fun consultarComandasCocina(
        textResultadoCocina: TextView,
        progressBarCocina: ProgressBar
    ) {
        progressBarCocina.visibility = View.VISIBLE

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = RetrofitClient.instance.consultarComandasCocina()

                withContext(Dispatchers.Main) {
                    progressBarCocina.visibility = View.GONE

                    if (response.success && !response.comandas.isNullOrEmpty()) {
                        val resultado = StringBuilder()

                        for (comanda in response.comandas) {
                            resultado.append(
                                "COMANDA ID: ${comanda.id}\n" +
                                        "Estado: ${comanda.estado}\n" +
                                        "Mesa: ${comanda.mesa?.numero}\n" +
                                        "Mesero: ${comanda.mesero?.nombre}\n" +
                                        "Total: ${comanda.total}\n"
                            )

                            if (!comanda.detalles.isNullOrEmpty()) {
                                resultado.append("Detalle:\n")
                                for (detalle in comanda.detalles) {
                                    resultado.append(
                                        "- ${detalle.platillo?.nombre} | " +
                                                "Cantidad: ${detalle.cantidad} | " +
                                                "Subtotal: ${detalle.subtotal}\n"
                                    )
                                }
                            }

                            resultado.append("\n")
                        }

                        textResultadoCocina.text = resultado.toString()
                        Toast.makeText(this@Cocina, "Pedidos consultados correctamente", Toast.LENGTH_SHORT).show()
                    } else {
                        textResultadoCocina.text = response.message ?: "No hay pedidos pendientes"
                        Toast.makeText(this@Cocina, response.message ?: "No hay pedidos pendientes", Toast.LENGTH_SHORT).show()
                    }
                }

            } catch (e: HttpException) {
                withContext(Dispatchers.Main) {
                    progressBarCocina.visibility = View.GONE
                    textResultadoCocina.text = "Error HTTP: ${e.code()}"
                    Toast.makeText(this@Cocina, "Error HTTP: ${e.code()}", Toast.LENGTH_SHORT).show()
                    Log.e("CocinaConsultarError", "Error HTTP: ${e.message}")
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    progressBarCocina.visibility = View.GONE
                    textResultadoCocina.text = "Error: ${e.message}"
                    Toast.makeText(this@Cocina, "Error de conexión", Toast.LENGTH_SHORT).show()
                    Log.e("CocinaConsultarError", "Error: ${e.message}")
                }
            }
        }
    }

    private fun actualizarEstadoComanda(
        idComanda: Int,
        nuevoEstado: String,
        textResultadoCocina: TextView,
        progressBarCocina: ProgressBar
    ) {
        progressBarCocina.visibility = View.VISIBLE

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = RetrofitClient.instance.actualizarEstadoComanda(
                    idComanda,
                    EstadoRequest(nuevoEstado)
                )

                withContext(Dispatchers.Main) {
                    progressBarCocina.visibility = View.GONE

                    if (response.success) {
                        textResultadoCocina.text =
                            "Estado actualizado correctamente\n\n" +
                                    "Comanda ID: ${response.comanda?.id}\n" +
                                    "Nuevo estado: ${response.comanda?.estado}"

                        Toast.makeText(
                            this@Cocina,
                            "Comanda cambiada a $nuevoEstado",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        textResultadoCocina.text = response.message ?: "No se pudo actualizar el estado"
                        Toast.makeText(
                            this@Cocina,
                            response.message ?: "No se pudo actualizar el estado",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

            } catch (e: HttpException) {
                withContext(Dispatchers.Main) {
                    progressBarCocina.visibility = View.GONE

                    if (e.code() == 404) {
                        textResultadoCocina.text = "Comanda no encontrada"
                        Toast.makeText(this@Cocina, "Comanda no encontrada", Toast.LENGTH_SHORT).show()
                    } else if (e.code() == 400) {
                        textResultadoCocina.text = "Estado inválido o datos incorrectos"
                        Toast.makeText(this@Cocina, "Estado inválido o datos incorrectos", Toast.LENGTH_SHORT).show()
                    } else {
                        textResultadoCocina.text = "Error HTTP: ${e.code()}"
                        Toast.makeText(this@Cocina, "Error HTTP: ${e.code()}", Toast.LENGTH_SHORT).show()
                    }

                    Log.e("CocinaActualizarEstadoError", "Error HTTP: ${e.message}")
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    progressBarCocina.visibility = View.GONE
                    textResultadoCocina.text = "Error: ${e.message}"
                    Toast.makeText(this@Cocina, "Error de conexión", Toast.LENGTH_SHORT).show()
                    Log.e("CocinaActualizarEstadoError", "Error: ${e.message}")
                }
            }
        }
    }
}