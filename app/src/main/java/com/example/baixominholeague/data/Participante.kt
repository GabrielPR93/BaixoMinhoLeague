package com.example.baixominholeague.data

class Participante {

    constructor(alias: String, nombreCompleto: String, imagen: String){
        this.alias = alias
        this.imagen = imagen
        this.nombreCompleto = nombreCompleto
    }

    var nombreCompleto: String? = null
    var alias: String? = null
    var imagen: String? = null
}