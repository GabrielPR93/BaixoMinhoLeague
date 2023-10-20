package com.example.baixominholeague.ui.menu.Inicio.recyclerViewParticipantes

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.example.baixominholeague.data.Participante
import com.example.baixominholeague.databinding.ItemEventBinding
import com.example.baixominholeague.databinding.ItemParticipanteBinding

class ParticipanteViewHolder(view: View) : RecyclerView.ViewHolder(view) {

    private var binding = ItemParticipanteBinding.bind(view)

    fun bind(participante: Participante) {
        binding.tvNombreParticipante.setText(participante.alias)
        binding.tvCorreoParticipante.setText(participante.correo)
    }
}