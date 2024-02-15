package com.example.practica_2

import android.app.DatePickerDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
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

class CreateEvento() : AppCompatActivity() , CoroutineScope {

    val this_activity = this
    var job = Job()

    lateinit var db_ref: DatabaseReference
    lateinit var st_ref: StorageReference
    lateinit var titleEdit : TextInputEditText
    lateinit var dateEdit : TextView
    lateinit var eventos_list : MutableList<Evento>

    lateinit var descriptionEdit : TextInputEditText
    lateinit var maxAttendance : TextInputEditText
    lateinit var createButton : ImageView
    lateinit var url_cover : Uri
    lateinit var cover_ImageView : ImageView
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
        setContentView(R.layout.create_event)
        url_cover = Uri.EMPTY
        var title = ""
        var description = ""
        var date =""
        var max_attendance = 0

        db_ref = FirebaseDatabase.getInstance().getReference()
        st_ref = FirebaseStorage.getInstance().getReference()

        titleEdit = findViewById(R.id.nameEdit)
        dateEdit = findViewById(R.id.dateEdit)
        descriptionEdit = findViewById(R.id.platformEdit)
        createButton = findViewById(R.id.create)
        cover_ImageView = findViewById(R.id.cover)
        maxAttendance = findViewById(R.id.numberEdit)

        eventos_list = Utilities.getEvents(db_ref)

        dateEdit.setOnClickListener {
            val calendar = Calendar.getInstance()
            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH)
            val day = calendar.get(Calendar.DAY_OF_MONTH)

            val datePickerDialog = DatePickerDialog(this, { _, selectedYear, selectedMonth, selectedDay ->
                val selectedDate = "$selectedDay/${selectedMonth + 1}/$selectedYear"
                dateEdit.text = selectedDate

                val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH)
                val date = sdf.parse(selectedDate)
                val timestamp = date?.time
                // Now you can use the timestamp for your needs
            }, year, month, day)

            datePickerDialog.show()
        }

        createButton.setOnClickListener {

            if (titleEdit.getText().toString().trim().isEmpty() ||
                descriptionEdit.getText().toString().isEmpty() ||
                dateEdit.text.toString().isEmpty() ||
                url_cover == null || maxAttendance.text.toString().isEmpty()
                    ) {
                Toast.makeText(
                    applicationContext, "Todos los campos son necesarios", Toast.LENGTH_SHORT
                ).show()

            } else if (url_cover == null) {
                Toast.makeText(
                    applicationContext, "Por favor, selecciona una imagen", Toast.LENGTH_SHORT
                ).show()
            } else if (Utilities.eventExists(eventos_list, title.trim() as String)) {
                Toast.makeText(applicationContext, "Ese evento ya existe", Toast.LENGTH_SHORT)
                    .show()
            } else {

                Log.v("status", "Datos del formulario procesados")

                title = titleEdit.getText().toString()
                description = descriptionEdit.getText().toString()
                max_attendance = maxAttendance.text.toString().toInt()
                Log.v("covnverting", Utilities.convertDateToTimestamp(dateEdit.text.toString()))
                date = Utilities.convertDateToTimestamp(dateEdit.text.toString())
//                Log.v("date", date.toInt().toString())


                var new_game: Evento?=null
                val id = db_ref.child("tienda").child("evento").push().key



                launch {

                    Log.v("status", "lanzando corrutina")

                    var id_cover: String? = db_ref.child("FG").child("game").push().key

                    Log.v("status", "obtenida id de la imagen: $id_cover")

                    var firebase_cover_url = ""

                    if(url_cover != Uri.EMPTY)
                    {
                        firebase_cover_url = Utilities.saveCover(st_ref, id_cover!!, url_cover!!)
                    }

                    Log.v("status", "obtenida url de la imagen del evento: $firebase_cover_url")

                    val id = db_ref.child("tienda").child("evento").push().key

                    Utilities.writeEvent(db_ref, title,description,max_attendance,date,id, firebase_cover_url)

                    Utilities.toastCoroutine(this@CreateEvento, applicationContext, "Evento creada con éxito")
                }

            }
        }


        cover_ImageView.setOnClickListener {
            gallery_access.launch("image/*")
        }
    }

    private fun showDatePickerDialog() {
        val c = Calendar.getInstance()
        val year = c.get(Calendar.YEAR)
        val month = c.get(Calendar.MONTH)
        val day = c.get(Calendar.DAY_OF_MONTH)

        val dpd = DatePickerDialog(
            this,
            DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                dateEdit.setText("" + dayOfMonth + "/" + (monthOfYear + 1) + "/" + year)
            },
            year,
            month,
            day
        )
    }

    override val coroutineContext: CoroutineContext
        get() {
            return Dispatchers.IO + job
        }
}