package com.example.baixominholeague.ui.menu

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.Typeface
import android.opengl.Visibility
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TableRow
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import com.example.baixominholeague.R
import com.example.baixominholeague.data.Jugador
import com.example.baixominholeague.databinding.FragmentClasificacionBinding
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.Serializable

private var _binding: FragmentClasificacionBinding? = null
private val binding get() = _binding!!
private var setupExecuted = false
private val db = FirebaseFirestore.getInstance()

class ClasificacionFragment : Fragment() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {

        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentClasificacionBinding.inflate(inflater, container, false)
        val view = binding.root

        setupPlayers()
        setupTournaments()
        //savePosition()

        return view
    }

    //REVISAR QUE SOLO GUARDE 1 VEZ y DIFERENCIA DE NOMBRE DE DOCUMENTO
    private fun savePosition(position:Int,name:String) {
        val campoNuevo = hashMapOf(
            "posicion" to position.toString()
        )

        val referencia = db.collection("jugadores")
            .document(name)

        referencia.update(campoNuevo as Map<String, Any>)
            .addOnSuccessListener {
                Log.i("Gabriel","Campo agregado correctamente")
            }
            .addOnFailureListener { exception ->
                Log.i("Gabriel","Error al agregar el campo")
            }

    }
    //TODO modificar lo de guardar la posicion
    private fun mostrarTorneosEnTabla(jugadoresPuntuacionMap: MutableMap<String, Int>, torneoJugadoresMap: MutableMap<String, HashMap<String, Int>>,) {
        // Obtener la referencia al TableLayout en tu layout
        val tableLayout = binding.tableLayout

        // Agregar la fila de cabecera
        val cabeceraRow = TableRow(requireContext())
        cabeceraRow.addView(crearTextViewCabecera(""))
        cabeceraRow.addView(crearTextViewCabecera("NOMBRE"))
        cabeceraRow.addView(crearTextViewCabecera("TOTAL"))

        // Agregar las celdas para los nombres de torneos
        for ((nombreTorneo, _) in torneoJugadoresMap) {
            cabeceraRow.addView(crearTextViewCabecera(nombreTorneo))
        }

        tableLayout.addView(cabeceraRow)
        val jugadoresOrdenados = jugadoresPuntuacionMap.toList().sortedByDescending { (_, puntuacion) -> puntuacion }

        // Recorrer los jugadores y agregar las filas correspondientes
        for ((index, jugador) in jugadoresOrdenados.withIndex()) {
            val fila = TableRow(requireContext())

            // Agregar la celda para la posición
            val posicionCell = crearTextViewBold((index + 1).toString()+"º")

            if (index < 3) {
                when (index) {
                    0 -> fila.setBackgroundColor(ContextCompat.getColor(requireContext(),R.color.oro))
                    1 -> fila.setBackgroundColor(ContextCompat.getColor(requireContext(),R.color.plata))
                    2 -> fila.setBackgroundColor(ContextCompat.getColor(requireContext(),R.color.bronce))
                }
            }

            fila.addView(posicionCell)

            fila.addView(crearTextViewCeldaNombre(jugador.first))

            fila.addView(crearTextViewBold(jugador.second.toString()))

            // Agregar las celdas para las puntuaciones en cada torneo
            for ((nombreTorneo, jugadoresMap) in torneoJugadoresMap) {
                val puntuacion = jugadoresMap[jugador.first] ?: 0
                fila.addView(crearTextViewCelda(puntuacion.toString()))
            }

            tableLayout.addView(fila)

            var nameSave = jugador.first.filter { !it.isWhitespace() }
            savePosition((index+1),nameSave.trim())
        }
    }


    private fun saveData(jugadores: MutableList<Jugador>) {
        //Guarda la puntuacion de todos los jugadores

        val jugadoresPuntuacion = mutableListOf<HashMap<String, Serializable>>()

        for (jugador in jugadores) {
            val datosJugador = hashMapOf<String, Serializable>(
                "nombre" to jugador.nombre,
                "puntuacion" to "2"
            )
            jugadoresPuntuacion.add(datosJugador)
        }
        db.collection("clasificacionMovimiento").document("TORNEO 4").set(
            hashMapOf("jugadores" to jugadoresPuntuacion)
        ).addOnSuccessListener {
            Log.i("GAB", "datos guardados")
        }
    }

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

    private fun setupTournaments() {
        val jugadoresPuntuacionMap = mutableMapOf<String, Int>()
        val torneoJugadoresMap = hashMapOf<String, HashMap<String, Int>>()
        binding.ProgresBarClasi.isVisible=true

        val tournamentsCollectionRef = db.collection("clasificacionMovimiento")

        tournamentsCollectionRef.get().addOnSuccessListener { tournamentSnapshots ->

            for (tournamentSnapshot in tournamentSnapshots) {
                val jugadoresPuntuacion =
                    tournamentSnapshot.data["jugadores"] as? List<HashMap<String, Serializable>>

                jugadoresPuntuacion?.forEach { jugadorDatos ->
                    val nombre = jugadorDatos["nombre"] as? String
                    val puntuacion = jugadorDatos["puntuacion"] as? String

                    //Log.i("GAB",tournamentSnapshot.id+" puntuacion : "+jugadorDatos["nombre"].toString()+ jugadorDatos["puntuacion"])

                    if (nombre != null && puntuacion != null) {
                        val totalPuntuacion = jugadoresPuntuacionMap[nombre] ?: 0
                        jugadoresPuntuacionMap[nombre] = totalPuntuacion + puntuacion.toInt()

                        //torneoJugadoresMap todos los datos
                        val totalPuntuacionTorneos =
                            torneoJugadoresMap.getOrDefault(tournamentSnapshot.id, hashMapOf())
                        val jugadorPuntuacionMap = totalPuntuacionTorneos.getOrDefault(nombre, 0)

                        totalPuntuacionTorneos[nombre] = jugadorPuntuacionMap + puntuacion.toInt()
                        torneoJugadoresMap[tournamentSnapshot.id] = totalPuntuacionTorneos

                    }
//                    for ((torneoId, jugadoresMap) in torneoJugadoresMap) {
//                        println("ID del torneo: $torneoId")
//                        for ((nombre, puntuacion) in jugadoresMap) {
//                            println("Jugador:-> $nombre, Puntuación: $puntuacion")
//                        }
//                        println()
//                    }
                }
            }
//
//            for ((nombre, puntuacion) in jugadoresPuntuacionMap) {
//                Log.i("GAB", "Nombre: $nombre, Puntuación Total: $puntuacion")
//            }
              var jugadores0rdenados =
                jugadoresPuntuacionMap.toList().sortedByDescending { (_, puntuacion) -> puntuacion }
                    .toMap()
            for ((nombre, puntuacion) in jugadores0rdenados) {
                Log.i("GAB", "Nombre: --->> $nombre, Puntuación Total: $puntuacion")
            }

//            jugadoresPuntuacionMap.clear()
//            jugadoresPuntuacionMap.putAll(jugadores0rdenados)

                mostrarTorneosEnTabla(jugadoresPuntuacionMap,torneoJugadoresMap)
                binding.ProgresBarClasi.isVisible=false

            }
        }
    private fun crearTextViewCabecera(texto: String): TextView {
        val textView = TextView(requireContext())
        textView.text = texto
        textView.typeface = Typeface.DEFAULT_BOLD
        textView.setTextColor(Color.WHITE)
        textView.setBackgroundColor(ContextCompat.getColor(requireContext(),R.color.blue))
        textView.setPadding(30, 20, 30, 20)
        textView.gravity = Gravity.START // Alineación del texto al centro
        return textView
    }

    private fun crearTextViewCelda(texto: String): TextView {
        val textView = TextView(requireContext())
        textView.text = texto
        textView.setPadding(20, 20, 20, 20)
        textView.gravity = Gravity.CENTER
        return textView
    }
    private fun crearTextViewBold(texto: String): TextView {
        val textView = TextView(requireContext())
        textView.text = texto
        textView.typeface = Typeface.DEFAULT_BOLD
        textView.setPadding(20, 20, 40, 20)
        textView.gravity = Gravity.CENTER
        return textView
    }
    private fun crearTextViewCeldaNombre(texto: String): TextView {
        val textView = TextView(requireContext())
        textView.text = texto
        textView.setPadding(20, 0, 20, 20)
        textView.gravity = Gravity.START
        return textView
    }
    }
