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
import okhttp3.Call
import okhttp3.Callback
import java.sql.Date
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import org.json.JSONObject
import java.io.IOException
import java.util.Date as Date1


class Utilities {
    companion object{
        fun getSavedDate(context: Context): String {
            val sharedPreferences = context.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
            return sharedPreferences.getString("lastDate", "").toString()
        }

        fun saveCurrencyPreference(context: Context, isDollar: Boolean) {
            val sharedPreferences = context.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
            val editor = sharedPreferences.edit()
            editor.putBoolean("isDollar", isDollar)
            editor.apply()
        }

        fun getCurrencyPreference(context: Context): Boolean {
            val sharedPreferences = context.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
            return sharedPreferences.getBoolean("isDollar", false)
        }

        fun saveCurrentDate(context: Context) {
            val sharedPreferences = context.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
            val editor = sharedPreferences.edit()
            val currentDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date1())
            editor.putString("lastDate", currentDate)
            editor.apply()
        }

        fun isDifferentDay(context: Context): Boolean {
            val savedDate = getSavedDate(context)
            val currentDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date1())
            return savedDate != currentDate
        }

        fun getSavedCurrencyRate(context: Context): Float {
            val sharedPreferences = context.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
            return sharedPreferences.getFloat("rate", 0.0f)
        }

        fun getCurrencyRate(context: Context) {
            if (isDifferentDay(context)) {
                val client = OkHttpClient()
                val request = Request.Builder()
                    .url("https://api.exchangerate-api.com/v4/latest/USD")
                    .build()

                client.newCall(request).enqueue(object : Callback {
                    override fun onFailure(call: Call, e: IOException) {
                        e.printStackTrace()
                    }

                    override fun onResponse(call: Call, response: Response) {
                        response.use {
                            if (!response.isSuccessful) throw IOException("Unexpected code $response")

                            val jsonObject = JSONObject(response.body!!.string())
                            val rate = jsonObject.getJSONObject("rates").getDouble("EUR")

                            // Save the rate to SharedPreferences
                            val sharedPreferences = context.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
                            val editor = sharedPreferences.edit()
                            editor.putFloat("rate", rate.toFloat())
                            editor.apply()

                            // Save the current date to SharedPreferences
                            saveCurrentDate(context)
                        }
                    }
                })
            }
            saveCurrentDate(context)
            Log.v("saving", "Currency rate saved")
        }

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



        fun howManyCartas(Cartas : List<Carta>, nombre:String):Int{
            return Cartas.filter { it.nombre == nombre }.size
        }

        fun howManyEvents(Eventos: MutableList<Carta>, nombre:String):Int{
            return Eventos.filter { it.nombre == nombre }.size
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

        fun convertDateToTimestamp(dateString: String): String {
            val format = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            val date = format.parse(dateString)
            val time = date?.time ?: 0
            Log.v("time", time.toString())
            return time.toString()
        }

        fun convertTimestampToDate(timestamp: String): String {
            val date = Date(timestamp.toLong())
            val format = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            return format.format(date)
        }

        fun writeEvent(
            db_ref: DatabaseReference,
            title: String,
            description: String,
            max_attendance: Int,
            date: String,
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