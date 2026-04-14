package com.example.onetwothreefood

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
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

class AdminMesas : AppCompatActivity() {

    data class Mesa(
        val id: Int,
        val numero: Int,
        val capacidad: Int,
        val estado: String
    )

    data class CreateMesaRequest(
        val numero: Int,
        val capacidad: Int,
        val estado: String
    )

    data class UpdateMesaRequest(
        val numero: Int?,
        val capacidad: Int?,
        val estado: String?
    )

    data class GenericResponse(
        val success: Boolean,
        val message: String
    )

    data class MesaResponse(
        val success: Boolean,
        val message: String?,
        val mesa: Mesa?
    )

    data class MesasResponse(
        val success: Boolean,
        val message: String?,
        val mesas: List<Mesa>?
    )

    interface ApiService {

        @POST("mesas")
        suspend fun registrarMesa(
            @Body request: CreateMesaRequest
        ): MesaResponse

        @GET("mesas")
        suspend fun consultarMesas(): MesasResponse

        @GET("mesas/{id}")
        suspend fun consultarMesaPorId(
            @Path("id") id: Int
        ): MesaResponse

        @PUT("mesas/{id}")
        suspend fun actualizarMesa(
            @Path("id") id: Int,
            @Body request: UpdateMesaRequest
        ): MesaResponse

        @DELETE("mesas/{id}")
        suspend fun eliminarMesa(
            @Path("id") id: Int
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
        setContentView(R.layout.activity_admin_mesas)

        val editIdMesa = findViewById<EditText>(R.id.editIdMesa)
        val editNumeroMesa = findViewById<EditText>(R.id.editNumeroMesa)
        val editCapacidadMesa = findViewById<EditText>(R.id.editCapacidadMesa)
        val editEstadoMesa = findViewById<EditText>(R.id.editEstadoMesa)

        val btnRegistrarMesa = findViewById<MaterialButton>(R.id.btnRegistrarMesa)
        val btnConsultarMesas = findViewById<MaterialButton>(R.id.btnConsultarMesas)
        val btnConsultarMesaPorId = findViewById<MaterialButton>(R.id.btnConsultarMesaPorId)
        val btnActualizarMesa = findViewById<MaterialButton>(R.id.btnActualizarMesa)
        val btnEliminarMesa = findViewById<MaterialButton>(R.id.btnEliminarMesa)
        val btnRegresarAdminMesas = findViewById<MaterialButton>(R.id.btnRegresarAdminMesas)

        val textResultadoAdminMesas = findViewById<TextView>(R.id.textResultadoAdminMesas)
        val progressBarAdminMesas = findViewById<ProgressBar>(R.id.progressBarAdminMesas)

        btnRegistrarMesa.setOnClickListener {
            val numeroTexto = editNumeroMesa.text.toString().trim()
            val capacidadTexto = editCapacidadMesa.text.toString().trim()
            val estado = editEstadoMesa.text.toString().trim()

            if (numeroTexto.isEmpty() || capacidadTexto.isEmpty() || estado.isEmpty()) {
                Toast.makeText(this, "Completa número, capacidad y estado", Toast.LENGTH_SHORT).show()
            } else {
                registrarMesa(
                    numeroTexto.toInt(),
                    capacidadTexto.toInt(),
                    estado,
                    textResultadoAdminMesas,
                    progressBarAdminMesas
                )
            }
        }

        btnConsultarMesas.setOnClickListener {
            consultarMesas(textResultadoAdminMesas, progressBarAdminMesas)
        }

        btnConsultarMesaPorId.setOnClickListener {
            val idTexto = editIdMesa.text.toString().trim()

            if (idTexto.isEmpty()) {
                Toast.makeText(this, "Ingresa un ID", Toast.LENGTH_SHORT).show()
            } else {
                consultarMesaPorId(
                    idTexto.toInt(),
                    editNumeroMesa,
                    editCapacidadMesa,
                    editEstadoMesa,
                    textResultadoAdminMesas,
                    progressBarAdminMesas
                )
            }
        }

        btnActualizarMesa.setOnClickListener {
            val idTexto = editIdMesa.text.toString().trim()

            if (idTexto.isEmpty()) {
                Toast.makeText(this, "Ingresa el ID de la mesa a actualizar", Toast.LENGTH_SHORT).show()
            } else {
                val numero = editNumeroMesa.text.toString().trim().ifEmpty { null }?.toInt()
                val capacidad = editCapacidadMesa.text.toString().trim().ifEmpty { null }?.toInt()
                val estado = editEstadoMesa.text.toString().trim().ifEmpty { null }

                actualizarMesa(
                    idTexto.toInt(),
                    numero,
                    capacidad,
                    estado,
                    textResultadoAdminMesas,
                    progressBarAdminMesas
                )
            }
        }

        btnEliminarMesa.setOnClickListener {
            val idTexto = editIdMesa.text.toString().trim()

            if (idTexto.isEmpty()) {
                Toast.makeText(this, "Ingresa el ID de la mesa a eliminar", Toast.LENGTH_SHORT).show()
            } else {
                eliminarMesa(
                    idTexto.toInt(),
                    textResultadoAdminMesas,
                    progressBarAdminMesas
                )
            }
        }

        btnRegresarAdminMesas.setOnClickListener {
            finish()
        }
    }

    private fun registrarMesa(
        numero: Int,
        capacidad: Int,
        estado: String,
        textResultado: TextView,
        progressBar: ProgressBar
    ) {
        progressBar.visibility = View.VISIBLE

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = RetrofitClient.instance.registrarMesa(
                    CreateMesaRequest(numero, capacidad, estado)
                )

                withContext(Dispatchers.Main) {
                    progressBar.visibility = View.GONE

                    if (response.success && response.mesa != null) {
                        textResultado.text =
                            "Mesa registrada correctamente\n\n" +
                                    "ID: ${response.mesa.id}\n" +
                                    "Número: ${response.mesa.numero}\n" +
                                    "Capacidad: ${response.mesa.capacidad}\n" +
                                    "Estado: ${response.mesa.estado}"

                        Toast.makeText(this@AdminMesas, "Mesa registrada correctamente", Toast.LENGTH_SHORT).show()
                    } else {
                        textResultado.text = response.message ?: "No se pudo registrar la mesa"
                        Toast.makeText(this@AdminMesas, response.message ?: "No se pudo registrar la mesa", Toast.LENGTH_SHORT).show()
                    }
                }

            } catch (e: HttpException) {
                withContext(Dispatchers.Main) {
                    progressBar.visibility = View.GONE

                    if (e.code() == 409) {
                        textResultado.text = "Ya existe una mesa con ese número"
                        Toast.makeText(this@AdminMesas, "Ya existe una mesa con ese número", Toast.LENGTH_SHORT).show()
                    } else {
                        textResultado.text = "Error HTTP: ${e.code()}"
                        Toast.makeText(this@AdminMesas, "Error HTTP: ${e.code()}", Toast.LENGTH_SHORT).show()
                    }

                    Log.e("AdminMesasRegistrarError", "Error HTTP: ${e.message}")
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    progressBar.visibility = View.GONE
                    textResultado.text = "Error: ${e.message}"
                    Toast.makeText(this@AdminMesas, "Error de conexión", Toast.LENGTH_SHORT).show()
                    Log.e("AdminMesasRegistrarError", "Error: ${e.message}")
                }
            }
        }
    }

    private fun consultarMesas(
        textResultado: TextView,
        progressBar: ProgressBar
    ) {
        progressBar.visibility = View.VISIBLE

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = RetrofitClient.instance.consultarMesas()

                withContext(Dispatchers.Main) {
                    progressBar.visibility = View.GONE

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

                        textResultado.text = resultado.toString()
                        Toast.makeText(this@AdminMesas, "Consulta realizada correctamente", Toast.LENGTH_SHORT).show()
                    } else {
                        textResultado.text = response.message ?: "No hay mesas registradas"
                        Toast.makeText(this@AdminMesas, response.message ?: "No hay mesas registradas", Toast.LENGTH_SHORT).show()
                    }
                }

            } catch (e: HttpException) {
                withContext(Dispatchers.Main) {
                    progressBar.visibility = View.GONE
                    textResultado.text = "Error HTTP: ${e.code()}"
                    Toast.makeText(this@AdminMesas, "Error HTTP: ${e.code()}", Toast.LENGTH_SHORT).show()
                    Log.e("AdminMesasConsultarTodasError", "Error HTTP: ${e.message}")
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    progressBar.visibility = View.GONE
                    textResultado.text = "Error: ${e.message}"
                    Toast.makeText(this@AdminMesas, "Error de conexión", Toast.LENGTH_SHORT).show()
                    Log.e("AdminMesasConsultarTodasError", "Error: ${e.message}")
                }
            }
        }
    }

    private fun consultarMesaPorId(
        id: Int,
        editNumeroMesa: EditText,
        editCapacidadMesa: EditText,
        editEstadoMesa: EditText,
        textResultado: TextView,
        progressBar: ProgressBar
    ) {
        progressBar.visibility = View.VISIBLE

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = RetrofitClient.instance.consultarMesaPorId(id)

                withContext(Dispatchers.Main) {
                    progressBar.visibility = View.GONE

                    if (response.success && response.mesa != null) {
                        editNumeroMesa.setText(response.mesa.numero.toString())
                        editCapacidadMesa.setText(response.mesa.capacidad.toString())
                        editEstadoMesa.setText(response.mesa.estado)

                        textResultado.text =
                            "ID: ${response.mesa.id}\n" +
                                    "Número: ${response.mesa.numero}\n" +
                                    "Capacidad: ${response.mesa.capacidad}\n" +
                                    "Estado: ${response.mesa.estado}"
                    } else {
                        textResultado.text = response.message ?: "Mesa no encontrada"
                        Toast.makeText(this@AdminMesas, response.message ?: "Mesa no encontrada", Toast.LENGTH_SHORT).show()
                    }
                }

            } catch (e: HttpException) {
                withContext(Dispatchers.Main) {
                    progressBar.visibility = View.GONE

                    if (e.code() == 404) {
                        textResultado.text = "Mesa no encontrada"
                        Toast.makeText(this@AdminMesas, "Mesa no encontrada", Toast.LENGTH_SHORT).show()
                    } else {
                        textResultado.text = "Error HTTP: ${e.code()}"
                        Toast.makeText(this@AdminMesas, "Error HTTP: ${e.code()}", Toast.LENGTH_SHORT).show()
                    }

                    Log.e("AdminMesasConsultarIdError", "Error HTTP: ${e.message}")
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    progressBar.visibility = View.GONE
                    textResultado.text = "Error: ${e.message}"
                    Toast.makeText(this@AdminMesas, "Error de conexión", Toast.LENGTH_SHORT).show()
                    Log.e("AdminMesasConsultarIdError", "Error: ${e.message}")
                }
            }
        }
    }

    private fun actualizarMesa(
        id: Int,
        numero: Int?,
        capacidad: Int?,
        estado: String?,
        textResultado: TextView,
        progressBar: ProgressBar
    ) {
        progressBar.visibility = View.VISIBLE

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = RetrofitClient.instance.actualizarMesa(
                    id,
                    UpdateMesaRequest(numero, capacidad, estado)
                )

                withContext(Dispatchers.Main) {
                    progressBar.visibility = View.GONE

                    if (response.success && response.mesa != null) {
                        textResultado.text =
                            "Mesa actualizada correctamente\n\n" +
                                    "ID: ${response.mesa.id}\n" +
                                    "Número: ${response.mesa.numero}\n" +
                                    "Capacidad: ${response.mesa.capacidad}\n" +
                                    "Estado: ${response.mesa.estado}"

                        Toast.makeText(this@AdminMesas, "Mesa actualizada correctamente", Toast.LENGTH_SHORT).show()
                    } else {
                        textResultado.text = response.message ?: "No se pudo actualizar"
                        Toast.makeText(this@AdminMesas, response.message ?: "No se pudo actualizar", Toast.LENGTH_SHORT).show()
                    }
                }

            } catch (e: HttpException) {
                withContext(Dispatchers.Main) {
                    progressBar.visibility = View.GONE

                    if (e.code() == 404) {
                        textResultado.text = "Mesa no encontrada"
                        Toast.makeText(this@AdminMesas, "Mesa no encontrada", Toast.LENGTH_SHORT).show()
                    } else {
                        textResultado.text = "Error HTTP: ${e.code()}"
                        Toast.makeText(this@AdminMesas, "Error HTTP: ${e.code()}", Toast.LENGTH_SHORT).show()
                    }

                    Log.e("AdminMesasActualizarError", "Error HTTP: ${e.message}")
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    progressBar.visibility = View.GONE
                    textResultado.text = "Error: ${e.message}"
                    Toast.makeText(this@AdminMesas, "Error de conexión", Toast.LENGTH_SHORT).show()
                    Log.e("AdminMesasActualizarError", "Error: ${e.message}")
                }
            }
        }
    }

    private fun eliminarMesa(
        id: Int,
        textResultado: TextView,
        progressBar: ProgressBar
    ) {
        progressBar.visibility = View.VISIBLE

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = RetrofitClient.instance.eliminarMesa(id)

                withContext(Dispatchers.Main) {
                    progressBar.visibility = View.GONE
                    textResultado.text = response.message
                    Toast.makeText(this@AdminMesas, response.message, Toast.LENGTH_SHORT).show()
                }

            } catch (e: HttpException) {
                withContext(Dispatchers.Main) {
                    progressBar.visibility = View.GONE

                    if (e.code() == 404) {
                        textResultado.text = "Mesa no encontrada"
                        Toast.makeText(this@AdminMesas, "Mesa no encontrada", Toast.LENGTH_SHORT).show()
                    } else {
                        textResultado.text = "Error HTTP: ${e.code()}"
                        Toast.makeText(this@AdminMesas, "Error HTTP: ${e.code()}", Toast.LENGTH_SHORT).show()
                    }

                    Log.e("AdminMesasEliminarError", "Error HTTP: ${e.message}")
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    progressBar.visibility = View.GONE
                    textResultado.text = "Error: ${e.message}"
                    Toast.makeText(this@AdminMesas, "Error de conexión", Toast.LENGTH_SHORT).show()
                    Log.e("AdminMesasEliminarError", "Error: ${e.message}")
                }
            }
        }
    }
}