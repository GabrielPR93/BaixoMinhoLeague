package com.example.baixominholeague

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import com.example.baixominholeague.databinding.ActivityMainBinding
import com.example.baixominholeague.ui.menu.ClasificacionFragment
import com.example.baixominholeague.ui.menu.InicioFragment
import com.example.baixominholeague.ui.menu.JugadoresFragment
import com.example.baixominholeague.ui.menu.PerfilFragment

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private var correo: String? = null

    companion object {
        const val CLAVE_CORREO = "correo"
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.bottomNavigation.setBackground(null);

        val intent = intent
        correo = intent.getStringExtra("correo")

        //Pasar correo a fragment de perfil
        val args = Bundle().apply {
            putString(CLAVE_CORREO,correo.toString())
        }
        val fragmentPerfil = PerfilFragment()
        fragmentPerfil.arguments=args

        saveData()





        binding.bottomNavigation.setOnNavigationItemSelectedListener { item ->

            when(item.itemId){
                R.id.home -> replaceFragment(InicioFragment())
                R.id.Buscar -> replaceFragment(JugadoresFragment())
                R.id.clasificacion -> replaceFragment(ClasificacionFragment())
                R.id.perfil -> replaceFragment(fragmentPerfil)

            }
            true
        }

        }

    private fun saveData() {
        //Guardado de datos
        val prefs = getSharedPreferences(getString(R.string.prefs_file),Context.MODE_PRIVATE).edit()
        prefs.putString("email",correo)
        prefs.apply()
    }

    private fun replaceFragment(fragment: Fragment){

        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.frameContainer,fragment)
        fragmentTransaction.commit()

    }
}