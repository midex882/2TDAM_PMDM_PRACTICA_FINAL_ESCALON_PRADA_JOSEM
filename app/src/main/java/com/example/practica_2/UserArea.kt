package com.example.practica_2

import android.content.Intent
import android.os.Bundle
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.user_area)

        var userId = Utilities.getUserId(this)

        var adaptador = ReservaCartaAdapter(mutableListOf(), this)

        userNameTextView = findViewById(R.id.userName)

        userNameTextView.text = Utilities.getUserName(this)

        val pojo_usuario:Usuario = intent.getParcelableExtra<Usuario>("usuario")!!
        val pojo_carta : Carta = intent.getParcelableExtra<Carta>("carta")!!

        db_ref= FirebaseDatabase.getInstance().getReference()

        var lista= mutableListOf<Pedido>()

        db_ref.child("tienda")
            .child("pedido")
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

//                                            pojo_carta.id= pojo_pedido.id_carta.toString()

                                            pojo_pedido.imagen = pojo_carta.imagen
                                            pojo_pedido.precio = pojo_carta.precio
                                            pojo_pedido.nombre_carta = pojo_carta.nombre

                                            lista.add(pojo_pedido)
                                            semaforo.countDown()
                                        }

                                        override fun onCancelled(error: DatabaseError) {
                                            println(error.message)
                                        }
                                    })
                                semaforo.await()

                            }
                        }

                        runOnUiThread {
                            recycler.adapter?.notifyDataSetChanged()

                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    println(error.message)
                }
            })

        recycler=findViewById(R.id.OrdersRecyclerView)
        recycler.adapter=ReservaCartaAdapter(lista, this)
        recycler.layoutManager= LinearLayoutManager(applicationContext)
        recycler.setHasFixedSize(true)

    }


}