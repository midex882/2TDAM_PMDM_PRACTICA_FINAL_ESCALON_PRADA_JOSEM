package com.example.practica_2

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference

class Login : AppCompatActivity(), CoroutineScope {

    val this_activity = this
    var job = Job()
    lateinit var db_ref: DatabaseReference
    lateinit var st_ref: StorageReference

    override val coroutineContext: CoroutineContext
        get() {
            return Dispatchers.IO + job
        }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.login)


        var loginButton = findViewById<Button>(R.id.loginButton)
        var usernameInputEdit = findViewById<TextInputEditText>(R.id.usernameEditText)
        var passwordInputEdit = findViewById<TextInputEditText>(R.id.passwordEditText)
        var textViewRegister = findViewById<TextView>(R.id.textViewRegister)

        db_ref = FirebaseDatabase.getInstance().getReference()
        st_ref = FirebaseStorage.getInstance().getReference()

        val user_list = Utilities.getUsers(db_ref)

       // Log.v("list", user_list.toString())

        textViewRegister.setOnClickListener {
            val intent = intent.setClass(this, Register::class.java)
            startActivity(intent)
        }

        loginButton.setOnClickListener {
            var username = usernameInputEdit.text.toString()
            var password = passwordInputEdit.text.toString()

            if(Utilities.userExists(user_list, username)){
                var user = user_list.filter { it.username == username }.first()
                if(user.contrasena == password){
                    Toast.makeText(
                        applicationContext, "Bienvenido, ${user.username}", Toast.LENGTH_SHORT
                    ).show()

                    val sharedPreferences: SharedPreferences = applicationContext.getSharedPreferences("UserPreferences", Context.MODE_PRIVATE)
                    val editor = sharedPreferences.edit()
                    editor.putString("KEY_USERNAME", username)
                    editor.putString("KEY_USERID", user.id)

                    if(user.admin)
                    {
                        editor.putString("KEY_ROLE", "admin")
                    }else{
                        editor.putString("KEY_ROLE", "user")
                    }

                    editor.apply()

                    this_activity.finish()
                }else{
                    Toast.makeText(
                        applicationContext, "Contraseña incorrecta", Toast.LENGTH_SHORT
                    ).show()
                }
            }else{
                Toast.makeText(
                    applicationContext, "Este usuario no existe", Toast.LENGTH_SHORT
                ).show()
            }

        }


    }
}