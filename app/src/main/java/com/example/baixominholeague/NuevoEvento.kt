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
import com.example.baixominholeague.databinding.ActivityNuevoEventoBinding
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.squareup.picasso.Picasso

class NuevoEvento : AppCompatActivity() {
    companion object {
        const val EMAIL_PUBLICADOR = "emailPublicador"
    }
    private lateinit var binding: ActivityNuevoEventoBinding
    private val db = FirebaseFirestore.getInstance()

    private val REQUEST_CODE_IMAGE_PICKER = 102
    private var selectedImageUri: String? = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNuevoEventoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupUI()

    }

    private fun setupUI() {

        binding.btnBackAdd.setOnClickListener { onBackPressed() }
        binding.etFecha.setOnClickListener{showDatePicker()}
        binding.etHora.setOnClickListener { showTimePicker() }
        precio()
        binding.btnImagen.setOnClickListener { selectImageEvent() }
        binding.btnDeleteImage.setOnClickListener { deleteRutaImagen() }
        binding.btnSaveTorneo.setOnClickListener { saveNewTorneo() }
    }

    private fun saveNewTorneo(){
        if(validarCampos()){
            //Se guarda
        }else{
            Toast.makeText(this, "Por favor, complete todos los campos requeridos.", Toast.LENGTH_SHORT).show()}
    }
    private fun validarCampos(): Boolean {
        var camposValidos = true

        val nombre = binding.etNombreNewEvent.text.toString()
        val fecha = binding.etFecha.text.toString()
        val hora = binding.etHora.text.toString()
        val precio = binding.etPrecio.text.toString()
        val ubicacion = binding.etUbicacion.text.toString()

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
            binding.tvUbicacionError.visibility = if (ubicacion.isEmpty()) View.VISIBLE else View.GONE
            binding.tvErrorFecha.setText("* Al menos uno de los campos fecha, hora o ubicación está vacío")
            binding.tvErrorFecha.visibility = View.VISIBLE
        } else {
            //Todo ver porque fallan los if individuales
            //binding.tvErrorFecha.visibility = View.GONE
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


    private fun showDatePicker(){

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

        datePickerDialog.datePicker.minDate=Calendar.getInstance().timeInMillis
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

    private fun precio(){
        binding.etPrecio.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}


            @SuppressLint("SuspiciousIndentation")
            override fun afterTextChanged(s: Editable?) {
                val text = s.toString()
                // Verifica si el texto es "0" y muestra "gratis" si es el caso
                if (text == "0" || text =="00") {
                    binding.etPrecio.filters = arrayOf(InputFilter.LengthFilter(6))
                        binding.etPrecio.setText("Gratis")

                } else {

                    binding.etPrecio.filters = arrayOf(InputFilter.LengthFilter(3))

                }
                   binding.etPrecio.setSelection(binding.etPrecio.text.length)
            }
        })
    }

    private fun deleteRutaImagen(){
        binding.tvImagen.setText("")
        binding.btnDeleteImage.visibility=View.GONE
    }

    private fun selectImageEvent(){
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
            // Aquí sé guarda la imagen en Firebase Storage
            //uploadImageToFirebaseStorage(selectedImageUri!!)

            binding.tvImagen.setText(selectedImageUri.toString())
            binding.btnDeleteImage.visibility= View.VISIBLE

        }
    }


}