package com.example.baixominholeague

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.example.baixominholeague.databinding.ActivityDetailJugadorBinding
import com.example.baixominholeague.databinding.ActivityMainBinding
import com.google.firebase.firestore.FirebaseFirestore

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
    }

    private fun getData() {

        var collection = db.collection("jugadores")

        collection.get().addOnSuccessListener { document ->

            for (player in document) {
                var correo = player.getString("correo")
                var id = player.getLong("id")?.toInt()

                if(playerId==id){
                    if (correo != null) {
                        getDataEmail(correo)
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
                for (document in result) {
                    if(document.id==correo){
                        var alias = document.getString("alias")
                        var nombre = document.getString("nombre")
                        var localidad = document.getString("localidad")
                        var posiciones = document.getString("posiciones")
                        var telefono = document.getString("telefono")

                        if(alias !=null && nombre!=null && localidad!=null && posiciones!=null && telefono!=null){
                            setupUi(alias,nombre,telefono,correo,localidad,posiciones)

                        }
                    }
                }
            }
            .addOnFailureListener { exception ->
                Log.i("GAB","Error al acceder")
            }

    }

    private fun setupUi(alias: String, nombre: String, telefono: String, correo: String, localidad: String, posiciones: String) {

        binding.tvAlias.setText(alias)
        binding.tvNombre.setText(nombre)
        binding.tvTelefono.setText(telefono)
        binding.tvCorreo.setText(correo)
        binding.tvLocalidad.setText(localidad)
        binding.tvPosiciones.setText(posiciones)
    }
}