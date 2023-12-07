package com.example.baixominholeague.ui.menu.Clasificacion.Jornadas.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.baixominholeague.R
import com.example.baixominholeague.data.Partido

class PartidosAdapter(private var partidos:List<Partido> = emptyList()):RecyclerView.Adapter<PartidosViewHolder>() {

    fun updateList(partidos: List<Partido>){
        this.partidos = partidos
        notifyDataSetChanged()
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PartidosViewHolder {
        return PartidosViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_jornada,parent,false))
    }

    override fun onBindViewHolder(holder: PartidosViewHolder, position: Int) {
       holder.bind(partidos[position])
    }
    override fun getItemCount() = partidos.size
}