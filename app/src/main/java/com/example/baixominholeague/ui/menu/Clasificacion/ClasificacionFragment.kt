package com.example.baixominholeague.ui.menu.Clasificacion

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.viewpager2.widget.ViewPager2
import com.example.baixominholeague.R
import com.example.baixominholeague.data.Jugador
import com.example.baixominholeague.databinding.FragmentClasificacionBinding
import com.example.baixominholeague.ui.menu.Inicio.EventosFragment
import com.example.baixominholeague.ui.menu.Inicio.NovedadesFragment
import com.example.baixominholeague.ui.menu.Inicio.ViewPagerAdapter
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import com.example.baixominholeague.data.Liga
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect

@AndroidEntryPoint
class ClasificacionFragment : Fragment() {

    private var _binding: FragmentClasificacionBinding? = null
    private val binding get() = _binding!!
    private var setupExecuted = false
    private val db = FirebaseFirestore.getInstance()
    private var adapter : ViewPagerAdapter? = null
    private val listaLigas: MutableList<Liga> = mutableListOf()
    private var isFirstCreation = true

    private val ligaViewModel by viewModels<ClasificacionViewModel>()
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

//        if (isFirstCreation) {
//            lifecycleScope.launch(Dispatchers.Main) {
//                try {
//                    obtenerDatosLigas()
//                    Log.i("Gabri", "Tamaño de listaLigases: ${listaLigas.size}")
//                } catch (e: Exception) {
//                    Log.e("Gabri", "Error en la corrutina: $e")
//                }
//            }
//            isFirstCreation = false
//        }
        initUI()
    }

    private fun initUI() {
        initUIState()
    }

    private fun initUIState() {
//        lifecycleScope.launch {
//            repeatOnLifecycle(Lifecycle.State.STARTED){
//                ligaViewModel.listaLigas.collect{listaLigas ->
//                    Spinners(listaLigas)
//                }
//            }
//        }
        Spinners()
        viewPager()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentClasificacionBinding.inflate(inflater, container, false)
        val view = binding.root

        setupPlayers()



        return view
    }
    fun viewPager(){
        val viewPager: ViewPager2 = binding.viewPagerClasificacion
        val tabs: TabLayout = binding.tabsClasificacion

        val fragments = listOf(ClasificacionGeneralFragment(), NovedadesFragment())
        adapter = ViewPagerAdapter(requireActivity(), fragments)
        viewPager.adapter = adapter

        TabLayoutMediator(tabs, viewPager) { tab, position ->
            when (position) {
                0 -> tab.text = getString(R.string.clasificacion)
                1 -> tab.text = getString(R.string.Jornadas)
            }
        }.attach()
        binding.viewPagerClasificacion.currentItem=0
    }
    fun quitarEspacios(frase: String): String {
        return frase.replace("\\s+".toRegex(), "")
    }
    fun Spinners() {

        val nombreLiga = resources.getStringArray(R.array.liga_options).toList()
        val divisiones = mutableListOf<String>()

        // Crear adaptadores para los Spinners
        val adapterSpinner1 = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, nombreLiga)
        val adapterSpinner2 = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, divisiones)

        // Configurar los adaptadores y el comportamiento de los Spinners
        adapterSpinner1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        adapterSpinner2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        binding.spinnerLiga.adapter = adapterSpinner1
        binding.spinnerDivision.adapter = adapterSpinner2

        binding.spinnerLiga.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parentView: AdapterView<*>?, selectedItemView: View?, position: Int, id: Long) {
                divisiones.clear()
                val opcionSeleccionada = nombreLiga[position]

                val divisionOptionsResourceId = resources.getIdentifier("division_options_${quitarEspacios(opcionSeleccionada)}", "array", requireContext().packageName)
                if (divisionOptionsResourceId != 0) {
                    val divisionOptions = resources.getStringArray(divisionOptionsResourceId).toList()
                    divisiones.addAll(divisionOptions)
                }
                // Notificar al adaptador del segundo Spinner sobre el cambio en los datos
                adapterSpinner2.notifyDataSetChanged()
            }

            override fun onNothingSelected(parentView: AdapterView<*>?) {
                // manejar la situación donde no se ha seleccionado nada
            }
        }
    }

    //REVISAR QUE SOLO GUARDE 1 VEZ y DIFERENCIA DE NOMBRE DE DOCUMENTO
//    private fun savePosition(position:Int,name:String) {
//        val campoNuevo = hashMapOf(
//            "posicion" to position.toString()
//        )
//
//
//        val referencia = db.collection("jugadores")
//            .document(name)
//
//        referencia.update(campoNuevo as Map<String, Any>)
//            .addOnSuccessListener {
//                Log.i("Gabriel", "Campo agregado correctamente")
//            }
//            .addOnFailureListener { exception ->
//                Log.i("Gabriel", "Error al agregar el campo")
//            }
//
//    }

//    private fun mostrarTorneosEnTabla(jugadoresPuntuacionMap: MutableMap<String, Int>, torneoJugadoresMap: MutableMap<String, HashMap<String, Int>>) {
//        val tableLayout = binding.tableLayout
//
//        // Agregar la fila de cabecera
//        val cabeceraRow = TableRow(requireContext())
//        cabeceraRow.addView(crearTextViewCabecera(""))
//        cabeceraRow.addView(crearTextViewCabecera(""))
//        cabeceraRow.addView(crearTextViewCabecera("NOMBRE"))
//        cabeceraRow.addView(crearTextViewCabecera("TOTAL"))
//
//        // Agregar las celdas para los nombres de torneos en la cabecera
//        for ((nombreTorneo, _) in torneoJugadoresMap) {
//            cabeceraRow.addView(crearTextViewCabecera(nombreTorneo))
//
//        }
//        tableLayout.addView(cabeceraRow)
//
//        // Recorrer los jugadores y agregar las filas correspondientes
//        for ((index, jugador) in jugadoresPuntuacionMap.toList().sortedByDescending { (_, puntuacion) -> puntuacion }.withIndex()) {
//            val fila = TableRow(requireContext())
//
//            val posicionCell2 = crearTextViewBold("")
//            fila.addView(posicionCell2)
//            // Agregar la celda para la posición
//            val posicionCell = crearTextViewBold((index + 1).toString() + "º")
//
//
//            when (index) {
//                0 -> posicionCell2.setBackgroundColor(
//                    ContextCompat.getColor(
//                        requireContext(),
//                        R.color.oro
//                    )
//                )
//                1 -> posicionCell2.setBackgroundColor(
//                    ContextCompat.getColor(
//                        requireContext(),
//                        R.color.plata
//                    )
//                )
//                2 -> posicionCell2.setBackgroundColor(
//                    ContextCompat.getColor(
//                        requireContext(),
//                        R.color.bronce
//                    )
//                )
//                else -> posicionCell2.setBackgroundColor(
//                    ContextCompat.getColor(
//                        requireContext(),
//                        R.color.blue2
//                    )
//                )
//            }
//
//            fila.addView(posicionCell)
//
//            fila.addView(crearTextViewCeldaNombre(jugador.first))
//
//            fila.addView(crearTextViewBold(jugador.second.toString()))
//
//            // Agregar las celdas para las puntuaciones en cada torneo
//            for ((nombreTorneo, jugadoresMap) in torneoJugadoresMap) {
//                val puntuacion = jugadoresMap[jugador.first] ?: 0
//                fila.addView(crearTextViewCelda(puntuacion.toString()))
//            }
//
//            tableLayout.addView(fila)
//        }
//
//    }
//

//    private fun saveData(jugadores: MutableList<Jugador>) {
//        //Guarda la puntuacion de todos los jugadores
//
//        val jugadoresPuntuacion = mutableListOf<HashMap<String, Serializable>>()
//
//        for (jugador in jugadores) {
//            val datosJugador = hashMapOf<String, Serializable>(
//                "nombre" to jugador.nombre,
//                "puntuacion" to "2"
//            )
//            jugadoresPuntuacion.add(datosJugador)
//        }
//        db.collection("clasificacionMovimiento").document("TORNEO 4").set(
//            hashMapOf("jugadores" to jugadoresPuntuacion)
//        ).addOnSuccessListener {
//            Log.i("GAB", "datos guardados")
//        }
//    }

    private fun setupPlayers() {
        //Obtiene todos los jugadores
            val jugadoresCollectionRef = db.collection("jugadores")

            jugadoresCollectionRef.get().addOnSuccessListener {
                val jugadores = mutableListOf<Jugador>()
                if (!setupExecuted) {
                    for (document in it) {
                        val jugador = document.toObject(Jugador::class.java)
                        if (jugador != null) {

                            jugadores.add(jugador)
                        }
                    }
                    setupExecuted = true
                    //saveData(jugadores)
                }
            }
    }

//    private fun setupTournaments() {
//        val jugadoresPuntuacionMap = mutableMapOf<String, Int>()
//        val torneoJugadoresMap = hashMapOf<String, HashMap<String, Int>>()
//        binding.ProgresBarClasi.isVisible=true
//
//        val tournamentsCollectionRef = db.collection("clasificacionMovimiento")
//
//        tournamentsCollectionRef.get().addOnSuccessListener { tournamentSnapshots ->
//
//            for (tournamentSnapshot in tournamentSnapshots) {
//                val jugadoresPuntuacion =
//                    tournamentSnapshot.data["jugadores"] as? List<HashMap<String, Serializable>>
//
//                jugadoresPuntuacion?.forEach { jugadorDatos ->
//                    val nombre = jugadorDatos["nombre"] as? String
//                    val puntuacion = jugadorDatos["puntuacion"] as? String
//
//                    if (nombre != null && puntuacion != null) {
//                        val totalPuntuacion = jugadoresPuntuacionMap[nombre] ?: 0
//                        jugadoresPuntuacionMap[nombre] = totalPuntuacion + puntuacion.toInt()
//
//                        //torneoJugadoresMap todos los datos
//                        val totalPuntuacionTorneos =
//                            torneoJugadoresMap.getOrDefault(tournamentSnapshot.id, hashMapOf())
//                        val jugadorPuntuacionMap = totalPuntuacionTorneos.getOrDefault(nombre, 0)
//
//                        totalPuntuacionTorneos[nombre] = jugadorPuntuacionMap + puntuacion.toInt()
//                        torneoJugadoresMap[tournamentSnapshot.id] = totalPuntuacionTorneos
//
//                    }
////                    for ((torneoId, jugadoresMap) in torneoJugadoresMap) {
////                        println("ID del torneo: $torneoId")
////                        for ((nombre, puntuacion) in jugadoresMap) {
////                            println("Jugador:-> $nombre, Puntuación: $puntuacion")
////                        }
////                        println()
////                    }
//                }
//            }
////
////            for ((nombre, puntuacion) in jugadoresPuntuacionMap) {
////                Log.i("GAB", "Nombre: $nombre, Puntuación Total: $puntuacion")
////            }
//              var jugadores0rdenados =
//                jugadoresPuntuacionMap.toList().sortedByDescending { (_, puntuacion) -> puntuacion }
//                    .toMap()
//            for ((nombre, puntuacion) in jugadores0rdenados) {
//                Log.i("GAB", "Nombre: --->> $nombre, Puntuación Total: $puntuacion")
//            }
//
////            jugadoresPuntuacionMap.clear()
////            jugadoresPuntuacionMap.putAll(jugadores0rdenados)
//
//                mostrarTorneosEnTabla(jugadoresPuntuacionMap,torneoJugadoresMap)
//                binding.ProgresBarClasi.isVisible=false
//
//            }
//        }
//    private fun crearTextViewCabecera(texto: String): TextView {
//        val textView = TextView(requireContext())
//        textView.text = texto
//        textView.typeface = Typeface.DEFAULT_BOLD
//        textView.setTextColor(Color.WHITE)
//        textView.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.blue))
//        textView.setPadding(30, 20, 30, 20)
//        textView.gravity = Gravity.START // Alineación del texto al centro
//        return textView
//    }
//
//    private fun crearTextViewCelda(texto: String): TextView {
//        val textView = TextView(requireContext())
//        textView.text = texto
//        textView.setPadding(20, 20, 20, 20)
//        textView.gravity = Gravity.CENTER
//        return textView
//    }
//    private fun crearTextViewBold(texto: String): TextView {
//        val textView = TextView(requireContext())
//        textView.text = texto
//        textView.typeface = Typeface.DEFAULT_BOLD
//        textView.setPadding(10, 20, 20, 20)
//        textView.gravity = Gravity.CENTER
//        return textView
//    }
//    private fun crearTextViewCeldaNombre(texto: String): TextView {
//        val textView = TextView(requireContext())
//        textView.text = texto
//        textView.setPadding(20, 0, 20, 20)
//        textView.gravity = Gravity.START
//        return textView
//    }
    }