package com.example.practica_2

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Filter
import android.widget.Filterable
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.database.FirebaseDatabase

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
        holder.price.text = item_actual.fecha.toString()

        val URL: String? = when(item_actual.imagen){
            null -> null
            else -> item_actual.imagen!!
        }

        Glide.with(contexto)
            .load(URL)
            .apply(Utilities.opcionesGlide(contexto))
            .transition(Utilities.transicion)
            .into(holder.cover)
//
        holder.buy.setOnClickListener {
            val id = db_ref.child("tienda").child("reservaEvento").push().key
            val creation = System.currentTimeMillis().toInt()

            val reservaEvento = ReservaEvento(
                id,
                Utilities.getUserId(contexto),
                item_actual.id,  // This assumes item_actual.id is the ID of the evento
                null,
                null,
                null,
                null,
            )

            Utilities.writeReservaEvento(db_ref, reservaEvento, id!!, activity)
        }

    }

    override fun getItemCount(): Int = lista_filtrada.size

    class EventoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val cover: ImageView = itemView.findViewById(R.id.picture)
        val title: TextView = itemView.findViewById(R.id.titleLayout)
        val platform: TextView = itemView.findViewById(R.id.platformLayout)
        val price: TextView = itemView.findViewById(R.id.dateLayout)
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