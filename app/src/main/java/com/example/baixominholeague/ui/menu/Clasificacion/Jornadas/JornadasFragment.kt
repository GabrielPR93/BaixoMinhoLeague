package com.example.baixominholeague.ui.menu.Clasificacion.Jornadas

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.lifecycle.viewModelScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.baixominholeague.data.Jornada
import com.example.baixominholeague.data.Partido
import com.example.baixominholeague.databinding.FragmentJornadasBinding
import com.example.baixominholeague.ui.menu.Clasificacion.ClasificacionGeneral.ClasificacionViewModel
import com.example.baixominholeague.ui.menu.Clasificacion.Jornadas.adapter.PartidosAdapter
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

@AndroidEntryPoint
class JornadasFragment : Fragment() {
    private var _binding: FragmentJornadasBinding? = null
    private val binding get() = _binding!!
    private lateinit var partidoAdapter: PartidosAdapter

    private val clasificacionViewModel by activityViewModels<ClasificacionViewModel>()
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
        initUIState()
        initList()
        nextJornada()
        previousJornada()
    }

    private fun initUIState() {

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED){
                clasificacionViewModel.listaJornadas.collect{listaJornadas ->
                    val primerJornada = listaJornadas.firstOrNull()
                    val listaPartidos = primerJornada?.partidos ?: emptyList()
                    binding.tvJornadaActual.setText(primerJornada?.nombreJornada?.uppercase())
                    partidoAdapter.updateList(listaPartidos)
                }
            }
        }
    }

    private fun initList() {
        partidoAdapter = PartidosAdapter()
        binding.recyclerViewJornadas.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = partidoAdapter
        }

    }
    private fun nextJornada() {
        binding.btnSiguiente.setOnClickListener {

            val siguienteJornadaConPartidos = clasificacionViewModel.obtenerSiguienteJornadaConPartidos()

            if (siguienteJornadaConPartidos != null) {

                val (siguienteJornada, listaPartidos) = siguienteJornadaConPartidos
                binding.tvJornadaActual.text = siguienteJornada.nombreJornada?.uppercase()
                partidoAdapter.updateList(listaPartidos)
            }
        }
    }

    private fun previousJornada() {
        binding.btnAnterior.setOnClickListener {
            val jornadaAnteriorConPartidos = clasificacionViewModel.obtenerJornadaAnteriorConPartidos()

            if (jornadaAnteriorConPartidos != null) {

                val (jornadaAnterior, listaPartidos) = jornadaAnteriorConPartidos
                binding.tvJornadaActual.text = jornadaAnterior.nombreJornada?.uppercase()
                partidoAdapter.updateList(listaPartidos)
            }
        }
    }
}