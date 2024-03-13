package com.example.baixominholeague.ui.menu.Inicio

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.baixominholeague.data.Evento
import com.example.baixominholeague.data.EventosViewModel
import com.example.baixominholeague.databinding.FragmentEventosBinding
import com.example.baixominholeague.ui.menu.Inicio.adapter.EventoAdapter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.launch

class EventosFragment : Fragment() {

    private var _binding: FragmentEventosBinding? = null

    private val binding get() = _binding!!
    private val db = FirebaseFirestore.getInstance()
    private val storageRef = FirebaseStorage.getInstance().reference
    private lateinit var eventoAdapter: EventoAdapter
    private val correo = FirebaseAuth.getInstance().currentUser?.email

    private val eventosViewModel: EventosViewModel by viewModels()


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initUI()

    }

    private fun initUI() {
        initUIState()
        iniList()
    }

    private fun iniList() {

        eventoAdapter = EventoAdapter(emptyList(), ::eliminarEvento) { nombreEvento ->
            navigateToDetailEvent(nombreEvento)
        }

        binding.reyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = eventoAdapter
        }

    }

    private fun initUIState() {

        eventosViewModel.progressBarVisible.observe(viewLifecycleOwner) { visible ->
            binding.progresBarEvents.visibility = if (visible) View.VISIBLE else View.GONE
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                eventosViewModel.eventos.collect {
                    eventoAdapter.updateList(it)
                }
            }
        }
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentEventosBinding.inflate(inflater, container, false)
        val view = binding.root

        return view
    }


//    fun ensureEventoAdapterInitialized() {
//        if (!::eventoAdapter.isInitialized) {
//
//            eventoAdapter = EventoAdapter(emptyList(), ::eliminarEvento) { nombreEvento -> navigateToDetailEvent(nombreEvento)
//
//            }
//        }
//    }
//
//
//    fun updateEventList(){
//        ensureEventoAdapterInitialized()
//
//        if (correo != null) {
//            Log.i("Gabriele","EJECUTADOOOO")
//            getEventsOrderByDate(correo)
//            eventoAdapter.notifyDataSetChanged()
//        }
//    }
//    fun getEventsOrderByDate(correo: String) {
//        val eventsCollection = db.collection("eventos")
//        val query = eventsCollection.orderBy("fecha", Query.Direction.ASCENDING)
//
//        val currentDate = Calendar.getInstance().time
//
//        query.get().addOnSuccessListener { document ->
//            val eventos = document.toObjects(Evento::class.java)
//            val filteredEventos = eventos.filter { evento ->
//                evento.fecha!! >= currentDate
//            }
//            eventoAdapter.updateList(filteredEventos)
//
//            for (evento in filteredEventos) {
//                if (evento.correo.equals(correo)) {
//                    evento.mostrarBotonCancelar = true
//                }
//            }
//            binding.progresBarEvents.visibility = View.GONE
//        }
//            .addOnFailureListener { exception ->
//                println("Error al obtener los eventos: $exception")
//            }
//    }

    private fun eliminarEvento(evento: Evento) {
        val alertDialog = AlertDialog.Builder(requireContext())
            .setTitle("Confirmar eliminación")
            .setMessage("¿Estás seguro de que deseas borrar el evento?")
            .setPositiveButton("Sí") { dialog, which ->
                evento.nombre?.let { nombreEvento ->
                    val imageRef = storageRef.child("images/${nombreEvento.lowercase()}.jpg")
                    db.collection("eventos").document(nombreEvento.lowercase()).delete()
                        .addOnSuccessListener {
                            deleteEventFromRealtimeDatabase(nombreEvento.lowercase())
                            if (correo != null) {
                                eventosViewModel.getEventsOrderByDate(correo)
                            }
                        }
                        .addOnFailureListener { exception ->
                            println("Error al eliminar el evento: $exception")
                        }

                    imageRef.delete()
                        .addOnSuccessListener { Log.i("Gabri","Imagen borrada correctamente de Firebase Storage") }
                        .addOnFailureListener { exception -> Log.i("Gabri","Error al eliminar la imagen: $exception") }
                }
            }
            .setNegativeButton("No", null)
            .create()

        alertDialog.show()
    }

    private fun deleteEventFromRealtimeDatabase(eventName: String) {
        val database = FirebaseDatabase.getInstance()
        val eventoRef = database.getReference("eventos/$eventName")

        eventoRef.removeValue()
            .addOnSuccessListener {
                Log.w("Gabri", "El evento se eliminó correctamente de la base de datos en tiempo real")
            }
            .addOnFailureListener { e ->
                Log.w("Gabri", "Error al eliminar el evento de la base de datos en tiempo real")
            }
    }

    private fun navigateToDetailEvent(nombre:String){
        val intent= Intent(requireContext(), DetailEvent::class.java)
        intent.putExtra(DetailEvent.NAME_EVENT,nombre)
        startActivity(intent)
    }

//    private fun navigateToDetailEvent(nombre:String){
//        findNavController().navigate(R.id.action_eventosFragment_to_detailEvent)
//    }

}

