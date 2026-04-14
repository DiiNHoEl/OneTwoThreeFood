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

class AdminPlatillos : AppCompatActivity() {

    data class Platillo(
        val id: Int,
        val nombre: String,
        val descripcion: String?,
        val precio: Double,
        val categoria: String,
        val disponible: Boolean
    )

    data class CreatePlatilloRequest(
        val nombre: String,
        val descripcion: String?,
        val precio: Double,
        val categoria: String,
        val disponible: Boolean
    )

    data class UpdatePlatilloRequest(
        val nombre: String?,
        val descripcion: String?,
        val precio: Double?,
        val categoria: String?,
        val disponible: Boolean?
    )

    data class PlatilloResponse(
        val success: Boolean,
        val message: String?,
        val platillo: Platillo?
    )

    data class PlatillosResponse(
        val success: Boolean,
        val message: String?,
        val platillos: List<Platillo>?
    )

    data class GenericResponse(
        val success: Boolean,
        val message: String
    )

    interface ApiService {

        @POST("platillos")
        suspend fun registrarPlatillo(
            @Body request: CreatePlatilloRequest
        ): PlatilloResponse

        @GET("platillos")
        suspend fun consultarPlatillos(): PlatillosResponse

        @GET("platillos/{id}")
        suspend fun consultarPlatilloPorId(
            @Path("id") id: Int
        ): PlatilloResponse

        @PUT("platillos/{id}")
        suspend fun actualizarPlatillo(
            @Path("id") id: Int,
            @Body request: UpdatePlatilloRequest
        ): PlatilloResponse

        @DELETE("platillos/{id}")
        suspend fun eliminarPlatillo(
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
        setContentView(R.layout.activity_admin_platillos)

        val editIdPlatillo = findViewById<EditText>(R.id.editIdPlatillo)
        val editNombrePlatillo = findViewById<EditText>(R.id.editNombrePlatillo)
        val editDescripcionPlatillo = findViewById<EditText>(R.id.editDescripcionPlatillo)
        val editPrecioPlatillo = findViewById<EditText>(R.id.editPrecioPlatillo)
        val editCategoriaPlatillo = findViewById<EditText>(R.id.editCategoriaPlatillo)
        val editDisponiblePlatillo = findViewById<EditText>(R.id.editDisponiblePlatillo)

        val btnRegistrarPlatillo = findViewById<MaterialButton>(R.id.btnRegistrarPlatillo)
        val btnConsultarPlatillos = findViewById<MaterialButton>(R.id.btnConsultarPlatillos)
        val btnConsultarPlatilloPorId = findViewById<MaterialButton>(R.id.btnConsultarPlatilloPorId)
        val btnActualizarPlatillo = findViewById<MaterialButton>(R.id.btnActualizarPlatillo)
        val btnEliminarPlatillo = findViewById<MaterialButton>(R.id.btnEliminarPlatillo)
        val btnRegresarAdminPlatillos = findViewById<MaterialButton>(R.id.btnRegresarAdminPlatillos)

        val textResultado = findViewById<TextView>(R.id.textResultadoAdminPlatillos)
        val progressBar = findViewById<ProgressBar>(R.id.progressBarAdminPlatillos)

        btnRegistrarPlatillo.setOnClickListener {
            val nombre = editNombrePlatillo.text.toString().trim()
            val descripcion = editDescripcionPlatillo.text.toString().trim().ifEmpty { null }
            val precioTexto = editPrecioPlatillo.text.toString().trim()
            val categoria = editCategoriaPlatillo.text.toString().trim()
            val disponibleTexto = editDisponiblePlatillo.text.toString().trim()

            if (nombre.isEmpty() || precioTexto.isEmpty() || categoria.isEmpty() || disponibleTexto.isEmpty()) {
                Toast.makeText(this, "Completa nombre, precio, categoría y disponible", Toast.LENGTH_SHORT).show()
            } else {
                val disponible = when (disponibleTexto.lowercase()) {
                    "Si" -> true
                    "si" -> true
                    "Sí" -> true
                    "sí" -> true
                    "No" -> false
                    "no" -> false
                    else -> {
                        Toast.makeText(this, "Escribe Si o No en disponibilidad", Toast.LENGTH_SHORT).show()
                        return@setOnClickListener
                    }
                }

                registrarPlatillo(
                    nombre,
                    descripcion,
                    precioTexto.toDouble(),
                    categoria,
                    disponible,
                    textResultado,
                    progressBar
                )
            }
        }

        btnConsultarPlatillos.setOnClickListener {
            consultarPlatillos(textResultado, progressBar)
        }

        btnConsultarPlatilloPorId.setOnClickListener {
            val idTexto = editIdPlatillo.text.toString().trim()

            if (idTexto.isEmpty()) {
                Toast.makeText(this, "Ingresa un ID", Toast.LENGTH_SHORT).show()
            } else {
                consultarPlatilloPorId(
                    idTexto.toInt(),
                    editNombrePlatillo,
                    editDescripcionPlatillo,
                    editPrecioPlatillo,
                    editCategoriaPlatillo,
                    editDisponiblePlatillo,
                    textResultado,
                    progressBar
                )
            }
        }

        btnActualizarPlatillo.setOnClickListener {
            val idTexto = editIdPlatillo.text.toString().trim()

            if (idTexto.isEmpty()) {
                Toast.makeText(this, "Ingresa el ID del platillo a actualizar", Toast.LENGTH_SHORT).show()
            } else {
                val nombre = editNombrePlatillo.text.toString().trim().ifEmpty { null }
                val descripcion = editDescripcionPlatillo.text.toString().trim().ifEmpty { null }
                val precio = editPrecioPlatillo.text.toString().trim().ifEmpty { null }?.toDouble()
                val categoria = editCategoriaPlatillo.text.toString().trim().ifEmpty { null }
                val disponibleTexto = editDisponiblePlatillo.text.toString().trim().ifEmpty { null }

                val disponible = when (disponibleTexto?.lowercase()) {
                    null -> null
                    "si" -> true
                    "sí" -> true
                    "no" -> false
                    "Si" -> true
                    "Sí" -> true
                    "No" -> false
                    else -> {
                        Toast.makeText(this, "Escribe Si o No en disponibilidad", Toast.LENGTH_SHORT).show()
                        return@setOnClickListener
                    }
                }

                actualizarPlatillo(
                    idTexto.toInt(),
                    nombre,
                    descripcion,
                    precio,
                    categoria,
                    disponible,
                    textResultado,
                    progressBar
                )
            }
        }

        btnEliminarPlatillo.setOnClickListener {
            val idTexto = editIdPlatillo.text.toString().trim()

            if (idTexto.isEmpty()) {
                Toast.makeText(this, "Ingresa el ID del platillo a eliminar", Toast.LENGTH_SHORT).show()
            } else {
                eliminarPlatillo(
                    idTexto.toInt(),
                    textResultado,
                    progressBar
                )
            }
        }

        btnRegresarAdminPlatillos.setOnClickListener {
            finish()
        }
    }

    private fun registrarPlatillo(
        nombre: String,
        descripcion: String?,
        precio: Double,
        categoria: String,
        disponible: Boolean,
        textResultado: TextView,
        progressBar: ProgressBar
    ) {
        progressBar.visibility = View.VISIBLE

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = RetrofitClient.instance.registrarPlatillo(
                    CreatePlatilloRequest(nombre, descripcion, precio, categoria, disponible)
                )

                withContext(Dispatchers.Main) {
                    progressBar.visibility = View.GONE

                    if (response.success && response.platillo != null) {
                        textResultado.text =
                            "Platillo registrado correctamente\n\n" +
                                    "ID: ${response.platillo.id}\n" +
                                    "Nombre: ${response.platillo.nombre}\n" +
                                    "Descripción: ${response.platillo.descripcion}\n" +
                                    "Precio: ${response.platillo.precio}\n" +
                                    "Categoría: ${response.platillo.categoria}\n" +
                                    "Disponible: ${if (response.platillo.disponible) "Si" else "No"}"

                        Toast.makeText(this@AdminPlatillos, "Platillo registrado correctamente", Toast.LENGTH_SHORT).show()
                    } else {
                        textResultado.text = response.message ?: "No se pudo registrar el platillo"
                        Toast.makeText(this@AdminPlatillos, response.message ?: "No se pudo registrar el platillo", Toast.LENGTH_SHORT).show()
                    }
                }

            } catch (e: HttpException) {
                withContext(Dispatchers.Main) {
                    progressBar.visibility = View.GONE
                    textResultado.text = "Error HTTP: ${e.code()}"
                    Toast.makeText(this@AdminPlatillos, "Error HTTP: ${e.code()}", Toast.LENGTH_SHORT).show()
                    Log.e("AdminPlatillosRegistrarError", "Error HTTP: ${e.message}")
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    progressBar.visibility = View.GONE
                    textResultado.text = "Error: ${e.message}"
                    Toast.makeText(this@AdminPlatillos, "Error de conexión", Toast.LENGTH_SHORT).show()
                    Log.e("AdminPlatillosRegistrarError", "Error: ${e.message}")
                }
            }
        }
    }

    private fun consultarPlatillos(
        textResultado: TextView,
        progressBar: ProgressBar
    ) {
        progressBar.visibility = View.VISIBLE

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = RetrofitClient.instance.consultarPlatillos()

                withContext(Dispatchers.Main) {
                    progressBar.visibility = View.GONE

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

                        textResultado.text = resultado.toString()
                        Toast.makeText(this@AdminPlatillos, "Consulta realizada correctamente", Toast.LENGTH_SHORT).show()
                    } else {
                        textResultado.text = response.message ?: "No hay platillos registrados"
                        Toast.makeText(this@AdminPlatillos, response.message ?: "No hay platillos registrados", Toast.LENGTH_SHORT).show()
                    }
                }

            } catch (e: HttpException) {
                withContext(Dispatchers.Main) {
                    progressBar.visibility = View.GONE
                    textResultado.text = "Error HTTP: ${e.code()}"
                    Toast.makeText(this@AdminPlatillos, "Error HTTP: ${e.code()}", Toast.LENGTH_SHORT).show()
                    Log.e("AdminPlatillosConsultarTodosError", "Error HTTP: ${e.message}")
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    progressBar.visibility = View.GONE
                    textResultado.text = "Error: ${e.message}"
                    Toast.makeText(this@AdminPlatillos, "Error de conexión", Toast.LENGTH_SHORT).show()
                    Log.e("AdminPlatillosConsultarTodosError", "Error: ${e.message}")
                }
            }
        }
    }

    private fun consultarPlatilloPorId(
        id: Int,
        editNombre: EditText,
        editDescripcion: EditText,
        editPrecio: EditText,
        editCategoria: EditText,
        editDisponible: EditText,
        textResultado: TextView,
        progressBar: ProgressBar
    ) {
        progressBar.visibility = View.VISIBLE

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = RetrofitClient.instance.consultarPlatilloPorId(id)

                withContext(Dispatchers.Main) {
                    progressBar.visibility = View.GONE

                    if (response.success && response.platillo != null) {
                        editNombre.setText(response.platillo.nombre)
                        editDescripcion.setText(response.platillo.descripcion ?: "")
                        editPrecio.setText(response.platillo.precio.toString())
                        editCategoria.setText(response.platillo.categoria)
                        editDisponible.setText(if (response.platillo.disponible) "Si" else "No")

                        textResultado.text =
                            "ID: ${response.platillo.id}\n" +
                                    "Nombre: ${response.platillo.nombre}\n" +
                                    "Descripción: ${response.platillo.descripcion}\n" +
                                    "Precio: ${response.platillo.precio}\n" +
                                    "Categoría: ${response.platillo.categoria}\n" +
                                    "Disponible: ${if (response.platillo.disponible) "Si" else "No"}"
                    } else {
                        textResultado.text = response.message ?: "Platillo no encontrado"
                        Toast.makeText(this@AdminPlatillos, response.message ?: "Platillo no encontrado", Toast.LENGTH_SHORT).show()
                    }
                }

            } catch (e: HttpException) {
                withContext(Dispatchers.Main) {
                    progressBar.visibility = View.GONE

                    if (e.code() == 404) {
                        textResultado.text = "Platillo no encontrado"
                        Toast.makeText(this@AdminPlatillos, "Platillo no encontrado", Toast.LENGTH_SHORT).show()
                    } else {
                        textResultado.text = "Error HTTP: ${e.code()}"
                        Toast.makeText(this@AdminPlatillos, "Error HTTP: ${e.code()}", Toast.LENGTH_SHORT).show()
                    }

                    Log.e("AdminPlatillosConsultarIdError", "Error HTTP: ${e.message}")
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    progressBar.visibility = View.GONE
                    textResultado.text = "Error: ${e.message}"
                    Toast.makeText(this@AdminPlatillos, "Error de conexión", Toast.LENGTH_SHORT).show()
                    Log.e("AdminPlatillosConsultarIdError", "Error: ${e.message}")
                }
            }
        }
    }

    private fun actualizarPlatillo(
        id: Int,
        nombre: String?,
        descripcion: String?,
        precio: Double?,
        categoria: String?,
        disponible: Boolean?,
        textResultado: TextView,
        progressBar: ProgressBar
    ) {
        progressBar.visibility = View.VISIBLE

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = RetrofitClient.instance.actualizarPlatillo(
                    id,
                    UpdatePlatilloRequest(nombre, descripcion, precio, categoria, disponible)
                )

                withContext(Dispatchers.Main) {
                    progressBar.visibility = View.GONE

                    if (response.success && response.platillo != null) {
                        textResultado.text =
                            "Platillo actualizado correctamente\n\n" +
                                    "ID: ${response.platillo.id}\n" +
                                    "Nombre: ${response.platillo.nombre}\n" +
                                    "Descripción: ${response.platillo.descripcion}\n" +
                                    "Precio: ${response.platillo.precio}\n" +
                                    "Categoría: ${response.platillo.categoria}\n" +
                                    "Disponible: ${if (response.platillo.disponible) "Si" else "No"}"

                        Toast.makeText(this@AdminPlatillos, "Platillo actualizado correctamente", Toast.LENGTH_SHORT).show()
                    } else {
                        textResultado.text = response.message ?: "No se pudo actualizar"
                        Toast.makeText(this@AdminPlatillos, response.message ?: "No se pudo actualizar", Toast.LENGTH_SHORT).show()
                    }
                }

            } catch (e: HttpException) {
                withContext(Dispatchers.Main) {
                    progressBar.visibility = View.GONE

                    if (e.code() == 404) {
                        textResultado.text = "Platillo no encontrado"
                        Toast.makeText(this@AdminPlatillos, "Platillo no encontrado", Toast.LENGTH_SHORT).show()
                    } else {
                        textResultado.text = "Error HTTP: ${e.code()}"
                        Toast.makeText(this@AdminPlatillos, "Error HTTP: ${e.code()}", Toast.LENGTH_SHORT).show()
                    }

                    Log.e("AdminPlatillosActualizarError", "Error HTTP: ${e.message}")
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    progressBar.visibility = View.GONE
                    textResultado.text = "Error: ${e.message}"
                    Toast.makeText(this@AdminPlatillos, "Error de conexión", Toast.LENGTH_SHORT).show()
                    Log.e("AdminPlatillosActualizarError", "Error: ${e.message}")
                }
            }
        }
    }

    private fun eliminarPlatillo(
        id: Int,
        textResultado: TextView,
        progressBar: ProgressBar
    ) {
        progressBar.visibility = View.VISIBLE

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = RetrofitClient.instance.eliminarPlatillo(id)

                withContext(Dispatchers.Main) {
                    progressBar.visibility = View.GONE
                    textResultado.text = response.message
                    Toast.makeText(this@AdminPlatillos, response.message, Toast.LENGTH_SHORT).show()
                }

            } catch (e: HttpException) {
                withContext(Dispatchers.Main) {
                    progressBar.visibility = View.GONE

                    if (e.code() == 404) {
                        textResultado.text = "Platillo no encontrado"
                        Toast.makeText(this@AdminPlatillos, "Platillo no encontrado", Toast.LENGTH_SHORT).show()
                    } else {
                        textResultado.text = "Error HTTP: ${e.code()}"
                        Toast.makeText(this@AdminPlatillos, "Error HTTP: ${e.code()}", Toast.LENGTH_SHORT).show()
                    }

                    Log.e("AdminPlatillosEliminarError", "Error HTTP: ${e.message}")
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    progressBar.visibility = View.GONE
                    textResultado.text = "Error: ${e.message}"
                    Toast.makeText(this@AdminPlatillos, "Error de conexión", Toast.LENGTH_SHORT).show()
                    Log.e("AdminPlatillosEliminarError", "Error: ${e.message}")
                }
            }
        }
    }
}