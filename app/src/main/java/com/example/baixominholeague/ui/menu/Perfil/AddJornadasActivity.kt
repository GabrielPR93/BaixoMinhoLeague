package com.example.baixominholeague.ui.menu.Perfil

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.baixominholeague.R
import com.example.baixominholeague.data.Jornada

import com.example.baixominholeague.databinding.ActivityAddJornadasBinding
import com.example.baixominholeague.ui.menu.Clasificacion.ClasificacionGeneral.ClasificacionViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class AddJornadasActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddJornadasBinding
    private var selectedLiga = ""
    private var selectedDivision = ""
    private val viewModel: ClasificacionViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddJornadasBinding.inflate(layoutInflater)
        setContentView(binding.root)

        Spinner()
        initUIState()
        addJornada()
    }

    private fun initUIState() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.listaJornadas.collect { jornadas ->
                    actualizarInterfazConJornadas(jornadas)
                    binding.tvNumeroJornadas.text = "${jornadas.size} Jornadas"
                }
            }
        }
    }

    private fun addJornada() {
        binding.btnAddJornada.setOnClickListener {
            val contenedorJornadas: LinearLayout = binding.linearLayoutJornadas

            // Crear TextView para el nombre de la jornada
            val tvNombreJornada = TextView(this)
            tvNombreJornada.text =
                "Nueva Jornada" // Puedes establecer un nombre predeterminado o solicitar uno al usuario

            // Puedes personalizar el TextView según tus necesidades
            val layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            layoutParams.setMargins(0, 0, 0, 16) // Márgenes opcionales entre los elementos
            tvNombreJornada.layoutParams = layoutParams

            // Agregar TextView al contenedor
            contenedorJornadas.addView(tvNombreJornada)

            // Crear EditText para el equipo 1
            val etEquipo1 = EditText(this)
            etEquipo1.hint = "Equipo 1" // Puedes personalizar el hint según tus necesidades

            // Agregar EditText al contenedor
            contenedorJornadas.addView(etEquipo1)

            // Crear TextView "vs"
            val tvVs = TextView(this)
            tvVs.text = "vs"

            // Puedes personalizar el TextView según tus necesidades
            val layoutParamsVs = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            layoutParamsVs.setMargins(0, 0, 0, 16) // Márgenes opcionales entre los elementos
            tvVs.layoutParams = layoutParamsVs

            // Agregar TextView "vs" al contenedor
            contenedorJornadas.addView(tvVs)

            // Crear EditText para el equipo 2
            val etEquipo2 = EditText(this)
            etEquipo2.hint = "Equipo 2" // Puedes personalizar el hint según tus necesidades

            // Agregar EditText al contenedor
            contenedorJornadas.addView(etEquipo2)
        }
    }

    private fun actualizarInterfazConJornadas(jornadas: List<Jornada>) {
        val contenedorJornadas: LinearLayout = binding.linearLayoutJornadas

        // Eliminar vistas antiguas si es necesario
        contenedorJornadas.removeAllViews()

        // Crear vistas para cada jornada y agregarlas al contenedor
        for (jornada in jornadas) {
            // Crear TextView para el nombre de la jornada
            val tvNombreJornada = TextView(this)
            tvNombreJornada.text = jornada.nombreJornada

            // Puedes personalizar el TextView según tus necesidades
            val layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            layoutParams.setMargins(0, 0, 0, 16) // Márgenes opcionales entre los TextViews
            tvNombreJornada.layoutParams = layoutParams

            // Agregar TextView al contenedor
            contenedorJornadas.addView(tvNombreJornada)
        }
    }


    private fun Spinner() {
        val nombreLiga = resources.getStringArray(R.array.liga_options).toList()
        val divisiones = mutableListOf<String>()

        val adapterSpinner = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_dropdown_item,
            nombreLiga
        )
        val adapterSpinner2 = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_dropdown_item,
            divisiones
        )

        adapterSpinner.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        adapterSpinner2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        binding.spinnerAddLigaAddJornada.adapter = adapterSpinner
        binding.spinnerAddDivision.adapter = adapterSpinner2

        binding.spinnerAddLigaAddJornada.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parentView: AdapterView<*>?,
                    selectedItemView: View?,
                    position: Int,
                    id: Long
                ) {
                    divisiones.clear()
                    selectedLiga = nombreLiga[position]

                    val divisionOptionsResourceId = resources.getIdentifier(
                        "division_options_${quitarEspacios(selectedLiga)}",
                        "array",
                        this@AddJornadasActivity.packageName
                    )
                    if (divisionOptionsResourceId != 0) {
                        val divisionOptions =
                            resources.getStringArray(divisionOptionsResourceId)?.toList()
                                ?: emptyList()
                        divisiones.addAll(divisionOptions)
                    }
                        binding.spinnerAddDivision.setSelection(0)
                    adapterSpinner2.notifyDataSetChanged()
                    selectedDivision = binding.spinnerAddDivision.selectedItem?.toString() ?: ""
                    viewModel.obtenerJornadas(selectedLiga,selectedDivision)

                }

                override fun onNothingSelected(parentView: AdapterView<*>?) {

                }
            }

        binding.spinnerAddDivision.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parentView: AdapterView<*>?,
                    selectedItemView: View?,
                    position: Int,
                    id: Long
                ) {

                    val seleccionLiga = binding.spinnerAddLigaAddJornada.selectedItem?.toString()
                    val seleccionDivision = divisiones[position]
                    Log.i("GAbri","SPINEER 1: $seleccionLiga y $selectedDivision   SPINNER 2: $seleccionLiga y $seleccionDivision")

                    if (seleccionLiga != null && seleccionDivision != null) {
                        if(seleccionLiga != selectedLiga || seleccionDivision != selectedDivision){
                            viewModel.obtenerJornadas(selectedLiga,seleccionDivision)

                            if(seleccionDivision != selectedDivision){
                               selectedDivision=seleccionDivision
                           }
                        }
                    }
                }

                override fun onNothingSelected(parentView: AdapterView<*>?) {

                }
            }
    }

    fun quitarEspacios(frase: String): String {
        return frase.replace("\\s+".toRegex(), "")
    }

}