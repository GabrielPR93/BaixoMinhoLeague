package com.example.baixominholeague.ui.menu.Inicio.adapter.adapterParticipantes

import android.net.Uri
import android.util.Log
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.example.baixominholeague.R
import com.example.baixominholeague.data.Participante
import com.example.baixominholeague.databinding.ItemParticipanteBinding
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso

class ParticipanteViewHolder(view: View) : RecyclerView.ViewHolder(view) {

    private var binding = ItemParticipanteBinding.bind(view)

    fun bind(participante: Participante) {
        binding.tvAliasParticipante.setText(participante.alias)
        binding.tvNombreCompleto.setText(participante.nombreCompleto)
        binding.progresBarParticipante.visibility = View.VISIBLE

        val imagenUri = participante.imagen?.let { Uri.parse(it) }

        if (imagenUri != null && imagenUri.toString()!="") {
            Picasso.get().load(imagenUri).into(binding.imageViewProfileParticipante, object : Callback {
                override fun onSuccess() {
                    binding.progresBarParticipante.visibility = View.GONE
                }

                override fun onError(e: Exception?) {
                    binding.progresBarParticipante.visibility = View.GONE
                    Log.i("Gabri", "Error al cargar la imagen: $e")
                }
            })
        } else {
            // Si participante.imagen es nulo, mostrar una imagen predeterminada
            Picasso.get().load(R.drawable.perfil1).into(binding.imageViewProfileParticipante)
            binding.progresBarParticipante.visibility = View.GONE
        }
    }
}