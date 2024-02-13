package com.example.practica_2

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.util.concurrent.CountDownLatch

class UserArea : AppCompatActivity(){

    lateinit var userNameTextView : TextView
    lateinit var recycler: androidx.recyclerview.widget.RecyclerView
    lateinit var db_ref : com.google.firebase.database.DatabaseReference
    lateinit var logOutButton : Button

    override fun onBackPressed() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.user_area)

        logOutButton = findViewById(R.id.logOutButton)

        logOutButton.setOnClickListener {
            Utilities.logOut(this)
            val intent = Intent(this, MainActivity::class.java) //cambiar por la actividad de login
            startActivity(intent)
            finish()
        }

        db_ref = FirebaseDatabase.getInstance().getReference()

        var lista= mutableListOf<Pedido>()
        var userId = Utilities.getUserId(this)

        db_ref.child("tienda")
            .child("reservaCarta")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    lista.clear()
                    GlobalScope.launch ( Dispatchers.IO){
                        snapshot.children.forEach{ hijo: DataSnapshot?->
                            val pojo_pedido= hijo?.getValue(Pedido::class.java)

                            if(pojo_pedido!!.id_usuario == userId){
                                var pojo_carta: Carta
                                //USAMOS EL SEMAFORO PARA SINCRONIZAR: LINEALIZAMOS EL CODIGO
                                var semaforo = CountDownLatch(1)

                                db_ref.child("tienda")
                                    .child("carta")
                                    .child(pojo_pedido!!.id_carta!!)
                                    .addListenerForSingleValueEvent(object : ValueEventListener {
                                        override fun onDataChange(snapshot: DataSnapshot) {
                                            pojo_carta = snapshot!!.getValue(Carta::class.java)!!

                                            pojo_pedido.imagen = pojo_carta.imagen
                                            pojo_pedido.precio = pojo_carta.precio
                                            pojo_pedido.nombre_carta = pojo_carta.nombre

                                            lista.add(pojo_pedido)
                                            semaforo.countDown()

                                            // Update RecyclerView adapter here
                                            runOnUiThread {
                                                recycler.adapter?.notifyDataSetChanged()
                                            }
                                        }

                                        override fun onCancelled(error: DatabaseError) {
                                            println(error.message)
                                        }
                                    })
                                semaforo.await()

                            }
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    println(error.message)
                }
            })


        var adaptador = ReservaCartaAdapter(lista, this)


        userNameTextView = findViewById(R.id.userName)

        userNameTextView.text = Utilities.getUserName(this)

        db_ref= FirebaseDatabase.getInstance().getReference()



        recycler=findViewById(R.id.OrdersRecyclerView)
        recycler.adapter = adaptador
        recycler.layoutManager= LinearLayoutManager(applicationContext, LinearLayoutManager.HORIZONTAL,false)
        recycler.setHasFixedSize(true)

    }
}
