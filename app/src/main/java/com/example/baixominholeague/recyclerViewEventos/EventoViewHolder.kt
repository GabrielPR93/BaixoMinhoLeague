package com.example.baixominholeague.recyclerViewEventos

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.example.baixominholeague.data.Evento
import com.example.baixominholeague.data.Jugador
import com.example.baixominholeague.databinding.ItemEventBinding
import com.example.baixominholeague.databinding.ItemJugadorBinding

class EventoViewHolder(view: View): RecyclerView.ViewHolder(view) {

    private val binding = ItemEventBinding.bind(view)

    fun bind(evento: Evento) {
        binding.tvFecha.setText(evento.fecha)
        binding.tvHora.setText(evento.hora)
        binding.tvEventName.setText(evento.nombre)
        binding.tvPrecio.setText("Inscripción:")
        if(evento.precio.isNullOrEmpty()){
            binding.tvInscripcion.setText("Gratis")
        }else{
            binding.tvInscripcion.setText(evento.precio+" €")
        }
        binding.tvCorreo.setText(evento.correo)
        binding.tvPubli.setText("Publicado por:")
    }

}