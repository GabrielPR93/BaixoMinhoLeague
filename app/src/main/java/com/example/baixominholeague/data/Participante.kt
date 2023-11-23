package com.example.baixominholeague.data

class Participante {

    var nombreCompleto: String? = null
    var alias: String? = null
    var imagen: String? = null
    constructor(alias: String, nombreCompleto: String, imagen: String){
        this.alias = alias
        this.imagen = imagen
        this.nombreCompleto = nombreCompleto
    }


}