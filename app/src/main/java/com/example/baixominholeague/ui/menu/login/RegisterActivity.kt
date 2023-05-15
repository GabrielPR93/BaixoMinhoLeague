package com.example.baixominholeague.ui.menu.login

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AlertDialog
import com.example.baixominholeague.MainActivity
import com.example.baixominholeague.R
import com.example.baixominholeague.databinding.ActivityRegisterBinding
import com.google.firebase.auth.FirebaseAuth

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.buttomBackLogin.setOnClickListener {
            startActivity(Intent(this,LoginActivity::class.java))
            finish()
        }

        binding.buttomCrearCuenta.setOnClickListener {
            register()


        }
    }

    private fun register() {
        //title = "Autenticación"

            if(binding.editTextPassword.text.toString().equals(binding.editTextPaswoord2.text.toString()) && checkEmpty(binding.editTextEmail.text.toString(),binding.editTextPassword.text.toString(),binding.editTextPaswoord2.text.toString())){
                FirebaseAuth.getInstance()
                    .createUserWithEmailAndPassword(binding.editTextEmail.text.toString(),binding.editTextPassword.text.toString()).addOnCompleteListener {
                        if(it.isSuccessful){
                          showHome(it.result?.user?.email?:"")

                        }else{
                            showAlert()

                        }
                    }
            }else{
                showAlert()
            }
    }

    private fun showHome(email: String){
        val homeIntent: Intent = Intent(this,MainActivity::class.java).apply {
            putExtra("email",email)

        }
        startActivity(homeIntent)

    }

    private fun showAlert() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Error")
        builder.setMessage("Se ha producido un error autenticando al usuario")
        builder.setPositiveButton("Aceptar",null)
        val dialog: AlertDialog = builder.create()
        dialog.show()
    }

    private fun checkEmpty(email: String, password: String, repeatPassword: String): Boolean {
        return email.isNotEmpty() && password.isNotEmpty() && repeatPassword.isNotEmpty()
    }
}