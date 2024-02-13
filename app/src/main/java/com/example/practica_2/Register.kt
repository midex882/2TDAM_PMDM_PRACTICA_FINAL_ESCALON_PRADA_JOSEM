package com.example.practica_2

import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

class Register : AppCompatActivity(), CoroutineScope {

    lateinit var db_ref: DatabaseReference
    var job = Job()

    val this_activity = this

    override val coroutineContext: CoroutineContext
        get() {
            return Dispatchers.IO + job
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.register)
        db_ref = FirebaseDatabase.getInstance().getReference()

        var usernameInputEdit = findViewById<TextInputEditText>(R.id.usernameEditText)
        var passwordInputEdit = findViewById<TextInputEditText>(R.id.passwordEditText)
        var nameInputEdit = findViewById<TextInputEditText>(R.id.nameEditText)
        var emailInputEdit = findViewById<TextInputEditText>(R.id.emailEditText)

        var registerButton = findViewById<Button>(R.id.registerButton)

        registerButton.setOnClickListener {
            var username = usernameInputEdit.text.toString()
            var password = passwordInputEdit.text.toString()
            var name = nameInputEdit.text.toString()
            var email = emailInputEdit.text.toString()
            val id = db_ref.child("tienda").child("usuario").push().key
            var user = Usuario(id,name, username, email, password, false)
            Log.v("registrando usuario: ", user.toString())
            db_ref = FirebaseDatabase.getInstance().getReference()

            launch {

                Utilities.writeUser(db_ref, user,id)

                Utilities.toastCoroutine(this_activity, applicationContext, "Usuario creado con éxito")

            }



        }
    }

}