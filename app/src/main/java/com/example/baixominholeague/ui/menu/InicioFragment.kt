package com.example.baixominholeague.ui.menu

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.example.baixominholeague.databinding.FragmentInicioBinding

class InicioFragment : Fragment() {


    private var _binding: FragmentInicioBinding? = null
    private val binding get() = _binding!!
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

        return view
    }
    fun mostrarDatos(nombre: String) {
        //nombreTextView.text = nombre
        binding.textViewNombreTorneo.text = nombre
        Log.i("GAB","NOMBREEEEEEEEEEEEEEEEEEEEEEEEE: "+nombre.toString())

    }

    companion object {

        private const val NOMBRE = "nombre"
        private const val FECHA = "fecha"
        private const val HORA = "hora"
        private const val PRECIO = "precio"
        @JvmStatic
        fun newInstance(nombre: String, fecha: String,hora: String, precio: String) =
            InicioFragment().apply {
                arguments = Bundle().apply {
                    putString(NOMBRE, nombre)
                    putString(FECHA, fecha)
                    putString(HORA, hora)
                    putString(PRECIO, precio)
                }
            }
    }
}