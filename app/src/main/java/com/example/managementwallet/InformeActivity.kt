package com.example.managementwallet

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class InformeActivity : AppCompatActivity() {
    private lateinit var tvDatos: TextView
    private lateinit var etSaldoActual: TextView
    private lateinit var mListView: ListView
    private var db: FirebaseFirestore = FirebaseFirestore.getInstance()
    private val TAG = "InformeActivity"
    private var datos = ""
    var fechaActual: Calendar = Calendar.getInstance()
    private val sdf = SimpleDateFormat("yyyy-MM-dd", Locale("es_ES"))

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_informe)
        tvDatos = findViewById(R.id.tvDatos)
        etSaldoActual = findViewById(R.id.etSaldoActual)
        mListView = findViewById<ListView>(R.id.lvMovimientoDia)
        val sbMsjSaldo =
            "Saldo actual: $" + LoginActivity.objDatosSesionUsuario.cajaUsuario.toString()
        etSaldoActual.text = sbMsjSaldo
        consultarMovimientosDiaUsuario()
    }

    fun consultarMovimientosDiaUsuario() {
        datos = ""
        var arrayAdapter: ArrayAdapter<*>
        val users = ArrayList<String>()

        db.collection("movimientos")
            .whereEqualTo("usuario_registro", LoginActivity.objDatosSesionUsuario.correoUsuario)
            .whereEqualTo("fecha", sdf.format(fechaActual.time).toString())
            .get()
            .addOnSuccessListener { resultado ->
                for (documento in resultado) {
                    users.add(
                        "Concepto: " + documento["descripcion_movimiento"].toString() + "\n" +
                                "Valor: $" + documento["valor"].toString() + "\n" +
                                "Fecha Movimiento: " + documento["fecha"].toString()
                    )
                }


                /*for (documento in resultado) {
                    val descripcionGasto = documento["descripcion_movimiento"].toString()
                    val valorGasto = documento["valor"].toString()
                    var fecha = "Fecha no indicada"
                    datos += "${documento.id}: $descripcionGasto, $valorGasto : $fecha\n"


                    users = arrayOf(
                        "Virat Kohli", "Rohit Sharma", "Steve Smith",
                        "Kane Williamson", "Ross Taylor"
                    )
                }*/

                println(users)

                arrayAdapter = ArrayAdapter(
                    this,
                    android.R.layout.simple_list_item_1, users
                )
                mListView.adapter = arrayAdapter
                tvDatos.text = datos
            }
            .addOnFailureListener { exception ->
                Log.i(TAG, "Error en consultar usuario")
            }
    }
}