package com.example.baixominholeague

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.graphics.Color
import android.icu.util.Calendar
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.InputType
import android.util.Log
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.fragment.app.Fragment
import com.example.baixominholeague.data.Evento
import com.example.baixominholeague.databinding.ActivityMainBinding
import com.example.baixominholeague.ui.menu.ClasificacionFragment
import com.example.baixominholeague.ui.menu.InicioFragment
import com.example.baixominholeague.ui.menu.JugadoresFragment
import com.example.baixominholeague.ui.menu.PerfilFragment
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val db = FirebaseFirestore.getInstance()
    private var fragmentPerfil = PerfilFragment()
    //private var miFragmento = InicioFragment()

    private var correo: String? = null
    private var correoLogin: String? = null
    private var alias: String? = null
    private var nombre: String? = null
    private var telefono: String? = null
    private var localidad: String? = null
    private var posiciones: String? = null
    private var args: Bundle? = null

    companion object {
        const val CLAVE_CORREO = "correo"
        const val CLAVE_ALIAS = "alias"
        const val CLAVE_NOMBRE = "nombre"
        const val CLAVE_TELEFONO = "telefono"
        const val CLAVE_LOCALIDAD = "localidad"
        const val CLAVE_POSICIONES = "posiciones"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.bottomNavigation.setBackground(null);

        val intent = intent
        correo = intent.getStringExtra("email")
        correoLogin = intent.getStringExtra("correoLogin")

        getDataBd() //Acceder a datos del usuario
        saveData()

        //supportFragmentManager.beginTransaction().replace(R.id.frameContainer,miFragmento).commit()


        //Cargar el fragment de Inicio al iniciar
        if (savedInstanceState == null) {
            replaceFragment(InicioFragment())
        }

        binding.bottomNavigation.setOnNavigationItemSelectedListener { item ->

            when (item.itemId) {
                R.id.home -> replaceFragment(InicioFragment())
                R.id.Buscar -> replaceFragment(JugadoresFragment())
                R.id.clasificacion -> replaceFragment(ClasificacionFragment())
                R.id.perfil -> replaceFragment(fragmentPerfil)

            }
            true
        }

        binding.floatinButton.setOnClickListener {
            showDialogChampionship()
        }

    }


    private fun getDataBd() {
        db.collection("users").document(correo ?: correoLogin.orEmpty()).get()
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    val document = it.result
                    if (document != null) {
                        alias = document.get("alias") as String?
                        nombre = document.get("nombre") as String?
                        telefono = document.get("telefono") as String?
                        localidad = document.get("localidad") as String?
                        posiciones = document.get("posiciones") as String?

                        args = Bundle().apply {

                            putString(CLAVE_CORREO, correo?.toString() ?: correoLogin.toString())
                            putString(CLAVE_ALIAS, alias?.toString())
                            putString(CLAVE_NOMBRE, nombre?.toString())
                            putString(CLAVE_TELEFONO, telefono?.toString())
                            putString(CLAVE_LOCALIDAD, localidad?.toString())
                            putString(CLAVE_POSICIONES, posiciones?.toString())
                        }
                        fragmentPerfil.arguments = args
                    }
                }
            }

    }

    private fun saveData() {
        //Guardado de datos
        val prefs =
            getSharedPreferences(getString(R.string.prefs_file), Context.MODE_PRIVATE).edit()
        prefs.putString("email", correo ?: correoLogin)
        prefs.apply()
    }

    private fun replaceFragment(fragment: Fragment) {

        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.frameContainer, fragment)
        fragmentTransaction.commit()

    }

    private fun showDialogChampionship() {
        val dialogBuilder = AlertDialog.Builder(this)
        dialogBuilder.setTitle("Añadir Campeonato")

        val container = LinearLayout(this)
        container.orientation = LinearLayout.VERTICAL

        val editTextNombre = EditText(this)
        editTextNombre.hint = "Ingrese nombre de campeonato"
        editTextNombre.setPadding(30, 50, 30, 30)
        editTextNombre.setBackgroundColor(Color.WHITE)
        container.addView(editTextNombre)

        val editText = EditText(this)
        editText.inputType = InputType.TYPE_CLASS_NUMBER
        editText.setPadding(30, 50, 30, 30)
        editText.setBackgroundColor(Color.WHITE)
        editText.hint = "Precio"
        container.addView(editText)

        val editTextFecha = EditText(this)
        editTextFecha.hint = "Seleccione una fecha"
        editTextFecha.setPadding(30,50,30,30)
        editTextFecha.setBackgroundColor(Color.WHITE)
        editTextFecha.isFocusable = false
        container.addView(editTextFecha)

        val editTextHora = EditText(this)
        editTextHora.hint = "Seleccione una hora"
        editTextHora.setPadding(30,50,30,30)
        editTextHora.setBackgroundColor(Color.WHITE)
        editTextHora.isFocusable = false
        container.addView(editTextHora)

        // Agregar DatePicker al contenedor
        val calendar = Calendar.getInstance()
        val dateSetListener = DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
            val selectedDate = "$dayOfMonth/${monthOfYear + 1}/$year"
            editTextFecha.setText(selectedDate)
        }

        editTextFecha.setOnClickListener {
            val datePickerDialog = DatePickerDialog(
                this,
                dateSetListener,
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            )
            datePickerDialog.show()
        }

        //Timepicker
        val timeSetListener = TimePickerDialog.OnTimeSetListener { _, hourOfDay, minute ->
            val selectedTime = String.format("%02d:%02d", hourOfDay, minute)
            editTextHora.setText(selectedTime)
        }

        editTextHora.setOnClickListener {
            val currentTime = Calendar.getInstance()
            val hour = currentTime.get(Calendar.HOUR_OF_DAY)
            val minute = currentTime.get(Calendar.MINUTE)

            val timePickerDialog = TimePickerDialog(
                this,
                timeSetListener,
                hour,
                minute,
                true
            )
            timePickerDialog.show()
        }


        dialogBuilder.setView(container)

        dialogBuilder.setPositiveButton("Aceptar") { dialog, which ->

            val nombre = editTextNombre.text.toString()
            val fecha = editTextFecha.text.toString()
            val hora = editTextHora.text.toString()
            val precio = editText.text.toString()

            //miFragmento.mostrarDatos(nombre)
            (correo?:correoLogin)?.let { saveEvent(nombre,fecha,hora,precio, it) }
        }

        dialogBuilder.setNegativeButton("Cancelar") { dialog, which ->

        }

        val dialog = dialogBuilder.create()
        dialog.show()
    }

    private fun saveEvent(nombre: String, fecha: String, hora: String, precio: String, correo: String) {

        if (nombre.isNotEmpty() && fecha.isNotEmpty() && hora.isNotEmpty()) {
            db.collection("eventos").document(nombre).set(
                hashMapOf(
                    "nombre" to nombre,
                    "fecha" to fecha,
                    "hora" to hora,
                    "precio" to precio,
                    "correo" to correo.orEmpty()
                )
            ).addOnSuccessListener {
                Toast.makeText(this, "Guardado correctamente", Toast.LENGTH_SHORT).show()
            }.addOnFailureListener { e ->
                Toast.makeText(this, "Error al guardar: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(this, "El nombre del evento no puede estar vacío", Toast.LENGTH_SHORT).show()
        }
    }


}