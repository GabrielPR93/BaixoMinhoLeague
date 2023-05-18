package com.example.baixominholeague.recyclerViewJugadores

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.baixominholeague.R
import com.example.baixominholeague.data.Jugador

class JugadorAdapter(private var jugadores: List<Jugador>, private val onItemSelected: (Int) -> Unit) : RecyclerView.Adapter<JugadorViewHolder>() {

    fun updateList(jugadores: List<Jugador>){
        this.jugadores=jugadores
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): JugadorViewHolder {
        return JugadorViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_jugador, parent, false))

    }

    override fun onBindViewHolder(holder: JugadorViewHolder, position: Int) {

        holder.bind(jugadores[position],onItemSelected)
    }

    override fun getItemCount(): Int {
        return jugadores.size
    }


}
