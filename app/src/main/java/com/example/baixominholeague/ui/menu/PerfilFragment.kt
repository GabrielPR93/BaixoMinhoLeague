package com.example.baixominholeague.ui.menu

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import com.example.baixominholeague.R
import com.example.baixominholeague.databinding.FragmentPerfilBinding
import com.example.baixominholeague.MainActivity.Companion.CLAVE_CORREO
import com.example.baixominholeague.ui.menu.login.LoginActivity
import com.google.firebase.auth.FirebaseAuth

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

        binding.textViewCorreo.text = correo

        logout()


        return view
    }


    private fun logout() {
        binding.buttomLogout.setOnClickListener {
            //Borramos datos de la sesión
            val prefs = requireActivity().getSharedPreferences(getString(R.string.prefs_file), Context.MODE_PRIVATE).edit()
            prefs.clear()
            prefs.apply()
            //Cerramos la sesión
            FirebaseAuth.getInstance().signOut()
            startActivity(Intent(activity,LoginActivity::class.java))
            requireActivity().finish()

        }
    }

//    companion object {
//
//        @JvmStatic
//        fun newInstance(param1: String) =
//            PerfilFragment().apply {
//                arguments = Bundle().apply {
//                    putString("correo",correo)
//                }
//            }
//    }
}