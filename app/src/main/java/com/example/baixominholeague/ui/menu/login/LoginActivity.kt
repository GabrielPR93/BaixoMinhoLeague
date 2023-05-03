package com.example.baixominholeague.ui.menu.login

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import com.example.baixominholeague.R
import com.example.baixominholeague.databinding.ActivityLoginBinding
import com.example.baixominholeague.databinding.ActivityMainBinding
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.auth.FirebaseAuth

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        //Analytics Event
        val analytics: FirebaseAnalytics = FirebaseAnalytics.getInstance(this)
        val bundle = Bundle()
        bundle.putString("message","Integracion de Firebase")
        analytics.logEvent("InitScreen",bundle)

        //Setup
        setup()
    }

    private fun setup() {
        title = "Autenticación"

        binding.buttomLogin.setOnClickListener {
            if(binding.editTextNombre.text.isNotEmpty() && binding.editTextPassword.text.isNotEmpty()){
                FirebaseAuth.getInstance()
                    .createUserWithEmailAndPassword(binding.editTextNombre.text.toString(),binding.editTextPassword.text.toString()).addOnCompleteListener {
                        if(it.isSuccessful){

                        }else{
                            showAlert()
                        }
                    }
            }
        }

    }

    private fun showAlert() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Error")
        builder.setMessage("Se ha producido un error autenticando al usuario")
        builder.setPositiveButton("Aceptar",null)
        val dialog: AlertDialog = builder.create()
        dialog.show()
    }
}