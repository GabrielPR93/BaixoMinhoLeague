package com.example.baixominholeague.ui.menu.Clasificacion.adapter

import android.graphics.Color
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
        val colorBlanco = ContextCompat.getColor(context, R.color.blanco_gris)
        val isCurrentUserInTeam =
            equipo.jugadores.any { jugador -> jugador.correo == currentUser?.email }
        if (isCurrentUserInTeam) {
            val colorNegro = ContextCompat.getColor(context, R.color.DarkSecondari)
            val colorVerde = ContextCompat.getColor(context, R.color.greenPrimary)

            binding.apply {
                cardViewParticipante.setBackgroundResource(R.drawable.backgraound_card_selected)
                tvNombreEquipo.setTextColor(colorNegro)
                tvPartidosJugados.setTextColor(colorNegro)
                tvPartidosGanados.setTextColor(colorNegro)
                tvPartidosEmpatados.setTextColor(colorNegro)
                tvPartidosPerdidos.setTextColor(colorNegro)
                tvPuntuacion.setTextColor(colorVerde)
            }
        } else {
            binding.apply {
                cardViewParticipante.setBackgroundResource(R.drawable.background_card_rounded)
                tvNombreEquipo.setTextColor(colorBlanco)
                tvPartidosJugados.setTextColor(colorBlanco)
                tvPartidosGanados.setTextColor(colorBlanco)
                tvPartidosEmpatados.setTextColor(colorBlanco)
                tvPartidosPerdidos.setTextColor(colorBlanco)
            }
        }

        binding.apply {
            tvNombreEquipo.text = equipo.nombreEquipo.toString()
            tvPosicion.text = (position + 1).toString()
            tvPuntuacion.setText("${equipo.puntos.toString()} Pts")
            tvPartidosJugados.setText("PJ:${equipo.partidosJugados.toString()}")
            tvPartidosGanados.setText("G:${equipo.partidosGanados.toString()}")
            tvPartidosEmpatados.setText("E:${equipo.partidosEmpatados.toString()}")
            tvPartidosPerdidos.setText("P:${equipo.partidosPerdidos.toString()}")
        }
    }
}


