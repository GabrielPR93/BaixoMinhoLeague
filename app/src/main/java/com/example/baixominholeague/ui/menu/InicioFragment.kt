package com.example.baixominholeague.ui.menu

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ListView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.baixominholeague.data.Evento
import com.example.baixominholeague.recyclerViewEventos.EventoAdapter
import com.example.baixominholeague.databinding.FragmentInicioBinding
import com.google.firebase.firestore.FirebaseFirestore

class InicioFragment : Fragment() {


    private var _binding: FragmentInicioBinding? = null
    private val binding get() = _binding!!
    private val db = FirebaseFirestore.getInstance()

    private lateinit var eventoAdapter: EventoAdapter


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {

        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentInicioBinding.inflate(inflater,container,false)
        val view = binding.root

        eventoAdapter = EventoAdapter(emptyList())
        binding.reyclerView.adapter = eventoAdapter
        binding.reyclerView.layoutManager = LinearLayoutManager(requireContext())

        getEventsOrderByDate()

        return view
    }

    private fun getEventsOrderByDate() {

        val eventsCollection = db.collection("eventos")
        val query = eventsCollection.orderBy("fecha")

        query.get().addOnSuccessListener { document ->
            val eventos = document.toObjects(Evento::class.java)
           eventoAdapter.updateList(eventos)

        }
            .addOnFailureListener{exception ->
                println("Error al obtener los eventos: $exception")
            }
    }

}

