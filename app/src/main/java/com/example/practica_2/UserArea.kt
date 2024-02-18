package com.example.practica_2

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
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
    lateinit var ordersTextVuew : TextView
    lateinit var reservationsTextView : TextView
    lateinit var recyclerOrders: androidx.recyclerview.widget.RecyclerView
    lateinit var recyclerEvents: androidx.recyclerview.widget.RecyclerView
    lateinit var db_ref : com.google.firebase.database.DatabaseReference
    lateinit var logOutButton : Button
    lateinit var configurationButton : ImageView

    override fun onBackPressed() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.user_area)

        lateinit var userId : String
        configurationButton = findViewById(R.id.configurationButton)

        userNameTextView = findViewById(R.id.userName)
        ordersTextVuew = findViewById(R.id.ordersTextView)
        reservationsTextView = findViewById(R.id.reservationsTextView)


        logOutButton = findViewById(R.id.logOutButton)

        val intent = getIntent()
        if (intent.hasExtra("user_id")) {
            Log.v("UserArea", "UserArea has user_id")
            userId = intent.getStringExtra("user_id")!!
            logOutButton.visibility = Button.INVISIBLE
            configurationButton.visibility = Button.INVISIBLE
            userNameTextView.text = intent.getStringExtra("username")!!
        } else {
            userId = Utilities.getUserId(this)


            userNameTextView.text = Utilities.getUserName(this)
        }

        configurationButton.setOnClickListener {
            val intent = Intent(this, Configuracion::class.java)
            startActivity(intent)
        }

        logOutButton.setOnClickListener {
            Utilities.logOut(this)
            val intent = Intent(this, MainActivity::class.java) //cambiar por la actividad de login
            startActivity(intent)
            finish()
        }

        db_ref = FirebaseDatabase.getInstance().getReference()

        var lista= mutableListOf<Pedido>()




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
                                                recyclerOrders.adapter?.notifyDataSetChanged()
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

        if (lista.isEmpty()) {
            ordersTextVuew.visibility = TextView.INVISIBLE
        }

        var adaptador = ReservaCartaAdapter(lista, this)




        db_ref= FirebaseDatabase.getInstance().getReference()

        recyclerOrders=findViewById(R.id.OrdersRecyclerView)
        recyclerOrders.adapter = adaptador
        recyclerOrders.layoutManager= LinearLayoutManager(applicationContext, LinearLayoutManager.HORIZONTAL,false)
        recyclerOrders.setHasFixedSize(true)

        Log.v("OrdersArea", "OrdersArea created")

        var listaReservas = mutableListOf<ReservaEvento>()

        db_ref.child("tienda")
            .child("reservaEvento")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    listaReservas.clear()
                    GlobalScope.launch ( Dispatchers.IO){
                        snapshot.children.forEach{ hijo: DataSnapshot?->
                            val reservaEvento = hijo?.getValue(ReservaEvento::class.java)

                            if(reservaEvento!!.id_usuario == userId){
                                var evento: Evento
                                // Use a semaphore to synchronize: linearize the code
                                var semaforo = CountDownLatch(1)

                                db_ref.child("tienda")
                                    .child("evento")
                                    .child(reservaEvento.id_evento!!)
                                    .addListenerForSingleValueEvent(object : ValueEventListener {
                                        override fun onDataChange(snapshot: DataSnapshot) {
                                            evento = snapshot!!.getValue(Evento::class.java)!!

                                            reservaEvento.nombre_evento = evento.nombre
                                            reservaEvento.fecha_evento = Utilities.convertTimestampToDate(evento.fecha.toString())
                                            reservaEvento.imagen_evento = evento.imagen

                                            listaReservas.add(reservaEvento)
                                            semaforo.countDown()

                                            // Update RecyclerView adapter here
                                            runOnUiThread {
                                                recyclerEvents.adapter?.notifyDataSetChanged()
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

        if (listaReservas.isEmpty()) {
            reservationsTextView.visibility = TextView.INVISIBLE
        }

        var adaptadorReservas = ReservaEventoAdapter(listaReservas, this)


        recyclerEvents =findViewById(R.id.reservationsRecyclerView)
        recyclerEvents.adapter = adaptadorReservas
        recyclerEvents.layoutManager= LinearLayoutManager(applicationContext, LinearLayoutManager.HORIZONTAL,false)
        recyclerEvents.setHasFixedSize(true)

        Log.v("ReservationsArea", "ReservationsArea created")

    }
}
