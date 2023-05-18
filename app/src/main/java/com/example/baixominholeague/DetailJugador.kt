package com.example.baixominholeague

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

class DetailJugador : AppCompatActivity() {
    companion object {
        const val ID_PLAYER = "id_player"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail_jugador)
    }
}