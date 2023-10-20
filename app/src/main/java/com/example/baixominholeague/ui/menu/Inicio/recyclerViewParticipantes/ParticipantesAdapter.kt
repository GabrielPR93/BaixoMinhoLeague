package com.example.baixominholeague.ui.menu.Inicio.recyclerViewParticipantes

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.baixominholeague.R
import com.example.baixominholeague.data.Evento
import com.example.baixominholeague.data.Participante

class ParticipantesAdapter (private var participantes: List<Participante>): RecyclerView.Adapter<ParticipanteViewHolder>() {

    fun updateList(participantes: List<Participante>){
        this.participantes=participantes
        notifyDataSetChanged()

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ParticipanteViewHolder {
        return ParticipanteViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_participante, parent,false))
    }

    override fun onBindViewHolder(holder: ParticipanteViewHolder, position: Int) {
        holder.bind(participantes[position])
    }

    override fun getItemCount(): Int {
        return participantes.size
    }
}