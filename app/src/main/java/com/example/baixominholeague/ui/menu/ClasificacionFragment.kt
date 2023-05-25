package com.example.baixominholeague.ui.menu

import android.graphics.Color
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
import com.example.baixominholeague.R
import com.example.baixominholeague.data.Jugador
import com.example.baixominholeague.databinding.FragmentClasificacionBinding
import com.example.baixominholeague.databinding.ItemPlayerBinding
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.Serializable

private var _binding: FragmentClasificacionBinding? = null
private val binding get() = _binding!!
private var setupExecuted = false
private val db = FirebaseFirestore.getInstance()
private val jugadoresPuntuacionMap = mutableMapOf<String, Int>()
private val torneoJugadoresMap = hashMapOf<String, HashMap<String, Int>>()


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
        mostrarTorneosEnTabla()
        return view
    }

    private fun mostrarTorneosEnTabla() {
        // Obtener la referencia al TableLayout en tu layout
        val tableLayout = binding.tableLayout

        // Agregar la fila de cabecera
        val cabeceraRow = TableRow(requireContext())
        cabeceraRow.addView(crearTextViewCabecera(" "))
        cabeceraRow.addView(crearTextViewCabecera("  NOMBRE"))

        // Agregar las celdas para los nombres de torneos
        for ((nombreTorneo, _) in torneoJugadoresMap) {
            cabeceraRow.addView(crearTextViewCabecera(nombreTorneo))
        }
        cabeceraRow.addView(crearTextViewCabecera("TOTAL"))
        tableLayout.addView(cabeceraRow)
        val jugadoresOrdenados = jugadoresPuntuacionMap.toList().sortedByDescending { (_, puntuacion) -> puntuacion }
//        for ((nombre,jugadoresMAp) in torneoJugadoresMap){
//            for((nombre,puntuacion) in jugadoresMAp){
//                Log.i("GAB","LAYOUT: "+nombre+puntuacion)
//            }
//        }
        // Recorrer los jugadores y agregar las filas correspondientes
        for ((index, jugador) in jugadoresOrdenados.withIndex()) {
            val fila = TableRow(requireContext())

            // Agregar la celda para la posición
            val posicionCell = crearTextViewCelda((index + 1).toString()+"º")
            fila.addView(posicionCell)

            fila.addView(crearTextViewCeldaNombre(jugador.first))

            // Agregar las celdas para las puntuaciones en cada torneo
            for ((nombreTorneo, jugadoresMap) in torneoJugadoresMap) {
                val puntuacion = jugadoresMap[jugador.first] ?: 0
                fila.addView(crearTextViewCelda(puntuacion.toString()))
            }

         fila.addView(crearTextViewCelda(jugador.second.toString()))

            tableLayout.addView(fila)
        }
    }

    private fun crearTextViewCabecera(texto: String): TextView {
        val textView = TextView(requireContext())
        textView.text = texto
        textView.setBackgroundColor(Color.LTGRAY)
        textView.setPadding(10, 10, 10, 10)
        textView.gravity = Gravity.START // Alineación del texto al centro
        return textView
    }

    private fun crearTextViewCelda(texto: String): TextView {
        val textView = TextView(requireContext())
        textView.text = texto
        textView.setPadding(10, 10, 10, 10)
        textView.gravity = Gravity.CENTER
        return textView
    }  private fun crearTextViewCeldaNombre(texto: String): TextView {
        val textView = TextView(requireContext())
        textView.text = texto
        textView.setPadding(10, 10, 10, 10)
        textView.gravity = Gravity.START
        return textView
    }


    private fun saveData(jugadores: MutableList<Jugador>) {
        val jugadoresPuntuacion = mutableListOf<HashMap<String, Serializable>>()

        for (jugador in jugadores) {
            val datosJugador = hashMapOf<String, Serializable>(
                "nombre" to jugador.nombre,
                "puntuacion" to "10"
            )
            jugadoresPuntuacion.add(datosJugador)
        }
        db.collection("clasificacionMovimiento").document("TORNEO 2").set(
            hashMapOf("jugadores" to jugadoresPuntuacion)
        ).addOnSuccessListener {
            Log.i("GAB", "datos guardados")
        }

    }

    private fun setupPlayers() {

        CoroutineScope(Dispatchers.IO).launch {
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
    }

    private fun setupTournaments() {
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



        }
        //setupTable()

        }
    }

}