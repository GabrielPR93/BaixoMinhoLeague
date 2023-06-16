package com.example.baixominholeague.recyclerViewEventos

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.baixominholeague.R
import com.example.baixominholeague.data.Evento


class EventoAdapter(private var eventos: List<Evento>, private val eliminarEvento: (Evento) -> Unit) : RecyclerView.Adapter<EventoViewHolder>() {


    fun updateList(eventos: List<Evento>){
        this.eventos=eventos
        notifyDataSetChanged()

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EventoViewHolder {
        return EventoViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_event, parent, false),eliminarEvento)

    }

    override fun getItemCount(): Int {
       return eventos.size
    }

    override fun onBindViewHolder(holder: EventoViewHolder, position: Int) {
       holder.bind(eventos[position])


    }
}
