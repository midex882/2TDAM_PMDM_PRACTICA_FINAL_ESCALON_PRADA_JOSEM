package com.example.practica_2

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class ListEvents : AppCompatActivity(){
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: EventoAdapter
    private var eventList: MutableList<Evento> = mutableListOf()

    override fun onBackPressed() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.list_events)



        var db_ref = FirebaseDatabase.getInstance().getReference()

        var lista : MutableList<Evento> = mutableListOf()
        db_ref = FirebaseDatabase.getInstance().getReference()

        db_ref.child("tienda")
            .child("evento")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    lista.clear()
                    snapshot.children.forEach{hijo: DataSnapshot?
                        ->
                        val pojo_game = hijo?.getValue(Evento::class.java)
                        lista.add(pojo_game!!)
                    }
                    recyclerView.adapter?.notifyDataSetChanged()
                }

                override fun onCancelled(error: DatabaseError) {
                    println(error.message)
                }

            })

        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        var adapter = EventoAdapter(lista, this)
        recyclerView.adapter = adapter
    }
}