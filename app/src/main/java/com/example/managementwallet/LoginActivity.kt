package com.example.managementwallet

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore


class LoginActivity : AppCompatActivity() {
    private lateinit var editTextUser: EditText
    private lateinit var editTextPass: EditText
    private lateinit var auth: FirebaseAuth
    private var db: FirebaseFirestore = FirebaseFirestore.getInstance()
    private val TAG = "CuentaActivity"

    object objDatosSesionUsuario {
        var nombreUsuario = ""
        var apellidoUsuario = ""
        var correoUsuario = ""
        var cajaUsuario: Double = 0.0
        var fechaRegistro = ""
    }

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        editTextUser = findViewById(R.id.editTextUser)
        editTextPass = findViewById(R.id.editTextPass)
        auth = FirebaseAuth.getInstance()
    }

    fun consultarDatosUsuario() {

        db.collection("usuario_caja")
            .document(objDatosSesionUsuario.correoUsuario)
            .get()
            .addOnSuccessListener { resultado ->
                objDatosSesionUsuario.nombreUsuario =
                    resultado["nombre_usuario"].toString()
                objDatosSesionUsuario.apellidoUsuario =
                    resultado["apellido_usuario"].toString()
                objDatosSesionUsuario.cajaUsuario =
                    resultado["caja"].toString().toDouble()
                objDatosSesionUsuario.fechaRegistro =
                    resultado["fecha_registro"].toString()
            }
            .addOnFailureListener { exception ->
                Log.i(TAG, "Error en consultar usuario")
            }
    }


    fun irRegister(view: View) {
        val irRegister = Intent(this, RegisterActivity::class.java)
        startActivity(irRegister)
        finish()
    }

    fun loginUser(view: View) {
        val userE: String = editTextUser.text.toString()
        val password: String = editTextPass.text.toString()

        if (!TextUtils.isEmpty(userE) && !TextUtils.isEmpty(password)) {

            auth.signInWithEmailAndPassword(userE, password)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        objDatosSesionUsuario.correoUsuario = editTextUser.text.toString()
                        action()
                    } else {
                        Toast.makeText(
                            this, "Error de autenticacion",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
        }
    }

    private fun action() {
        consultarDatosUsuario()
        val action = Intent(this, HomeActivity::class.java)
        startActivity(action)
        finish()
    }

    fun irForgot(view: View) {
        val irForgot = Intent(this, ForgotPassActivity::class.java)
        startActivity(irForgot)
        finish()
    }


}