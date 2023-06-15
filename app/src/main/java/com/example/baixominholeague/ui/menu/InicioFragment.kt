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
import com.example.baixominholeague.data.Evento
import com.example.baixominholeague.recyclerViewEventos.EventoAdapter
import com.example.baixominholeague.databinding.FragmentInicioBinding
import com.example.baixominholeague.recyclerViewJugadores.JugadorAdapter
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

        //adapter = JugadorAdapter(jugadores){idJugador -> navigateToDetailPlayer(idJugador) }
        eventoAdapter = EventoAdapter(emptyList())
        binding.reyclerView.adapter = eventoAdapter
        binding.reyclerView.layoutManager = LinearLayoutManager(requireContext())

        getEventsOrderByDate()



        return view
    }

    private fun getEventsOrderByDate() {

        val eventsCollection = db.collection("eventos")
        val query = eventsCollection.orderBy("fecha", Query.Direction.ASCENDING)

        query.get().addOnSuccessListener { document ->
            val eventos = document.toObjects(Evento::class.java)
           eventoAdapter.updateList(eventos)

            for (evento in eventos){
                if(evento.correo.equals(correo)){
                    evento.mostrarBotonCancelar = true
                }
            }
        }
            .addOnFailureListener{exception ->
                println("Error al obtener los eventos: $exception")
            }

    }


}

