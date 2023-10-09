package com.example.baixominholeague.ui.menu.Jugadores

import android.content.ContentResolver
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.core.view.isVisible
import com.example.baixominholeague.R
import com.example.baixominholeague.databinding.ActivityDetailJugadorBinding
import com.google.firebase.firestore.FirebaseFirestore
import com.squareup.picasso.Picasso

class DetailJugador : AppCompatActivity() {
    companion object {
        const val ID_PLAYER = "id_player"
    }

    private var db = FirebaseFirestore.getInstance()
    private var playerId: Int? = null
    private lateinit var binding: ActivityDetailJugadorBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailJugadorBinding.inflate(layoutInflater)
        setContentView(binding.root)

        playerId = intent.getIntExtra(ID_PLAYER,-1)

        getData()

        binding.btnBack.setOnClickListener {
            onBackPressed()
        }
    }

    private fun getData() {

        var collection = db.collection("jugadores")

        collection.get().addOnSuccessListener { document ->

            for (player in document) {
                var correo = player.getString("correo")
                var nombre = player.getString("nombre")
                var id = player.getLong("id")?.toInt()

                if(playerId==id){
                    if (correo != null && nombre!=null) {
                        getDataEmail(correo)
                        loadPositions(nombre)

                    }
                }
            }

        }.addOnFailureListener { exception ->
                Log.i("GAB","Error al acceder")
            }
    }

    private fun getDataEmail(correo: String) {
        var userCollection = db.collection("users")

        userCollection.get()
            .addOnSuccessListener { result ->
                var documentExist = false
                for (document in result) {
                    if(document.id==correo){
                        documentExist = true
                        var alias = document.getString("alias")
                        var nombre = document.getString("nombre")
                        var localidad = document.getString("localidad")
                        var posiciones = document.getString("posiciones")
                        var telefono = document.getString("telefono")
                        var foto = document.getString("foto")
                        var otros = document.getString("otros")

                        if(alias !=null && nombre!=null && localidad!=null && posiciones!=null && telefono!=null && foto!=null && otros !=null){
                            setupUi(alias,nombre,telefono,correo,localidad,posiciones,foto,otros)

                        }
                    }
                    if(!documentExist){
                        val defaultFotoUri = Uri.parse(
                            ContentResolver.SCHEME_ANDROID_RESOURCE +
                                    "://" + resources.getResourcePackageName(R.drawable.profile) +
                                    "/" + resources.getResourceTypeName(R.drawable.profile) +
                                    "/" + resources.getResourceEntryName(R.drawable.profile)
                        )
                        setupUi("","","",correo ,"","",defaultFotoUri.toString(),"")
                    }
                }
            }
            .addOnFailureListener { exception ->
                Log.i("GAB","Error al acceder")
            }

    }

    private fun setupUi(alias: String, nombre: String, telefono: String, correo: String, localidad: String, posiciones: String, foto: String, otros: String) {

        if(!foto.isNullOrEmpty()){
            Picasso.get().load(Uri.parse(foto)).into(binding.myCircleImageView)
            binding.ProgresBarImageView.isVisible=false
        }
        binding.tvAlias.setText(alias)
        binding.tvNombre.setText(nombre)
        binding.tvTelefono.setText(telefono)
        binding.tvCorreo.setText(correo)
        binding.tvLocalidad.setText(localidad)
        binding.tvOtros.setText(otros)
        binding.tvPosiciones.setText(posiciones)

    }

    private fun loadPositions(nombreJugadorBuscado: String) {
        val collectionRef = db.collection("clasificacionMovimiento")
        val stringBuilder = StringBuilder()
        val separador = "◈ "

        collectionRef.get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    val nombreDocumento = document.id
                    val matrizJugadores = document.get("jugadores") as ArrayList<HashMap<String, Any>>

                    for (jugador in matrizJugadores) {

                        val nombreJugador = jugador["nombre"] as String
                        val puntuacionJugador = jugador["puntuacion"] as String
                        val puntuacionJugadorInt = puntuacionJugador.toInt()

                        if (nombreJugador == nombreJugadorBuscado && puntuacionJugadorInt > 0) {

                            when(puntuacionJugadorInt){
                                25 -> stringBuilder.append(separador).append(nombreDocumento).append("  -  ").append("1ª Posición").appendLine().append("\n")
                                18 -> stringBuilder.append(separador).append(nombreDocumento).append("  -  ").append("2ª Posición").appendLine().append("\n")
                                10 -> stringBuilder.append(separador).append(nombreDocumento).append("  -  ").append("3ª Posición").appendLine().append("\n")
                                else ->  stringBuilder.append(separador).append(nombreDocumento).appendLine().append("\n")
                            }
                        }
                    }
                    binding.tvTorneosDisputados.text=stringBuilder.toString()
                }
            }
            .addOnFailureListener { exception ->
                Log.i("GAB","Error al acceder")
            }
    }
}