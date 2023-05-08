package com.example.baixominholeague.ui.menu

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.baixominholeague.R
import com.example.baixominholeague.databinding.FragmentPerfilBinding
import com.example.baixominholeague.MainActivity.Companion.CLAVE_CORREO

class PerfilFragment : Fragment() {

    private var _binding: FragmentPerfilBinding? = null
    private val binding get() = _binding!!

    private var correo: String? = null



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {

            correo = it.getString(CLAVE_CORREO)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentPerfilBinding.inflate(inflater,container,false)
        val view = binding.root

        val correo1=arguments?.getString(CLAVE_CORREO)

        binding.textViewCorreo.text = "correo: "+correo1



        return view
    }

    companion object {

        @JvmStatic
        fun newInstance(param1: String) =
            PerfilFragment().apply {
                arguments = Bundle().apply {
                    putString("correo",correo)
                }
            }
    }
}