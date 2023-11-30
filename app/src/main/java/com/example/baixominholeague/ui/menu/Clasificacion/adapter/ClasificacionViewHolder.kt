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
        binding.tvPuntuacion.setText("98 Pts")
        binding.tvPartidosJugados.setText("PJ:10")
        binding.tvPartidosGanados.setText("G:8")
        binding.tvPartidosEmpatados.setText("E:10")
        binding.tvPartidosPerdidos.setText("P:10")

    }
}

