package com.example.baixominholeague.recyclerViewEventos

import android.icu.text.SimpleDateFormat
import android.opengl.Visibility
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.example.baixominholeague.data.Evento
import com.example.baixominholeague.databinding.FragmentInicioBinding
import com.example.baixominholeague.databinding.ItemEventBinding
import com.example.baixominholeague.ui.menu.InicioFragment
import com.google.firebase.firestore.FirebaseFirestore
import java.util.*

class EventoViewHolder(view: View): RecyclerView.ViewHolder(view) {

    private val binding = ItemEventBinding.bind(view)
    private val db = FirebaseFirestore.getInstance()

    fun bind(evento: Evento) {

        binding.tvFecha.setText(SimpleDateFormat("dd/MM/yyyy - HH:mm", Locale.getDefault()) .format(evento.fecha)+" h")
        binding.tvEventName.setText(evento.nombre)
        binding.tvPrecio.setText("Inscripción:")
        if(evento.precio.isNullOrEmpty()){
            binding.tvInscripcion.setText("Gratis")
        }else{
            binding.tvInscripcion.setText(evento.precio+" €")
        }
        binding.tvCorreo.setText(evento.correo)
        binding.tvPubli.setText("Publicado por:")

        if(evento.mostrarBotonCancelar){
            binding.btnCancel.visibility = View.VISIBLE
            binding.btnCancel.setOnClickListener{
                eliminarEvento(evento)
            }
        }else{
            binding.btnCancel.visibility = View.GONE
        }
    }

    private fun eliminarEvento(evento: Evento) {
        val alertDialog = AlertDialog.Builder(itemView.context)
            .setTitle("Confirmar eliminación")
            .setMessage("¿Estás seguro de que deseas borrar el evento?")
            .setPositiveButton("Sí") { dialog, which ->

                evento.nombre?.let { db.collection("eventos").document(it).delete() }

            }
            .setNegativeButton("No", null)
            .create()

        alertDialog.show()

    }

}
