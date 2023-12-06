package com.example.baixominholeague.ui.menu.Perfil

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import com.example.baixominholeague.R
import com.example.baixominholeague.data.Equipo
import com.example.baixominholeague.data.Jugador
import com.example.baixominholeague.databinding.ActivityAddPlayerAndTournamentBinding
import com.example.baixominholeague.ui.menu.Jugadores.JugadoresFragment
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import java.util.*

class AddPlayerAndTournament : AppCompatActivity() {

    private lateinit var binding: ActivityAddPlayerAndTournamentBinding
    private val jugadoresFragment = JugadoresFragment()
    private val playersTeam = mutableListOf<Jugador>()
    private val playerScores = HashMap<CheckBox, EditText>()
    private val db = FirebaseFirestore.getInstance()
    private val jugadores = mutableListOf<Jugador>()
    private var selectedLiga = ""
    private var selectedDivision = ""


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
        binding.btnAddTeam.setOnClickListener {
            binding.linearLayoutPlayers.clearFocus()
            saveNewTeam()

        }
    }

    private fun initUI() {
        Spinner()
        setupPlayers()
    }

    private fun Spinner() {
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

        binding.spinnerAddLiga.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parentView: AdapterView<*>?,
                    selectedItemView: View?,
                    position: Int,
                    id: Long
                ) {
                    divisiones.clear()
                    selectedLiga = nombreLiga[position]

                    val divisionOptionsResourceId = resources.getIdentifier(
                        "division_options_${quitarEspacios(selectedLiga)}",
                        "array",
                        this@AddPlayerAndTournament.packageName
                    )
                    if (divisionOptionsResourceId != 0) {
                        val divisionOptions =
                            resources.getStringArray(divisionOptionsResourceId)?.toList()
                                ?: emptyList()
                        divisiones.addAll(divisionOptions)
                    }
                    if (divisiones.isNotEmpty()) {
                        binding.spinnerAddDivision.setSelection(0)
                        selectedDivision = divisiones[0]
                    }

                    // Notificar al adaptador del segundo Spinner sobre el cambio en los datos
                    adapterSpinner2.notifyDataSetChanged()

                }

                override fun onNothingSelected(parentView: AdapterView<*>?) {

                }
            }

        binding.spinnerAddDivision.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parentView: AdapterView<*>?,
                    selectedItemView: View?,
                    position: Int,
                    id: Long
                ) {
                    selectedDivision = divisiones[position]

                }

                override fun onNothingSelected(parentView: AdapterView<*>?) {

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

            checkBox.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) {
                    if (!playersTeam.contains(player)) {
                        playersTeam.add(player)
                        Log.i("GABRI", "Jugador agregado: $player")
                    }
                } else {
                    playersTeam.remove(player)
                    Log.i("GABRI", "Jugador removido: $player")
                }
            }
        }
    }

    private fun Int.dpToPx(): Int {
        val scale = resources.displayMetrics.density
        return (this * scale + 0.5f).toInt()
    }

    private fun setupPlayers() {
        val jugadoresCollectionRef = db.collection("jugadores").whereEqualTo("equipo", false)

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

    private fun afterSave() {
        binding.tvAddNombreTeam.setText("")
        binding.btnBackPerfil.requestFocus()
        updatePlayers()
    }

    private fun updatePlayers() {
        val jugadoresRef = db.collection("jugadores")
        val playerNames = playersTeam.map { it.nombre }


        // Realiza una consulta para obtener los documentos que contienen el nombre del jugador
        Log.i("GAbri", "NOMBRES : $playerNames")

        val query = jugadoresRef.whereIn("nombre", playerNames)

        // Obtiene los documentos y actualiza el campo "equipo" en cada uno
        query.get().addOnSuccessListener { documents ->
            db.runTransaction { transaction ->
                for (document in documents) {
                    Log.i("GAbri", "DOcument : ${document.id.toString()}")
                    // Actualiza el campo "equipo" con el nuevo valor
                    transaction.update(document.reference, "equipo", true)
                }
                null
            }
        }.addOnCompleteListener {
            jugadores.removeIf { jugador -> playerNames.contains(jugador.nombre) }
            uiPlayers(jugadores)
            playersTeam.clear()
        }
    }

    private fun saveNewTeam() {
        val nameTeam = binding.tvAddNombreTeam.text.toString().trim()

        if (nameTeam.isNotEmpty() && selectedLiga != "" && selectedDivision != "") {

            // Obtén la referencia al documento "counter"
            val counterRef = db.collection("counter").document("counter")

            // Realiza una transacción para obtener y actualizar el contador
            db.runTransaction { transaction ->
                val currentCount = transaction.get(counterRef).getLong("counterteam") ?: 0
                val newCount = currentCount + 1

                // Actualiza el contador en la transacción
                transaction.update(counterRef, "counterteam", newCount)

                // Crea el nuevo equipo con el ID actualizado
                val nuevoEquipo = Equipo(
                    newCount.toInt(),
                    nameTeam,
                    selectedDivision,
                    playersTeam,
                    partidosTotales = mapOf(
                        "puntos" to 0,
                        "partidosJugados" to 0,
                        "partidosGanados" to 0,
                        "partidosEmpatados" to 0,
                        "partidosPerdidos" to 0
                    )

                )

                val equiposRef = db.collection("equipos").document(selectedLiga)
                transaction.update(equiposRef, "equipos", FieldValue.arrayUnion(nuevoEquipo))

                // Devuelve null para indicar el éxito de la transacción
                null
            }.addOnSuccessListener {
                showToast("Equipo guardado correctamente")
                afterSave()
            }.addOnFailureListener { exception ->
                Log.w("SaveTeam", "Error al guardar un nuevo equipo", exception)
            }
        } else {
            showToast("El nombre del equipo no puede estar vacío")
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