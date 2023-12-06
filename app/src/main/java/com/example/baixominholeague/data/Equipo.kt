package com.example.baixominholeague.data

import com.google.firebase.firestore.PropertyName

data class Equipo (

    @PropertyName("id") val id: Int? = null,
    @PropertyName("nombreEquipo") val nombreEquipo: String = "",
    @PropertyName("division") val division: String = "",
    @PropertyName("jugadores") val jugadores: List<Jugador> = emptyList(),
    @PropertyName("partidosTotales") val partidosTotales: Map<String, Int> = mapOf(
    "puntos" to 0,
    "partidosJugados" to 0,
    "partidosGanados" to 0,
    "partidosEmpatados" to 0,
    "partidosPerdidos" to 0
)
)