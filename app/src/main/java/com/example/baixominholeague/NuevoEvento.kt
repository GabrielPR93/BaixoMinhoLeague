package com.example.baixominholeague

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.baixominholeague.databinding.ActivityNuevoEventoBinding

class NuevoEvento : AppCompatActivity() {
    companion object {
        const val EMAIL_PUBLICADOR = "emailPublicador"
    }
    private lateinit var binding: ActivityNuevoEventoBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNuevoEventoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnBackAdd.setOnClickListener { onBackPressed() }
    }
}