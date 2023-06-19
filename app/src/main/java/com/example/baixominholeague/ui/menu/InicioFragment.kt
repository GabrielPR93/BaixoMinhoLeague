package com.example.baixominholeague.ui.menu

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.baixominholeague.MainActivity
import com.example.baixominholeague.MainActivity.Companion.CLAVE_CORREO
import com.example.baixominholeague.R
import com.example.baixominholeague.data.Evento
import com.example.baixominholeague.recyclerViewEventos.EventoAdapter
import com.example.baixominholeague.databinding.FragmentInicioBinding
import com.example.baixominholeague.recyclerViewJugadores.JugadorAdapter
import com.google.android.gms.dynamic.SupportFragmentWrapper
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.protobuf.Empty

class InicioFragment : Fragment() {


    private var _binding: FragmentInicioBinding? = null
    private val binding get() = _binding!!
    private val db = FirebaseFirestore.getInstance()

    private lateinit var eventoAdapter: EventoAdapter
    private var correo: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {

            correo = it.getString(CLAVE_CORREO)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentInicioBinding.inflate(inflater,container,false)
        val view = binding.root

        binding.progresBarEvents.visibility=View.VISIBLE
        eventoAdapter = EventoAdapter(emptyList(),::eliminarEvento)
        binding.reyclerView.adapter = eventoAdapter
        binding.reyclerView.layoutManager = LinearLayoutManager(requireContext())

        getEventsOrderByDate()

        return view
    }

     fun getEventsOrderByDate() {

        val eventsCollection = db.collection("eventos")
        val query = eventsCollection.orderBy("fecha", Query.Direction.ASCENDING)

        query.get().addOnSuccessListener { document ->
            val eventos = document.toObjects(Evento::class.java)
           eventoAdapter.updateList(eventos)

            for (evento in eventos){
                if(evento.correo.equals(correo)){
                    evento.mostrarBotonCancelar = true
                    binding.progresBarEvents.visibility=View.GONE
                }
            }
        }
            .addOnFailureListener{exception ->
                println("Error al obtener los eventos: $exception")
            }
    }
    private fun eliminarEvento(evento: Evento) {
        val alertDialog = AlertDialog.Builder(requireContext())
            .setTitle("Confirmar eliminación")
            .setMessage("¿Estás seguro de que deseas borrar el evento?")
            .setPositiveButton("Sí") { dialog, which ->
                evento.nombre?.let { nombreEvento ->
                    db.collection("eventos").document(nombreEvento).delete()
                        .addOnSuccessListener {
                            getEventsOrderByDate()
                        }
                        .addOnFailureListener { exception ->
                            println("Error al eliminar el evento: $exception")
                        }
                }
            }
            .setNegativeButton("No", null)
            .create()

        alertDialog.show()
    }
}

