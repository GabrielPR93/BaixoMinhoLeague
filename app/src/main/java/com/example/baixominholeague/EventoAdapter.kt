package com.example.baixominholeague

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import com.example.baixominholeague.data.Evento
import com.example.baixominholeague.databinding.ItemJugadorBinding
import com.google.firebase.firestore.FirebaseFirestore

class EventoAdapter(context: Context, resource: Int, eventos: List<Evento>) :
    ArrayAdapter<Evento>(context, resource, eventos) {


    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val itemView: View = convertView ?: LayoutInflater.from(context).inflate(R.layout.item_event, parent, false)

        val evento: Evento? = getItem(position)
        val correoTextView: TextView = itemView.findViewById(R.id.tvCorreo)
        val fechaTextView: TextView = itemView.findViewById(R.id.tvFecha)
        val horaTextView: TextView = itemView.findViewById(R.id.tvHora)
        val nombreTextView: TextView = itemView.findViewById(R.id.tvEventName)
        val precioTextView: TextView = itemView.findViewById(R.id.tvInscripcion)

        evento?.let {
            correoTextView.text = it.correo
            fechaTextView.text = it.fecha
            horaTextView.text = it.hora
            nombreTextView.text = it.nombre
            precioTextView.text = it.precio
        }

        return itemView
    }
}
