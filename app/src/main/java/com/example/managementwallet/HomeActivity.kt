package com.example.managementwallet

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.DatePicker
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.*

class HomeActivity : AppCompatActivity() {
    private lateinit var tvDatos: TextView
    private lateinit var etDescripcionGasto: EditText
    private lateinit var etValorGasto: EditText
    private lateinit var etFecha: EditText
    private var db: FirebaseFirestore = FirebaseFirestore.getInstance()
    private var datos = ""

    var fechaEscogida: Calendar = Calendar.getInstance()
    private val sdf = SimpleDateFormat("yyyy-MM-dd", Locale("es_ES"))
    var listenerFecha =
        DatePickerDialog.OnDateSetListener { _: DatePicker?, year: Int, month: Int, dayOfMonth: Int ->

            fechaEscogida.clear() //Se borran horas, minutos y segundos.
            fechaEscogida.set(Calendar.YEAR, year)
            fechaEscogida.set(Calendar.MONTH, month)
            fechaEscogida.set(Calendar.DAY_OF_MONTH, dayOfMonth)

            etFecha.setText(sdf.format(fechaEscogida.time).toString())
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        tvDatos = findViewById(R.id.tvDatos)
        etDescripcionGasto = findViewById(R.id.etDescripcionGasto)
        etValorGasto = findViewById(R.id.etValorGasto)
        etFecha = findViewById(R.id.etFecha)
    }

    fun consultarDatos(view: View) {
        datos = ""

        db.collection("gastos")
            //document
            .get()
            .addOnSuccessListener { resultado ->
                for (documento in resultado) {
                    //datos += "${documento.id}: ${documento.data}\n"
                    //documento["descripcion"].toString() or data.values
                    val descripcionGasto = documento["descripcion"].toString()
                    val valorGasto = documento["valor"].toString()
                    var fecha = "Fecha no indicada"

                    try {
                        if (documento["fecha"] != null) {
                            //fecha = (documento["fecha"] as Timestamp).toDate().toString()
                            fecha = documento["fecha"].toString()
                        }
                    } catch (e: Exception) {
                    }

                    datos += "${documento.id}: $descripcionGasto, $valorGasto : $fecha\n"
                }
                tvDatos.text = datos
            }
            .addOnFailureListener { exception ->
                tvDatos.text = "No se ha podido conectar"
            }
    }

    fun guardarGasto(view: View) {
        if (etDescripcionGasto.text.isNotBlank()) {
            val gasto = hashMapOf(
                //"id" to 4,
                "descripcion" to etDescripcionGasto.text.toString(),
                "valor" to etValorGasto.text.toString(),
                "fecha" to sdf.format(fechaEscogida.time)
            )

            db.collection("gastos")
                .document("4")
                .set(gasto) //add
                .addOnSuccessListener { resultado ->
                    tvDatos.text = "Se añadió el gasto Correctamente"
                }
                .addOnFailureListener { exception ->
                    tvDatos.text = "No se ha podido conectar"
                }
        }
    }

    //Recibe el documento del movimiento.
    fun borrarGasto(view: View) {
        db.collection("gastos")
            .document("4")
            .delete()
            .addOnSuccessListener { resultado ->
                tvDatos.text = "El gasto se borró correctamente"
            }
            .addOnFailureListener { exception ->
                tvDatos.text = "No se ha podido conectar"
            }
    }

    fun obtenerFecha(view: View) {

        val fechaSeleccionada = etFecha.text.toString();

        val cal: Calendar = Calendar.getInstance()

        //En caso de haber seleccionado una fecha, se define el calendario con ésta misma.
        if (fechaSeleccionada.isNotBlank()) {
            cal.time = sdf.parse(fechaSeleccionada)!!
        }

        DatePickerDialog(
            this,
            listenerFecha,
            cal.get(Calendar.YEAR),
            cal.get(Calendar.MONTH),
            cal.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    fun irRegisterMov(view: View) {
        val irRegister = Intent(this, InformeActivity::class.java)
        startActivity(irRegister)
        //finish()
    }
}