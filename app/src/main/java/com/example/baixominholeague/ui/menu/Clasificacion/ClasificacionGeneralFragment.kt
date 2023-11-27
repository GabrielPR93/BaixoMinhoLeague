package com.example.baixominholeague.ui.menu.Clasificacion

import android.os.Bundle
import android.util.Log
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
import com.example.baixominholeague.ui.menu.Clasificacion.adapter.OnSpinnerSelectedListener
import com.example.baixominholeague.ui.menu.Inicio.adapter.EventoAdapter

class ClasificacionGeneralFragment : Fragment(), OnSpinnerSelectedListener {

    private var _binding: FragmentClasificacionGeneralBinding? = null
    private val binding get() = _binding!!
    private lateinit var equipoAdapter: ClasificacionAdapter
    private var spinnerSelectedListener: OnSpinnerSelectedListener? = null

    val equiposDePrueba2 = listOf(
        Equipo(0, "Equipo 1","1ª Division","Gabriel","Dany",0,1),
        Equipo(1, "Equipo 2","1ª Division","Gabriel","Dany",0,1),
        Equipo(1, "Equipo 3","1ª Division","Gabriel","Dany",0,1),

        )
    val equiposDePrueba3 = listOf(
        Equipo(0, "Equipo 4","1ª Division","Gabriel","Dany",0,1),
        Equipo(1, "Equipo 5","1ª Division","Gabriel","Dany",0,1),
        Equipo(1, "Equipo 6","1ª Division","Gabriel","Dany",0,1),

        )

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initUI()

    }

    private fun initUI() {
        val equiposDePrueba = listOf(
            Equipo(0, "Equipo A","1ª Division","Gabriel","Dany",0,1),
            Equipo(1, "Equipo B","1ª Division","Gabriel","Dany",0,1),
            Equipo(1, "Equipo C","1ª Division","Gabriel","Dany",0,1),

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
    fun setCommunicationListener(listener: OnSpinnerSelectedListener) {
        this.spinnerSelectedListener = listener
    }
    fun updateData(selectedLiga: String, selectedDivision: String){
        Log.i("GAbri","CAMBIOOOOOOO: $selectedLiga y $selectedDivision")
    }
    override fun onLigaSelected(selectedLiga: String, selectedDivision: String) {
        updateData(selectedLiga,selectedDivision)
    }
}
