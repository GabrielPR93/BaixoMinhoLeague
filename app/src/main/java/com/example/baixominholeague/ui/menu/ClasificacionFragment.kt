package com.example.baixominholeague.ui.menu

import android.os.Bundle
import android.util.Log
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

        torneoJugadoresMap.map {nombre ->
            Log.i("GABRI", "))_> "+nombre.key)
        }


        return view
    }



    private fun setupTable() {
        val headerRow = TableRow(requireContext())

        // Crear y configurar las celdas de la cabecera
        val positionHeaderCell = TextView(requireContext())
        positionHeaderCell.text = "Posición"
        positionHeaderCell.setPadding(5, 5, 5, 5)
        headerRow.addView(positionHeaderCell)

        val nombresTorneos = torneoJugadoresMap.keys.toList()

        val tournamentHeaders = nombresTorneos.map { nombreTorneo ->
            val headerCell = TextView(requireContext())
            headerCell.text = nombreTorneo
            headerCell.setPadding(5, 5, 5, 5)
            headerCell
        }
        tournamentHeaders.forEach { headerRow.addView(it) }

        val totalPointsHeaderCell = TextView(requireContext())
        totalPointsHeaderCell.text = "Puntos Totales"
        totalPointsHeaderCell.setPadding(5, 5, 5, 5)
        headerRow.addView(totalPointsHeaderCell)

        // Agregar la fila de cabecera a la tabla
        binding.tableLayout.addView(headerRow)

        for ((index, entry) in torneoJugadoresMap.entries.withIndex()) {
            val (nombre, puntajesTorneo) = entry
            val row = TableRow(requireContext())

            // Crear y configurar las celdas para cada jugador
            val playerNameCell = TextView(requireContext())
            playerNameCell.text = nombre
            playerNameCell.setPadding(5, 5, 5, 5)
            row.addView(playerNameCell)

            val tournamentPointsCells = nombresTorneos.map { nombreTorneo ->
                val pointsCell = TextView(requireContext())
                val puntosTorneo = puntajesTorneo[nombreTorneo] ?: 0
                pointsCell.text = puntosTorneo.toString()
                pointsCell.setPadding(5, 5, 5, 5)
                pointsCell
            }
            tournamentPointsCells.forEach { row.addView(it) }

            val totalPointsCell = TextView(requireContext())
            val puntuacionTotal = puntajesTorneo.values.sum()
            totalPointsCell.text = puntuacionTotal.toString()
            totalPointsCell.setPadding(5, 5, 5, 5)
            row.addView(totalPointsCell)

            // Agregar la fila a la tabla
            binding.tableLayout.addView(row)
        }
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
                    //setupPlayerList(jugadores)
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
            val jugadores0rdenados =
                jugadoresPuntuacionMap.toList().sortedByDescending { (_, puntuacion) -> puntuacion }
                    .toMap()
            for ((nombre, puntuacion) in jugadores0rdenados) {
                Log.i("GAB", "Nombre: -->> $nombre, Puntuación Total: $puntuacion")
            }
            jugadoresPuntuacionMap.clear()
            jugadoresPuntuacionMap.putAll(jugadores0rdenados)

        }
        setupTable()
    }

}