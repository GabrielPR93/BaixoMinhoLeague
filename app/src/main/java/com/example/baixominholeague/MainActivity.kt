package com.example.baixominholeague

import android.app.Activity
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import com.example.baixominholeague.databinding.ActivityMainBinding
import com.example.baixominholeague.ui.menu.CalendarioFragment
import com.example.baixominholeague.ui.menu.ClasificacionFragment
import com.example.baixominholeague.ui.menu.InicioFragment
import com.example.baixominholeague.ui.menu.JugadoresFragment

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.bottomNavigation.setBackground(null);


        binding.bottomNavigation.setOnNavigationItemSelectedListener { item ->

            when(item.itemId){
                R.id.home -> replaceFragment(InicioFragment())
                R.id.Buscar -> replaceFragment(JugadoresFragment())
                R.id.clasificacion -> replaceFragment(ClasificacionFragment())
                R.id.calendario -> replaceFragment(CalendarioFragment())

            }
            true
        }

        }
    private fun replaceFragment(fragment: Fragment){

        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.frameContainer,fragment)
        fragmentTransaction.commit()

    }
}