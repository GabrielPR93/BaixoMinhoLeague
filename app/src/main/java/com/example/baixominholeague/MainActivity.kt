package com.example.baixominholeague

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.baixominholeague.databinding.ActivityMainBinding
import com.example.baixominholeague.ui.menu.ClasificacionFragment
import com.example.baixominholeague.ui.menu.Inicio.InicioFragment
import com.example.baixominholeague.ui.menu.Jugadores.JugadoresFragment
import com.example.baixominholeague.ui.menu.PerfilFragment
import com.google.firebase.firestore.FirebaseFirestore
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val db = FirebaseFirestore.getInstance()
    private var fragmentPerfil = PerfilFragment()
    private var fragmentInicio = InicioFragment()

    private val REQUEST_ADD_EVENT = 200
    private val REQUEST_CODE_PERMISSIONS = 101

    private var correo: String? = null
    private var correoLogin: String? = null
    private var alias: String? = null
    private var nombre: String? = null
    private var foto: String? = null
    private var telefono: String? = null
    private var localidad: String? = null
    private var posiciones: String? = null
    private var otros: String? = null

    private var args: Bundle? = null
    private val requiredPermissions = arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE)

    private lateinit var addEventLauncher: ActivityResultLauncher<Intent>


    companion object {
        const val CLAVE_CORREO = "correo"
        const val CLAVE_ALIAS = "alias"
        const val CLAVE_NOMBRE = "nombre"
        const val CLAVE_TELEFONO = "telefono"
        const val CLAVE_LOCALIDAD = "localidad"
        const val CLAVE_POSICIONES = "posiciones"
        const val CLAVE_OTROS = "otros"
        const val CLAVE_FOTO = "foto"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.bottomNavigation.setBackground(null);

        if (!arePermissionsGranted()) {
            requestPermissions()
        }

        val intent = intent
        correo = intent.getStringExtra("email")
        correoLogin = intent.getStringExtra("correoLogin")

        getDataBd() //Acceder a datos del usuario
        saveData()

        // Pasar el correo al fragment Inicio
        val args = Bundle()
        args.putString(CLAVE_CORREO, correo?.toString() ?: correoLogin.toString())

        //Cargar el fragment de Inicio al iniciar
        if (savedInstanceState == null) {
            replaceFragment(fragmentInicio)
            fragmentInicio?.arguments = args
        }


        binding.bottomNavigation.setOnNavigationItemSelectedListener { item ->

            when (item.itemId) {
                R.id.home -> replaceFragment(fragmentInicio)
                R.id.Buscar -> replaceFragment(JugadoresFragment())
                R.id.clasificacion -> replaceFragment(ClasificacionFragment())
                R.id.perfil -> replaceFragment(fragmentPerfil)

            }
            true
        }

        binding.floatinButton.setOnClickListener {
            alias?.let { it1 -> navigateToNewEvent(it1) }
        }

        updateNewEvent()

    }

    //Actualiza los eventos despues de añadir uno nuevo
    private fun updateNewEvent() {
        addEventLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            if (result.resultCode == Activity.RESULT_OK) {

                fragmentInicio.updateEventList()
            }
        }
    }

    private fun navigateToNewEvent(Usuario: String) {
        val intent = Intent(this, NuevoEvento::class.java)
        intent.putExtra(NuevoEvento.USUARIO_PUBLICADOR, Usuario)
        addEventLauncher.launch(intent)
    }


    //Precarga de la imagen de perfil
    private fun loadFoto(foto: String?) {
        if (foto != null) {
            val imageUri = Uri.parse(foto)
            val picasso = Picasso.get()
            picasso.load(imageUri).fetch(object : Callback {
                override fun onSuccess() {
                    // La imagen se ha precargado correctamente

                }

                override fun onError(e: Exception) {
                    // Error al precargar la imagen
                }
            })
        }
    }

    fun arePermissionsGranted(): Boolean {
        for (permission in requiredPermissions) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    permission
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                return false
            }
        }
        return true
    }

    fun requestPermissions() {
        ActivityCompat.requestPermissions(this, requiredPermissions, REQUEST_CODE_PERMISSIONS)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            var allPermissionsGranted = true
            for (result in grantResults) {
                if (result != PackageManager.PERMISSION_GRANTED) {
                    allPermissionsGranted = false
                    break
                }
            }
            if (allPermissionsGranted) {
                // Los permisos fueron concedidos
            } else {
                // Los permisos no fueron concedidos
            }
        }
    }

    private fun getDataBd() {
        db.collection("users").document(correo ?: correoLogin.orEmpty()).get()
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    val document = it.result
                    if (document != null) {
                        alias = document.get("alias") as String?
                        nombre = document.get("nombre") as String?
                        telefono = document.get("telefono") as String?
                        localidad = document.get("localidad") as String?
                        posiciones = document.get("posiciones") as String?
                        foto = document.get("foto") as String?
                        otros = document.get("otros") as String?

                        args = Bundle().apply {

                            putString(CLAVE_CORREO, correo?.toString() ?: correoLogin.toString())
                            putString(CLAVE_ALIAS, alias?.toString())
                            putString(CLAVE_NOMBRE, nombre?.toString())
                            putString(CLAVE_TELEFONO, telefono?.toString())
                            putString(CLAVE_LOCALIDAD, localidad?.toString())
                            putString(CLAVE_POSICIONES, posiciones?.toString())
                            putString(CLAVE_FOTO, foto?.toString())
                            putString(CLAVE_OTROS,otros?.toString())
                        }
                        fragmentPerfil.arguments = args

                        loadFoto(foto)
                    }
                }
            }

    }

    private fun saveData() {
        //Guardado de datos
        val prefs =
            getSharedPreferences(getString(R.string.prefs_file), Context.MODE_PRIVATE).edit()
        prefs.putString("email", correo ?: correoLogin)
        prefs.apply()
    }

    private fun replaceFragment(fragment: Fragment) {

        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
            .replace(R.id.frameContainer, fragment)
            .commit()

    }

}