package com.example.baixominholeague.ui.menu.Jugadores.adapter

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.example.baixominholeague.data.Jugador
import com.example.baixominholeague.databinding.ItemJugadorBinding

class JugadorViewHolder(view: View):RecyclerView.ViewHolder(view) {

    private val binding = ItemJugadorBinding.bind(view)

    fun bind(jugador: Jugador, onItemSelected:(Int) -> Unit) {
        binding.tvPlayerName.setText(jugador.nombre)
        binding.root.setOnClickListener { jugador.id?.let { it -> onItemSelected(it) } }
    }
}