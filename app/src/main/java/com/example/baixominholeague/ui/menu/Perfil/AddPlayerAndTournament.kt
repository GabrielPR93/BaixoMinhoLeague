package com.example.baixominholeague.ui.menu.Perfil

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import com.example.baixominholeague.R
import com.example.baixominholeague.data.Jugador
import com.example.baixominholeague.databinding.ActivityAddPlayerAndTournamentBinding
import com.example.baixominholeague.ui.menu.Jugadores.JugadoresFragment
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.tasks.await
import java.io.Serializable
import java.util.*

class AddPlayerAndTournament : AppCompatActivity() {

    private lateinit var binding: ActivityAddPlayerAndTournamentBinding
    private val jugadoresFragment = JugadoresFragment()
    private val playerMatrix = mutableListOf<HashMap<String, Serializable>>()
    private val playerScores = HashMap<CheckBox, EditText>()
    private val db = FirebaseFirestore.getInstance()
    private val jugadores = mutableListOf<Jugador>()
    private val ligas = mutableListOf<String>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddPlayerAndTournamentBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initUI()
        binding.btnBackPerfil.setOnClickListener {
            onBackPressed()
        }
        binding.btnAddPlayer.setOnClickListener {
            saveNewPlayer()
        }
        binding.btnAddTournament.setOnClickListener {
            binding.linearLayoutPlayers.clearFocus()


        }
    }

    private fun initUI() {
        Spinner()
        setupPlayers()
    }

    private fun Spinner(){
        val nombreLiga = resources.getStringArray(R.array.liga_options).toList()
        val divisiones = mutableListOf<String>()

        val adapterSpinner = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_dropdown_item,
            nombreLiga
        )
        val adapterSpinner2 = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_dropdown_item,
            divisiones
        )

        adapterSpinner.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        adapterSpinner2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        binding.spinnerAddLiga.adapter = adapterSpinner
        binding.spinnerAddDivision.adapter = adapterSpinner2

        binding.spinnerAddLiga.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parentView: AdapterView<*>?,
                selectedItemView: View?,
                position: Int,
                id: Long
            ) {
                divisiones.clear()
               var seleccionLigaSpinner1 = nombreLiga[position]

                val divisionOptionsResourceId = resources.getIdentifier(
                    "division_options_${quitarEspacios(seleccionLigaSpinner1)}",
                    "array",
                    this@AddPlayerAndTournament.packageName
                )
                if (divisionOptionsResourceId != 0) {
                    val divisionOptions =
                        resources.getStringArray(divisionOptionsResourceId)?.toList() ?: emptyList()
                    divisiones.addAll(divisionOptions)
                }
                binding.spinnerAddDivision.setSelection(0)
                // Notificar al adaptador del segundo Spinner sobre el cambio en los datos
                adapterSpinner2.notifyDataSetChanged()

            }

            override fun onNothingSelected(parentView: AdapterView<*>?) {
                // No se ha seleccionado nada
            }
        }

    }
    fun quitarEspacios(frase: String): String {
        return frase.replace("\\s+".toRegex(), "")
    }
    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun uiPlayers(jugadores: List<Jugador>) {
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

            binding.linearLayoutPlayers.addView(linearLayout)

            val playerData = HashMap<String, Serializable>()

            checkBox.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) {
                    val playerName = player.nombre

                    if (!playerName.isNullOrEmpty()) {
                        playerData["nombre"] = playerName

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
        }
    }

    // Extension function to convert dp to pixels
    private fun Int.dpToPx(): Int {
        val scale = resources.displayMetrics.density
        return (this * scale + 0.5f).toInt()
    }

    private fun setupPlayers() {
        val jugadoresCollectionRef = db.collection("jugadores").whereEqualTo("equipo",false)

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

            jugadoresCollection.whereEqualTo("nombre", nombreCapitalizado).get()
                .addOnSuccessListener { querySnapshot ->
                    if (querySnapshot.isEmpty) {

                        db.runTransaction { transaction ->
                            val jugadorRef = db.collection("counter").document("counter")
                            val snapshot = transaction.get(jugadorRef)
                            val currentCount = snapshot.getLong("count") ?: 0
                            val nextCount = currentCount + 1

                            val jugadorId = nextCount.toInt()
                            val jugadorData = hashMapOf(
                                "nombre" to nombreCapitalizado,
                                "correo" to correo,
                                "id" to jugadorId,
                                "equipo" to false
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
                            showToast("Guardado correctamente")
                            binding.editextAddNombrePlayer.setText("")
                            binding.editextAddCorreoPlayer.setText("")
                            binding.editextAddNombrePlayer.clearFocus()
                        }.addOnFailureListener { e ->
                            showToast("Error al guardar jugador")
                        }
                            .addOnCompleteListener { } //<-- onPlayerAdded() Todo Cambiar este metodo
                    } else {
                        showToast("Ya existe un jugador con este nombre")
                    }
                }
        } else {
            showToast("Por favor, ingrese un nombre válido")
        }
    }
}