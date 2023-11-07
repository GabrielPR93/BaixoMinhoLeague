package com.example.baixominholeague.ui.menu.Inicio

import android.content.Context
import android.content.Intent
import android.icu.text.SimpleDateFormat
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.baixominholeague.R
import com.example.baixominholeague.databinding.ActivityDetailEventBinding
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.firestore.FirebaseFirestore
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import java.util.*


class DetailEvent : AppCompatActivity() {


    companion object {
        const val NAME_EVENT = "name_event"
    }

    private lateinit var binding: ActivityDetailEventBinding
    private var db = FirebaseFirestore.getInstance()
    val database = FirebaseDatabase.getInstance()
    private val currentUser = FirebaseAuth.getInstance().currentUser?.email
    private var buttonPressed: Boolean = false
    private lateinit var alias: String
    private lateinit var nombre: String
    private lateinit var imagenUsuario: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailEventBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val nameEvent = intent.getStringExtra(NAME_EVENT)
        binding.tvTituloEvent.setText(nameEvent)

        if (currentUser != null) {
            getAliasAndNAme(currentUser)
        }

        if (nameEvent != null) {
            buttonPressed = loadButtonStateLocal(nameEvent)
            updateButtonUI()
            loadButtonStateFromDataBase(nameEvent)
            loadParticipantes(nameEvent.lowercase())
            getDetailEvent(nameEvent)
        }

        binding.btnParticipar.setOnClickListener {

            val eventoRef = database.getReference("eventos/${nameEvent?.lowercase()}")

            if (buttonPressed) {
                binding.btnParticipar.setBackgroundColor(ContextCompat.getColor(this, R.color.blue))
                binding.btnParticipar.setText("No Asistiré")
                eventoRef.child("participantes").setValue(ServerValue.increment(-1))
                saveButtonStateLocal(false, nameEvent!!)
                saveButtonStateFromDataBase(false, nameEvent!!)
                deleteParticipacion(nameEvent!!)
                buttonPressed = false
            } else {
                binding.btnParticipar.setBackgroundColor(
                    ContextCompat.getColor(
                        this,
                        R.color.teal_200
                    )
                )
                binding.btnParticipar.setText("Asistiré")
                eventoRef.child("participantes").setValue(ServerValue.increment(1))
                saveButtonStateLocal(true, nameEvent!!)
                saveButtonStateFromDataBase(true, nameEvent!!)
                saveParticipacion(alias, nombre, nameEvent!!, imagenUsuario)
                buttonPressed = true
            }
        }

        binding.imageButtonBack.setOnClickListener { onBackPressed() }
        binding.textButton.setOnClickListener {
            if (nameEvent != null) {
                navigateToParticipantes(nameEvent)
            }
        }
    }

    private fun navigateToParticipantes(nombre: String) {
        val intent = Intent(this, ParticipantesActivity::class.java)
        intent.putExtra(DetailEvent.NAME_EVENT, nombre)
        startActivity(intent)
    }


    private fun getDetailEvent(nameEvent: String) {
        val collectionRef = db.collection("eventos")

        collectionRef.get()
            .addOnSuccessListener { result ->
                for (document in result) {

                    if (document.id == nameEvent.lowercase()) {
                        val correoJugador = document.getString("correo")
                        val nombreUsuario = document.getString("nombreUsuario")
                        val fecha = document.getTimestamp("fecha")
                        val precio = document.getString("precio")
                        val ubicacion = document.getString("ubicacion")
                        val descripcion = document.getString("descripcion")
                        val imagen = document.getString("imagen")

                        if (correoJugador != null && fecha != null && precio != null && ubicacion != null && nombreUsuario != null) {
                            setupUI(
                                correoJugador,
                                fecha,
                                precio,
                                ubicacion,
                                descripcion,
                                imagen,
                                nombreUsuario
                            )
                        }
                    }
                }
            }
            .addOnFailureListener { exception ->
                Log.i("GAB", "Error al acceder")
            }
    }

    private fun setupUI(
        correoJugador: String,
        fecha: Timestamp,
        precio: String,
        ubicacion: String,
        descripcion: String?,
        imagen: String?,
        nombreUsuario: String
    ) {
        var nuevaFecha = fecha.toDate()
        var progressBar = binding.progressBarImageEvent
        var imageViewEvent = binding.imageViewEvent
        progressBar.visibility = View.VISIBLE

        binding.tvUsuarioPublicado.text =
            if (nombreUsuario.isNullOrEmpty()) correoJugador else nombreUsuario
        binding.tvFechaEvent.setText(
            SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(
                nuevaFecha
            )
        )
        binding.tvHoraEvent.setText(
            SimpleDateFormat(
                "HH:mm",
                Locale.getDefault()
            ).format(nuevaFecha) + " H"
        )
        binding.tvPrecio.setText(precio)
        binding.tvUbicacion.setText(ubicacion)
        binding.tvDescripcion.setText(descripcion)

        if (imagen != null && imagen.toString() != "") {
            Picasso.get().load(Uri.parse(imagen)).fit().centerCrop().into(imageViewEvent, object : Callback {
                override fun onSuccess() {
                    progressBar.visibility = View.GONE
                }

                override fun onError(e: Exception?) {
                    progressBar.visibility = View.GONE
                    Log.i("Gabri", "Error al cargar la imagen: $e")
                }
            })
        } else {
            Picasso.get().load(R.drawable.imagennodisponible).into(imageViewEvent)
            progressBar.visibility = View.GONE
        }
    }

    private fun updateButtonUI() {
        if (buttonPressed) {
            binding.btnParticipar.setBackgroundColor(ContextCompat.getColor(this, R.color.teal_200))
            binding.btnParticipar.setText("Asistiré")
        } else {
            binding.btnParticipar.setBackgroundColor(ContextCompat.getColor(this, R.color.blue))
            binding.btnParticipar.setText("No Asistiré")
        }
    }

    private fun getAliasAndNAme(correo: String) {
        db.collection("users").document(correo).get()
            .addOnSuccessListener { documentSnapshot ->
                if (documentSnapshot.exists()) {
                    alias = documentSnapshot.getString("alias") ?: ""
                    nombre = documentSnapshot.getString("nombre") ?: ""
                    imagenUsuario = documentSnapshot.getString("foto") ?: ""

                } else {
                    Log.i("Gabri", "Error: El documento no existe")
                }
            }
            .addOnFailureListener { exception -> Log.i("Gabri", "Error: $exception") }
    }

    private fun saveButtonStateFromDataBase(buttonPressed: Boolean, eventName: String) {
        val usuarioRef = db.collection("users").document(currentUser!!)
        val participacionesRef = usuarioRef.collection("participaciones")
        val nuevoDocumento = participacionesRef.document(eventName)

        nuevoDocumento.get().addOnSuccessListener { documentSnapshot ->
            if (documentSnapshot.exists()) {
                // El documento ya existe, actualízalo
                nuevoDocumento.update("estadoBoton", buttonPressed)
            } else {
                // El documento no existe, agrégalo
                val nuevoElemento = hashMapOf(
                    "estadoBoton" to buttonPressed
                )
                nuevoDocumento.set(nuevoElemento)
            }
        }
    }

    private fun saveButtonStateLocal(buttonPressed: Boolean, eventName: String) {
        val prefs =
            getSharedPreferences(getString(R.string.prefs_file), Context.MODE_PRIVATE).edit()
        prefs.putBoolean("button_pressed_$eventName", buttonPressed)
        prefs.apply()

    }

    private fun loadButtonStateLocal(eventName: String): Boolean {
        // Cargar el estado del botón desde las preferencias compartidas
        val prefs = getSharedPreferences(getString(R.string.prefs_file), Context.MODE_PRIVATE)
        return prefs.getBoolean("button_pressed_$eventName", false)
    }

    private fun loadButtonStateFromDataBase(eventName: String) {
        val usuarioRef = db.collection("users").document(currentUser!!)
        val participacionRef = usuarioRef.collection("participaciones").document(eventName)

        participacionRef.get().addOnSuccessListener { documentSnapshot ->
            if (documentSnapshot.exists()) {
                val estadoBoton = documentSnapshot.getBoolean("estadoBoton")
                if (estadoBoton != null) {
                    if (estadoBoton) {
                        binding.btnParticipar.setBackgroundColor(
                            ContextCompat.getColor(
                                this,
                                R.color.teal_200
                            )
                        )
                        binding.btnParticipar.setText("Asistiré")
                        buttonPressed = true
                    } else {
                        buttonPressed = false
                    }
                }
            } else {
                Log.i("Gabri", "El documento no existe: ${participacionRef.toString()}")
            }
        }.addOnFailureListener { exception ->
            Log.i("Gabri", "Error: $exception")
        }
    }

    private fun loadParticipantes(nombreEvento: String) {
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

    private fun saveParticipacion(
        alias: String,
        nombre: String,
        eventName: String,
        imagenUsuario: String
    ) {
        val usuarioRef = db.collection("eventos").document(eventName.lowercase())
        val participantesRef = usuarioRef.collection("participantes")
        val nuevoDocumento = participantesRef.document(currentUser!!)

        val nuevoElemento = hashMapOf(
            "alias" to alias,
            "nombre" to nombre,
            "imagen" to imagenUsuario
        )

        nuevoDocumento.set(nuevoElemento)
    }

    private fun deleteParticipacion(eventName: String) {
        val eventoRef = db.collection("eventos")
        val participantesRef = eventoRef.document(eventName.lowercase()).collection("participantes")
        val documentoAEliminar = participantesRef.document(currentUser!!)
        documentoAEliminar.delete()
    }

}