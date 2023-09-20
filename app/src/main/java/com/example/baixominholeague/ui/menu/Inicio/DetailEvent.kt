package com.example.baixominholeague.ui.menu.Inicio

import android.icu.text.SimpleDateFormat
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.example.baixominholeague.databinding.ActivityDetailEventBinding
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import com.squareup.picasso.Picasso
import java.util.*

class DetailEvent : AppCompatActivity() {


    companion object {
        const val NAME_EVENT = "name_event"
    }

    private lateinit var binding: ActivityDetailEventBinding
    private  var db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailEventBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val nameEvent = intent.getStringExtra(NAME_EVENT)
        Log.i("GAbri","$nameEvent")
        binding.tvTituloEvent.setText(nameEvent)

        if (nameEvent != null) {
            getDetailEvent(nameEvent)
        }
    }
    private fun getDetailEvent(nameEvent: String){
        val collectionRef = db.collection("eventos")

        collectionRef.get()
            .addOnSuccessListener { result ->
                for (document in result) {

                    if(document.id==nameEvent.lowercase()){
                        val correoJugador = document.getString("correo")
                        val fecha = document.getTimestamp("fecha")
                        val precio = document.getString("precio")
                        val ubicacion = document.getString("ubicacion")
                        val descripcion = document.getString("descripcion")
                        val imagen = document.getString("imagen")

                        if (correoJugador != null && fecha !=null && precio != null && ubicacion != null) {
                            setupUI(correoJugador,fecha,precio,ubicacion,descripcion,imagen)
                        }
                    }

                }
            }
            .addOnFailureListener { exception ->
                Log.i("GAB","Error al acceder")
            }
    }

    private fun setupUI(correo: String, fecha: Timestamp, precio: String, ubicacion: String,descripcion: String?,imagen: String?){
        var nuevaFecha = fecha.toDate()
        binding.tvCorreoPublicado.setText(correo)
        binding.tvFechaEvent.setText(SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()) .format(nuevaFecha))
        binding.tvHoraEvent.setText(SimpleDateFormat("HH:mm", Locale.getDefault()) .format(nuevaFecha)+" H")
        binding.tvPrecio.setText(precio)
        binding.tvUbicacion.setText(ubicacion)
        binding.tvDescripcion.setText(descripcion)
        Picasso.get().load(Uri.parse(imagen)).into(binding.imageViewEvent)
    }


}