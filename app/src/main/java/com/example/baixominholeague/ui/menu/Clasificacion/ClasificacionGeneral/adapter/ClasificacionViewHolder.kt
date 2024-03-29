package com.example.baixominholeague.ui.menu.Clasificacion.ClasificacionGeneral.adapter

import android.view.View
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.baixominholeague.R
import com.example.baixominholeague.data.Equipo
import com.example.baixominholeague.databinding.ItemEquipoBinding
import com.google.firebase.auth.FirebaseAuth

class ClasificacionViewHolder(view: View) : RecyclerView.ViewHolder(view) {

    private val binding = ItemEquipoBinding.bind(view)
    private val currentUser = FirebaseAuth.getInstance().currentUser

    fun bind(equipo: Equipo, position: Int) {
        val context = binding.tvNombreEquipo.context
        val colorBlanco = ContextCompat.getColor(context, R.color.negroBlanco)
        val colorNegro = ContextCompat.getColor(context, R.color.DarkSecondari)
        val colorAzulYVerde = ContextCompat.getColor(context, R.color.blue)

        val isCurrentUserInTeam =
            equipo.jugadores.any { jugador -> jugador.correo == currentUser?.email }
        if (isCurrentUserInTeam) {

            binding.apply {
                cardViewParticipante.setBackgroundResource(R.drawable.backgraound_card_selected)
                tvNombreEquipo.setTextColor(colorNegro)
                tvPartidosJugados.setTextColor(colorNegro)
                tvPartidosGanados.setTextColor(colorNegro)
                tvPartidosEmpatados.setTextColor(colorNegro)
                tvPartidosPerdidos.setTextColor(colorNegro)
                tvPuntuacion.setTextColor(colorNegro)
            }
        } else {
            binding.apply {
                cardViewParticipante.setBackgroundResource(R.drawable.background_card_rounded)
                tvNombreEquipo.setTextColor(colorBlanco)
                tvPartidosJugados.setTextColor(colorBlanco)
                tvPartidosGanados.setTextColor(colorBlanco)
                tvPartidosEmpatados.setTextColor(colorBlanco)
                tvPartidosPerdidos.setTextColor(colorBlanco)
                tvPuntuacion.setTextColor(colorAzulYVerde)
            }
        }

        binding.apply {
            tvNombreEquipo.text = equipo.nombreEquipo.toString()
            tvPosicion.text = (position + 1).toString()
            tvPuntuacion.setText("${equipo.partidosTotales["puntos"].toString()} Pts")
            tvPartidosJugados.setText("PJ:${equipo.partidosTotales["partidosJugados"].toString()}")
            tvPartidosGanados.setText("G:${equipo.partidosTotales["partidosGanados"].toString()}")
            tvPartidosEmpatados.setText("E:${equipo.partidosTotales["partidosEmpatados"].toString()}")
            tvPartidosPerdidos.setText("P:${equipo.partidosTotales["partidosPerdidos"].toString()}")
        }
    }
}


