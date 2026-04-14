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
import retrofit2.http.GET
import retrofit2.http.PUT
import retrofit2.http.Path

class Cajero : AppCompatActivity() {

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

    data class ComandaResponse(
        val success: Boolean,
        val message: String?,
        val comanda: Comanda?
    )

    interface ApiService {

        @GET("comandas/listas/caja")
        suspend fun consultarComandasListas(): ComandasResponse

        @PUT("comandas/{id}/cobrar")
        suspend fun cobrarComanda(
            @Path("id") id: Int
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
        setContentView(R.layout.activity_cajero)

        val textUsuarioCajero = findViewById<TextView>(R.id.textUsuarioCajero)
        val editIdComandaCajero = findViewById<EditText>(R.id.editIdComandaCajero)

        val btnConsultarComandasCajero = findViewById<MaterialButton>(R.id.btnConsultarComandasCajero)
        val btnCobrarComanda = findViewById<MaterialButton>(R.id.btnCobrarComanda)
        val btnCerrarSesionCajero = findViewById<MaterialButton>(R.id.btnCerrarSesionCajero)

        val textResultadoCajero = findViewById<TextView>(R.id.textResultadoCajero)
        val progressBarCajero = findViewById<ProgressBar>(R.id.progressBarCajero)

        val preferencias = getSharedPreferences("datos_login", Context.MODE_PRIVATE)

        val usuarioIntent = intent.getStringExtra("usuario")
        val usuarioGuardado = if (!usuarioIntent.isNullOrEmpty()) {
            usuarioIntent
        } else {
            preferencias.getString("usuario_recordado", "") ?: ""
        }

        textUsuarioCajero.text = "Usuario cajero: $usuarioGuardado"

        btnConsultarComandasCajero.setOnClickListener {
            consultarComandasListas(textResultadoCajero, progressBarCajero)
        }

        btnCobrarComanda.setOnClickListener {
            val idTexto = editIdComandaCajero.text.toString().trim()

            if (idTexto.isEmpty()) {
                Toast.makeText(this, "Ingresa el ID de la comanda", Toast.LENGTH_SHORT).show()
            } else {
                cobrarComanda(
                    idTexto.toInt(),
                    textResultadoCajero,
                    progressBarCajero
                )
            }
        }

        btnCerrarSesionCajero.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
    }

    private fun consultarComandasListas(
        textResultadoCajero: TextView,
        progressBarCajero: ProgressBar
    ) {
        progressBarCajero.visibility = View.VISIBLE

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = RetrofitClient.instance.consultarComandasListas()

                withContext(Dispatchers.Main) {
                    progressBarCajero.visibility = View.GONE

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

                        textResultadoCajero.text = resultado.toString()
                        Toast.makeText(this@Cajero, "Comandas listas consultadas correctamente", Toast.LENGTH_SHORT).show()
                    } else {
                        textResultadoCajero.text = response.message ?: "No hay comandas listas para cobro"
                        Toast.makeText(this@Cajero, response.message ?: "No hay comandas listas para cobro", Toast.LENGTH_SHORT).show()
                    }
                }

            } catch (e: HttpException) {
                withContext(Dispatchers.Main) {
                    progressBarCajero.visibility = View.GONE
                    textResultadoCajero.text = "Error HTTP: ${e.code()}"
                    Toast.makeText(this@Cajero, "Error HTTP: ${e.code()}", Toast.LENGTH_SHORT).show()
                    Log.e("CajeroConsultarError", "Error HTTP: ${e.message}")
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    progressBarCajero.visibility = View.GONE
                    textResultadoCajero.text = "Error: ${e.message}"
                    Toast.makeText(this@Cajero, "Error de conexión", Toast.LENGTH_SHORT).show()
                    Log.e("CajeroConsultarError", "Error: ${e.message}")
                }
            }
        }
    }

    private fun cobrarComanda(
        idComanda: Int,
        textResultadoCajero: TextView,
        progressBarCajero: ProgressBar
    ) {
        progressBarCajero.visibility = View.VISIBLE

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = RetrofitClient.instance.cobrarComanda(idComanda)

                withContext(Dispatchers.Main) {
                    progressBarCajero.visibility = View.GONE

                    if (response.success) {
                        textResultadoCajero.text =
                            "Comanda cobrada correctamente\n\n" +
                                    "Comanda ID: ${response.comanda?.id}\n" +
                                    "Nuevo estado: ${response.comanda?.estado}\n" +
                                    "Mesa liberada correctamente"

                        Toast.makeText(
                            this@Cajero,
                            "Comanda cobrada correctamente",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        textResultadoCajero.text = response.message ?: "No se pudo cobrar la comanda"
                        Toast.makeText(
                            this@Cajero,
                            response.message ?: "No se pudo cobrar la comanda",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

            } catch (e: HttpException) {
                withContext(Dispatchers.Main) {
                    progressBarCajero.visibility = View.GONE

                    when (e.code()) {
                        404 -> {
                            textResultadoCajero.text = "Comanda no encontrada"
                            Toast.makeText(this@Cajero, "Comanda no encontrada", Toast.LENGTH_SHORT).show()
                        }
                        400 -> {
                            textResultadoCajero.text = "Solo se pueden cobrar comandas listas"
                            Toast.makeText(this@Cajero, "Solo se pueden cobrar comandas listas", Toast.LENGTH_SHORT).show()
                        }
                        else -> {
                            textResultadoCajero.text = "Error HTTP: ${e.code()}"
                            Toast.makeText(this@Cajero, "Error HTTP: ${e.code()}", Toast.LENGTH_SHORT).show()
                        }
                    }

                    Log.e("CajeroCobrarError", "Error HTTP: ${e.message}")
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    progressBarCajero.visibility = View.GONE
                    textResultadoCajero.text = "Error: ${e.message}"
                    Toast.makeText(this@Cajero, "Error de conexión", Toast.LENGTH_SHORT).show()
                    Log.e("CajeroCobrarError", "Error: ${e.message}")
                }
            }
        }
    }
}