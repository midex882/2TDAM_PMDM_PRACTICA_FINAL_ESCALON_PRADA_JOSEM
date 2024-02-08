package com.example.practica_2

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button

class MainActivity : AppCompatActivity() {

    lateinit var btnTienda : Button
    lateinit var btnLogin : Button
    lateinit var btnCreate : Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val fragment = HeaderFragment()

        supportFragmentManager.beginTransaction().add(R.id.headerFragment, fragment).commit()

        if(!Utilities.userLogged(this)){
            val intent = Intent(this, Login::class.java)
            startActivity(intent)
        }

        btnTienda = findViewById(R.id.btnTienda)
        btnCreate = findViewById(R.id.btnCreate)

        if(Utilities.checkAdminStatus(this))
        {
            btnCreate.visibility = View.VISIBLE
        }else{
            btnCreate.visibility = View.INVISIBLE
        }

//        btnTienda.setOnClickListener {
//            val intent = Intent(this, Tienda::class.java)
//            startActivity(intent)
//        }

        btnTienda.setOnClickListener {
            val intent = Intent(this, ListCartas::class.java)
            startActivity(intent)
        }

        btnCreate.setOnClickListener {
            val intent = Intent(this, CreateCarta::class.java)
            startActivity(intent)
        }


    }
}