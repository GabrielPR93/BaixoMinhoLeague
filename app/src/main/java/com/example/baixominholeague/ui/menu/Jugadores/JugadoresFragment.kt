package com.example.baixominholeague.ui.menu.Jugadores

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import androidx.core.view.isVisible
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.baixominholeague.data.Jugador
import com.example.baixominholeague.databinding.FragmentJugadoresBinding
import com.example.baixominholeague.ui.menu.Jugadores.adapter.JugadorAdapter
import com.google.firebase.firestore.FirebaseFirestore

class JugadoresFragment : Fragment() {

    private var _binding: FragmentJugadoresBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapter: JugadorAdapter
    private var setupExecuted = false
    private val jugadores = mutableListOf<Jugador>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setup()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentJugadoresBinding.inflate(inflater,container,false)
        val view = binding.root

        adapter = JugadorAdapter(jugadores){ idJugador -> findNavController().navigate(JugadoresFragmentDirections.actionJugadoresFragmentToDetailJugador(idJugador)) }


        searchPlayer()


        return view
    }
    private fun setup() {
        // Obtener jugadores solo si aún no se ha hecho
        if (!setupExecuted) {
            getJugadores()
        }

        // Configurar el RecyclerView y el adaptador
        val recyclerView = binding.recyclerViewJugadores
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = adapter
    }

    private fun searchPlayer() {

        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                searchByName(newText.toString())
                return false
            }
        })
    }

    private fun searchByName(query: String) {
        binding.ProgresBar.isVisible=true

        val filteredJugadores = jugadores.filter { jugador ->
            jugador.nombre.contains(query, ignoreCase = true)
        }
        Log.i("GAB","NAME")
        adapter.updateList(filteredJugadores)
        binding.ProgresBar.isVisible=false
    }

    private fun getJugadores() {
        val jugadoresCollectionRef = FirebaseFirestore.getInstance().collection("jugadores")

        jugadoresCollectionRef.get()
            .addOnSuccessListener { querySnapshot ->
                for (document in querySnapshot) {
                    val jugador = document.toObject(Jugador::class.java)
                    // Agregar jugador solo si no existe en la lista
                    if (!jugadores.contains(jugador)) {
                        jugadores.add(jugador)
                    }
                }
                setupExecuted = true
                adapter.updateList(jugadores)
            }
            .addOnFailureListener { exception ->
                Log.e("Error", "Error al obtener los jugadores", exception)
            }
    }
}