package com.example.baixominholeague.ui.menu.Perfil

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.marginStart
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.baixominholeague.R
import com.example.baixominholeague.databinding.ActivityAddJornadasBinding
import com.example.baixominholeague.ui.menu.Clasificacion.ClasificacionGeneral.ClasificacionViewModel
import kotlinx.coroutines.launch

class AddJornadasActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddJornadasBinding
    private var selectedLiga = ""
    private var selectedDivision = ""
    private var numJornada = 0
    private val viewModel: ClasificacionViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddJornadasBinding.inflate(layoutInflater)
        setContentView(binding.root)

        Spinner()
        initUIState()
        addJornada()
        btnBack()
    }

    private fun initUIState() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.listaJornadas.collect { jornadas ->
                    numJornada = jornadas.size
                    binding.tvNumeroJornadas.text = "${jornadas.size} Jornadas"
                }
            }
        }
    }

    private fun btnBack() {
        binding.btnBackPerfil.setOnClickListener { onBackPressed() }
    }
    @SuppressLint("ResourceAsColor")
    private fun crearEditText(hint: String, gravity: Int): EditText {
        val editText = EditText(this)
        editText.hint = hint
        editText.setBackgroundColor(android.R.color.transparent)
        val params = LinearLayout.LayoutParams(
            0,
            LinearLayout.LayoutParams.WRAP_CONTENT,
            2.0f
        )
        params.gravity = gravity
        editText.layoutParams = params
        editText.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16f)
        return editText
    }
    @SuppressLint("ResourceAsColor")
    private fun crearEquipoVsEquipoLayout(): LinearLayout {
        val linearLayout = LinearLayout(this)
            val layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        layoutParams.setMargins(25,0,0,0)

        linearLayout.layoutParams = layoutParams
        linearLayout.orientation = LinearLayout.HORIZONTAL

        // Crear EditText para el equipo 1
        val etEquipo1 = crearEditText("Equipo Local", Gravity.CENTER_VERTICAL)
        linearLayout.addView(etEquipo1)

        // Crear TextView "VS"
        val tvVs = TextView(this,null,R.style.textoStyleBold)
        tvVs.text = "VS"
        val paramsTvVs = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT,
            1.0f
        )
        paramsTvVs.gravity = Gravity.CENTER
        tvVs.layoutParams = paramsTvVs
        tvVs.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16f)
        linearLayout.addView(tvVs)

        // Crear EditText para el equipo 2
        val etEquipo2 = crearEditText("Equipo Visitante", Gravity.END)
        linearLayout.addView(etEquipo2)

        // Crear ImageButton
        val imageButton = ImageButton(this)
        imageButton.setImageResource(R.drawable.round_delete_forever_24)
        imageButton.setBackgroundColor(android.R.color.transparent)
        val layoutParamsImageButton = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        layoutParamsImageButton.gravity = Gravity.CENTER_VERTICAL
        imageButton.layoutParams = layoutParamsImageButton
        linearLayout.addView(imageButton)

        imageButton.setOnClickListener {
            val parent = linearLayout.parent as? LinearLayout
            parent?.removeView(linearLayout)
        }


        return linearLayout
    }
    @SuppressLint("ResourceAsColor")
    private fun addJornada() {
        val contenedorJornadas: LinearLayout = binding.linearLayoutJornadas

        binding.btnAddJornada.setOnClickListener {
            numJornada++

            val linearLayoutJornadas = LinearLayout(this)
            linearLayoutJornadas.layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            linearLayoutJornadas.orientation = LinearLayout.VERTICAL
            // Crear una nueva instancia de linearLayoutJornadas para cada jornada
            val linearLayoutTituloJornada = LinearLayout(this)
            linearLayoutTituloJornada.layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            linearLayoutTituloJornada.orientation = LinearLayout.HORIZONTAL
            // Crear TextView para el nombre de la jornada
            val tvNombreJornada = TextView(this,null,R.style.textoStyleBold)

            tvNombreJornada.text =
                "JORNADA $numJornada" // Puedes establecer un nombre predeterminado o solicitar uno al usuario

            // Puedes personalizar el TextView según tus necesidades
            val layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            layoutParams.setMargins(0, 70, 40, 20) // Márgenes opcionales entre los elementos
            tvNombreJornada.layoutParams = layoutParams
            tvNombreJornada.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 17f)

            // Agregar TextView al contenedor
            linearLayoutTituloJornada.addView(tvNombreJornada)

            val imageButtonAddMatch = ImageButton(this)
            imageButtonAddMatch.setBackgroundColor(android.R.color.transparent)
            imageButtonAddMatch.setImageResource(R.drawable.add) // Reemplaza "tu_icono" con el nombre de tu recurso de imagen

            // Puedes personalizar el ImageButton según tus necesidades
            val layoutParamsImageButtonAddMatch = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            layoutParamsImageButtonAddMatch.setMargins(0,30,10,0)
            imageButtonAddMatch.layoutParams = layoutParamsImageButtonAddMatch
            linearLayoutTituloJornada.addView(imageButtonAddMatch)


            val linearLayoutHorizontal = crearEquipoVsEquipoLayout()
            linearLayoutJornadas.addView(linearLayoutTituloJornada)
            linearLayoutJornadas.addView(linearLayoutHorizontal)

            //contenedorJornadas.addView(linearLayoutTituloJornada)
            contenedorJornadas.addView(linearLayoutJornadas)

            imageButtonAddMatch.setOnClickListener {
                // Crear una nueva instancia de linearLayoutHorizontal para cada partido
                val linearLayoutNewMatch = crearEquipoVsEquipoLayout()
                linearLayoutJornadas.addView(linearLayoutNewMatch)
            }
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
                    binding.linearLayoutJornadas.removeAllViews()

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
                    viewModel.obtenerJornadas(selectedLiga, selectedDivision)

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
                    binding.linearLayoutJornadas.removeAllViews()
                    val seleccionLiga = binding.spinnerAddLigaAddJornada.selectedItem?.toString()
                    val seleccionDivision = divisiones[position]
                    Log.i(
                        "GAbri",
                        "SPINEER 1: $selectedLiga y $selectedDivision   SPINNER 2: $seleccionLiga y $seleccionDivision"
                    )

                    if (seleccionLiga != null && seleccionDivision != null) {
                        if (seleccionLiga != selectedLiga || seleccionDivision != selectedDivision) {
                            viewModel.obtenerJornadas(seleccionLiga, seleccionDivision)
                            Log.i("GAbri", "ENTROOOOOO")
                            if (seleccionDivision != selectedDivision) {
                                selectedDivision = seleccionDivision
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