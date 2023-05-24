package com.example.baixominholeague.ui.menu

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
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
        _binding = FragmentClasificacionBinding.inflate(inflater,container,false)
        val view = binding.root


        setupPlayers()
        setupTournaments()
        //setupPlayerList(jugadoresPuntuacionMap)
        mostrarClasificacion(jugadoresPuntuacionMap)

        return view
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
            Log.i("GAB","datos guardados")
        }

    }

    private fun setupPlayers() {

        CoroutineScope(Dispatchers.IO).launch {
            val jugadoresCollectionRef = db.collection("jugadores")

            jugadoresCollectionRef.get().addOnSuccessListener {
                val jugadores = mutableListOf<Jugador>()
                if(!setupExecuted){
                    for (document in it) {
                        val jugador = document.toObject(Jugador::class.java)
                        if(jugador!=null){

                            jugadores.add(jugador)

                        }
                    }
                    setupExecuted=true
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
                val jugadoresPuntuacion = tournamentSnapshot.data["jugadores"] as? List<HashMap<String, Serializable>>

                jugadoresPuntuacion?.forEach { jugadorDatos ->
                    val nombre = jugadorDatos["nombre"] as? String
                    val puntuacion = jugadorDatos["puntuacion"] as? String

                    Log.i("GAB",tournamentSnapshot.id+" puntuacion : "+jugadorDatos["nombre"].toString()+ jugadorDatos["puntuacion"])

                    if (nombre != null && puntuacion != null) {
                        val totalPuntuacion = jugadoresPuntuacionMap[nombre] ?: 0
                        jugadoresPuntuacionMap[nombre] = totalPuntuacion + puntuacion.toInt()

                    }
                }
            }

            for ((nombre, puntuacion) in jugadoresPuntuacionMap) {
                Log.i("GAB", "Nombre: $nombre, Puntuación Total: $puntuacion")
            }
        }
    }


    private fun setupPlayerList(jugadores: MutableMap<String, Int>) {
        val container = binding.playerContainer


        // Obtén la lista de jugadores (por ejemplo, desde Firestore)
        //val jugadoresRank = jugadores

//        for (i in jugadores.indices) {
//            val jugador = jugadores[i]
//            Log.i("GAB",jugadores[i].nombre)
//
//            // Inflar el diseño de elemento de lista
            val itemBinding = ItemPlayerBinding.inflate(layoutInflater, container, false)
//
//            // Obtener las referencias a las vistas del elemento
//            itemBinding.Rank.text = (i + 1).toString()
//            itemBinding.nombre.text = jugador.nombre
//            itemBinding.puntos.text = jugador.puntuacion.toString()
//
//            // Agregar el elemento al contenedor de vista

//        }
    jugadores.forEach{

        itemBinding.nombre.text = it.key
        itemBinding.puntos.text = it.value.toString()

        Log.i("GAB", "-->"+it.key + it.value.toString())

    }
        container.addView(itemBinding.root)

    }

    private fun mostrarClasificacion(jugadores: Map<String, Int>) {
        val container = binding.playerContainer

        container.removeAllViews() // Limpiar los elementos anteriores

        // Ordenar los jugadores por puntuación descendente
        val jugadoresOrdenados = jugadores.toList().sortedByDescending { (_, puntuacion) -> puntuacion }

        for (i in jugadoresOrdenados.indices) {
            val (nombre, puntuacion) = jugadoresOrdenados[i]

            // Inflar el diseño de elemento de la clasificación
            val itemBinding = ItemPlayerBinding.inflate(layoutInflater, container, false)

            // Obtener las referencias a las vistas del elemento
            itemBinding.Rank.text = (i + 1).toString()
            itemBinding.nombre.text = nombre
            itemBinding.puntos.text = puntuacion.toString()

            // Agregar el elemento al contenedor de vista
            container.addView(itemBinding.root)
        }
    }
}