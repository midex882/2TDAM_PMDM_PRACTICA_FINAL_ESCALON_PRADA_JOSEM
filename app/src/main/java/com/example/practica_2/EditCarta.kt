package com.example.practica_2

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

class EditCarta : AppCompatActivity(), CoroutineScope {

    lateinit var titleEdit : TextInputEditText
    lateinit var platformEdit : TextInputEditText
    lateinit var priceLayout :TextInputEditText
    lateinit var coverImage : ImageView
    private lateinit var modificar: ImageView
    private lateinit var volver: ImageView


    private var url_cover: Uri? = null
    private lateinit var db_ref: DatabaseReference
    private lateinit var st_ref: StorageReference
    private  lateinit var  pojo_carta : Carta
    private lateinit var cartas_list: MutableList<Carta>



    private lateinit var job: Job


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.edit_carta)


        val this_activity = this
        job = Job()

        pojo_carta = intent.getParcelableExtra<Carta>("carta")!!


        titleEdit = findViewById(R.id.nameEdit)
        platformEdit = findViewById(R.id.platformEdit)
        priceLayout = findViewById(R.id.priceEdit)
        coverImage = findViewById(R.id.cover)
        modificar = findViewById(R.id.edit)

        titleEdit.setText(pojo_carta.nombre)
        platformEdit.setText(pojo_carta.categoria)
        priceLayout.setText(pojo_carta.precio.toInt().toString())

        Glide.with(applicationContext)
            .load(pojo_carta.imagen)
            .apply(Utilities.opcionesGlide(applicationContext))
            .transition(Utilities.transicion)
            .into(coverImage)

        db_ref = FirebaseDatabase.getInstance().getReference()
        st_ref = FirebaseStorage.getInstance().getReference()

        cartas_list = Utilities.getCartas(db_ref)

        modificar.setOnClickListener {

            if (titleEdit.text.toString().trim().isEmpty() ||
                platformEdit.text.toString().trim().isEmpty() ||
                priceLayout.text.toString() == "0.0"
            ) {
                Toast.makeText(
                    applicationContext, "Faltan datos en el " +
                            "formulario", Toast.LENGTH_SHORT
                ).show()

            } else if (Utilities.howManyCartas(cartas_list, titleEdit.text.toString().trim()) > 1) {
                Toast.makeText(applicationContext, "Esa carta ya existe", Toast.LENGTH_SHORT)
                    .show()
            } else {

                //GlobalScope(Dispatchers.IO)
                var url_cover_firebase = String()
                launch {
                    if(url_cover == null){
                        url_cover_firebase = pojo_carta.imagen!!
                    }else{
                        url_cover_firebase =
                            Utilities.saveCover(st_ref, pojo_carta.id!!, url_cover!!)
                    }


                    Utilities.writeCarta(
                        db_ref,
                        pojo_carta.id!!,
                        titleEdit.text.toString().trim(),
                        platformEdit.text.toString().trim(),
                        priceLayout.text.toString().toInt().toFloat(),
                        true,
                        url_cover_firebase
                    )
                    Utilities.toastCoroutine(
                        this_activity,
                        applicationContext,
                        "Carta modificado con exito"
                    )
                    finish()
                }


            }




        }

        coverImage.setOnClickListener {
            gallery_access.launch("image/*")
        }

    }

    override fun onDestroy() {
        job.cancel()
        super.onDestroy()
    }

    private val gallery_access = registerForActivityResult(ActivityResultContracts.GetContent())
    {uri: Uri? ->
        if(uri!=null){
            url_cover = uri
            coverImage.setImageURI(uri)
        }
    }

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.IO + job
}