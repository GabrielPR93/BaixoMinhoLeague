package com.example.baixominholeague.data

import java.sql.Timestamp
import java.util.Date

class Evento{
    var id: Int? = null
    var correo: String? = null
    var fecha: Date? = null
    var hora: String? = null
    var nombre: String? = null
    var ubicacion: String? = null
    var precio: String? = null
    var mostrarBotonCancelar: Boolean = false

    constructor() {

    }
}

