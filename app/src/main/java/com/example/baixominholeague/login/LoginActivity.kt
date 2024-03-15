package com.example.baixominholeague.login

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.InputType
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatDelegate
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment

import com.example.baixominholeague.MainActivity
import com.example.baixominholeague.R
import com.example.baixominholeague.databinding.ActivityLoginBinding
import com.example.baixominholeague.login.ResetPassword.Companion.CLAVE_CORREO_RESET
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.GoogleAuthProvider

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

        loadTheme()

        setup()
        session()
        showPassword()

    }

    private fun loadTheme() {
        val sharedPrefs = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        var themeMode = sharedPrefs.getInt("themeMode", AppCompatDelegate.MODE_NIGHT_NO)


        if (themeMode != AppCompatDelegate.getDefaultNightMode()) {
            AppCompatDelegate.setDefaultNightMode(themeMode)
            recreate()
            Log.i("GAB","RECREADO !")
        }
    }
    private fun setup() {

        binding.tvNewUser.setOnClickListener { startActivity(Intent(this, RegisterActivity::class.java)) }

        binding.buttomLogin.setOnClickListener { login() }

        binding.buttomGoogle.setOnClickListener { googleInit() }

        binding.tvResetPassword.setOnClickListener { showResetPassword(binding.editTextNombre.text.toString()?:"") }


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
            showHome()
        }
    }

    private fun login() {
        if (checkEmpty(
                binding.editTextNombre.text.toString(),
                binding.editTextPassword.text.toString()
            )
        ) {
            binding.progresBarLogin.visibility = View.VISIBLE
            FirebaseAuth.getInstance()
                .signInWithEmailAndPassword(
                    binding.editTextNombre.text.toString(),
                    binding.editTextPassword.text.toString()
                ).addOnCompleteListener {
                    binding.progresBarLogin.visibility = View.INVISIBLE
                    if (it.isSuccessful) {
                        showHome()

                    } else {
                        showAlert()

                    }
                }
                .addOnFailureListener { binding.progresBarLogin.visibility = View.INVISIBLE}
        }
    }

    private fun showHome() {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }

    private fun showResetPassword(email: String?) {
        val homeIntent: Intent = Intent(this, ResetPassword::class.java).apply {
            putExtra(CLAVE_CORREO_RESET, email)
        }
        startActivity(homeIntent)
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

        binding.progresBarLogin.visibility = View.VISIBLE
        if (requestCode == GOOGLE_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)

            try {
                val account = task.getResult(ApiException::class.java)

                if (account != null) {

                    val credential = GoogleAuthProvider.getCredential(account.idToken, null)
                    FirebaseAuth.getInstance().signInWithCredential(credential)
                        .addOnCompleteListener {
                            binding.progresBarLogin.visibility = View.INVISIBLE
                            if (it.isSuccessful) {
                                showHome()
                            } else {
                                showAlert()
                            }
                        }
                }
            } catch (e: ApiException) {
                binding.progresBarLogin.visibility = View.INVISIBLE
                showAlert()
            }

        }
    }

    private fun showPassword() {
        var isPasswordVisible = false
        val editextPassword = binding.editTextPassword
        var inputType: Int

        binding.btnshowPassword.setOnClickListener {
            isPasswordVisible = !isPasswordVisible
            if (isPasswordVisible) {
                inputType = InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
                binding.btnshowPassword.setImageResource(R.drawable.baseline_visibility_off_24)

            } else {
                inputType = InputType.TYPE_TEXT_VARIATION_PASSWORD or InputType.TYPE_CLASS_TEXT
                binding.btnshowPassword.setImageResource(R.drawable.outline_visibility_24)
            }

            editextPassword.inputType = inputType
            editextPassword.setSelection(editextPassword.text.length)
        }
    }
}