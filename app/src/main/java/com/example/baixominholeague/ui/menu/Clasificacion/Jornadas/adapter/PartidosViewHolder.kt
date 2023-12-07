package com.example.baixominholeague.ui.menu.Clasificacion.Jornadas.adapter

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.example.baixominholeague.data.Partido
import com.example.baixominholeague.databinding.ItemJornadaBinding

class PartidosViewHolder(view:View): RecyclerView.ViewHolder(view) {

    private val binding = ItemJornadaBinding.bind(view)
    fun bind(partido: Partido){
        binding.apply {
            tvNombreEquipoLocal.text = partido.nombreEquipoLocal
            tvNombreEquipoVisitante.text = partido.nombreEquipoVisitante

            if(!partido.resultadoPrimerSet.equals("")){
                tvMarcador.text = partido.resultadoPrimerSet
                tvMarcador2.visibility = View.VISIBLE
                tvMarcador2.text = partido.resultadoSegundoSet
            }

            if(partido.nombreEquipoVisitante.equals("")){
                tvNombreEquipoVisitante.text = "Descansa"
            }
        }
    }
}