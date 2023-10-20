package com.example.baixominholeague.recyclerViewEventos

import android.icu.text.SimpleDateFormat
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.example.baixominholeague.data.Evento
import com.example.baixominholeague.databinding.ItemEventBinding
import com.example.baixominholeague.ui.menu.PerfilFragment.Companion.CORREO_ADMIN
import com.google.firebase.auth.FirebaseAuth
import java.util.*

class EventoViewHolder(view: View, private val eliminarEvento: (Evento) -> Unit): RecyclerView.ViewHolder(view) {

    private val binding = ItemEventBinding.bind(view)
    private val currentUser = FirebaseAuth.getInstance().currentUser

    fun bind(evento: Evento, onItemSelected:(String) -> Unit) {

        binding.tvFecha.setText(SimpleDateFormat("dd/MM/yyyy - HH:mm", Locale.getDefault()) .format(evento.fecha)+" H")
        binding.tvEventName.setText(evento.nombre)
        binding.tvPrecio.setText("Inscripción:")
        binding.tvUbicacion.setText(evento.ubicacion)
        binding.tvInscripcion.setText(evento.precio)

        if(evento.mostrarBotonCancelar || currentUser?.email.equals(CORREO_ADMIN)){
            binding.btnCancel.visibility = View.VISIBLE
            binding.btnCancel.setOnClickListener{
                eliminarEvento(evento)
            }
        }else{
            binding.btnCancel.visibility = View.GONE
        }

        binding.root.setOnClickListener{ evento.nombre?.let { it -> onItemSelected(it) }}
    }
}
