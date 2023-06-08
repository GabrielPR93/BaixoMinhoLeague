package com.example.baixominholeague.ui.menu

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.TextView
import com.example.baixominholeague.EventoAdapter
import com.example.baixominholeague.R
import com.example.baixominholeague.databinding.FragmentInicioBinding
import com.google.firebase.firestore.FirebaseFirestore

class InicioFragment : Fragment() {


    private var _binding: FragmentInicioBinding? = null
    private val binding get() = _binding!!
    private val db = FirebaseFirestore.getInstance()

    private lateinit var listView: ListView
    private lateinit var adapter: ArrayAdapter<String>


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {

        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentInicioBinding.inflate(inflater,container,false)
        val view = binding.root

        //adapter = EventoAdapter(this, R.layout.item_event) --> No pasar lista de eventos al adapter
        binding.listView.adapter = adapter

        getEventsOrderByDate()

        return view
    }

    private fun getEventsOrderByDate() {

        val eventsCollection = db.collection("eventos")
        val query = eventsCollection.orderBy("fecha")

        query.get().addOnSuccessListener { document ->
            for (document in document){
                val correo = document.get("correo")
                val fecha = document.get("fecha")
                val hora = document.get("hora")
                val nombre = document.get("nombre")
                val precio = document.get("precio")

                println("Fecha: $fecha nombre: $nombre")

            }
        }
            .addOnFailureListener{exception ->
                println("Error al obtener los eventos: $exception")
            }

    }


//    companion object {
//
//        private const val NOMBRE = "nombre"
//        private const val FECHA = "fecha"
//        private const val HORA = "hora"
//        private const val PRECIO = "precio"
//        @JvmStatic
//        fun newInstance(nombre: String, fecha: String,hora: String, precio: String) =
//            InicioFragment().apply {
//                arguments = Bundle().apply {
//                    putString(NOMBRE, nombre)
//                    putString(FECHA, fecha)
//                    putString(HORA, hora)
//                    putString(PRECIO, precio)
//                }
//            }
//    }
}

