package com.example.practica_2

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager

class UserArea : AppCompatActivity(){

    lateinit var userNameTextView : TextView
    lateinit var recycler: androidx.recyclerview.widget.RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.user_area)

        var adaptador = CartaAdapter(mutableListOf())

        userNameTextView = findViewById(R.id.userName)

        userNameTextView.text = Utilities.getUserName(this)

        recycler = findViewById(R.id.recyclerView)
        recycler.adapter = adaptador
        recycler.layoutManager = LinearLayoutManager(applicationContext, LinearLayoutManager.HORIZONTAL, false)

    }


}