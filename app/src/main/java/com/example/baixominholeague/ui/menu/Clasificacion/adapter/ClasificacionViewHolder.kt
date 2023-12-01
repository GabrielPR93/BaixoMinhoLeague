package com.example.baixominholeague.ui.menu.Clasificacion.adapter

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.example.baixominholeague.data.Equipo
import com.example.baixominholeague.databinding.ItemEquipoBinding

class ClasificacionViewHolder(view: View): RecyclerView.ViewHolder(view) {

    private val binding = ItemEquipoBinding.bind(view)

    fun bind(equipo: Equipo,position: Int){
        val context = binding.tvNombreEquipo.context
        binding.tvNombreEquipo.text = equipo.nombre.toString()
        binding.tvPosicion.text = (position+1).toString()
        binding.tvPuntuacion.setText("${equipo.puntos.toString()} Pts")
        binding.tvPartidosJugados.setText("PJ:${equipo.partidosJugados.toString()}")
        binding.tvPartidosGanados.setText("G:${equipo.partidosGanados.toString()}")
        binding.tvPartidosEmpatados.setText("E:${equipo.partidosEmpatados.toString()}")
        binding.tvPartidosPerdidos.setText("P:${equipo.partidosPerdidos.toString()}")
    }
}


