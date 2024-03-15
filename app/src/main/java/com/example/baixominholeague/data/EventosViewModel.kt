package com.example.baixominholeague.data

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.flow.MutableStateFlow
import java.util.Calendar

class EventosViewModel : ViewModel() {
    private val db = FirebaseFirestore.getInstance()
    private val _eventos = MutableStateFlow<List<Evento>>(emptyList())
    val eventos: MutableStateFlow<List<Evento>> = _eventos
    private val currentUser = FirebaseAuth.getInstance().currentUser?.email

    private val _progressBarVisible = MutableLiveData<Boolean>()
    val progressBarVisible: LiveData<Boolean> get() = _progressBarVisible

    private val _eventoAgregado = MutableLiveData<Boolean>()
    val eventoAgregado: LiveData<Boolean> = _eventoAgregado
    init {
        if (currentUser != null) {
            getEventsOrderByDate(currentUser)
        }
    }

    fun notificarEventoAgregado() {
        _eventoAgregado.value = true
    }
    fun getEventsOrderByDate(correo: String) {
        _progressBarVisible.value = true
        val eventsCollection = db.collection("eventos")
        val query = eventsCollection.orderBy("fecha", Query.Direction.ASCENDING)

        val currentDate = Calendar.getInstance().time

        query.get().addOnSuccessListener { document ->
            val eventos = document.toObjects(Evento::class.java)
            val filteredEventos = eventos.filter { evento ->
                evento.fecha!! >= currentDate
            }

            _eventos.value= filteredEventos

            for (evento in filteredEventos) {
                if (evento.correo.equals(correo)) {
                    evento.mostrarBotonCancelar = true
                }
            }
            _progressBarVisible.value = false
        }
            .addOnFailureListener { exception ->
                println("Error al obtener los eventos: $exception")
                _progressBarVisible.value = false
            }
        Log.i("GAB","ENTRO EN GETEVENTS")
    }
}
