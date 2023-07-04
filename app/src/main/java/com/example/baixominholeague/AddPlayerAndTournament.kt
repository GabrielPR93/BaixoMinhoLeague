package com.example.baixominholeague

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
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

        binding.btnBackPerfil.setOnClickListener{
            onBackPressed()
        }
        binding.btnAddPlayer.setOnClickListener{
            saveNewPlayer()
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