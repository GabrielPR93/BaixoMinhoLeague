package com.example.baixominholeague.ui.menu.Perfil

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.baixominholeague.databinding.ActivityConfiguracionBinding

class Configuracion : AppCompatActivity() {

    private lateinit var binding: ActivityConfiguracionBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityConfiguracionBinding.inflate(layoutInflater)
        setContentView(binding.root)

    }
}