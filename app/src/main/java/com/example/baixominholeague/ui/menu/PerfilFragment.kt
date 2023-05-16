package com.example.baixominholeague.ui.menu

import android.content.Context
import android.content.Intent
import android.opengl.Visibility
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog

import androidx.core.view.isVisible
import com.example.baixominholeague.MainActivity.Companion.CLAVE_ALIAS
import com.example.baixominholeague.R
import com.example.baixominholeague.databinding.FragmentPerfilBinding
import com.example.baixominholeague.MainActivity.Companion.CLAVE_CORREO
import com.example.baixominholeague.MainActivity.Companion.CLAVE_LOCALIDAD
import com.example.baixominholeague.MainActivity.Companion.CLAVE_NOMBRE
import com.example.baixominholeague.MainActivity.Companion.CLAVE_POSICIONES
import com.example.baixominholeague.MainActivity.Companion.CLAVE_TELEFONO
import com.example.baixominholeague.ui.menu.login.LoginActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class PerfilFragment : Fragment() {

    private var _binding: FragmentPerfilBinding? = null
    private val binding get() = _binding!!
    private var correo: String? = null
    private var alias: String? = null
    private var nombre: String? = null
    private var telefono: String? = null
    private var localidad: String? = null
    private var posiciones: String? = null
    private val db = FirebaseFirestore.getInstance()



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {

            correo = it.getString(CLAVE_CORREO)
            alias = it.getString(CLAVE_ALIAS)
            nombre = it.getString(CLAVE_NOMBRE)
            telefono = it.getString(CLAVE_TELEFONO)
            localidad = it.getString(CLAVE_LOCALIDAD)
            posiciones = it. getString(CLAVE_POSICIONES)

        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentPerfilBinding.inflate(inflater,container,false)
        val view = binding.root

        setupUi()
        showMenuEdit()
        saveData()
        deleteData()

        logout()

        return view
    }

    private fun setupUi() {

        binding.textViewCorreo.text = correo
        binding.editTextAlias.setText(alias)
        binding.editTextNombre.setText(nombre)
        binding.editTextTelefono.setText(telefono)
        binding.editTextLocalidad.setText(localidad)
        binding.editTextPosiciones.setText(posiciones)

    }

    private fun saveData() {
        binding.save.setOnClickListener{

            db.collection("users").document(correo.orEmpty()).set(
                hashMapOf("alias" to binding.editTextAlias.text.toString(),
                "nombre" to binding.editTextNombre.text.toString(),
                "telefono" to binding.editTextTelefono.text.toString(),
                "localidad" to binding.editTextLocalidad.text.toString(),
                "posiciones" to binding.editTextPosiciones.text.toString())
            )

            Toast.makeText(requireContext(),"Guardado correctamente",Toast.LENGTH_SHORT).show()
        }
    }
    private fun deleteData() {
        binding.delet.setOnClickListener{
            val alertDialog = AlertDialog.Builder(requireContext())
                .setTitle("Confirmar eliminación")
                .setMessage("¿Estás seguro de que deseas borrar los datos?")
                .setPositiveButton("Sí") { dialog, which ->

                    db.collection("users").document(correo.orEmpty()).delete()
                    binding.editTextAlias.text.clear()
                    binding.editTextNombre.text.clear()
                    binding.editTextTelefono.text.clear()
                    binding.editTextLocalidad.text.clear()
                    binding.editTextPosiciones.text.clear()
                }
                .setNegativeButton("No", null)
                .create()

            alertDialog.show()
        }
    }

    private fun showMenuEdit() {

        binding.buttomMenuEdit.setOnClickListener{
            if(!binding.save.isVisible){
                binding.save.show()
                binding.delet.show()
                binding.buttomMenuEdit.setImageResource(R.drawable.edit_off_24)

                binding.editTextAlias.isEnabled = true
                binding.editTextNombre.isEnabled = true
                binding.editTextTelefono.isEnabled = true
                binding.editTextLocalidad.isEnabled = true
                binding.editTextPosiciones.isEnabled = true
                binding.editeTextEdicion.isVisible = true
                binding.editTextNombre.requestFocus()

            }else{
                binding.save.hide()
                binding.delet.hide()
                binding.editeTextEdicion.isVisible = false
                binding.buttomMenuEdit.setImageResource(R.drawable.edit_24)

                binding.editTextAlias.isEnabled = false
                binding.editTextNombre.isEnabled = false
                binding.editTextTelefono.isEnabled = false
                binding.editTextLocalidad.isEnabled = false
                binding.editTextPosiciones.isEnabled = false
                binding.editeTextEdicion.isVisible = false
            }

        }
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