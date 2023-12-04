package com.example.baixominholeague.data

import com.google.firebase.firestore.PropertyName

data class Equipo (

    @PropertyName("id") val id: Int? = null,
    @PropertyName("nombreEquipo") val nombreEquipo: String = "",
    @PropertyName("division") val division: String = "",
    @PropertyName("puntos") val puntos: Int = 0,
    @PropertyName("partidosJugados") val partidosJugados: Int = 0,
    @PropertyName("partidosGanados") val partidosGanados: Int = 0,
    @PropertyName("partidosEmpatados") val partidosEmpatados: Int = 0,
    @PropertyName("partidosPerdidos") val partidosPerdidos: Int = 0,
    @PropertyName("jugadores") val jugadores: List<Jugador> = emptyList()
)