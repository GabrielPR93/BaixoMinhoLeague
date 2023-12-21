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
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.baixominholeague.R
import com.example.baixominholeague.data.Jornada
import com.example.baixominholeague.data.Partido
import com.example.baixominholeague.databinding.ActivityAddJornadasBinding
import com.example.baixominholeague.ui.menu.Clasificacion.ClasificacionGeneral.ClasificacionViewModel
import com.google.android.material.snackbar.Snackbar
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
        btnSaveMatchs()
    }

    private fun btnSaveMatchs() {
        binding.btnSaveMatchs.setOnClickListener {
            saveMatchsUI()
        }
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

        val etEquipo1 = crearEditText("Equipo Local", Gravity.CENTER_VERTICAL)
        linearLayout.addView(etEquipo1)

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

        val etEquipo2 = crearEditText("Equipo Visitante", Gravity.END)
        linearLayout.addView(etEquipo2)

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

            val linearLayoutTituloJornada = LinearLayout(this)
            linearLayoutTituloJornada.layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            linearLayoutTituloJornada.orientation = LinearLayout.HORIZONTAL

            val tvNombreJornada = TextView(this,null,R.style.textoStyleBold)

            tvNombreJornada.text =
                "JORNADA $numJornada"

            val layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            layoutParams.setMargins(0, 70, 40, 20)
            tvNombreJornada.layoutParams = layoutParams
            tvNombreJornada.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 17f)

            linearLayoutTituloJornada.addView(tvNombreJornada)

            val imageButtonAddMatch = ImageButton(this)
            imageButtonAddMatch.setBackgroundColor(android.R.color.transparent)
            imageButtonAddMatch.setImageResource(R.drawable.add)

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

            contenedorJornadas.addView(linearLayoutJornadas)

            imageButtonAddMatch.setOnClickListener {

                val linearLayoutNewMatch = crearEquipoVsEquipoLayout()
                linearLayoutJornadas.addView(linearLayoutNewMatch)
            }


        }
    }

    private fun saveMatchsUI() {
        val contenedorJornadas: LinearLayout = binding.linearLayoutJornadas
        val listaJornadas = mutableListOf<Jornada>()

        for (i in 0 until contenedorJornadas.childCount) {

            val jornadaLayout = contenedorJornadas.getChildAt(i) as? LinearLayout
            if (jornadaLayout != null) {

                val nombreJornadaTextView =
                    (jornadaLayout.getChildAt(0) as? LinearLayout)?.getChildAt(0) as? TextView
                val nombreJornada = nombreJornadaTextView?.text.toString()

                if (!nombreJornada.isNullOrEmpty()) {
                    val listaPartidos = mutableListOf<Partido>()

                    for (count in 1 until jornadaLayout.childCount) {
                        val equipoLayout = jornadaLayout.getChildAt(count) as? LinearLayout

                        if (equipoLayout != null) {
                            val nombreEquipoLocalTextView =
                                (jornadaLayout.getChildAt(count) as? LinearLayout)?.getChildAt(0) as? EditText
                            val nombreEquipoLocal = nombreEquipoLocalTextView?.text.toString()

                            val nombreEquipoVisitanteTextView =
                                (jornadaLayout.getChildAt(count) as? LinearLayout)?.getChildAt(2) as? EditText
                            val nombreEquipoVisitante =
                                nombreEquipoVisitanteTextView?.text.toString()

                            if (!nombreEquipoLocal.isNullOrEmpty() && !nombreEquipoVisitante.isNullOrEmpty()) {
                                val partido =
                                    Partido(nombreEquipoLocal, nombreEquipoVisitante, "", "")
                                listaPartidos.add(partido)
                            }
                        }
                    }

                    val jornada = Jornada(nombreJornada, "", listaPartidos.toList())
                    listaJornadas.add(jornada)

                }
            }
        }

        if (selectedLiga != null && selectedDivision != null) {
            viewModel.saveMatchs(selectedLiga, selectedDivision, listaJornadas)
            viewModel.onSaveComplete = { success ->
                if (success) {
                    showSaveSuccessNotification()
                    contenedorJornadas.removeAllViews()
                } else {
                    showSaveErrorNotification()
                }
            }
        }

    }

    private fun showSaveErrorNotification() {
        Snackbar.make(
            binding.root,
            "Error al guardar las jornadas",
            Snackbar.LENGTH_SHORT
        ).show()
    }

    private fun showSaveSuccessNotification() {
        Snackbar.make(
            binding.root,
            "Jornadas guardadas correctamente",
            Snackbar.LENGTH_SHORT
        ).show()
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
                    Log.i(
                        "GAbri",
                        "SPINEER 1*: $selectedLiga y $selectedDivision"
                    )

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