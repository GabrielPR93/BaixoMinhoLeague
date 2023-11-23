package com.example.baixominholeague.ui.menu.Clasificacion

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.baixominholeague.data.Liga
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

@HiltViewModel
class ClasificacionViewModel @Inject constructor(): ViewModel() {
    //private var listaLigas: MutableLiveData<List<Liga>> = MutableLiveData()
    private var _listaLigas = MutableStateFlow<List<Liga>>(emptyList())
    val listaLigas: StateFlow<List<Liga>> = _listaLigas

    init {
        viewModelScope.launch {
//            try {
//                val ligas = ligaService.obtenerLigasDesdeFirebase()
//                _listaLigas.value = ligas
//            } catch (e: Exception) {
//                // Manejar el error según sea necesario
//
//            }
        }
//        fun obtenerLigasDesdeFirebase( db: FirebaseFirestore): List<Liga> {
//            val listaLigas: MutableList<Liga> = mutableListOf()
//            val referenciaColeccion = db.collection("ligas")
//
//            try {
//                val documents = referenciaColeccion.get().await()
//
//                for (document in documents) {
//                    val nombre = document.id.toString()
//                    val divisiones = document.get("divisiones") as? List<String>
//
//                    val liga = Liga(nombre, divisiones)
//                    listaLigas.add(liga)
//                }
//            } catch (e: Exception) {
//                // Manejar el error según sea necesario
//                Log.e("Gabri", "Error al obtener datos de ligas desde Firebase: $e")
//            }
//
//            return listaLigas
//        }
    }
}