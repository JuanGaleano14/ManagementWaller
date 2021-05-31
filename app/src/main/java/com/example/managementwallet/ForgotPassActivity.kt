package com.example.managementwallet

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth

class ForgotPassActivity : AppCompatActivity() {
    private lateinit var editTextUser: EditText
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forgot_pass)

        editTextUser = findViewById(R.id.editTextUser)
        auth = FirebaseAuth.getInstance()
    }

    fun send(view: View) {
        val email = editTextUser.text.toString()

        if (!TextUtils.isEmpty(email)) {
            auth.sendPasswordResetEmail(email)
                .addOnCompleteListener { task ->

                    if (task.isSuccessful) {
                        ret()
                    } else {
                        Toast.makeText(this, "Error al enviar el email", Toast.LENGTH_LONG).show()
                    }
                }
        }
    }

    private fun ret() {
        val ret = Intent(this, LoginActivity::class.java)
        startActivity(ret)
        finish()
    }

    fun irLogin(view: View) {
        val irLogin = Intent(this, LoginActivity::class.java)
        startActivity(irLogin)
        finish()
    }
}