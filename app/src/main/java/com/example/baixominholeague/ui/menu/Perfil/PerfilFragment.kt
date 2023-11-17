package com.example.baixominholeague.ui.menu.Perfil

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import android.widget.ImageView
import android.widget.Toast

import androidx.appcompat.app.AlertDialog


import androidx.core.view.isVisible
import androidx.navigation.fragment.findNavController
import com.example.baixominholeague.MainActivity
import com.example.baixominholeague.R
import com.example.baixominholeague.databinding.FragmentPerfilBinding
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.squareup.picasso.Picasso


class PerfilFragment : Fragment() {

    private var _binding: FragmentPerfilBinding? = null
    private val binding get() = _binding!!
    private val currentUser = FirebaseAuth.getInstance().currentUser
    private var correo: String? = null
    private val db = FirebaseFirestore.getInstance()

    private val REQUEST_CODE_IMAGE_PICKER = 102
    private var selectedImageUri: String? = ""
    lateinit var imageViewPerfil: ImageView

    companion object {
        const val CORREO_ADMIN = "admin@gmail.com"
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentPerfilBinding.inflate(inflater, container, false)
        val view = binding.root

        if(currentUser?.email!=null){correo=currentUser.email}

        binding.btnSelectImage.setOnClickListener {
            val mainActivity = requireActivity() as MainActivity
            launchImagePicker()
            if (mainActivity.arePermissionsGranted()) {
                Log.i("GAB", "Los permisos están concedidos")
            } else {
                mainActivity.requestPermissions()
                Log.i("GAB", "Los permisos no están concedidos")
            }
        }
        binding.btnAddPlayer.setOnClickListener {

            startActivity(Intent(requireContext(), AddPlayerAndTournament::class.java))
        }

        getDataUser()
        showMenuEdit()
        saveData()
        deleteData()

        configuration()

        return view
    }
    private fun getDataUser(){
        if(correo!=null){
            getDataUserEmail(correo!!){ dataUser ->
                if(dataUser != null){
                    setupUi(
                        dataUser["alias"] as? String ?: "",
                        dataUser["nombre"] as? String ?: "",
                        dataUser["telefono"] as? String ?: "",
                        dataUser["localidad"] as? String ?: "",
                        dataUser["otros"] as? String ?: "",
                        dataUser["posiciones"] as? String ?: "",
                        dataUser["foto"] as? String ?: "",
                    )
                }else{
                    println("Usuario no encontrado o error en la consulta")}
            }
        }

    }
    fun getDataUserEmail(correo: String, onComplete: (Map<String, Any>?) -> Unit) {
        val db = FirebaseFirestore.getInstance()
        val usersCollection = db.collection("users")

        val query = usersCollection.document(correo)

        query.get()
            .addOnCompleteListener { task: Task<DocumentSnapshot> ->
                if (task.isSuccessful) {
                    val document = task.result

                    if (document != null && document.exists()) {
                        val datosUsuario = document.data
                        onComplete(datosUsuario)
                    } else {
                        // El documento no existe
                        onComplete(null)
                    }
                } else {
                    // Hubo un error en la consulta
                    onComplete(null)
                }
            }
    }

    private fun configuration(){
        binding.buttomLogout.setOnClickListener {
            findNavController().navigate(R.id.action_perfilFragment_to_configuracion)
        }
    }
    fun launchImagePicker() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        intent.type = "image/*"
        startActivityForResult(intent, REQUEST_CODE_IMAGE_PICKER)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_IMAGE_PICKER && resultCode == Activity.RESULT_OK && data != null) {
            selectedImageUri = data.data.toString()
            // Aquí sé guarda la imagen en Firebase Storage
            uploadImageToFirebaseStorage(selectedImageUri!!)

            Picasso.get().load(Uri.parse(selectedImageUri))
                .into(binding.imageViewProfile)
        }
    }

    private fun uploadImageToFirebaseStorage(imageUri: String) {
        val storageRef = FirebaseStorage.getInstance().reference
        val imageFileName = "profile_image_$correo.jpg"
        val imageRef = storageRef.child(imageFileName)

        val uploadTask = imageRef.putFile(Uri.parse(imageUri))
        uploadTask.addOnSuccessListener { taskSnapshot ->
            // La imagen se cargó exitosamente en Firebase Storage
            // Ahora sé obtiene la URL de descarga de la imagen y sé guarda en Firebase Firestore
            imageRef.downloadUrl.addOnSuccessListener { uri ->
                selectedImageUri = uri.toString()
                // Guardar la URL de descarga de la imagen en Firebase Firestore
                db.collection("users").document(correo.orEmpty()).update("foto", selectedImageUri)
            }
        }.addOnFailureListener { exception ->
            Log.e(
                "PerfilFragment",
                "Error al cargar la imagen en Firebase Storage: ${exception.message}"
            )
        }
    }

    private fun loadEmail(correo: String) {
        val collectionRef = db.collection("jugadores")

        collectionRef.get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    val correoJugador = document.getString("correo")
                    val nombreJugador = document.getString("nombre")

                    if (correoJugador.equals(correo)) {

                        loadPositions(nombreJugador.toString())
                    }
                }
            }
            .addOnFailureListener { exception ->
                Log.i("GAB", "Error al acceder")
            }
    }

    private fun loadPositions(nombreJugadorBuscado: String) {
        val collectionRef = db.collection("clasificacionMovimiento")
        val separador = " ────⌲ "
        val stringBuilder = StringBuilder()

        collectionRef.get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    val nombreDocumento = document.id
                    val matrizJugadores =
                        document.get("jugadores") as ArrayList<HashMap<String, Any>>

                    for (jugador in matrizJugadores) {

                        val nombreJugador = jugador["nombre"] as String
                        val puntuacionJugador = jugador["puntuacion"] as String
                        val puntuacionJugadorInt = puntuacionJugador.toInt()

                        if (nombreJugador == nombreJugadorBuscado && puntuacionJugadorInt > 0) {

                            when (puntuacionJugadorInt) {
                                25 -> stringBuilder.append(nombreDocumento).append(separador)
                                    .append(puntuacionJugador + " Puntos").append("   -   ")
                                    .append("1ª Posición").appendLine().append("\n")
                                18 -> stringBuilder.append(nombreDocumento).append(separador)
                                    .append(puntuacionJugador + " Puntos").append("   -   ")
                                    .append("2ª Posición").appendLine().append("\n")
                                10 -> stringBuilder.append(nombreDocumento).append(separador)
                                    .append(puntuacionJugador + " Puntos").append("   -   ")
                                    .append("3ª Posición").appendLine().append("\n")
                                else -> stringBuilder.append(nombreDocumento).append(separador)
                                    .append(puntuacionJugador + " Puntos").appendLine().append("\n")
                            }
                            // Log.i("GABRI","Documento: $nombreDocumento, Nombre del jugador : $nombreJugador, PUntuacion : $puntuacionJugador")
                        }
                    }
                    binding.textViewTorneos.text = stringBuilder.toString()
                }
            }
            .addOnFailureListener { exception ->
                Log.i("GAB", "Error al acceder")
            }
    }

    private fun setupUi(alias: String, nombre: String, telefono: String, localidad: String, otros: String, posiciones: String, foto: String) {

        binding.textViewCorreo.text = correo
        binding.editTextAlias.setText(alias)
        binding.editTextNombre.setText(nombre)
        binding.editTextTelefono.setText(telefono)
        binding.editTextLocalidad.setText(localidad)
        binding.editTextOtrosDatos.setText(otros)
        binding.editTextPosiciones.setText(posiciones)

        if (correo.equals(CORREO_ADMIN)) {
            binding.textViewTituloTorneos.visibility = View.GONE
            binding.btnAddPlayer.visibility = View.VISIBLE
        }

        if (selectedImageUri != "") {//Para que al cambiar la imagen se actualize (solo entra si se cambia la imagen)
            selectedImageUri = Uri.parse(selectedImageUri).toString()
            Picasso.get().load(selectedImageUri)
                .into(binding.imageViewProfile)
        } else if (foto != "") { //Cargar la imagen de perfil guardada
            selectedImageUri = Uri.parse(foto).toString()
            Picasso.get().load(selectedImageUri)
                .into(binding.imageViewProfile)
        }

        loadEmail(correo.toString())

    }

    private fun saveData() {
        binding.save.setOnClickListener {

            db.collection("users").document(correo.orEmpty()).set(
                hashMapOf(
                    "alias" to binding.editTextAlias.text.toString(),
                    "nombre" to binding.editTextNombre.text.toString(),
                    "telefono" to binding.editTextTelefono.text.toString(),
                    "localidad" to binding.editTextLocalidad.text.toString(),
                    "posiciones" to binding.editTextPosiciones.text.toString(),
                    "otros" to binding.editTextOtrosDatos.text.toString(),
                    "foto" to selectedImageUri
                )

            )
            Toast.makeText(requireContext(), "Guardado correctamente", Toast.LENGTH_SHORT).show()
        }
    }

    private fun deleteData() {
        binding.delet.setOnClickListener {
            val alertDialog = AlertDialog.Builder(requireContext())
                .setTitle("Confirmar eliminación")
                .setMessage("¿Estás seguro de que deseas borrar los datos?")
                .setPositiveButton("Sí") { dialog, which ->

                    db.collection("users").document(correo.orEmpty()).delete()
                    //binding.editTextAlias.text.clear()
                    binding.editTextNombre.text.clear()
                    binding.editTextTelefono.text.clear()
                    binding.editTextLocalidad.text.clear()
                    binding.editTextPosiciones.text.clear()
                    binding.editTextOtrosDatos.text.clear()
                }
                .setNegativeButton("No", null)
                .create()

            alertDialog.show()
        }
    }

    private fun showMenuEdit() {

        binding.buttomMenuEdit.setOnClickListener {
            if (!binding.save.isVisible) {
                binding.save.show()
                binding.delet.show()
                binding.buttomMenuEdit.setImageResource(R.drawable.edit_off_24)

                binding.btnSelectImage.isVisible = true
                binding.editTextAlias.isEnabled = true
                binding.editTextNombre.isEnabled = true
                binding.editTextTelefono.isEnabled = true
                binding.editTextLocalidad.isEnabled = true
                binding.editTextPosiciones.isEnabled = true
                binding.editTextOtrosDatos.isEnabled = true
                binding.editeTextEdicion.isVisible = true
                binding.editTextNombre.requestFocus()

            } else {
                binding.save.hide()
                binding.delet.hide()
                binding.editeTextEdicion.isVisible = false
                binding.buttomMenuEdit.setImageResource(R.drawable.edit_24)

                binding.btnSelectImage.isVisible = false
                binding.editTextAlias.isEnabled = false
                binding.editTextNombre.isEnabled = false
                binding.editTextTelefono.isEnabled = false
                binding.editTextLocalidad.isEnabled = false
                binding.editTextPosiciones.isEnabled = false
                binding.editTextOtrosDatos.isEnabled = false
                binding.editeTextEdicion.isVisible = false
            }
        }
    }


}