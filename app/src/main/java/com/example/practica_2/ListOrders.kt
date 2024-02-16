package com.example.practica_2

import android.content.Intent
import android.os.Bundle
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.practica_2.databinding.ListOrdersBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.util.concurrent.CountDownLatch

class ListOrders : AppCompatActivity() {

    lateinit var db_ref : com.google.firebase.database.DatabaseReference
    lateinit var recyclerOrders: androidx.recyclerview.widget.RecyclerView

    override fun onBackPressed() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }

    override fun onCreate (savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.list_orders)

        db_ref = FirebaseDatabase.getInstance().getReference()


        var lista= mutableListOf<Pedido>()




        db_ref.child("tienda")
            .child("reservaCarta")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    lista.clear()
                    GlobalScope.launch ( Dispatchers.IO){
                        snapshot.children.forEach{ hijo: DataSnapshot?->
                            var pojo_pedido= hijo?.getValue(Pedido::class.java)

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


                            var pojo_usuario: Usuario
                            db_ref.child("tienda")
                                .child("usuario")
                                .child(pojo_pedido!!.id_usuario!!)
                                .addListenerForSingleValueEvent(object : ValueEventListener {
                                    override fun onDataChange(snapshot: DataSnapshot) {
                                        pojo_usuario = snapshot!!.getValue(Usuario::class.java)!!

                                        pojo_pedido.nombre_usuario = pojo_usuario.username

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

                override fun onCancelled(error: DatabaseError) {
                    println(error.message)
                }
            })


        var adaptador = AdminOrdersAdapter(lista, this)




        db_ref= FirebaseDatabase.getInstance().getReference()

        recyclerOrders=findViewById(R.id.OrdersRecyclerView)
        recyclerOrders.adapter = adaptador
        recyclerOrders.layoutManager= LinearLayoutManager(applicationContext)
        recyclerOrders.setHasFixedSize(true)
    }



}