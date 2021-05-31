package com.example.managementwallet

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore


class RegisterActivity : AppCompatActivity() {

    private lateinit var editTextName: EditText
    private lateinit var editTextLast: EditText
    private lateinit var editTextU: EditText
    private lateinit var editTextP: EditText

    //private lateinit var progressBar: ProgressBar
    private lateinit var dbReference: DatabaseReference
    private lateinit var database: FirebaseDatabase
    private var db: FirebaseFirestore = FirebaseFirestore.getInstance()
    private lateinit var auth: FirebaseAuth
    private lateinit var checkReg: CheckBox
    private val TAG = "RegisterActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        editTextName = findViewById(R.id.editTextName)
        editTextLast = findViewById(R.id.editTextLast)
        editTextU = findViewById(R.id.editTextU)
        editTextP = findViewById(R.id.editTextP)
        checkReg = findViewById(R.id.checkReg)
        database = FirebaseDatabase.getInstance()
        auth = FirebaseAuth.getInstance()

        dbReference = database.reference.child("users")
    }


    fun irLogin(view: View) {
        val irLogin = Intent(this, LoginActivity::class.java)
        startActivity(irLogin)
        finish();
    }

    fun register(view: View) {
        createNewUser()
        registrarUsuarioCaja(view)
    }

    private fun createNewUser() {
        val name: String = editTextName.text.toString()
        val lastName: String = editTextLast.text.toString()
        val email: String = editTextU.text.toString()
        val pass: String = editTextP.text.toString()
        val check: Boolean = checkReg.isChecked

        if (
            !TextUtils.isEmpty(name)
            && !TextUtils.isEmpty(lastName)
            && !TextUtils.isEmpty(email)
            && !TextUtils.isEmpty(pass)
            && check
        ) {
            auth.createUserWithEmailAndPassword(email, pass)
                .addOnCompleteListener(this) { task ->

                    if (task.isComplete) {
                        val user: FirebaseUser? = auth.currentUser
                        verifyEmail(user)

                        val userBD = user?.uid?.let { dbReference.child(it) }
                        userBD?.child("nombre")?.setValue(name)
                        userBD?.child("apellido")?.setValue(lastName)
                        action()
                    }
                }
        } else {
            Toast.makeText(this, "Por favor llenar todos los campos", Toast.LENGTH_LONG).show()
        }
    }

    fun registrarUsuarioCaja(view: View) {
        if (editTextU.text.isNotBlank()) {
            val usuarioCaja = hashMapOf(
                "nombre_usuario" to editTextName.text.toString(),
                "apellido_usuario" to editTextLast.text.toString(),
                "caja" to 0
            )

            db.collection("usuario_caja")
                .document(editTextU.text.toString())
                .set(usuarioCaja)
                .addOnSuccessListener { resultado ->
                    Log.i(TAG, "Usuario caja registrado correctamente")
                }
                .addOnFailureListener { exception ->
                    Log.e(TAG, "Error en registro usuario caja")
                }
        }
    }

    private fun action() {
        val action = Intent(this, LoginActivity::class.java)
        startActivity(action)
        finish();
    }

    private fun verifyEmail(user: FirebaseUser?) {
        user?.sendEmailVerification()
            ?.addOnCompleteListener(this) { task ->

                if (task.isComplete) {
                    Toast.makeText(this, "Email enviado correctamente", Toast.LENGTH_LONG).show()
                } else {
                    Toast.makeText(this, "Error en envío de email", Toast.LENGTH_LONG).show()
                }
            }

    }

}