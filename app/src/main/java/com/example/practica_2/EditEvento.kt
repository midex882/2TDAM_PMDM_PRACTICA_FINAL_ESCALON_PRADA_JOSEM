package com.example.practica_2

import android.app.DatePickerDialog
import android.net.Uri
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
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
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import kotlin.coroutines.CoroutineContext

class EditEvento : AppCompatActivity(), CoroutineScope {

    lateinit var titleEdit : TextInputEditText
    lateinit var descriptionEdit : TextInputEditText
    lateinit var dateLayout : TextView
    lateinit var maxAttendance : TextInputEditText
    lateinit var coverImage : ImageView
    private lateinit var modificar: ImageView


    private var url_cover: Uri? = null
    private lateinit var db_ref: DatabaseReference
    private lateinit var st_ref: StorageReference
    private  lateinit var  pojo_evento : Evento
    private lateinit var eventos_list: MutableList<Carta>



    private lateinit var job: Job


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.edit_evento)




        val this_activity = this
        job = Job()

        pojo_evento = intent.getParcelableExtra<Evento>("evento")!!


        titleEdit = findViewById(R.id.nameEdit)
        descriptionEdit = findViewById(R.id.descriptionEdit)
        dateLayout = findViewById(R.id.dateEdit)
        coverImage = findViewById(R.id.cover)
        maxAttendance = findViewById(R.id.numberEdit)
        modificar = findViewById(R.id.edit)

        titleEdit.setText(pojo_evento.nombre)
        descriptionEdit.setText(pojo_evento.descripcion)
        dateLayout.setText(Utilities.convertTimestampToDate(pojo_evento.fecha!!.toString()))
        maxAttendance.setText(pojo_evento.aforo_max.toString())

        dateLayout.setOnClickListener {
            val calendar = Calendar.getInstance()
            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH)
            val day = calendar.get(Calendar.DAY_OF_MONTH)

            val datePickerDialog = DatePickerDialog(this, { _, selectedYear, selectedMonth, selectedDay ->
                val selectedDate = "$selectedDay/${selectedMonth + 1}/$selectedYear"
                dateLayout.text = selectedDate

                val sdf = SimpleDateFormat("dd/mm/yyyy", Locale.ENGLISH)
                val date = sdf.parse(selectedDate)
                val timestamp = date?.time
                // Now you can use the timestamp for your needs
            }, year, month, day)

            datePickerDialog.show()
        }

        Glide.with(applicationContext)
            .load(pojo_evento.imagen)
            .apply(Utilities.opcionesGlide(applicationContext))
            .transition(Utilities.transicion)
            .into(coverImage)

        db_ref = FirebaseDatabase.getInstance().getReference()
        st_ref = FirebaseStorage.getInstance().getReference()

        eventos_list = Utilities.getCartas(db_ref)

        modificar.setOnClickListener {

            if (titleEdit.text.toString().trim().isEmpty() ||
                descriptionEdit.text.toString().trim().isEmpty() ||
                dateLayout.text.toString().trim().isEmpty() ||
                maxAttendance.text.toString().trim().isEmpty()
            ) {
                Toast.makeText(
                    applicationContext, "Faltan datos en el " +
                            "formulario", Toast.LENGTH_SHORT
                ).show()

            } else if (Utilities.howManyEvents(eventos_list, titleEdit.toString().trim()) > 1) {
                Toast.makeText(applicationContext, "Ese evento ya existe", Toast.LENGTH_SHORT)
                    .show()
            } else {

                //GlobalScope(Dispatchers.IO)
                var url_cover_firebase = String()
                launch {
                    if(url_cover == null){
                        url_cover_firebase = pojo_evento.imagen!!
                    }else{
                        url_cover_firebase =
                            Utilities.saveCover(st_ref, pojo_evento.id!!, url_cover!!)
                    }

                    var date = Utilities.convertDateToTimestamp(dateLayout.text.toString())


                    Utilities.writeEvent(
                        db_ref,
                        titleEdit.text.toString().trim(),
                        descriptionEdit.text.toString().trim(),
                        maxAttendance.text.toString().toInt(),
                        date,
                        pojo_evento.id!!,
                        url_cover_firebase
                    )
                    Utilities.toastCoroutine(
                        this_activity,
                        applicationContext,
                        "Evento modificado con exito"
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