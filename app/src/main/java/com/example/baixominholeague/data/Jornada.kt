package com.example.baixominholeague.data

import com.google.firebase.firestore.PropertyName

data class Jornada(
    @PropertyName("nombreJornada") val nombreJornada: String = "",
    @PropertyName("fecha") val fecha: String = "",
    @PropertyName("partidos") val partidos: List<Partido> = emptyList(),
)