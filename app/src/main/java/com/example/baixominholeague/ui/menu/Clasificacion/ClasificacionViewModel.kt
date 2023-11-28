package com.example.baixominholeague.ui.menu.Clasificacion

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.baixominholeague.data.Equipo
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

@HiltViewModel
class ClasificacionViewModel @Inject constructor() : ViewModel() {
    private val _listaEquipos = MutableSharedFlow<List<Equipo>>(replay = 1)
    val listaEquipos: SharedFlow<List<Equipo>> = _listaEquipos.asSharedFlow()

    private val db = FirebaseFirestore.getInstance()

    fun obtenerDatos(liga: String, division: String) {
        val equipoRef = db.collection("equipos").document(liga)
        viewModelScope.launch {
            try {
                val documentSnapshot = equipoRef.get().await()
                if (documentSnapshot.exists()) {
                    val equiposArray = documentSnapshot.get("equipos") as? ArrayList<Map<String, Any>>
                    if (equiposArray != null) {
                        val equipos = equiposArray.map { equipoMap ->
                            Equipo(
                                id = (equipoMap["id"] as Long).toInt(),
                                nombre = equipoMap["nombre"] as String,
                                division = equipoMap["division"] as String,
                            )
                        }
                        val equiposFiltrados = equipos.filter { equipo -> equipo.division == division }
                        _listaEquipos.emit(equiposFiltrados)
                    } else {
                        println("No se encontró la matriz 'equipos' en el documento")
                    }
                } else {
                    println("No existe el documento")
                }
            } catch (exception: Exception) {
                println("Error obteniendo documento: $exception")
            }
        }
    }
}
