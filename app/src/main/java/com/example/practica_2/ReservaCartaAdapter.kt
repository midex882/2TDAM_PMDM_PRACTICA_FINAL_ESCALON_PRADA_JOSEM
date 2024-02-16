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

class ReservaCartaAdapter(private val game_list: MutableList<Pedido>, context: Context): RecyclerView.Adapter<ReservaCartaAdapter.ReservaCartaViewHolder>(),
    Filterable {
    private lateinit var contexto: Context
    private var lista_filtrada = game_list
    private var db_ref = FirebaseDatabase.getInstance().getReference()


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReservaCartaAdapter.ReservaCartaViewHolder {
        val vista_item = LayoutInflater.from(parent.context).inflate(R.layout.pedido_carta,parent,false)
        contexto = parent.context
        return ReservaCartaViewHolder(vista_item)
    }

    override fun onBindViewHolder(holder: ReservaCartaAdapter.ReservaCartaViewHolder, position: Int) {
        val item_actual = lista_filtrada[position]



        holder.title.text = item_actual.nombre_carta
        holder.platform.text = item_actual.nombre_carta
        holder.price.text = item_actual.precio!!.toInt().toFloat().toString()+"€"

        if (item_actual.processed) {
            holder.status.text = "Procesado"
            holder.status.setTextColor(contexto.resources.getColor(R.color.green))
        }


        val URL: String? = when(item_actual.imagen){
            null -> null
            else -> item_actual.imagen!!
        }

        Glide.with(contexto)
            .load(URL)
            .apply(Utilities.opcionesGlide(contexto))
            .transition(Utilities.transicion)
            .into(holder.cover)
    }

    override fun getItemCount(): Int = lista_filtrada.size

    class ReservaCartaViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val cover: ImageView = itemView.findViewById(R.id.picture)
        val title: TextView = itemView.findViewById(R.id.titleLayout)
        val platform: TextView = itemView.findViewById(R.id.platformLayout)
        val price: TextView = itemView.findViewById(R.id.priceLayout)
        val status : TextView = itemView.findViewById(R.id.statusLayout )
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
                        it.nombre_carta?.toLowerCase()?.contains(searchText) == true
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