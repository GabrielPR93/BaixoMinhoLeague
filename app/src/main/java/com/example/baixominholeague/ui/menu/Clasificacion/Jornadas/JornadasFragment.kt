package com.example.baixominholeague.ui.menu.Clasificacion.Jornadas

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.baixominholeague.data.Partido
import com.example.baixominholeague.databinding.FragmentJornadasBinding
import com.example.baixominholeague.ui.menu.Clasificacion.Jornadas.adapter.PartidosAdapter


class JornadasFragment : Fragment() {
    private var _binding: FragmentJornadasBinding? = null
    private val binding get() = _binding!!
    private lateinit var partidoAdapter: PartidosAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initUI()

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentJornadasBinding.inflate(inflater, container, false)
        val view = binding.root



        return view
    }
    private fun initUI() {

        initList()
    }
    private fun initUIState() {
        partidoAdapter.updateList(
            listOf(
                Partido("Equipo A", "Equipo B", "2 - 4", "4 - 1"),
                Partido("Equipo C", "Equipo D", "0 - 4", "3 - 4"),
                Partido("Jugador 13 Jugador 14", "Equipo E", "0 - 4", "3 - 4"),
                Partido("Jugador 17 Jugador 18", "Equipo F", "", ""),
                Partido("Jugador 15 Jugador 16", "Equipo G", "0 - 4", "3 - 4"),
                Partido("Jugador 19 Jugador 22", "", "", ""),
            )
        )
    }

    private fun initList() {
        partidoAdapter = PartidosAdapter()
        binding.recyclerViewJornadas.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = partidoAdapter
        }
        initUIState()
    }
}