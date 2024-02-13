package com.example.practica_2

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputEditText
import androidx.activity.result.contract.ActivityResultContracts
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext


class CreateCarta : AppCompatActivity(), CoroutineScope {

    val this_activity = this
    var job = Job()

    override val coroutineContext: CoroutineContext
        get() {
            return Dispatchers.IO + job
        }

    var title = ""
    var platform = ""
    var price = 0.0f
    var url_cover : Uri? = null
    var creation : Int? = null
    lateinit var db_ref: DatabaseReference
    lateinit var st_ref: StorageReference
    lateinit var cartas_list : MutableList<Carta>
    lateinit var cover_ImageView : ImageView
    lateinit var titleEdit : TextInputEditText
    lateinit var platformEdit : TextInputEditText
    lateinit var ratingLayout : TextInputEditText
    lateinit var createButton : ImageView

    private val gallery_access = registerForActivityResult(ActivityResultContracts.GetContent())
    {uri: Uri? ->
        if(uri!=null){
            url_cover = uri
            cover_ImageView.setImageURI(uri)
        }
    }
    override fun onBackPressed() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.create_carta)

        db_ref = FirebaseDatabase.getInstance().getReference()
        st_ref = FirebaseStorage.getInstance().getReference()

        titleEdit = findViewById(R.id.nameEdit)
        platformEdit = findViewById(R.id.platformEdit)
        ratingLayout = findViewById(R.id.priceEdit)
        createButton = findViewById(R.id.create)

        cartas_list = Utilities.getCartas(db_ref)

        cover_ImageView = findViewById(R.id.cover)
        cover_ImageView.setOnClickListener {
            gallery_access.launch("image/*")
        }



        createButton.setOnClickListener {

            if (titleEdit.getText().toString().trim().isEmpty() ||
                platformEdit.getText().toString().isEmpty()
            ) {
                Toast.makeText(
                    applicationContext, "Todos los campos son necesarios", Toast.LENGTH_SHORT
                ).show()

            } else if (url_cover == null) {
                Toast.makeText(
                    applicationContext, "Por favor, selecciona una imagen", Toast.LENGTH_SHORT
                ).show()
            } else if (Utilities.cartaExists(cartas_list, title.trim())) {
                Toast.makeText(applicationContext, "Esa carta ya existe", Toast.LENGTH_SHORT)
                    .show()
            } else {

                Log.v("status", "Datos del formulario procesados")

                title = titleEdit.getText().toString()
                platform = platformEdit.getText().toString()
                price = ratingLayout.text.toString().toDouble().toFloat()

                Log.v("title", title)
                Log.v("platform", platform)
                Log.v("rating", price.toString())


                var new_game: Carta?=null
                val id = db_ref.child("tienda").child("carta").push().key

                Log.v("status", "id de la carta creada: $id")



                launch {

                    Log.v("status", "lanzando corrutina")

                    var id_cover: String? = db_ref.child("FG").child("game").push().key

                    Log.v("status", "obtenida id de la imagen: $id_cover")

                    val firebase_cover_url = Utilities.saveCover(st_ref, id_cover!!, url_cover!!)

                    Log.v("status", "obtenida url de la caratula: $firebase_cover_url")

                    Log.v("db_ref", db_ref.toString())
                    Log.v("id_cover", id_cover)
                    Log.v("title", title.trim())
                    Log.v("price", price.toString())
                    Log.v(platform, platform)
                    Log.v("creation", creation.toString())
                    Log.v("firebase_cover_url", firebase_cover_url)


                    Utilities.writeCarta(db_ref, id_cover!!, title.trim(), platform, price,true, firebase_cover_url)

                    Utilities.toastCoroutine(this_activity, applicationContext, "Carta creada con éxito")
                }

            }
        }
    }
}