package com.example.baixominholeague.data

import com.google.firebase.firestore.PropertyName

data class Partido (
    @PropertyName("nombreEquipoLocal") val nombreEquipoLocal: String = "",
    @PropertyName("nombreEquipoVisitante") val nombreEquipoVisitante: String = "",
    @PropertyName("resultadoPrimerSet") val resultadoPrimerSet: String = "",
    @PropertyName("resultadoSegundoSet") val resultadoSegundoSet: String = "",
)