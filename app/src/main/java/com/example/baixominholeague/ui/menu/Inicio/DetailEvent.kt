package com.example.baixominholeague.ui.menu.Inicio

import android.content.Context
import android.icu.text.SimpleDateFormat
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.example.baixominholeague.R
import com.example.baixominholeague.databinding.ActivityDetailEventBinding
import com.google.firebase.Timestamp
import com.google.firebase.database.*
import com.google.firebase.firestore.FirebaseFirestore
import com.squareup.picasso.Picasso
import java.util.*


class DetailEvent : AppCompatActivity() {


    companion object {
        const val NAME_EVENT = "name_event"
    }

    private lateinit var binding: ActivityDetailEventBinding
    private  var db = FirebaseFirestore.getInstance()
    val database = FirebaseDatabase.getInstance()
    private var buttonPressed: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailEventBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val nameEvent = intent.getStringExtra(NAME_EVENT)
        binding.tvTituloEvent.setText(nameEvent)

        if (nameEvent != null) {
            loadParticipantes(nameEvent.lowercase())
            getDetailEvent(nameEvent)
        }
        buttonPressed=loadButtonState(nameEvent!!)
        if(buttonPressed){
            binding.btnParticipar.setBackgroundColor(ContextCompat.getColor(this,R.color.teal_200))
            binding.btnParticipar.setText("Asistiré")
        }



        binding.btnParticipar.setOnClickListener {
            val eventoRef = database.getReference("eventos/${nameEvent.lowercase()}")

            if(buttonPressed){
                binding.btnParticipar.setBackgroundColor(ContextCompat.getColor(this, R.color.blue))
                binding.btnParticipar.setText("No Asistiré")
                eventoRef.child("participantes").setValue(ServerValue.increment(-1))
                saveButtonState(false,nameEvent!!)
                buttonPressed=false
            }else{
                binding.btnParticipar.setBackgroundColor(ContextCompat.getColor(this, R.color.teal_200))
                binding.btnParticipar.setText("Asistiré")
                eventoRef.child("participantes").setValue(ServerValue.increment(1))
                saveButtonState(true,nameEvent!!)
                buttonPressed=true
            }
        }

        binding.imageButtonBack.setOnClickListener { onBackPressed() }
    }
    private fun getDetailEvent(nameEvent: String){
        val collectionRef = db.collection("eventos")

        collectionRef.get()
            .addOnSuccessListener { result ->
                for (document in result) {

                    if(document.id==nameEvent.lowercase()){
                        val correoJugador = document.getString("correo")
                        val nombreUsuario = document.getString("nombreUsuario")
                        val fecha = document.getTimestamp("fecha")
                        val precio = document.getString("precio")
                        val ubicacion = document.getString("ubicacion")
                        val descripcion = document.getString("descripcion")
                        val imagen = document.getString("imagen")

                        if (correoJugador !=null && fecha !=null && precio != null && ubicacion != null && nombreUsuario != null) {
                            setupUI(correoJugador,fecha,precio,ubicacion,descripcion,imagen,nombreUsuario)
                        }
                    }

                }
            }
            .addOnFailureListener { exception ->
                Log.i("GAB","Error al acceder")
            }
    }

    private fun setupUI(correoJugador:String, fecha: Timestamp, precio: String, ubicacion: String,descripcion: String?,imagen: String?,nombreUsuario: String){
        var nuevaFecha = fecha.toDate()
        binding.tvUsuarioPublicado.text= if(nombreUsuario.isNullOrEmpty()) correoJugador else nombreUsuario
        binding.tvFechaEvent.setText(SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()) .format(nuevaFecha))
        binding.tvHoraEvent.setText(SimpleDateFormat("HH:mm", Locale.getDefault()) .format(nuevaFecha)+" H")
        binding.tvPrecio.setText(precio)
        binding.tvUbicacion.setText(ubicacion)
        binding.tvDescripcion.setText(descripcion)
        Picasso.get().load(Uri.parse(imagen)).into(binding.imageViewEvent)

    }

    private fun saveButtonState(buttonPressed: Boolean, eventName: String) {
        val prefs = getSharedPreferences(getString(R.string.prefs_file), Context.MODE_PRIVATE).edit()
        prefs.putBoolean("button_pressed_$eventName", buttonPressed)
        prefs.apply()
    }

    private fun loadButtonState(eventName: String): Boolean {
        // Cargar el estado del botón desde las preferencias compartidas
        val prefs = getSharedPreferences(getString(R.string.prefs_file), Context.MODE_PRIVATE)
        return prefs.getBoolean("button_pressed_$eventName", false)
    }

    private fun loadParticipantes(nombreEvento: String){
        val eventoRef = database.getReference("eventos/$nombreEvento")

        eventoRef.child("participantes").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val participantes = snapshot.getValue(Long::class.java)
                    if (participantes != null) {
                        binding.textButton.text = "$participantes Participantes"
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("Firebase", "Error al leer los datos: ${error.message}")
            }
        })

    }
}