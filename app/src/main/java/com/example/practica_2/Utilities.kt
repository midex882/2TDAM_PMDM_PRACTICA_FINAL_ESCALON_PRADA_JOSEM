package com.example.practica_2

import android.content.Context
import android.content.SharedPreferences
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestOptions
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.StorageReference
import kotlinx.coroutines.tasks.await
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class Utilities {
    companion object{

        fun userLogged(context: Context): Boolean {
            val sharedPreferencesFileName = "UserPreferences"
            val sharedPreferences: SharedPreferences = context.getSharedPreferences(sharedPreferencesFileName, Context.MODE_PRIVATE)
            return sharedPreferences.getString("KEY_USERNAME", "") != ""
        }

        fun getUserName(context: Context): String {
            val sharedPreferencesFileName = "UserPreferences"
            val sharedPreferences: SharedPreferences = context.getSharedPreferences(sharedPreferencesFileName, Context.MODE_PRIVATE)
            return sharedPreferences.getString("KEY_USERNAME", "").toString()
        }

        fun getUserId(context: Context): String {
            val sharedPreferencesFileName = "UserPreferences"
            val sharedPreferences: SharedPreferences = context.getSharedPreferences(sharedPreferencesFileName, Context.MODE_PRIVATE)
            return sharedPreferences.getString("KEY_USERID", "").toString()
        }

        fun userExists(Users : List<Usuario>, nombre:String):Boolean{
            return Users.any{ it.username!!.lowercase()==nombre.lowercase()}
        }

        fun cartaExists(Cartas: MutableList<Carta>, nombre:String):Boolean{
            return Cartas.any{ it.nombre!!.lowercase()==nombre.lowercase()}
        }

        fun eventExists(Eventos: MutableList<Evento>, nombre:String):Boolean{
            return Eventos.any{ it.nombre!!.lowercase()==nombre.lowercase()}
        }

        fun logOut(context: Context){
            val sharedPreferencesFileName = "UserPreferences"
            val sharedPreferences: SharedPreferences = context.getSharedPreferences(sharedPreferencesFileName, Context.MODE_PRIVATE)
            val editor: SharedPreferences.Editor = sharedPreferences.edit()
            editor.putString("KEY_USERNAME", "")
            editor.putString("KEY_USERID", "")
            editor.putString("KEY_ROLE", "")
            editor.apply()
        }

//        fun howManyGames(Games : List<Game>, nombre:String):Int{
//            return Games.filter { it.title == nombre }.size
//        }

        fun writeUser(db_ref: DatabaseReference, user: Usuario, id: String?) {

            Log.v("db_ref", db_ref.toString())

            Log.v("id", id.toString())


            db_ref.child("tienda").child("usuario").child(id!!).setValue(user)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Log.v("help", "User created successfully")
                    } else {
                        Log.e("help", "Error creating user", task.exception)
                    }
                }
        }

        fun writeOrder(db_ref: DatabaseReference, reserva: Pedido, id: String?, activity: AppCompatActivity) {
            db_ref.child("tienda").child("reservaCarta").child(id!!).setValue(reserva)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Log.v("help", "Order registered successfully")
                        activity.runOnUiThread {
                            toastCoroutine(activity, activity, "Order registered successfully")
                        }
                    } else {
                        Log.e("help", "Error creating order", task.exception)
                    }
                }
        }

        fun writeReservaEvento(db_ref: DatabaseReference, reserva: ReservaEvento, id: String, activity: AppCompatActivity) {
            db_ref.child("tienda").child("reservaEvento").child(id).setValue(reserva)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Log.v("help", "Reserva registrada con éxito")
                        toastCoroutine(activity, activity, "Reserva registrada con éxito")
                    } else {
                        Log.e("help", "Error creating order", task.exception)
                    }
                }
        }

        fun writeCarta(db_ref: DatabaseReference,
                       id: String,
                       title: String,
                       platform: String,
                       price: Float,
                       available : Boolean,
                       url_firebase: String) {
            db_ref.child("tienda").child("carta").child(id).setValue(Carta(id, title, available, platform, price, url_firebase))
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Log.v("help", "Card created successfully")
                    } else {
                        Log.e("help", "Error creating card", task.exception)
                    }
                }
        }

        fun convertDateToTimestamp(dateString: String): Long {
            val format = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            val date = format.parse(dateString)
            return date?.time ?: 0
        }

        fun writeEvent(db_ref: DatabaseReference,
                       title: String,
                       description: String,
                       max_attendance: Int,
                       date: Int ,
                       id: String?,
                       url_firebase: String) {
            db_ref.child("tienda").child("evento").child(id!!).setValue(
                Evento(id,
                    title,
                    description,
                    max_attendance,
                    0,
                    url_firebase,
                    date
                    ))
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Log.v("help", "Event created successfully")

                    } else {
                        Log.e("help", "Error creating event", task.exception)
                    }
                }
        }



        fun getUsers(db_ref: DatabaseReference):MutableList<Usuario>{
            var lista = mutableListOf<Usuario>()

            db_ref.child("tienda")
                .child("usuario")
                .addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        snapshot.children.forEach{hijo :DataSnapshot ->
                            val pojo_Game = hijo.getValue(Usuario::class.java)
                            lista.add(pojo_Game!!)

                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        println(error.message)
                    }
                })



            return lista
        }

        fun getCartas(db_ref: DatabaseReference):MutableList<Carta>{
            var lista = mutableListOf<Carta>()

            db_ref.child("tienda")
                .child("carta")
                .addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        snapshot.children.forEach{hijo :DataSnapshot ->
                            val pojo_Game = hijo.getValue(Carta::class.java)
                            lista.add(pojo_Game!!)
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        println(error.message)
                    }
                })

            return lista
        }

        fun getEvents(db_ref: DatabaseReference):MutableList<Evento>{
            var lista = mutableListOf<Evento>()

            db_ref.child("tienda")
                .child("evento")
                .addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        snapshot.children.forEach{hijo :DataSnapshot ->
                            val pojo_Game = hijo.getValue(Evento::class.java)
                            lista.add(pojo_Game!!)
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        println(error.message)
                    }
                })

            return lista
        }





        suspend fun saveCover(sto_ref: StorageReference, id:String, imagen: Uri):String{
            lateinit var firebase_cover_url: Uri

            firebase_cover_url = sto_ref.child("tienda").child("imagen").child(id)
                .putFile(imagen).await().storage.downloadUrl.await()

            return firebase_cover_url.toString()
        }

        fun toastCoroutine(activity: AppCompatActivity, context: Context, text:String){
            activity.runOnUiThread{
                Toast.makeText(
                    context,
                    text,
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        fun animacion_carga(contexto: Context): CircularProgressDrawable {
            val animacion = CircularProgressDrawable(contexto)
            animacion.strokeWidth = 5f
            animacion.centerRadius = 30f
            animacion.start()
            return animacion
        }

        fun checkAdminStatus(context: Context): Boolean {
            val sharedPreferencesFileName = "UserPreferences"
            val sharedPreferences: SharedPreferences = context.getSharedPreferences(sharedPreferencesFileName, Context.MODE_PRIVATE)
            return sharedPreferences.getString("KEY_ROLE", "") == "admin"
        }



        val transicion = DrawableTransitionOptions.withCrossFade(500)

        fun opcionesGlide(context: Context): RequestOptions {
            val options = RequestOptions()
                .placeholder(animacion_carga(context))
                .fallback(R.drawable.logo)
                .error(R.drawable.e404)
            return options
        }


        fun getDate(milliSeconds: Long): String? {
            val formatter = SimpleDateFormat("dd-mm-yyyy")
            val calendar: Calendar = Calendar.getInstance()
            calendar.setTimeInMillis(milliSeconds.toInt().toLong())
            return formatter.format(calendar.getTime())
        }


    }
}