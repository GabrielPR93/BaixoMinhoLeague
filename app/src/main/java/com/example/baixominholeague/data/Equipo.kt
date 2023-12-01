package com.example.baixominholeague.data

import com.google.firebase.firestore.PropertyName

data class Equipo (

    @PropertyName("id") val id: Int? = null,
    @PropertyName("nombre") val nombre: String = "",
    @PropertyName("division") val division: String = "",
    @PropertyName("puntos") val puntos: Int = 0,
    @PropertyName("partidosJugados") val partidosJugados: Int = 0,
    @PropertyName("partidosGanados") val partidosGanados: Int = 0,
    @PropertyName("partidosEmpatados") val partidosEmpatados: Int = 0,
    @PropertyName("partidosPerdidos") val partidosPerdidos: Int = 0,
//    @PropertyName("nombreJugador1") val nombreJugador1: String = "",
//    @PropertyName("nombreJugador2") val nombreJugador2: String = "",
//    @PropertyName("idJugador1") val idJugador1: Int? = null ,
//    @PropertyName("idJugador2") val idJugador2: Int? = null,
)