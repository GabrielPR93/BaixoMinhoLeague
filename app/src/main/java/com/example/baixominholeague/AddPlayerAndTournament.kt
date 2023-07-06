package com.example.baixominholeague

import android.app.ActionBar.LayoutParams
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.InputFilter
import android.text.InputType
import android.text.method.DigitsKeyListener
import android.util.Log
import android.widget.*
import androidx.constraintlayout.widget.ConstraintLayout
import com.example.baixominholeague.data.Jugador
import com.example.baixominholeague.databinding.ActivityAddPlayerAndTournamentBinding
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import java.util.*

class AddPlayerAndTournament : AppCompatActivity() {

    private lateinit var binding: ActivityAddPlayerAndTournamentBinding
    private val db = FirebaseFirestore.getInstance()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityAddPlayerAndTournamentBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupPlayers()
        binding.btnBackPerfil.setOnClickListener{
            onBackPressed()
        }
        binding.btnAddPlayer.setOnClickListener{
            saveNewPlayer()
        }

    }

    private fun uiPlayers(jugadores: MutableList<Jugador>) {
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
            nameTextView.layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
            linearLayout.addView(nameTextView)

            val checkBox = CheckBox(this)
            linearLayout.addView(checkBox)

            val editTextParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
            editTextParams.setMargins(25.dpToPx(), 0, 0, 0)

            val editText = EditText(this)
            editText.hint = "Puntuación"
            editText.inputType=InputType.TYPE_CLASS_NUMBER
            editText.keyListener = DigitsKeyListener.getInstance("0123456789")
            editText.filters = arrayOf<InputFilter>(InputFilter.LengthFilter(2))
            editText.maxLines = 1
            editText.setBackgroundColor(Color.WHITE)
            editText.layoutParams = editTextParams
            linearLayout.addView(editText)

            binding.linearLayoutPlayers.addView(linearLayout)
        }

        val buttonLayoutParams = ConstraintLayout.LayoutParams(ConstraintLayout.LayoutParams.MATCH_PARENT,ConstraintLayout.LayoutParams.WRAP_CONTENT)
        val button = Button(this)
        button.text = "Añadir Torneo"
        button.setBackgroundResource(R.drawable.searchbackground)
        button.layoutParams=buttonLayoutParams
        button.layoutParams
        binding.linearLayoutButton.addView(button)

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
            val jugadores = mutableListOf<Jugador>()
                for (document in it) {
                    val jugador = document.toObject(Jugador::class.java)
                    if (jugador != null) {

                        jugadores.add(jugador)
                    }
                }
            uiPlayers(jugadores)
            }
        }

    private fun saveNewPlayer() {
        val jugadorRef = db.collection("counter").document("counter")

        db.runTransaction { transaction ->
            val snapshot = transaction.get(jugadorRef)
            val currentCount = snapshot.getLong("count") ?: 0
            val nextCount = currentCount + 1

            val jugadorId = nextCount.toInt()
            val nombre = binding.editextAddNombrePlayer.text.toString()
            val nombreCapitalizado = nombre.substring(0, 1).uppercase() + nombre.substring(1).lowercase()
            val correo = binding.editextAddCorreoPlayer.text.toString()

            if (nombre.isNotEmpty()) {
                val jugadorData = hashMapOf(
                    "nombre" to nombreCapitalizado,
                    "correo" to correo,
                    "id" to jugadorId
                )

                val jugadoresCollection = db.collection("jugadores")
                transaction.set(jugadoresCollection.document(nombreCapitalizado+jugadorId.toString()), jugadorData)
                transaction.update(jugadorRef, "count", nextCount)

                runOnUiThread {
                    Toast.makeText(this, "Guardado correctamente", Toast.LENGTH_SHORT).show()
                }
            }

            null
        }.addOnSuccessListener {
            // La transacción se completó exitosamente
            binding.editextAddNombrePlayer.setText("")
            binding.editextAddNombrePlayer.clearFocus()
            binding.editextAddCorreoPlayer.setText("")
        }.addOnFailureListener { e ->
            Toast.makeText(this, "Error al guardar: ${e.message}", Toast.LENGTH_SHORT).show()
            Log.i("GABRI","${e.message}")
        }
    }

}