package com.example.baixominholeague

import android.annotation.SuppressLint
import android.app.Activity
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import android.graphics.Color
import android.icu.util.Calendar
import android.net.Uri
import android.opengl.Visibility
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.text.Editable
import android.text.InputFilter
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.core.net.toUri
import com.example.baixominholeague.databinding.ActivityNuevoEventoBinding
import com.example.baixominholeague.ui.menu.Inicio.InicioFragment
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.squareup.picasso.Picasso
import java.sql.Timestamp
import java.text.SimpleDateFormat
import com.google.firebase.database.FirebaseDatabase
import java.util.*

class NuevoEvento : AppCompatActivity() {
    companion object {
        const val EMAIL_PUBLICADOR = "emailPublicador"
    }

    private lateinit var binding: ActivityNuevoEventoBinding
    private val db = FirebaseFirestore.getInstance()
    val database = FirebaseDatabase.getInstance()
    private var fragmentInicio = InicioFragment()

    private val REQUEST_CODE_IMAGE_PICKER = 102
    private var selectedImageUri: String? = null
    private var correo: String? = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNuevoEventoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        correo = intent.getStringExtra(EMAIL_PUBLICADOR)

        setupUI()

    }

    private fun setupUI() {

        binding.btnBackAdd.setOnClickListener { onBackPressed() }
        binding.etFecha.setOnClickListener { showDatePicker() }
        binding.etHora.setOnClickListener { showTimePicker() }
        precio()
        binding.btnImagen.setOnClickListener { selectImageEvent() }
        binding.btnDeleteImage.setOnClickListener { deleteRutaImagen() }
        binding.btnSaveTorneo.setOnClickListener { saveNewTorneo() }

    }

    private fun saveNewTorneo() {
        binding.linearLayout.clearFocus()
        if (validarCampos()) {
            saveData(correo)
            addEventBdRealTime(binding.etNombreNewEvent.text.toString().lowercase())
        } else {
            Toast.makeText(
                this,
                "Por favor, complete todos los campos requeridos.",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun validarCampos(): Boolean {
        var camposValidos = true

        val nombre = binding.etNombreNewEvent.text.toString().trim()
        val fecha = binding.etFecha.text.toString()
        val hora = binding.etHora.text.toString()
        val precio = binding.etPrecio.text.toString().trim()
        val ubicacion = binding.etUbicacion.text.toString().trim()

        if (nombre.isEmpty()) {
            camposValidos = false
            binding.tvNombreError.visibility = View.VISIBLE
            binding.tvErrorName.visibility = View.VISIBLE
        } else {
            binding.tvNombreError.visibility = View.GONE
            binding.tvErrorName.visibility = View.GONE
        }

        if (fecha.isEmpty() || hora.isEmpty() || ubicacion.isEmpty()) {
            camposValidos = false
            binding.tvFechaError.visibility = if (fecha.isEmpty()) View.VISIBLE else View.GONE
            binding.tvHoraError.visibility = if (hora.isEmpty()) View.VISIBLE else View.GONE
            binding.tvUbicacionError.visibility =
                if (ubicacion.isEmpty()) View.VISIBLE else View.GONE
            binding.tvErrorFecha.setText("* Al menos uno de los campos fecha, hora o ubicación está vacío")
            binding.tvErrorFecha.visibility = View.VISIBLE

        } else {
            binding.tvFechaError.visibility = View.GONE
            binding.tvHoraError.visibility = View.GONE
            binding.tvUbicacionError.visibility = View.GONE
            binding.tvErrorFecha.visibility = View.GONE
        }

        if (precio.isEmpty()) {
            camposValidos = false
            binding.tvPrecioError.visibility = View.VISIBLE
            binding.tvErrorPrecio.visibility = View.VISIBLE
        } else {
            binding.tvPrecioError.visibility = View.GONE
            binding.tvErrorPrecio.visibility = View.GONE
        }
        return camposValidos
    }


    private fun showDatePicker() {

        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(
            this,
            DatePickerDialog.OnDateSetListener { _, selectedDay, selectedMonth, selectedYear ->

                val selectedDate = "$selectedYear-${selectedMonth + 1}-$selectedDay"
                binding.etFecha.setText(selectedDate)
            },
            year,
            month,
            dayOfMonth
        )

        datePickerDialog.datePicker.minDate = Calendar.getInstance().timeInMillis
        datePickerDialog.show()

    }

    private fun showTimePicker() {
        val calendar = Calendar.getInstance()
        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        val minute = calendar.get(Calendar.MINUTE)

        val timePickerDialog = TimePickerDialog(
            this,
            TimePickerDialog.OnTimeSetListener { _, selectedHour, selectedMinute ->

                val selectedTime = String.format("%02d:%02d", selectedHour, selectedMinute)
                binding.etHora.setText(selectedTime)
            },
            hour,
            minute,
            true
        )

        timePickerDialog.show()
    }

    private fun precio() {
        binding.etPrecio.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}


            @SuppressLint("SuspiciousIndentation")
            override fun afterTextChanged(s: Editable?) {
                val text = s.toString()
                // Verifica si el texto es "0" y muestra "gratis" si es el caso
                if (text == "0" || text == "00") {
                    binding.etPrecio.filters = arrayOf(InputFilter.LengthFilter(6))
                    binding.etPrecio.setText("Gratis")

                } else if (text.equals("Grati")) {
                    binding.etPrecio.setText("")
                    binding.etPrecio.filters = arrayOf(InputFilter.LengthFilter(3))

                }
                binding.etPrecio.setSelection(binding.etPrecio.text.length)
            }
        })
    }

    private fun deleteRutaImagen() {
        binding.tvImagen.setText("")
        binding.btnDeleteImage.visibility = View.GONE
    }

    private fun selectImageEvent() {
        launchImagePicker()
    }

    fun launchImagePicker() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        intent.type = "image/*"
        startActivityForResult(intent, REQUEST_CODE_IMAGE_PICKER)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_IMAGE_PICKER && resultCode == Activity.RESULT_OK && data != null) {
            selectedImageUri = data.data.toString()

            binding.tvImagen.setText(selectedImageUri.toString())
            binding.btnDeleteImage.visibility = View.VISIBLE

        }
    }

    private fun saveData(correo: String?) {
        val nombre = binding.etNombreNewEvent.text.toString().lowercase()
        val precio = binding.etPrecio.text.toString()
        binding.progresBar.visibility = View.VISIBLE

        val eventosRef = db.collection("eventos")

        // Realizar una consulta para verificar si ya existe un documento con el mismo nombre
        eventosRef.whereEqualTo("nombre", nombre).get()
            .addOnSuccessListener { querySnapshot ->
                if (querySnapshot.isEmpty) {
                    // No existe un documento con el mismo nombre, puedes guardarlo
                    val evento = hashMapOf(
                        "correo" to correo,
                        "nombre" to binding.etNombreNewEvent.text.toString().uppercase(),
                        "descripcion" to binding.etDescripcion.text.toString().trim(),
                        "fecha" to getTimestampFromDateAndTime(
                            binding.etFecha.text.toString(),
                            binding.etHora.text.toString()
                        ),
                        "ubicacion" to binding.etUbicacion.text.toString(),
                        "imagen" to "",
                        "precio" to  if(precio != "Gratis") "$precio €" else precio
                    )

                    // Verificar si se seleccionó una imagen
                    if (selectedImageUri != null) {
                        // Subir la imagen a Firebase Storage
                        val storageRef = FirebaseStorage.getInstance().reference
                        val imageRef = storageRef.child("images/$nombre.jpg")

                        val uploadTask = imageRef.putFile(selectedImageUri!!.toUri())

                        uploadTask.addOnSuccessListener {
                            // La imagen se cargó correctamente, ahora obtenemos su URL
                            imageRef.downloadUrl.addOnSuccessListener { uri ->
                                // Agregar la URL de la imagen al objeto de evento
                                evento["imagen"] = uri.toString()

                                // Guardar los datos en Firestore
                                val eventosRef = db.collection("eventos")
                                eventosRef.document(nombre)
                                    .set(evento)
                                    .addOnSuccessListener {
                                        showToast("Guardado correctamente")

                                    }
                                    .addOnFailureListener { e ->
                                        showToast("Error al guardar los datos")
                                    }
                                    .addOnCompleteListener {
                                        binding.progresBar.visibility = View.GONE
                                        setResult(Activity.RESULT_OK)
                                        finish()
                                    }
                            }
                        }
                            .addOnFailureListener { e ->
                                showToast("Error al cargar la imagen")
                                binding.progresBar.visibility = View.GONE
                            }

                    } else {
                        // No se seleccionó ninguna imagen, guardar los datos sin imagen
                        eventosRef.document(nombre)
                            .set(evento)
                            .addOnSuccessListener {
                                showToast("Guardado correctamente")

                            }
                            .addOnFailureListener { e ->
                                showToast("Error al guardar los datos: ${e.message}")
                            }
                            .addOnCompleteListener {
                                binding.progresBar.visibility = View.GONE
                                setResult(Activity.RESULT_OK)
                                finish()
                            }
                    }
                } else {
                    // Ya existe un documento con el mismo nombre, muestra un mensaje de aviso
                    showToast("Ya existe un evento con este nombre")
                    binding.progresBar.visibility = View.GONE
                }
            }
            .addOnFailureListener { e ->
                showToast("Error al verificar el nombre del evento")
                binding.progresBar.visibility = View.GONE
            }

    }


    // Función para convertir una fecha y hora en un objeto Timestamp
    private fun getTimestampFromDateAndTime(date: String, time: String): Timestamp {
        val dateTimeString = "$date $time"
        val format = SimpleDateFormat("dd-MM-yyyy HH:mm", Locale.getDefault())
        val parsedDate = format.parse(dateTimeString)
        return Timestamp(parsedDate?.time ?: 0)
    }

    private fun addEventBdRealTime(nombreEvento: String){
        val nuevoEvento = HashMap<String, Any>()
        nuevoEvento["participantes"] = 0

        val eventoRef = database.getReference("eventos/$nombreEvento")

        eventoRef.setValue(nuevoEvento)
            .addOnSuccessListener {
                Log.w("Gabri", "El evento se agregó correcatmente a la bd Realtime")
            }
            .addOnFailureListener { e ->
                Log.w("Gabri", "Error al agregar el evento a la bd Realtime")
            }
    }


    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

}