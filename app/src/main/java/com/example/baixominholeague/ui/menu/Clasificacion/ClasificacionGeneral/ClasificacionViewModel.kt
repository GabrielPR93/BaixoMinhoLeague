package com.example.baixominholeague.ui.menu.Clasificacion.ClasificacionGeneral

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.baixominholeague.data.Equipo
import com.example.baixominholeague.data.Jornada
import com.example.baixominholeague.data.Jugador
import com.example.baixominholeague.data.Partido
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

    private val _listaJornadas = MutableStateFlow<List<Jornada>>(emptyList())
    val listaJornadas: MutableStateFlow<List<Jornada>> = _listaJornadas

    private var indiceJornadaActual = 0
    var onSaveComplete: ((Boolean) -> Unit)? = null

    private val db = FirebaseFirestore.getInstance()
    val nombreLiga = "Liga do BaixoMiño"
    val nombreDivision = "1ª División"

    init {
        obtenerDatos(nombreLiga, nombreDivision)
        obtenerJornadas(nombreLiga, nombreDivision)
    }

    fun obtenerDatos(liga: String, division: String) {
        val equipoRef = db.collection("equipos").document(liga)
        viewModelScope.launch {
            try {
                val documentSnapshot = equipoRef.get().await()
                if (documentSnapshot.exists()) {
                    val equiposArray =
                        documentSnapshot.get("equipos") as? ArrayList<Map<String, Any>>
                    if (equiposArray != null) {

                        val equipos = equiposArray.map { equipoMap ->
                            val partidos =
                                equipoMap["partidosTotales"] as Map<String, Any> ?: emptyMap()
                            val partidosTotales =
                                equipoMap["partidosTotales"] as? Map<String, Long> ?: emptyMap()
                            val jugadoresArray =
                                equipoMap["jugadores"] as? ArrayList<Map<String, Any>>
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
                                    "partidosJugados" to (partidosTotales["partidosJugados"]
                                        ?: 0).toInt(),
                                    "partidosGanados" to (partidosTotales["partidosGanados"]
                                        ?: 0).toInt(),
                                    "partidosEmpatados" to (partidosTotales["partidosEmpatados"]
                                        ?: 0).toInt(),
                                    "partidosPerdidos" to (partidosTotales["partidosPerdidos"]
                                        ?: 0).toInt()
                                )

                            )
                        }

                        // Filtrar fuera del bloque map
                        val equiposFiltrados = equipos.filter { it.division == division }

                        _listaEquipos.value =
                            equiposFiltrados.sortedByDescending { it.partidosTotales["puntos"] }
                        Log.i("GAbri", "EQUIPOS: $equipos")

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

    fun obtenerJornadas(liga: String, nombreDivision: String) {
        val jornadasRef = db.collection("jornadas").document(liga)

        viewModelScope.launch {
            try {
                val snapshot = jornadasRef.get().await()

                if (snapshot.exists()) {
                    val divisionMap = snapshot[nombreDivision] as? Map<*, *>

                    if (divisionMap != null) {
                        val jornadas = divisionMap["jornadas"] as? List<Map<*, *>> ?: emptyList()

                        val listaJornadas = jornadas.mapNotNull { map ->
                            val fecha = map["fecha"] as? String
                            val nombreJornada = map["nombreJornada"] as? String
                            val partidosMap = map["partidos"] as? List<Map<*, *>> ?: emptyList()
                            val partidos = partidosMap.mapNotNull { partidoMap ->

                                val nombreEquipoLocal =
                                    partidoMap["nombreEquipoLocal"] as? String ?: ""
                                val nombreEquipoVisitante =
                                    partidoMap["nombreEquipoVisitante"] as? String ?: ""
                                val resultadoPrimerSet =
                                    partidoMap["resultadoPrimerSet"] as? String ?: ""
                                val resultadoSegundoSet =
                                    partidoMap["resultadoSegundoSet"] as? String ?: ""

                                Partido(
                                    nombreEquipoLocal,
                                    nombreEquipoVisitante,
                                    resultadoPrimerSet,
                                    resultadoSegundoSet
                                )
                            }

                            if (fecha != null && nombreJornada != null) {
                                Jornada(nombreJornada, fecha, partidos)
                            } else {
                                null
                            }
                        }

                        _listaJornadas.value = listaJornadas
                    }
                }
            } catch (e: Exception) {
                Log.e("GAbri", "Error al obtener las jornadas", e)
            }
        }
    }

    fun obtenerSiguienteJornadaConPartidos(): Pair<Jornada, List<Partido>>? {
        if (indiceJornadaActual < _listaJornadas.value.size - 1) {
            indiceJornadaActual++
            val siguienteJornada = _listaJornadas.value[indiceJornadaActual]
            return Pair(siguienteJornada, siguienteJornada.partidos)
        }
        return null
    }

    fun obtenerJornadaAnteriorConPartidos(): Pair<Jornada, List<Partido>>? {
        if (indiceJornadaActual > 0) {
            indiceJornadaActual--
            val jornadaAnterior = _listaJornadas.value[indiceJornadaActual]
            return Pair(jornadaAnterior, jornadaAnterior.partidos)
        }
        return null
    }

    fun saveMatchs(liga: String, nombreDivision: String, listaJornadas: List<Jornada>) {
        val jornadasRef = db.collection("jornadas").document(liga)

        viewModelScope.launch {
            try {
                // Obtener el documento actual para actualizar o crear uno nuevo
                val snapshot = jornadasRef.get().await()

                // Crear un nuevo mapa para almacenar las jornadas
                val nuevasJornadas = mutableMapOf<String, Any?>()

                // Verificar si ya hay datos para la división
                if (snapshot.exists()) {
                    // Copiar los datos existentes al nuevo mapa
                    nuevasJornadas.putAll(snapshot.data ?: emptyMap())
                }

                // Obtener las jornadas existentes si hay alguna
                val jornadasDivisionExistente = (nuevasJornadas[nombreDivision] as? Map<*, *>)?.get("jornadas") as? List<*>

                // Convertir las jornadas a un formato que Firestore pueda manejar
                val jornadasMap = listaJornadas.map { jornada ->
                    val partidosMap = jornada.partidos.map { partido ->
                        mapOf(
                            "nombreEquipoLocal" to partido.nombreEquipoLocal,
                            "nombreEquipoVisitante" to partido.nombreEquipoVisitante,
                            "resultadoPrimerSet" to partido.resultadoPrimerSet,
                            "resultadoSegundoSet" to partido.resultadoSegundoSet
                        )
                    }

                    mapOf(
                        "fecha" to jornada.fecha,
                        "nombreJornada" to jornada.nombreJornada,
                        "partidos" to partidosMap
                    )
                }

                // Agregar las nuevas jornadas a las existentes
                val nuevasJornadasDivisionList = (jornadasDivisionExistente as? MutableList<Any?>) ?: mutableListOf()
                nuevasJornadasDivisionList.addAll(jornadasMap)

                // Añadir las jornadas al mapa de la división
                nuevasJornadas[nombreDivision] = mapOf("jornadas" to nuevasJornadasDivisionList)

                // Actualizar o crear el documento en Firestore
                jornadasRef.set(nuevasJornadas).await()
                onSaveComplete?.invoke(true)
            } catch (e: Exception) {
                Log.e("GAbri", "Error al guardar las jornadas", e)
                onSaveComplete?.invoke(false)
            }
        }
    }




}
