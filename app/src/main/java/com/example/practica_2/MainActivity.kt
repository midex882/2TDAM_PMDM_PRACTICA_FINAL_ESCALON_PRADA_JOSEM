package com.example.practica_2

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button

class MainActivity : AppCompatActivity() {

    lateinit var btnTienda : Button
    lateinit var btnLogin : Button
    lateinit var btnCreateCarta : Button
    lateinit var btnEvents: Button
    lateinit var btnCreateEvent: Button
    lateinit var btnUsers: Button
    lateinit var btnCreator: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val fragment = HeaderFragment()

        supportFragmentManager.beginTransaction().add(R.id.headerFragment, fragment).commit()

        if(!Utilities.userLogged(this)){
            Log.v("User not logged", "User not logged")
            val intent = Intent(this, Login::class.java)
            startActivity(intent)
            finish()
        }

        btnTienda = findViewById(R.id.btnTienda)
        btnCreateCarta = findViewById(R.id.btnCreateCarta)
        btnEvents = findViewById(R.id.btnEventos)
        btnCreateEvent = findViewById(R.id.btnCreateEvent)
        btnUsers = findViewById(R.id.btnListUsers)
        btnCreator = findViewById(R.id.btnCreator)

        if(Utilities.checkAdminStatus(this))
        {
            btnCreateCarta.visibility = View.VISIBLE
            btnCreateEvent.visibility = View.VISIBLE
            btnUsers.visibility = View.VISIBLE
        }else{
            btnCreateCarta.visibility = View.INVISIBLE
            btnCreateEvent.visibility = View.INVISIBLE
            btnUsers.visibility = View.INVISIBLE
        }

        btnTienda.setOnClickListener {
            val intent = Intent(this, ListCartas::class.java)
            startActivity(intent)
            finish()
        }

        btnCreateCarta.setOnClickListener {
            val intent = Intent(this, CreateCarta::class.java)
            startActivity(intent)
            finish()
        }

        btnEvents.setOnClickListener {
            val intent = Intent(this, ListEvents::class.java)
            startActivity(intent)
            finish()
        }

        btnCreateEvent.setOnClickListener {
            val intent = Intent(this, CreateEvento::class.java)
            startActivity(intent)
            finish()
        }

        btnUsers.setOnClickListener {
            val intent = Intent(this, ListUsers::class.java)
            startActivity(intent)
            finish()
        }

        btnCreator.setOnClickListener {
            val intent = Intent(this, Creator::class.java)
            startActivity(intent)
            finish()
        }



    }
}