package com.example.baixominholeague

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.example.baixominholeague.databinding.ActivityMainBinding
import com.example.baixominholeague.ui.menu.Clasificacion.ClasificacionFragment
import com.example.baixominholeague.ui.menu.Clasificacion.ClasificacionGeneralFragment
import com.example.baixominholeague.ui.menu.Inicio.EventosFragment
import com.example.baixominholeague.ui.menu.Inicio.InicioFragment
import com.example.baixominholeague.ui.menu.Perfil.PerfilFragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController
    private val db = FirebaseFirestore.getInstance()
    private var fragmentPerfil = PerfilFragment()
    private val correo = FirebaseAuth.getInstance().currentUser?.email

    private val REQUEST_ADD_EVENT = 200
    private val REQUEST_CODE_PERMISSIONS = 101
    private val REQUEST_CODE_NUEVO_EVENTO = 102

    private var alias: String? = null
    private var nombre: String? = null
    private var foto: String? = null
    private var telefono: String? = null
    private var localidad: String? = null
    private var posiciones: String? = null
    private var otros: String? = null

    private var args: Bundle? = null
    private val requiredPermissions = arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE)

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
        initUI()

       binding.bottomNavigation.setBackground(null);
          loadTheme()

        if (!arePermissionsGranted()) {
            requestPermissions()
        }

        if (correo != null) {
            //Acceder a datos del usuario
            getDataBd(correo)
        }
        saveData()
    }
    private fun initUI(){
        initNavigation()
    }

    private fun initNavigation() {
        val navHost = supportFragmentManager.findFragmentById(R.id.navHostFragment) as NavHostFragment
            navController = navHost.navController
            binding.bottomNavigation.setupWithNavController(navController)

            binding.floatinButton.setOnClickListener {
                alias?.let { it1 -> navigateToNewEvent(it1)}
            }

    }

    private fun loadTheme(){
        val sharedPrefs = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)

        // Cargar el estado del botón y el tema
        val switchState = sharedPrefs.getBoolean("switchState", false)
        val themeMode = sharedPrefs.getInt("themeMode", AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)

// Configurar el estado del botón y el tema

        //binding.temaNocturnoSwitch.isChecked = switchState
        AppCompatDelegate.setDefaultNightMode(themeMode)

    }

//    Actualizar lista de eventos despues de añadir uno nuevo
//    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//        super.onActivityResult(requestCode, resultCode, data)
//
//        if (requestCode == REQUEST_CODE_NUEVO_EVENTO && resultCode == Activity.RESULT_OK) {
//          EventosFragment().updateEventList()
//        }
//    }

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
    //Actualizar lista de eventos despues de añadir uno nuevo
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == REQUEST_CODE_NUEVO_EVENTO && resultCode == Activity.RESULT_OK) {
            val navHostFragment = supportFragmentManager.findFragmentById(R.id.navHostFragment) as NavHostFragment
            val inicioFragment = navHostFragment.childFragmentManager.fragments.firstOrNull { it is InicioFragment } as? InicioFragment
            inicioFragment?.viewPager()

        }
    }

    private fun getDataBd(correo: String) {
        db.collection("users").document(correo).get()
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

                            putString(CLAVE_CORREO, correo.toString())
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
        prefs.putString("email", correo)
        prefs.apply()
    }
    private fun navigateToNewEvent(Usuario: String) {
        val intent = Intent(this, NuevoEvento::class.java)
        intent.putExtra(NuevoEvento.USUARIO_PUBLICADOR, Usuario)
        startActivityForResult(intent, REQUEST_CODE_NUEVO_EVENTO)
    }

//    private fun replaceFragment(fragment: Fragment) {
//        val fragmentManager = supportFragmentManager
//        val fragmentTransaction = fragmentManager.beginTransaction()
//
//        fragmentTransaction.replace(R.id.frameContainer, fragment)
//        fragmentTransaction.addToBackStack(null)
//        fragmentTransaction.commit()
//
//        // Escucha cambios en la pila de retroceso
//        fragmentManager.addOnBackStackChangedListener {
//            // Verifica si la pila de retroceso está vacía, lo que significa que estamos en el fragmento superior
//            val isBackStackEmpty = fragmentManager.backStackEntryCount == 0
//
//            // Ajusta la visibilidad del ViewPager y las pestañas en consecuencia
//            binding.viewPager.visibility = if (isBackStackEmpty) View.VISIBLE else View.GONE
//            binding.tabs.visibility = if (isBackStackEmpty) View.VISIBLE else View.GONE
//        }
//    }



}