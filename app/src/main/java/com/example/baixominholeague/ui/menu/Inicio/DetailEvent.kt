package com.example.baixominholeague.ui.menu.Inicio

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.baixominholeague.R
import com.example.baixominholeague.databinding.ActivityDetailEventBinding

class DetailEvent : AppCompatActivity() {


    companion object {
        const val NAME_EVENT = "name_event"
    }

    private lateinit var binding: ActivityDetailEventBinding
    private var nameEvent: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailEventBinding.inflate(layoutInflater)
        setContentView(binding.root)

        nameEvent = intent.getStringExtra(NAME_EVENT)
        binding.tvTituloEvent.setText(nameEvent)
    }
}