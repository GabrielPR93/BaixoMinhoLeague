package com.example.baixominholeague.ui.menu.Clasificacion.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.baixominholeague.R
import com.example.baixominholeague.data.Equipo

class ClasificacionAdapter(private var equipos: List<Equipo> = emptyList()): RecyclerView.Adapter<CLasificacionViewHolder>() {
    fun updateList(equipos: List<Equipo>){
        this.equipos=equipos
        notifyDataSetChanged()

    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CLasificacionViewHolder {
       return CLasificacionViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_equipo,parent,false))
    }

    override fun onBindViewHolder(holder: CLasificacionViewHolder, position: Int) {
       holder.bind(equipos[position],position)
    }

    override fun getItemCount() = equipos.size

}