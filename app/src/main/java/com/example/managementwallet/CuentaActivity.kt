package com.example.managementwallet

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.DatePicker
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.*

class CuentaActivity : AppCompatActivity() {
    private lateinit var etNombre: EditText
    private lateinit var etApellido: EditText
    private lateinit var etCorreo: EditText
    private lateinit var etFechaRegistro: EditText
    private var db: FirebaseFirestore = FirebaseFirestore.getInstance()
    private val TAG = "CuentaActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cuenta_usuario)

        etNombre = findViewById(R.id.etNombre)
        etApellido = findViewById(R.id.etApellido)
        etCorreo = findViewById(R.id.etCorreo)
        etFechaRegistro = findViewById(R.id.etFechaRegistro)

        etNombre.isEnabled = false
        etApellido.isEnabled = false
        etCorreo.isEnabled = false
        etFechaRegistro.isEnabled = false
        consultarDatosUsuario()
    }

    fun consultarDatosUsuario() {

        db.collection("usuario_caja")
            .document(LoginActivity.objDatosSesionUsuario.correoUsuario)
            .get()
            .addOnSuccessListener { resultado ->
                etNombre.setText(resultado["nombre_usuario"].toString())
                etApellido.setText(resultado["apellido_usuario"].toString())
                etCorreo.setText(LoginActivity.objDatosSesionUsuario.correoUsuario)
                etFechaRegistro.setText(resultado["fecha_registro"].toString())
            }
            .addOnFailureListener { exception ->
                Log.i(TAG, "Error en consultar usuario")
            }
    }
}