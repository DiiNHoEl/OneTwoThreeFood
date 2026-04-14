package com.example.onetwothreefood

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
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

class AdminPersonal : AppCompatActivity() {

    data class User(
        val id: Int,
        val nombre: String,
        val usuario: String,
        val rol: String
    )

    data class CreateUserRequest(
        val nombre: String,
        val usuario: String,
        val contrasena: String,
        val rol: String
    )

    data class UpdateUserRequest(
        val nombre: String?,
        val usuario: String?,
        val contrasena: String?,
        val rol: String?
    )

    data class GenericResponse(
        val success: Boolean,
        val message: String
    )

    data class UserResponse(
        val success: Boolean,
        val message: String?,
        val usuario: User?
    )

    data class UsersResponse(
        val success: Boolean,
        val message: String?,
        val usuarios: List<User>?
    )

    interface ApiService {

        @POST("usuarios")
        suspend fun registrarUsuario(
            @Body request: CreateUserRequest
        ): GenericResponse

        @GET("usuarios/{id}")
        suspend fun consultarUsuarioPorId(
            @Path("id") id: Int
        ): UserResponse

        @GET("usuarios/nombre/{nombre}")
        suspend fun consultarUsuarioPorNombre(
            @Path("nombre") nombre: String
        ): UsersResponse

        @GET("usuarios/usuario/{usuario}")
        suspend fun consultarUsuarioPorUsuario(
            @Path("usuario") usuario: String
        ): UserResponse

        @PUT("usuarios/{id}")
        suspend fun actualizarUsuario(
            @Path("id") id: Int,
            @Body request: UpdateUserRequest
        ): UserResponse

        @DELETE("usuarios/{id}")
        suspend fun eliminarUsuario(
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
        setContentView(R.layout.activity_admin_personal)

        val editId = findViewById<EditText>(R.id.editId)
        val editNombre = findViewById<EditText>(R.id.editNombre)
        val editUsuario = findViewById<EditText>(R.id.editUsuarioAdmin)
        val editContrasena = findViewById<EditText>(R.id.editContrasenaAdmin)
        val editRol = findViewById<EditText>(R.id.editRol)

        val btnRegistrarUsuario = findViewById<MaterialButton>(R.id.btnRegistrarUsuario)
        val btnConsultarId = findViewById<MaterialButton>(R.id.btnConsultarId)
        val btnConsultarNombre = findViewById<MaterialButton>(R.id.btnConsultarNombre)
        val btnConsultarUsuario = findViewById<MaterialButton>(R.id.btnConsultarUsuario)
        val btnActualizarUsuario = findViewById<MaterialButton>(R.id.btnActualizarUsuario)
        val btnEliminarUsuario = findViewById<MaterialButton>(R.id.btnEliminarUsuario)
        val btnRegresarAdminPersonal = findViewById<MaterialButton>(R.id.btnRegresarAdminPersonal)

        val textResultadoAdmin = findViewById<TextView>(R.id.textResultadoAdmin)
        val progressBarAdmin = findViewById<ProgressBar>(R.id.progressBarAdmin)

        btnRegistrarUsuario.setOnClickListener {
            val nombre = editNombre.text.toString().trim()
            val usuario = editUsuario.text.toString().trim()
            val contrasena = editContrasena.text.toString().trim()
            val rol = editRol.text.toString().trim()

            if (nombre.isEmpty() || usuario.isEmpty() || contrasena.isEmpty() || rol.isEmpty()) {
                Toast.makeText(this, "Completa nombre, usuario, contraseña y rol", Toast.LENGTH_SHORT).show()
            } else {
                registrarUsuario(nombre, usuario, contrasena, rol, textResultadoAdmin, progressBarAdmin)
            }
        }

        btnConsultarId.setOnClickListener {
            val idTexto = editId.text.toString().trim()

            if (idTexto.isEmpty()) {
                Toast.makeText(this, "Ingresa un ID", Toast.LENGTH_SHORT).show()
            } else {
                consultarPorId(idTexto.toInt(), editNombre, editUsuario, editRol, textResultadoAdmin, progressBarAdmin)
            }
        }

        btnConsultarNombre.setOnClickListener {
            val nombre = editNombre.text.toString().trim()

            if (nombre.isEmpty()) {
                Toast.makeText(this, "Ingresa un nombre", Toast.LENGTH_SHORT).show()
            } else {
                consultarPorNombre(
                    nombre,
                    editId,
                    editNombre,
                    editUsuario,
                    editRol,
                    textResultadoAdmin,
                    progressBarAdmin
                )
            }
        }

        btnConsultarUsuario.setOnClickListener {
            val usuario = editUsuario.text.toString().trim()

            if (usuario.isEmpty()) {
                Toast.makeText(this, "Ingresa un usuario", Toast.LENGTH_SHORT).show()
            } else {
                consultarPorUsuario(editId, editNombre, editUsuario, editRol, usuario, textResultadoAdmin, progressBarAdmin)
            }
        }

        btnActualizarUsuario.setOnClickListener {
            val idTexto = editId.text.toString().trim()

            if (idTexto.isEmpty()) {
                Toast.makeText(this, "Ingresa el ID del usuario a actualizar", Toast.LENGTH_SHORT).show()
            } else {
                val nombre = editNombre.text.toString().trim().ifEmpty { null }
                val usuario = editUsuario.text.toString().trim().ifEmpty { null }
                val contrasena = editContrasena.text.toString().trim().ifEmpty { null }
                val rol = editRol.text.toString().trim().ifEmpty { null }

                actualizarUsuario(
                    idTexto.toInt(),
                    nombre,
                    usuario,
                    contrasena,
                    rol,
                    textResultadoAdmin,
                    progressBarAdmin
                )
            }
        }

        btnEliminarUsuario.setOnClickListener {
            val idTexto = editId.text.toString().trim()

            if (idTexto.isEmpty()) {
                Toast.makeText(this, "Ingresa el ID del usuario a eliminar", Toast.LENGTH_SHORT).show()
            } else {
                eliminarUsuario(idTexto.toInt(), textResultadoAdmin, progressBarAdmin)
            }
        }

        btnRegresarAdminPersonal.setOnClickListener {
            finish()
        }
    }

    private fun registrarUsuario(
        nombre: String,
        usuario: String,
        contrasena: String,
        rol: String,
        textResultadoAdmin: TextView,
        progressBarAdmin: ProgressBar
    ) {
        progressBarAdmin.visibility = View.VISIBLE

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = RetrofitClient.instance.registrarUsuario(
                    CreateUserRequest(nombre, usuario, contrasena, rol)
                )

                withContext(Dispatchers.Main) {
                    progressBarAdmin.visibility = View.GONE
                    textResultadoAdmin.text = response.message
                    Toast.makeText(this@AdminPersonal, response.message, Toast.LENGTH_SHORT).show()
                }

            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    progressBarAdmin.visibility = View.GONE
                    textResultadoAdmin.text = "Error: ${e.message}"
                    Log.e("AdminPersonalRegistroError", "Error: ${e.message}")
                }
            }
        }
    }

    private fun consultarPorId(
        id: Int,
        editNombre: EditText,
        editUsuario: EditText,
        editRol: EditText,
        textResultadoAdmin: TextView,
        progressBarAdmin: ProgressBar
    ) {
        progressBarAdmin.visibility = View.VISIBLE

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = RetrofitClient.instance.consultarUsuarioPorId(id)

                withContext(Dispatchers.Main) {
                    progressBarAdmin.visibility = View.GONE

                    if (response.success && response.usuario != null) {
                        editNombre.setText(response.usuario.nombre)
                        editUsuario.setText(response.usuario.usuario)
                        editRol.setText(response.usuario.rol)

                        textResultadoAdmin.text =
                            "ID: ${response.usuario.id}\n" +
                                    "Nombre: ${response.usuario.nombre}\n" +
                                    "Usuario: ${response.usuario.usuario}\n" +
                                    "Rol: ${response.usuario.rol}"
                    } else {
                        textResultadoAdmin.text = response.message ?: "Usuario no encontrado"
                        Toast.makeText(this@AdminPersonal, response.message ?: "Usuario no encontrado", Toast.LENGTH_SHORT).show()
                    }
                }

            } catch (e: HttpException) {
                withContext(Dispatchers.Main) {
                    progressBarAdmin.visibility = View.GONE

                    if (e.code() == 404) {
                        textResultadoAdmin.text = "Usuario no encontrado"
                        Toast.makeText(this@AdminPersonal, "Usuario no encontrado", Toast.LENGTH_SHORT).show()
                    } else {
                        textResultadoAdmin.text = "Error HTTP: ${e.code()}"
                        Toast.makeText(this@AdminPersonal, "Error HTTP: ${e.code()}", Toast.LENGTH_SHORT).show()
                    }

                    Log.e("AdminPersonalConsultaIdError", "Error HTTP: ${e.message}")
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    progressBarAdmin.visibility = View.GONE
                    textResultadoAdmin.text = "Error: ${e.message}"
                    Toast.makeText(this@AdminPersonal, "Error de conexión", Toast.LENGTH_SHORT).show()
                    Log.e("AdminPersonalConsultaIdError", "Error: ${e.message}")
                }
            }
        }
    }

    private fun consultarPorNombre(
        nombre: String,
        editId: EditText,
        editNombre: EditText,
        editUsuario: EditText,
        editRol: EditText,
        textResultadoAdmin: TextView,
        progressBarAdmin: ProgressBar
    ) {
        progressBarAdmin.visibility = View.VISIBLE

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = RetrofitClient.instance.consultarUsuarioPorNombre(nombre)

                withContext(Dispatchers.Main) {
                    progressBarAdmin.visibility = View.GONE

                    if (response.success && !response.usuarios.isNullOrEmpty()) {
                        val resultado = StringBuilder()

                        for (usuario in response.usuarios) {
                            resultado.append(
                                "ID: ${usuario.id}\n" +
                                        "Nombre: ${usuario.nombre}\n" +
                                        "Usuario: ${usuario.usuario}\n" +
                                        "Rol: ${usuario.rol}\n\n"
                            )
                        }

                        textResultadoAdmin.text = resultado.toString()

                        val primerUsuario = response.usuarios[0]
                        editId.setText(primerUsuario.id.toString())
                        editNombre.setText(primerUsuario.nombre)
                        editUsuario.setText(primerUsuario.usuario)
                        editRol.setText(primerUsuario.rol)

                        Toast.makeText(
                            this@AdminPersonal,
                            "Consulta realizada correctamente",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        textResultadoAdmin.text = response.message ?: "No se encontraron usuarios"
                        Toast.makeText(
                            this@AdminPersonal,
                            response.message ?: "No se encontraron usuarios",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

            } catch (e: HttpException) {
                withContext(Dispatchers.Main) {
                    progressBarAdmin.visibility = View.GONE

                    if (e.code() == 404) {
                        textResultadoAdmin.text = "No se encontraron usuarios con ese nombre"
                        Toast.makeText(
                            this@AdminPersonal,
                            "No se encontraron usuarios con ese nombre",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        textResultadoAdmin.text = "Error HTTP: ${e.code()}"
                        Toast.makeText(
                            this@AdminPersonal,
                            "Error HTTP: ${e.code()}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                    Log.e("AdminPersonalConsultaNombreError", "Error HTTP: ${e.message}")
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    progressBarAdmin.visibility = View.GONE
                    textResultadoAdmin.text = "Error: ${e.message}"
                    Toast.makeText(this@AdminPersonal, "Error de conexión", Toast.LENGTH_SHORT).show()
                    Log.e("AdminPersonalConsultaNombreError", "Error: ${e.message}")
                }
            }
        }
    }

    private fun consultarPorUsuario(
        editId: EditText,
        editNombre: EditText,
        editUsuario: EditText,
        editRol: EditText,
        usuario: String,
        textResultadoAdmin: TextView,
        progressBarAdmin: ProgressBar
    ) {
        progressBarAdmin.visibility = View.VISIBLE

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = RetrofitClient.instance.consultarUsuarioPorUsuario(usuario)

                withContext(Dispatchers.Main) {
                    progressBarAdmin.visibility = View.GONE

                    if (response.success && response.usuario != null) {
                        editId.setText(response.usuario.id.toString())
                        editNombre.setText(response.usuario.nombre)
                        editUsuario.setText(response.usuario.usuario)
                        editRol.setText(response.usuario.rol)

                        textResultadoAdmin.text =
                            "ID: ${response.usuario.id}\n" +
                                    "Nombre: ${response.usuario.nombre}\n" +
                                    "Usuario: ${response.usuario.usuario}\n" +
                                    "Rol: ${response.usuario.rol}"
                    } else {
                        textResultadoAdmin.text = response.message ?: "Usuario no encontrado"
                        Toast.makeText(this@AdminPersonal, response.message ?: "Usuario no encontrado", Toast.LENGTH_SHORT).show()
                    }
                }

            } catch (e: HttpException) {
                withContext(Dispatchers.Main) {
                    progressBarAdmin.visibility = View.GONE

                    if (e.code() == 404) {
                        textResultadoAdmin.text = "Usuario no encontrado"
                        Toast.makeText(this@AdminPersonal, "Usuario no encontrado", Toast.LENGTH_SHORT).show()
                    } else {
                        textResultadoAdmin.text = "Error HTTP: ${e.code()}"
                        Toast.makeText(this@AdminPersonal, "Error HTTP: ${e.code()}", Toast.LENGTH_SHORT).show()
                    }

                    Log.e("AdminPersonalConsultaUsuarioError", "Error HTTP: ${e.message}")
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    progressBarAdmin.visibility = View.GONE
                    textResultadoAdmin.text = "Error: ${e.message}"
                    Toast.makeText(this@AdminPersonal, "Error de conexión", Toast.LENGTH_SHORT).show()
                    Log.e("AdminPersonalConsultaUsuarioError", "Error: ${e.message}")
                }
            }
        }
    }

    private fun actualizarUsuario(
        id: Int,
        nombre: String?,
        usuario: String?,
        contrasena: String?,
        rol: String?,
        textResultadoAdmin: TextView,
        progressBarAdmin: ProgressBar
    ) {
        progressBarAdmin.visibility = View.VISIBLE

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = RetrofitClient.instance.actualizarUsuario(
                    id,
                    UpdateUserRequest(nombre, usuario, contrasena, rol)
                )

                withContext(Dispatchers.Main) {
                    progressBarAdmin.visibility = View.GONE

                    if (response.success && response.usuario != null) {
                        textResultadoAdmin.text =
                            "Usuario actualizado correctamente\n\n" +
                                    "ID: ${response.usuario.id}\n" +
                                    "Nombre: ${response.usuario.nombre}\n" +
                                    "Usuario: ${response.usuario.usuario}\n" +
                                    "Rol: ${response.usuario.rol}"

                        Toast.makeText(this@AdminPersonal, "Usuario actualizado correctamente", Toast.LENGTH_SHORT).show()
                    } else {
                        textResultadoAdmin.text = response.message ?: "No se pudo actualizar"
                        Toast.makeText(this@AdminPersonal, response.message ?: "No se pudo actualizar", Toast.LENGTH_SHORT).show()
                    }
                }

            } catch (e: HttpException) {
                withContext(Dispatchers.Main) {
                    progressBarAdmin.visibility = View.GONE

                    if (e.code() == 404) {
                        textResultadoAdmin.text = "Usuario no encontrado"
                        Toast.makeText(this@AdminPersonal, "Usuario no encontrado", Toast.LENGTH_SHORT).show()
                    } else {
                        textResultadoAdmin.text = "Error HTTP: ${e.code()}"
                        Toast.makeText(this@AdminPersonal, "Error HTTP: ${e.code()}", Toast.LENGTH_SHORT).show()
                    }

                    Log.e("AdminPersonalActualizarError", "Error HTTP: ${e.message}")
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    progressBarAdmin.visibility = View.GONE
                    textResultadoAdmin.text = "Error: ${e.message}"
                    Toast.makeText(this@AdminPersonal, "Error de conexión", Toast.LENGTH_SHORT).show()
                    Log.e("AdminPersonalActualizarError", "Error: ${e.message}")
                }
            }
        }
    }

    private fun eliminarUsuario(
        id: Int,
        textResultadoAdmin: TextView,
        progressBarAdmin: ProgressBar
    ) {
        progressBarAdmin.visibility = View.VISIBLE

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = RetrofitClient.instance.eliminarUsuario(id)

                withContext(Dispatchers.Main) {
                    progressBarAdmin.visibility = View.GONE
                    textResultadoAdmin.text = response.message
                    Toast.makeText(this@AdminPersonal, response.message, Toast.LENGTH_SHORT).show()
                }

            } catch (e: HttpException) {
                withContext(Dispatchers.Main) {
                    progressBarAdmin.visibility = View.GONE

                    if (e.code() == 404) {
                        textResultadoAdmin.text = "Usuario no encontrado"
                        Toast.makeText(this@AdminPersonal, "Usuario no encontrado", Toast.LENGTH_SHORT).show()
                    } else {
                        textResultadoAdmin.text = "Error HTTP: ${e.code()}"
                        Toast.makeText(this@AdminPersonal, "Error HTTP: ${e.code()}", Toast.LENGTH_SHORT).show()
                    }

                    Log.e("AdminPersonalEliminarError", "Error HTTP: ${e.message}")
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    progressBarAdmin.visibility = View.GONE
                    textResultadoAdmin.text = "Error: ${e.message}"
                    Toast.makeText(this@AdminPersonal, "Error de conexión", Toast.LENGTH_SHORT).show()
                    Log.e("AdminPersonalEliminarError", "Error: ${e.message}")
                }
            }
        }
    }
}