package com.example.practica_2

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.EditText
import android.widget.ImageView
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener


class ListCartas : AppCompatActivity() {



    private lateinit var volver: ImageView
    private lateinit var spinnerLayout : Spinner
    private lateinit var searchBar : TextInputEditText
    private lateinit var recycler: RecyclerView
    private  lateinit var lista:MutableList<Carta>
    private lateinit var adaptador: CartaAdapter
    private lateinit var db_ref: DatabaseReference


    override fun onBackPressed() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.list_cards)

        val fragment = HeaderFragment()

        supportFragmentManager.beginTransaction().add(R.id.headerFragment, fragment).commit()

        volver = findViewById(R.id.back)

        lista = mutableListOf()
        db_ref = FirebaseDatabase.getInstance().getReference()

        db_ref.child("tienda")
            .child("carta")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    lista.clear()
                    snapshot.children.forEach{hijo: DataSnapshot?
                        ->
                        val pojo_game = hijo?.getValue(Carta::class.java)
                        lista.add(pojo_game!!)
                    }
                    recycler.adapter?.notifyDataSetChanged()
                }

                override fun onCancelled(error: DatabaseError) {
                    println(error.message)
                }

            })

        adaptador = CartaAdapter(lista,this)
        recycler = findViewById(R.id.recyclerView)
        recycler.adapter = adaptador
        recycler.layoutManager = LinearLayoutManager(applicationContext)
        recycler.setHasFixedSize(true)

        Log.v("test", lista.toString())

        val searchEditText = findViewById<EditText>(R.id.search)
        searchEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
                // No action needed here
            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                // No action needed here
            }

            override fun afterTextChanged(s: Editable) {
                (recycler.adapter as CartaAdapter).filter.filter(s.toString())
                Log.v("filtering", "filtering ${s.toString()} ")
            }
        })

    }

//    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
//        menuInflater.inflate(R.menu.menu,menu)
//        val item = menu?.findItem(R.id.search)
//        val searchView = item?.actionView as SearchView
//
//
//        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener{
//            override fun onQueryTextSubmit(query: String?): Boolean {
//                return true
//            }
//
//            override fun onQueryTextChange(newText: String?): Boolean {
//                adaptador.filter.filter((newText))
//                return true
//            }
//        })
//
//
//
////        item.setOnActionExpandListener(object : MenuItem.OnActionExpandListener{
////            override fun onMenuItemActionExpand(p0: MenuItem?): Boolean {
////                return  true
////            }
////
////            override fun onMenuItemActionCollapse(p0: MenuItem?): Boolean {
////                adaptador.filter.filter("")
////                return true
////            }
////
////        })
//
//        return super.onCreateOptionsMenu(menu)
//    }








}