package com.example.baixominholeague.data

import com.google.firebase.firestore.PropertyName
import java.lang.reflect.Constructor

data class Jugador(

    @PropertyName("id") val id: Int? = null,
    @PropertyName("nombre") val nombre: String = "",
    @PropertyName("correo") val correo: String = "",
    @PropertyName("puntuacion") val puntuacion: String? = ""

)
