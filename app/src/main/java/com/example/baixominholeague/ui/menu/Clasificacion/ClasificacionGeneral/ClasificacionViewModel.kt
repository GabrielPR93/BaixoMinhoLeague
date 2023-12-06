package com.example.baixominholeague.ui.menu.Clasificacion.ClasificacionGeneral

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.baixominholeague.data.Equipo
import com.example.baixominholeague.data.Jugador
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

@HiltViewModel
class ClasificacionViewModel @Inject constructor() : ViewModel() {
    private val _listaEquipos = MutableStateFlow<List<Equipo>>(emptyList())
    val listaEquipos: MutableStateFlow<List<Equipo>> = _listaEquipos

    private val db = FirebaseFirestore.getInstance()
    val nombreLiga = "Liga do BaixoMiño"
    val nombreDivision = "1ª División"

    init {
        obtenerDatos(nombreLiga,nombreDivision)
    }
    fun obtenerDatos(liga: String, division: String) {
        val equipoRef = db.collection("equipos").document(liga)
        viewModelScope.launch {
            try {
                val documentSnapshot = equipoRef.get().await()
                if (documentSnapshot.exists()) {
                    val equiposArray = documentSnapshot.get("equipos") as? ArrayList<Map<String, Any>>
                    if (equiposArray != null) {

                        val equipos = equiposArray.map { equipoMap ->
                            val partidos = equipoMap["partidosTotales"] as Map<String, Any> ?: emptyMap()
                            val partidosTotales = equipoMap["partidosTotales"] as? Map<String, Long> ?: emptyMap()
                            val jugadoresArray = equipoMap["jugadores"] as? ArrayList<Map<String, Any>>
                            val jugadores = jugadoresArray?.map { jugadorMap ->
                                Jugador(
                                    id = (jugadorMap["id"] as Long).toInt(),
                                    nombre = jugadorMap["nombre"] as String,
                                    correo = jugadorMap["correo"] as String
                                )
                            } ?: emptyList()

                            Equipo(
                                id = (equipoMap["id"] as Long).toInt(),
                                nombreEquipo = equipoMap["nombreEquipo"] as String,
                                division = equipoMap["division"] as String,
                                jugadores = jugadores,
                                partidosTotales = mapOf(
                                    "puntos" to (partidosTotales["puntos"] ?: 0).toInt(),
                                    "partidosJugados" to (partidosTotales["partidosJugados"] ?: 0).toInt(),
                                    "partidosGanados" to (partidosTotales["partidosGanados"] ?: 0).toInt(),
                                    "partidosEmpatados" to (partidosTotales["partidosEmpatados"] ?: 0).toInt(),
                                    "partidosPerdidos" to (partidosTotales["partidosPerdidos"] ?: 0).toInt()
                                )

                            )
                        }

                        // Filtrar fuera del bloque map
                        val equiposFiltrados = equipos.filter { it.division == division }

                        _listaEquipos.value = equiposFiltrados.sortedByDescending { it.partidosTotales["puntos"] }
                        Log.i("GAbri","EQUIPOS: $equipos")
                    } else {
                        println("No se encontró la matriz 'equipos' en el documento")
                    }
                } else {
                    println("No existe el documento")
                }
            } catch (exception: Exception) {
                println("Error obteniendo documento: $exception")
            }
        }
    }

}
