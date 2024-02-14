package com.example.practica_2

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class ListUsers : AppCompatActivity(){
    private lateinit var searchEditText: EditText
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: EventoAdapter
    private var userList: MutableList<Usuario> = mutableListOf()

    override fun onBackPressed() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.list_users)



        var db_ref = FirebaseDatabase.getInstance().getReference()

        var lista : MutableList<Usuario> = mutableListOf()

        db_ref.child("tienda")
            .child("usuario")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    lista.clear()
                    snapshot.children.forEach{hijo: DataSnapshot?
                        ->
                        val pojo_user = hijo?.getValue(Usuario::class.java)
                        lista.add(pojo_user!!)
                    }
                    recyclerView.adapter?.notifyDataSetChanged()
                    (recyclerView.adapter as UserAdapter).userListFull = lista

                }

                override fun onCancelled(error: DatabaseError) {
                    println(error.message)
                }

            })

        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        var adapter = UserAdapter(lista, this)
        recyclerView.adapter = adapter


        val searchEditText = findViewById<EditText>(R.id.search)
        searchEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
                // No action needed here
            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                // No action needed here
            }

            override fun afterTextChanged(s: Editable) {
                (recyclerView.adapter as UserAdapter).filter.filter(s.toString())
                Log.v("filtering", "filtering ${s.toString()} ")
            }
        })
    }
}