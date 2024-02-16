package com.example.practica_2

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Filter
import android.widget.Filterable
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class EventoAdapter(private val game_list: MutableList<Evento>,var activity: AppCompatActivity): RecyclerView.Adapter<EventoAdapter.EventoViewHolder>(), Filterable {
    private lateinit var contexto: Context
    private var lista_filtrada = game_list
    private var db_ref = FirebaseDatabase.getInstance().getReference()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EventoAdapter.EventoViewHolder {
        val vista_item = LayoutInflater.from(parent.context).inflate(R.layout.evento,parent,false)
        contexto = parent.context
        return EventoViewHolder(vista_item)
    }

    override fun onBindViewHolder(holder: EventoAdapter.EventoViewHolder, position: Int) {
        val item_actual = lista_filtrada[position]

        holder.title.text = item_actual.nombre
        holder.platform.text = item_actual.descripcion
        holder.price.text = Utilities.convertTimestampToDate(item_actual.fecha.toString())

        val URL: String? = when(item_actual.imagen){
            null -> null
            else -> item_actual.imagen!!
        }

        if(Utilities.checkAdminStatus(contexto))
        {
            holder.edit.visibility = View.VISIBLE
            holder.buy.text = "Ver"

            holder.buy.setOnClickListener {
                val activity = Intent(contexto, ListUsers::class.java)
                activity.putExtra("evento", item_actual.id)
                contexto.startActivity(activity)
            }

        }else{
            holder.edit.visibility = View.INVISIBLE
            holder.buy.setOnClickListener {
                val userId = Utilities.getUserId(contexto)
                val eventoId = item_actual.id  // This assumes item_actual.id is the ID of the evento

                // Check if the user already has a reservation for the event
                db_ref.child("tienda").child("reservaEvento").orderByChild("userId").equalTo(userId).addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        var alreadyRegistered = false
                        for (snapshot in dataSnapshot.children) {
                            val reservaEvento = snapshot.getValue(ReservaEvento::class.java)
                            if (reservaEvento != null && reservaEvento.id_evento == eventoId) {
                                alreadyRegistered = true
                                break
                            }
                        }

                        if (alreadyRegistered) {
                            Toast.makeText(contexto, "You already have a reservation for this event.", Toast.LENGTH_SHORT).show()
                        } else {
                            val id = db_ref.child("tienda").child("reservaEvento").push().key
                            val creation = System.currentTimeMillis().toInt()

                            val reservaEvento = ReservaEvento(
                                id,
                                userId,
                                eventoId,
                                null,
                                null,
                                null,
                                null,
                            )

                            // Update the aforo_ocupado field of the Evento object in the database
                            db_ref.child("tienda").child("evento").child(eventoId).get().addOnSuccessListener { dataSnapshot ->
                                val evento = dataSnapshot.getValue(Evento::class.java)
                                if (evento != null) {
                                    if (evento.aforo_ocupado < evento.aforo_max) {
                                        evento.aforo_ocupado += 1
                                        db_ref.child("tienda").child("evento").child(eventoId).setValue(evento)
                                        Utilities.writeReservaEvento(db_ref, reservaEvento, id!!, activity)
                                    } else {
                                        Toast.makeText(contexto, "Evento completo. No se pudo reservar", Toast.LENGTH_SHORT).show()
                                    }
                                }
                            }
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        TODO("Not yet implemented")
                    }

                })
            }


        }

        Glide.with(contexto)
            .load(URL)
            .apply(Utilities.opcionesGlide(contexto))
            .transition(Utilities.transicion)
            .into(holder.cover)
//

        holder.edit.setOnClickListener {
            val activity = Intent(contexto,EditEvento::class.java)
            activity.putExtra("evento", item_actual)
            contexto.startActivity(activity)
        }

    }

    override fun getItemCount(): Int = lista_filtrada.size

    class EventoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val cover: ImageView = itemView.findViewById(R.id.picture)
        val title: TextView = itemView.findViewById(R.id.titleLayout)
        val platform: TextView = itemView.findViewById(R.id.platformLayout)
        val price: TextView = itemView.findViewById(R.id.dateLayout)
        val edit : CardView = itemView.findViewById(R.id.edit)
        val buy: Button = itemView.findViewById(R.id.buyButton)
    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val filterResults = FilterResults()
                val searchText = constraint?.toString()?.toLowerCase()

                if (searchText.isNullOrEmpty()) {
                    filterResults.values = game_list
                } else {
                    val filteredList = game_list.filter {
                        it.nombre?.toLowerCase()?.contains(searchText) == true
                    }
                    filterResults.values = filteredList
                }

                return filterResults
            }

            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                notifyDataSetChanged()
            }
        }
    }
}