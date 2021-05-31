package com.example.managementwallet

import android.app.DatePickerDialog
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.*


class RegistrarMovimientosActivity : AppCompatActivity() {

    private lateinit var etFechaMov: EditText
    private lateinit var cmbConceptosMovimientos: Spinner
    private lateinit var etValorMovimiento: EditText
    private lateinit var rbIngresos: RadioButton
    private lateinit var rbEgresos: RadioButton

    private val TAG = "RegistrarMovimientosActivity"
    private var db: FirebaseFirestore = FirebaseFirestore.getInstance()

    //Formato de fecha
    private val sdf = SimpleDateFormat("yyyy-MM-dd", Locale("es_ES"))
    var fechaEscogida: Calendar = Calendar.getInstance()
    var listenerFecha =
        DatePickerDialog.OnDateSetListener { _: DatePicker?, year: Int, month: Int, dayOfMonth: Int ->

            fechaEscogida.clear() //Se borran horas, minutos y segundos.
            fechaEscogida.set(Calendar.YEAR, year)
            fechaEscogida.set(Calendar.MONTH, month)
            fechaEscogida.set(Calendar.DAY_OF_MONTH, dayOfMonth)

            etFechaMov.setText(sdf.format(fechaEscogida.time).toString())
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registrar_movimientos)

        //Obtener elementos de la vista
        etFechaMov = findViewById(R.id.etFechaMov)
        cmbConceptosMovimientos = findViewById(R.id.cmbConceptosMovimientos)
        etValorMovimiento = findViewById(R.id.etValorMovimiento)
        rbIngresos = findViewById(R.id.rbIngresos)
        rbEgresos = findViewById(R.id.rbEgresos)

        println("CORREO DE USUARIO LOGUEADO: " + LoginActivity.objDatosSesionUsuario.correoUsuario)
    }

    fun onRadioButtonClicked(view: View) {
        var nuListadoConceptos: Int = R.array.ingresos_array
        if (view is RadioButton) {
            // Validar si el RadioButton está marcado o no
            val checked = view.isChecked

            // Capturar el marcado de cada RadioButton
            when (view.getId()) {
                R.id.rbIngresos ->
                    if (checked) {
                        //Se despliega el listado de conceptos para Ingresos
                        nuListadoConceptos = R.array.ingresos_array
                    }
                R.id.rbEgresos ->
                    if (checked) {
                        //Se despliega el listado de conceptos para Egresos
                        println("egresos melosky")
                        nuListadoConceptos = R.array.egresos_array
                    }
            }

            ArrayAdapter.createFromResource(
                this,
                nuListadoConceptos,
                android.R.layout.simple_spinner_item
            ).also { adapter ->
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                cmbConceptosMovimientos.adapter = adapter
            }
        }
    }

    fun obtenerFecha(view: View) {
        val fechaSeleccionada = etFechaMov.text.toString();
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

    fun guardarGasto(view: View) {

        if (null != cmbConceptosMovimientos.selectedItem
            && cmbConceptosMovimientos.selectedItem.toString().isNotBlank()
            && cmbConceptosMovimientos.selectedItemPosition > 0
            && (rbIngresos.isChecked
                    || rbEgresos.isChecked)
            && etFechaMov.text.isNotBlank()
            && etValorMovimiento.text.isNotBlank()
        ) {
            val movimiento = hashMapOf(
                "descripcion_movimiento" to cmbConceptosMovimientos.selectedItem.toString(),
                "naturaleza" to if (rbIngresos.isChecked) "I" else "E",
                "valor" to etValorMovimiento.text.toString(),
                "fecha" to sdf.format(fechaEscogida.time),
                "usuario_registro" to LoginActivity.objDatosSesionUsuario.correoUsuario
            )

            db.collection("movimientos")
                .add(movimiento) //add
                .addOnSuccessListener { resultado ->
                    Toast.makeText(
                        this, "Movimiento registrado correctamente",
                        Toast.LENGTH_LONG
                    ).show()
                    calcularCajaUsuario()
                    Log.i(TAG, "Movimiento registrado correctamente")
                }
                .addOnFailureListener { exception ->
                    println("Error en registro de movimiento caja")
                }

        } else {
            Toast.makeText(
                this, "Por favor complete todos los campos",
                Toast.LENGTH_LONG
            ).show()
        }
    }


    fun limpiarCampos() {
        etFechaMov.setText("")
        etValorMovimiento.setText("")
    }

    //Método encargado de actualizar el valor de caja actual del cliente en sesión.
    fun calcularCajaUsuario() {

        var usuarioCaja: Double
        var blEstadoProceso = false
        db.collection("usuario_caja")
            .document(LoginActivity.objDatosSesionUsuario.correoUsuario)
            .get()
            .addOnSuccessListener { resultado ->

                usuarioCaja = resultado["caja"].toString().toDouble()
                LoginActivity.objDatosSesionUsuario.nombreUsuario =
                    resultado["nombre_usuario"].toString()
                LoginActivity.objDatosSesionUsuario.apellidoUsuario =
                    resultado["apellido_usuario"].toString()

                if (rbIngresos.isChecked) {
                    usuarioCaja += etValorMovimiento.text.toString().toDouble()
                } else {
                    usuarioCaja -= etValorMovimiento.text.toString().toDouble()
                }

                LoginActivity.objDatosSesionUsuario.cajaUsuario = usuarioCaja

                val objUsuarioActualizado = hashMapOf(
                    "nombre_usuario" to LoginActivity.objDatosSesionUsuario.nombreUsuario,
                    "apellido_usuario" to LoginActivity.objDatosSesionUsuario.apellidoUsuario,
                    "caja" to usuarioCaja
                )

                db.collection("usuario_caja")
                    .document(LoginActivity.objDatosSesionUsuario.correoUsuario)
                    .set(objUsuarioActualizado)
                    .addOnSuccessListener { resultado ->
                        blEstadoProceso = true
                        Log.i(TAG, "Caja actualizada correctamente")
                        if (blEstadoProceso) {
                            limpiarCampos()
                            println("termino")
                        } else {
                            println("nada que termina")
                        }
                    }
                    .addOnFailureListener { exception ->
                        Log.i(TAG, "Error en actualizar caja")
                    }
            }
    }

}