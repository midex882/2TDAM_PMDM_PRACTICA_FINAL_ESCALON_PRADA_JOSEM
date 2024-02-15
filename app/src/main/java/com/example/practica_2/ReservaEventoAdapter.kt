package com.example.practica_2

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.database.FirebaseDatabase

class ReservaEventoAdapter(private val reserva_list: MutableList<ReservaEvento>, context: Context): RecyclerView.Adapter<ReservaEventoAdapter.ReservaEventoViewHolder>(),
    Filterable {
    private lateinit var contexto: Context
    private var lista_filtrada = reserva_list
    private var db_ref = FirebaseDatabase.getInstance().getReference()


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReservaEventoAdapter.ReservaEventoViewHolder {
        val vista_item = LayoutInflater.from(parent.context).inflate(R.layout.reserva_evento,parent,false)
        contexto = parent.context
        return ReservaEventoViewHolder(vista_item)
    }

    override fun onBindViewHolder(holder: ReservaEventoAdapter.ReservaEventoViewHolder, position: Int) {
        val item_actual = lista_filtrada[position]



        holder.title.text = item_actual.nombre_evento
        holder.date.text = item_actual.fecha_evento


        val URL: String? = when(item_actual.imagen_evento){
            null -> null
            else -> item_actual.imagen_evento!!
        }

        Glide.with(contexto)
            .load(URL)
            .apply(Utilities.opcionesGlide(contexto))
            .transition(Utilities.transicion)
            .into(holder.cover)
    }

    override fun getItemCount(): Int = lista_filtrada.size

    class ReservaEventoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val cover: ImageView = itemView.findViewById(R.id.picture)
        val title: TextView = itemView.findViewById(R.id.titleLayout)
        val date: TextView = itemView.findViewById(R.id.dateLayout)
    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val filterResults = FilterResults()
                val searchText = constraint?.toString()?.toLowerCase()

                if (searchText.isNullOrEmpty()) {
                    filterResults.values = reserva_list
                } else {
                    val filteredList = reserva_list.filter {
                        it.nombre_evento?.toLowerCase()?.contains(searchText) == true
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