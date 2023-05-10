package com.example.baixominholeague.ui.menu.login

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.example.baixominholeague.MainActivity
import com.example.baixominholeague.R
import com.example.baixominholeague.databinding.ActivityLoginBinding
import com.google.android.gms.auth.api.Auth
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.oAuthCredential
import com.google.firebase.auth.ktx.oAuthProvider

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var googleClient: GoogleSignInClient
    private val GOOGLE_SIGN_IN = 200

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.Theme_BaixoMinhoLeagueSinActionBar)
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //Analytics Event
        val analytics: FirebaseAnalytics = FirebaseAnalytics.getInstance(this)
        val bundle = Bundle()
        bundle.putString("message", "Integracion de Firebase")
        analytics.logEvent("InitScreen", bundle)


        setup()
        session()

    }

    private fun setup() {

        binding.buttomRegister.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }

        binding.buttomLogin.setOnClickListener {
            login()
        }

        binding.buttomGoogle.setOnClickListener {
            googleInit()
        }

    }

    private fun googleInit() {
        val googleConf = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        googleClient = GoogleSignIn.getClient(this, googleConf)
        googleClient.signOut()

        startActivityForResult(googleClient.signInIntent, GOOGLE_SIGN_IN)
    }


    private fun session() {

        val prefs = getSharedPreferences(getString(R.string.prefs_file), Context.MODE_PRIVATE)
        val correo = prefs.getString("email", null)

        if (correo != null) {

            showHome(correo)
        }
    }

    private fun login() {
        if (checkEmpty(
                binding.editTextNombre.text.toString(),
                binding.editTextPassword.text.toString()
            )
        ) {
            FirebaseAuth.getInstance()
                .signInWithEmailAndPassword(
                    binding.editTextNombre.text.toString(),
                    binding.editTextPassword.text.toString()
                ).addOnCompleteListener {
                    if (it.isSuccessful) {
                        showHome(it.result?.user?.email ?: "")

                    } else {
                        showAlert()

                    }
                }
        }
    }

    private fun showHome(email: String) {
        val homeIntent: Intent = Intent(this, MainActivity::class.java).apply {
            putExtra("correo", email)
        }
        startActivity(homeIntent)
        finish()
    }

    private fun showAlert() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Error")
        builder.setMessage("Se ha producido un error autenticando al usuario")
        builder.setPositiveButton("Aceptar", null)
        val dialog: AlertDialog = builder.create()
        dialog.show()
    }

    private fun checkEmpty(email: String, password: String): Boolean {
        return email.isNotEmpty() && password.isNotEmpty()
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == GOOGLE_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)

            try {
                val account = task.getResult(ApiException::class.java)

                if (account != null) {

                    val credential = GoogleAuthProvider.getCredential(account.idToken, null)
                    FirebaseAuth.getInstance().signInWithCredential(credential)
                        .addOnCompleteListener {
                            if (it.isSuccessful) {
                                showHome(account.email ?: "")
                            } else {
                                showAlert()
                            }
                        }
                }
            } catch (e: ApiException) {
                showAlert()
            }

        }
    }
}