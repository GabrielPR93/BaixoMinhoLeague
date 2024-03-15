package com.example.baixominholeague.ui.menu.Perfil

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.AttributeSet
import android.util.Log
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatDelegate
import androidx.navigation.findNavController
import androidx.navigation.navArgs
import com.example.baixominholeague.R
import com.example.baixominholeague.databinding.ActivityConfiguracionBinding
import com.example.baixominholeague.login.LoginActivity
import com.google.firebase.auth.FirebaseAuth

class Configuracion : AppCompatActivity() {

    private lateinit var binding: ActivityConfiguracionBinding
    private var themeMode : Int = 0
    private var flag : Boolean = true


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityConfiguracionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        loadTheme()
        logout()

        binding.imageButtonBackPerfil.setOnClickListener { onBackPressedDispatcher.onBackPressed() }
        ThemeDark()
        val version = getAppVersion()
        binding.tvVersion.text = "Versión: $version"
    }
    private fun ThemeDark(){
        binding.temaNocturnoSwitch.setOnCheckedChangeListener { _, isChecked ->
             themeMode = if (isChecked) {
                AppCompatDelegate.MODE_NIGHT_YES
            } else {
                AppCompatDelegate.MODE_NIGHT_NO
            }

            saveTheme(themeMode, isChecked)
            AppCompatDelegate.setDefaultNightMode(themeMode)
            recreate()
        }
    }

    private fun getAppVersion(): String {
        return try {
            val pInfo = packageManager.getPackageInfo(packageName, 0)
            pInfo.versionName
        } catch (e: PackageManager.NameNotFoundException) {
            "Desconocido"
        }
    }
    private fun loadTheme() {
        val sharedPrefs = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        themeMode = sharedPrefs.getInt("themeMode", AppCompatDelegate.MODE_NIGHT_NO)
        val isChecked = sharedPrefs.getBoolean("isChecked", false)

        binding.temaNocturnoSwitch.isChecked = isChecked


        if (themeMode != AppCompatDelegate.getDefaultNightMode()) {
            AppCompatDelegate.setDefaultNightMode(themeMode)
            recreate()
            Log.i("GAB","RECREADO")
        }
    }

    private fun saveTheme(themeMode: Int, isChecked: Boolean) {
        val sharedPrefs = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        val editor = sharedPrefs.edit()

        editor.putInt("themeMode", themeMode)
        editor.putBoolean("isChecked", isChecked)
        editor.apply()
    }
    private fun handleLogout() {
        // Cambiar tema y recrear actividad después de cerrar sesión
        AppCompatDelegate.setDefaultNightMode(themeMode)
        recreate()
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
                    //Recreamos
                    handleLogout()
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