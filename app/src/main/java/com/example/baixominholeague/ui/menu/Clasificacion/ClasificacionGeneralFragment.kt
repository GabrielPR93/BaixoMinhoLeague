package com.example.baixominholeague.ui.menu.Clasificacion

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.baixominholeague.R
import com.example.baixominholeague.data.Equipo
import com.example.baixominholeague.databinding.FragmentClasificacionBinding
import com.example.baixominholeague.databinding.FragmentClasificacionGeneralBinding
import com.example.baixominholeague.ui.menu.Clasificacion.adapter.ClasificacionAdapter
import com.example.baixominholeague.ui.menu.Inicio.adapter.EventoAdapter

class ClasificacionGeneralFragment : Fragment() {

    private var _binding: FragmentClasificacionGeneralBinding? = null
    private val binding get() = _binding!!
    private lateinit var equipoAdapter: ClasificacionAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initUI()
    }

    private fun initUI() {
        val equiposDePrueba = listOf(
            Equipo(0, "Equipo A","1ª Division","Gabriel","Dany",0,1),
            Equipo(1, "Equipo B","1ª Division","Gabriel","Dany",0,1),
            Equipo(1, "Equipo C","1ª Division","Gabriel","Dany",0,1),

            // Agrega más equipos según sea necesario
        )
        equipoAdapter= ClasificacionAdapter(equiposDePrueba)
        binding.recyclerViewClasificacion.adapter = equipoAdapter
        binding.recyclerViewClasificacion.layoutManager = LinearLayoutManager(requireContext())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentClasificacionGeneralBinding.inflate(inflater, container, false)
        val view = binding.root

        return view
    }

}