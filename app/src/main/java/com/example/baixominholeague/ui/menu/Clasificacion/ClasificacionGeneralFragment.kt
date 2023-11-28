package com.example.baixominholeague.ui.menu.Clasificacion

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.baixominholeague.R
import com.example.baixominholeague.data.Equipo
import com.example.baixominholeague.databinding.FragmentClasificacionBinding
import com.example.baixominholeague.databinding.FragmentClasificacionGeneralBinding
import com.example.baixominholeague.ui.menu.Clasificacion.adapter.ClasificacionAdapter
import com.example.baixominholeague.ui.menu.Clasificacion.adapter.OnSpinnerSelectedListener
import com.example.baixominholeague.ui.menu.Inicio.adapter.EventoAdapter
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ClasificacionGeneralFragment : Fragment(), OnSpinnerSelectedListener {

    private var _binding: FragmentClasificacionGeneralBinding? = null
    private val binding get() = _binding!!
    private lateinit var equipoAdapter: ClasificacionAdapter
    private var spinnerSelectedListener: OnSpinnerSelectedListener? = null
    private val listaEquipos: MutableList<Equipo> = mutableListOf()

    private val clasificacionViewModel by viewModels<ClasificacionViewModel>()



    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initUI()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentClasificacionGeneralBinding.inflate(inflater, container, false)
        val view = binding.root
        Log.i("GAbri","SE CREO CALSIFICACION GENERAL **************")
        return view
    }
    private fun initUI() {
        initUIState()
        equipoAdapter= ClasificacionAdapter(emptyList())
        binding.recyclerViewClasificacion.adapter = equipoAdapter
        binding.recyclerViewClasificacion.layoutManager = LinearLayoutManager(requireContext())
    }

    private fun initUIState(){

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED){
                clasificacionViewModel.listaEquipos.collect{
                     equipoAdapter.updateList(it)
                    Log.i("GAbri","SE INICIOOOO + $it")
                }
            }
        }
    }
    fun setCommunicationListener(listener: OnSpinnerSelectedListener) {
        this.spinnerSelectedListener = listener
    }
    fun updateData(selectedLiga: String, selectedDivision: String){
        Log.i("GAbri","CAMBIOOOOOOO: $selectedLiga y $selectedDivision")
        if(isAdded && !isDetached){
            if(selectedLiga!=null && selectedDivision!=null){
                clasificacionViewModel.obtenerDatos(selectedLiga,selectedDivision)
            }
        }
    }
    override fun onLigaSelected(selectedLiga: String, selectedDivision: String) {
        updateData(selectedLiga,selectedDivision)
    }

}
