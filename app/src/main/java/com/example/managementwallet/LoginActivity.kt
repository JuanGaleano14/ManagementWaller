package com.example.managementwallet

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth


class LoginActivity : AppCompatActivity() {
    private lateinit var editTextUser: EditText
    private lateinit var editTextPass: EditText
    private lateinit var auth: FirebaseAuth

    object objDatosSesionUsuario {
        var nombreUsuario = ""
        var apellidoUsuario = ""
        var correoUsuario = ""
        var cajaUsuario : Double = 0.0
    }

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        editTextUser = findViewById(R.id.editTextUser)
        editTextPass = findViewById(R.id.editTextPass)
        auth = FirebaseAuth.getInstance()
    }


    fun irRegister(view: View) {
        val irRegister = Intent(this, RegisterActivity::class.java)
        //val irRegister = Intent(this, RegistrarMovimientosActivity::class.java)
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