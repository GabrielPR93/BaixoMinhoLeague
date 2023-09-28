package com.example.baixominholeague

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.InputFilter
import android.text.InputType
import android.text.method.DigitsKeyListener
import android.util.Log
import android.view.View
import android.widget.*
import com.example.baixominholeague.data.Jugador
import com.example.baixominholeague.databinding.ActivityAddPlayerAndTournamentBinding
import com.example.baixominholeague.databinding.FragmentJugadoresBinding
import com.example.baixominholeague.ui.menu.Jugadores.JugadoresFragment
import com.example.baixominholeague.ui.menu.Jugadores.OnPlayerAddedListener
import com.google.firebase.firestore.FirebaseFirestore
import java.io.Serializable
import java.util.*

class AddPlayerAndTournament : AppCompatActivity(), OnPlayerAddedListener {

    private lateinit var binding: ActivityAddPlayerAndTournamentBinding
    private val jugadoresFragment = JugadoresFragment()
    private val playerMatrix = mutableListOf<HashMap<String, Serializable>>()
    private val playerScores = HashMap<CheckBox, EditText>()
    private val db = FirebaseFirestore.getInstance()
    private val jugadores = mutableListOf<Jugador>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddPlayerAndTournamentBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupPlayers()
        binding.btnBackPerfil.setOnClickListener {
            onBackPressed()
        }
        binding.btnAddPlayer.setOnClickListener {
            saveNewPlayer()
        }
        binding.btnAddTournament.setOnClickListener {
            binding.linearLayoutPlayers.clearFocus()
            saveTournament()

        }
    }

    private fun saveTournament() {
        val nameDocument = binding.tvAddTournament.text.toString().uppercase()
        if (!nameDocument.isNullOrEmpty() && playerMatrix.isNotEmpty()) {
            Log.i("GABRI", "PLAYERMATRIX: $playerMatrix y PLAYERSCORE: $playerScores")
            db.collection("clasificacionMovimiento").document(nameDocument).set(
                hashMapOf("jugadores" to playerMatrix)
            ).addOnSuccessListener {
                Toast.makeText(this, "Añadido correctamente", Toast.LENGTH_SHORT).show()

                binding.tvAddTournament.text = null
                playerScores.values.forEach { editText -> editText.text = null }
                playerScores.keys.forEach { checkBox -> checkBox.isChecked = false }
                playerMatrix.clear()

            }
        } else {
            if (nameDocument.isNullOrEmpty()) showToast("Error al guardar: Nombre de torneo necesario") else showToast(
                "Error al guardar: Jugadores necesarios"
            )
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun uiPlayers(jugadores: MutableList<Jugador>) {
        binding.linearLayoutPlayers.removeAllViews()//eliminar la vista para actualizar

        val layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )

        for (player in jugadores) {

            val linearLayout = LinearLayout(this)
            linearLayout.orientation = LinearLayout.HORIZONTAL
            linearLayout.layoutParams = layoutParams

            val nameTextView = TextView(this)
            nameTextView.text = player.nombre
            nameTextView.textSize = 16f
            nameTextView.layoutParams =
                LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
            linearLayout.addView(nameTextView)

            val checkBox = CheckBox(this)
            linearLayout.addView(checkBox)

            val editTextParams =
                LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
            editTextParams.setMargins(25.dpToPx(), 0, 0, 0)

            val editText = EditText(this)
            editText.hint = "Puntuación"
            editText.inputType = InputType.TYPE_CLASS_NUMBER
            editText.keyListener = DigitsKeyListener.getInstance("0123456789")
            editText.filters = arrayOf<InputFilter>(InputFilter.LengthFilter(2))
            editText.maxLines = 1
            editText.setBackgroundColor(Color.WHITE)
            editText.layoutParams = editTextParams
            linearLayout.addView(editText)

            playerScores[checkBox] = editText

            binding.linearLayoutPlayers.addView(linearLayout)

            val playerData = HashMap<String, Serializable>()

            checkBox.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) {
                    val playerName = player.nombre
                    val playerScore = editText.text.toString()

                    if (!playerName.isNullOrEmpty() && !playerScore.isNullOrEmpty()) {
                        playerData["nombre"] = playerName
                        playerData["puntuacion"] = playerScore

                        if (!playerMatrix.contains(playerData)) {
                            playerMatrix.add(playerData)
                            Log.i("GABRI", "PLAYERMATRIX_ADD_check: $playerData")
                        }
                        Log.i("GABRI", "TAMAÑOOO check: " + playerMatrix.size.toString())
                    }
                } else {
                    playerMatrix.remove(playerData)
                    Log.i("GABRI", "TAMAÑOOO check: " + playerMatrix.size.toString())
                }
            }

            //Guardar los valores
            editText.onFocusChangeListener = View.OnFocusChangeListener { _, hasFocus ->
                if (!hasFocus) {
                    val playerName = player.nombre
                    val playerScore = editText.text.toString()

                    if (!playerName.isNullOrEmpty() && !playerScore.isNullOrEmpty()) {
                        //val playerData = HashMap<String, Serializable>()
                        playerData["nombre"] = playerName
                        playerData["puntuacion"] = playerScore

                        if (checkBox.isChecked) {
                            if (!playerMatrix.contains(playerData)) {
                                playerMatrix.add(playerData)
                            }
                            Log.i("GABRI", "PLAYERMATRIX_ADD: $playerData")

                        } else {
                            playerMatrix.remove(playerData)
                            Log.i("GABRI", "PLAYERMATRIX_REMOVE: $playerData")
                        }
                        Log.i("GABRI", "TAMAÑOOO: " + playerMatrix.size.toString())
                    }
                }
            }
        }
    }

    // Extension function to convert dp to pixels
    private fun Int.dpToPx(): Int {
        val scale = resources.displayMetrics.density
        return (this * scale + 0.5f).toInt()
    }

    private fun setupPlayers() {
        //Obtiene todos los jugadores
        val jugadoresCollectionRef = db.collection("jugadores")

        jugadoresCollectionRef.get().addOnSuccessListener {
            for (document in it) {
                val jugador = document.toObject(Jugador::class.java)
                if (jugador != null) {

                    jugadores.add(jugador)
                }
            }
            jugadores.sortBy { it.nombre }
            uiPlayers(jugadores)
        }
    }

    private fun saveNewPlayer() {
        val nombre = binding.editextAddNombrePlayer.text.toString().trim()
        val correo = binding.editextAddCorreoPlayer.text.toString()

        if (nombre.isNotEmpty()) {
            val nombreCapitalizado =
                nombre.substring(0, 1).uppercase() + nombre.substring(1).lowercase()
            val jugadoresCollection = db.collection("jugadores")

            // Verificar si ya existe un jugador con el mismo nombre
            jugadoresCollection.whereEqualTo("nombre", nombreCapitalizado).get()
                .addOnSuccessListener { querySnapshot ->
                    if (querySnapshot.isEmpty) {
                        // No existe un jugador con el mismo nombre, puedes guardarlo
                        db.runTransaction { transaction ->
                            val jugadorRef = db.collection("counter").document("counter")
                            val snapshot = transaction.get(jugadorRef)
                            val currentCount = snapshot.getLong("count") ?: 0
                            val nextCount = currentCount + 1

                            val jugadorId = nextCount.toInt()
                            val jugadorData = hashMapOf(
                                "nombre" to nombreCapitalizado,
                                "correo" to correo,
                                "id" to jugadorId
                            )

                            transaction.set(
                                jugadoresCollection.document(nombreCapitalizado + jugadorId.toString()),
                                jugadorData
                            )
                            transaction.update(jugadorRef, "count", nextCount)

                            jugadores.add(Jugador(jugadorId, nombreCapitalizado, correo))
                            jugadores.sortBy { it.nombre }
                            runOnUiThread {
                                uiPlayers(jugadores)
                            }

                            null
                        }.addOnSuccessListener {
                            // La transacción se completó exitosamente
                            Toast.makeText(this, "Guardado correctamente", Toast.LENGTH_SHORT)
                                .show()
                            binding.editextAddNombrePlayer.setText("")
                            binding.editextAddCorreoPlayer.setText("")
                            binding.editextAddNombrePlayer.clearFocus()
                        }.addOnFailureListener { e ->
                            Toast.makeText(this, "Error al guardar jugador", Toast.LENGTH_SHORT)
                                .show()
                        }
                            .addOnCompleteListener { onPlayerAdded() }
                    } else {
                        // Ya existe un jugador con el mismo nombre, muestra un mensaje de error
                        Toast.makeText(
                            this,
                            "Ya existe un jugador con este nombre",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
        } else {
            // El campo de nombre está vacío, muestra un mensaje de error
            Toast.makeText(this, "Por favor, ingrese un nombre válido", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onPlayerAdded() {
            jugadoresFragment?.onPlayerAdded()
    }

}