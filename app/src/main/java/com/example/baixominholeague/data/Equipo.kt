package com.example.baixominholeague.data

import com.google.firebase.firestore.PropertyName

data class Equipo (

    @PropertyName("id") val id: Int? = null,
    @PropertyName("nombre") val nombre: String = "",
    @PropertyName("division") val division: String = "",
//    @PropertyName("nombreJugador1") val nombreJugador1: String = "",
//    @PropertyName("nombreJugador2") val nombreJugador2: String = "",
//    @PropertyName("idJugador1") val idJugador1: Int? = null ,
//    @PropertyName("idJugador2") val idJugador2: Int? = null,
)