package com.example.baixominholeague.ui.menu.Inicio

import android.icu.util.Calendar
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Adapter
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.baixominholeague.data.Evento
import com.example.baixominholeague.data.Participante
import com.example.baixominholeague.databinding.ActivityParticipantesBinding
import com.example.baixominholeague.ui.menu.Inicio.recyclerViewParticipantes.ParticipantesAdapter
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

class ParticipantesActivity : AppCompatActivity() {

    private lateinit var binding: ActivityParticipantesBinding
    private  lateinit var participanteAdapter: ParticipantesAdapter
    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityParticipantesBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val listaParticipantes = listOf(
            Participante("Nombre 1", "correo1@example.com"),
            Participante("Nombre 2", "correo2@example.com"),
            Participante("Nombre 3", "correo3@example.com"),
            Participante("Nombre 4", "correo4@example.com"),

        )
        participanteAdapter = ParticipantesAdapter(listaParticipantes)
        binding.recyclerViewParticipantes.adapter = participanteAdapter
        binding.recyclerViewParticipantes.layoutManager = LinearLayoutManager(this)

    }


}