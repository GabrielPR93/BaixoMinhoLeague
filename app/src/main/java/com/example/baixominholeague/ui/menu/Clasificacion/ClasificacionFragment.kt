package com.example.baixominholeague.ui.menu.Clasificacion

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.viewpager2.widget.ViewPager2
import com.example.baixominholeague.R
import com.example.baixominholeague.data.Jugador
import com.example.baixominholeague.databinding.FragmentClasificacionBinding
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.google.firebase.firestore.FirebaseFirestore
import com.example.baixominholeague.ui.menu.Clasificacion.adapter.OnSpinnerSelectedListener
import com.example.baixominholeague.ui.menu.Clasificacion.adapter.ViewPagerAdapterClasificacion
import com.example.baixominholeague.ui.menu.Inicio.NovedadesFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ClasificacionFragment : Fragment(), OnSpinnerSelectedListener {

    private var _binding: FragmentClasificacionBinding? = null
    private val binding get() = _binding!!
    private var adapter: ViewPagerAdapterClasificacion? = null
    private var seleccionDivisionSpinner1 = ""
    private var seleccionLigaSpinner1 = ""
    private val clasificacionViewModel by activityViewModels<ClasificacionViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initUI()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentClasificacionBinding.inflate(inflater, container, false)
        val view = binding.root

        return view
    }

    private fun initUI() {
        viewPager()
        Spinners()
    }

    override fun onLigaSelected(selectedLiga: String, selectedDivision: String) {
        if (!selectedLiga.isNullOrEmpty() && !selectedDivision.isNullOrEmpty()) {
            clasificacionViewModel.obtenerDatos(selectedLiga, selectedDivision)
        }
    }

    fun viewPager() {
        val viewPager: ViewPager2 = binding.viewPagerClasificacion
        val tabs: TabLayout = binding.tabsClasificacion

        val fragments = listOf(ClasificacionGeneralFragment(), NovedadesFragment())

        adapter = ViewPagerAdapterClasificacion(requireActivity(), fragments)
        viewPager.adapter = adapter

        TabLayoutMediator(tabs, viewPager) { tab, position ->
            when (position) {
                0 -> tab.text = getString(R.string.clasificacion)
                1 -> tab.text = getString(R.string.Jornadas)
            }
        }.attach()
        binding.viewPagerClasificacion.currentItem = 0
    }

    fun quitarEspacios(frase: String): String {
        return frase.replace("\\s+".toRegex(), "")
    }


    fun Spinners() {

        val nombreLiga = resources.getStringArray(R.array.liga_options).toList()
        val divisiones = mutableListOf<String>()

        // Crear adaptadores para los Spinners
        val adapterSpinner1 = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_dropdown_item,
            nombreLiga
        )
        val adapterSpinner2 = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_dropdown_item,
            divisiones
        )

        // Configurar los adaptadores y el comportamiento de los Spinners
        adapterSpinner1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        adapterSpinner2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        binding.spinnerLiga.adapter = adapterSpinner1
        binding.spinnerDivision.adapter = adapterSpinner2

        binding.spinnerLiga.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parentView: AdapterView<*>?,
                selectedItemView: View?,
                position: Int,
                id: Long
            ) {
                divisiones.clear()
                seleccionLigaSpinner1 = nombreLiga[position]

                val divisionOptionsResourceId = resources.getIdentifier(
                    "division_options_${quitarEspacios(seleccionLigaSpinner1)}",
                    "array",
                    requireContext().packageName
                )
                if (divisionOptionsResourceId != 0) {
                    val divisionOptions =
                        resources.getStringArray(divisionOptionsResourceId)?.toList() ?: emptyList()
                    divisiones.addAll(divisionOptions)
                }
                binding.spinnerDivision.setSelection(0)
                // Notificar al adaptador del segundo Spinner sobre el cambio en los datos
                adapterSpinner2.notifyDataSetChanged()

                seleccionDivisionSpinner1 = binding.spinnerDivision.selectedItem?.toString() ?: ""
                onLigaSelected(seleccionLigaSpinner1, seleccionDivisionSpinner1)

            }

            override fun onNothingSelected(parentView: AdapterView<*>?) {
                // No se ha seleccionado nada
            }
        }

        binding.spinnerDivision.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parentView: AdapterView<*>?,
                    selectedItemView: View?,
                    position: Int,
                    id: Long
                ) {
                    val seleccionLiga = binding.spinnerLiga.selectedItem?.toString()
                    val seleccionDivision = divisiones[position]

                    if (seleccionLiga != null && seleccionDivision != null) {
                        if(seleccionLiga != seleccionLigaSpinner1 || seleccionDivision != seleccionDivisionSpinner1){
                            onLigaSelected(seleccionLiga, seleccionDivision)

                            if(seleccionDivision != seleccionDivisionSpinner1){
                                seleccionDivisionSpinner1=seleccionDivision
                            }
                        }
                    }
                }

                override fun onNothingSelected(parentView: AdapterView<*>?) {
                    // No se ha seleccionado nada
                }
            }
    }

}