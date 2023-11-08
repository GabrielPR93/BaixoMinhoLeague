package com.example.baixominholeague.ui.menu.Perfil

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatDelegate
import com.example.baixominholeague.R
import com.example.baixominholeague.databinding.ActivityConfiguracionBinding
import com.example.baixominholeague.login.LoginActivity
import com.google.firebase.auth.FirebaseAuth

class Configuracion : AppCompatActivity() {

    private lateinit var binding: ActivityConfiguracionBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityConfiguracionBinding.inflate(layoutInflater)
        setContentView(binding.root)
        loadTheme()
        logout()

        binding.imageButtonBackPerfil.setOnClickListener { onBackPressedDispatcher.onBackPressed() }
        ThemeDark()
    }

    private fun ThemeDark(){
        binding.temaNocturnoSwitch.setOnCheckedChangeListener { _, isChecked ->
            if(isChecked){
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                saveTheme()
            }else{
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                saveTheme()
            }
        }
    }
    private fun loadTheme(){
        val themeMode = AppCompatDelegate.getDefaultNightMode()

        when (themeMode) {
            AppCompatDelegate.MODE_NIGHT_NO -> {
                // El tema claro está activo
               binding.temaNocturnoSwitch.isChecked = false
            }
            AppCompatDelegate.MODE_NIGHT_YES -> {
                // El tema oscuro está activo
                binding.temaNocturnoSwitch.isChecked = true
            }
        }
    }
    private fun saveTheme(){
        val sharedPrefs = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        val editor = sharedPrefs.edit()

        val themeMode = if (binding.temaNocturnoSwitch.isChecked) {
            AppCompatDelegate.MODE_NIGHT_YES
        } else {
            AppCompatDelegate.MODE_NIGHT_NO
        }
        editor.putInt("themeMode", themeMode)
        editor.apply()
    }
    private fun logout() {
        binding.tvCerrarSesion.setOnClickListener {
            //Borramos datos de la sesión

            val alertDialog = AlertDialog.Builder(this)
                .setTitle("Cerrar Sesión")
                .setMessage("¿Estás seguro de que deseas cerrar sesión?")
                .setPositiveButton("Sí") { dialog, which ->

                    val prefs = getSharedPreferences(
                        getString(R.string.prefs_file),
                        Context.MODE_PRIVATE
                    ).edit()
                    prefs.clear()
                    prefs.apply()
                    //Cerramos la sesión
                    FirebaseAuth.getInstance().signOut()
                    val intent = Intent(this, LoginActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                    startActivity(intent)
                    finish()

                }
                .setNegativeButton("No", null)
                .create()

            alertDialog.show()

        }
    }
}