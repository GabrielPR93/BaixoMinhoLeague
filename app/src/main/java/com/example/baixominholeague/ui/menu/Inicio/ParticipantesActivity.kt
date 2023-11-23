package com.example.baixominholeague.ui.menu.Inicio

import android.os.Bundle
import android.util.Log
import android.widget.SearchView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.baixominholeague.data.Participante
import com.example.baixominholeague.databinding.ActivityParticipantesBinding
import com.example.baixominholeague.ui.menu.Inicio.adapter.adapterParticipantes.ParticipantesAdapter
import com.google.firebase.firestore.FirebaseFirestore

class ParticipantesActivity : AppCompatActivity() {


    private lateinit var binding: ActivityParticipantesBinding
    private  lateinit var participanteAdapter: ParticipantesAdapter
    private val db = FirebaseFirestore.getInstance()
    private val participantesList = mutableListOf<Participante>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityParticipantesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val nameEvent = intent.getStringExtra(DetailEvent.NAME_EVENT)
        if(nameEvent!=null){
            obtenerListaParticipantes(nameEvent)
        }

        binding.imageButtonBackParticipantes.setOnClickListener { onBackPressed() }
        searchParticipante()

    }

    private fun obtenerListaParticipantes(eventName: String) {
        val eventoRef = db.collection("eventos").document(eventName.lowercase())
        val participantesRef = eventoRef.collection("participantes")

        participantesRef.get()
            .addOnSuccessListener { querySnapshot ->
                participantesList.clear() // Limpia la lista antes de agregar nuevos datos

                for (document in querySnapshot.documents) {
                    // Obtén los datos de cada documento y crea un objeto Participante
                    val alias = document.getString("alias")
                    val nombre = document.getString("nombre")
                    val imagen = document.getString("imagen")

                    if (alias != null && nombre != null && imagen != null) {
                        val participante = Participante(alias, nombre, imagen)
                            participantesList.add(participante)

                    }
                }

                // Configura el adaptador con los datos obtenidos
                participanteAdapter = ParticipantesAdapter(participantesList)
                binding.recyclerViewParticipantes.adapter = participanteAdapter
                binding.recyclerViewParticipantes.layoutManager = LinearLayoutManager(this)
            }
            .addOnFailureListener { exception ->
                Log.w("Gabri", "Error getting documents: ", exception)
            }
    }

    private fun searchParticipante() {

      binding.searchViewParticipantes.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                searchByName(newText.toString())
                return false
            }
        })
    }

    private fun searchByName(query: String) {

        val filteredJugadores = participantesList.filter { participante ->
           participante.alias?.contains(query, ignoreCase = true) ?: false
        }
        participanteAdapter.updateList(filteredJugadores)

    }

}