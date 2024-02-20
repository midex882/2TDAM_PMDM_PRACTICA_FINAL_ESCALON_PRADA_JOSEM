package com.example.practica_2

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.google.firebase.Firebase
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.database
import org.checkerframework.checker.nullness.qual.NonNull


class MainActivity : AppCompatActivity() {

    lateinit var btnTienda : Button
    lateinit var btnCreateCarta : Button
    lateinit var btnEvents: Button
    lateinit var btnCreateEvent: Button
    lateinit var btnUsers: Button
    lateinit var btnCreator: Button
    lateinit var btnOrders: Button
    private lateinit var databaseReservas : DatabaseReference


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        databaseReservas = Firebase.database.getReference("reservaCarta")

        val sharedPref = getSharedPreferences("ThemePref", Context.MODE_PRIVATE)
        val isNightMode = sharedPref.getBoolean("isNightMode", false)

        if(Utilities.getCurrencyPreference(this)){
            Log.v("Currency", "Currency is set")
            if (Utilities.isDifferentDay(this)) {
                Log.v("Is different day", "Is different day")
                Utilities.getCurrencyRate(this)
            }
        }



        if (isNightMode) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)

        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }

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
        btnOrders = findViewById(R.id.btnListOrders)

        if(Utilities.checkAdminStatus(this))
        {
            btnCreateCarta.visibility = View.VISIBLE
            btnCreateEvent.visibility = View.VISIBLE
            btnUsers.visibility = View.VISIBLE
            btnOrders.visibility = View.VISIBLE
        }else{
            btnCreateCarta.visibility = View.INVISIBLE
            btnCreateEvent.visibility = View.INVISIBLE
            btnUsers.visibility = View.INVISIBLE
            btnOrders.visibility = View.INVISIBLE
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

        btnOrders.setOnClickListener {
            val intent = Intent(this, ListOrders::class.java)
            startActivity(intent)
            finish()
        }



    }
}