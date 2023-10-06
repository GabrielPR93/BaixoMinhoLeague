package com.example.baixominholeague.login

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import com.example.baixominholeague.databinding.ActivityResetPasswordBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidUserException

class ResetPassword : AppCompatActivity() {

    private lateinit var bindin: ActivityResetPasswordBinding
    val auth = FirebaseAuth.getInstance()

    companion object {
        const val CLAVE_CORREO_RESET = "correo_reset"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bindin = ActivityResetPasswordBinding.inflate(layoutInflater)
        setContentView(bindin.root)

        val intent = intent
        var correo = intent.getStringExtra(CLAVE_CORREO_RESET)
        bindin.editTextResetCorreo.setText(correo)

        bindin.btnResetPassword.setOnClickListener { resetPassword() }
        bindin.btnBackResetPassword.setOnClickListener { onBackPressedDispatcher.onBackPressed() }

    }

    private fun resetPassword(){
        var correo = bindin.editTextResetCorreo.text.toString()
        bindin.progresBarResetPassword.visibility = View.VISIBLE // Mostrar ProgressBar

        if(!correo.isNullOrEmpty()){
            auth.sendPasswordResetEmail(correo)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(this, "Revise su correo electrónico para restablecer su contraseña", Toast.LENGTH_SHORT).show()
                        showSuccessful(correo)
                    } else {
                        val exception = task.exception
                        if (exception is FirebaseAuthInvalidUserException) {
                            Toast.makeText(this, "No hay una cuenta registrada con este correo electrónico", Toast.LENGTH_SHORT).show()
                            bindin.progresBarResetPassword.visibility = View.INVISIBLE
                        } else {
                            Toast.makeText(this, "Error al enviar el correo electrónico de restablecimiento de contraseña", Toast.LENGTH_SHORT).show()
                            bindin.progresBarResetPassword.visibility = View.INVISIBLE
                        }
                    }
                    bindin.progresBarResetPassword.visibility = View.INVISIBLE // Ocultar ProgressBar al finalizar
                }
                .addOnFailureListener {
                    bindin.progresBarResetPassword.visibility = View.INVISIBLE // Ocultar ProgressBar al fallar
                }
        } else {
            Toast.makeText(this, "Inserte el correo electrónico para restablecer la contraseña", Toast.LENGTH_SHORT).show()
            bindin.progresBarResetPassword.visibility = View.INVISIBLE // Ocultar ProgressBar si no hay correo electrónico
        }
    }


    private fun showSuccessful(correo: String){
        bindin.tvSuccessful.setText("Consulte la dirección de correo electrónico ${correo.uppercase()} para restablecer su contraseña.\n Si no le aparece revise su carpeta de spam. ")
        bindin.tvSuccessful.visibility = View.VISIBLE
        bindin.tvInstrucciones.visibility = View.GONE
        bindin.editTextResetCorreo.visibility = View.GONE
        bindin.editTextResetCorreo2.visibility = View.GONE
        bindin.btnResetPassword.setText("Reenviar email")
        bindin.progresBarResetPassword.visibility = View.INVISIBLE
    }

}

