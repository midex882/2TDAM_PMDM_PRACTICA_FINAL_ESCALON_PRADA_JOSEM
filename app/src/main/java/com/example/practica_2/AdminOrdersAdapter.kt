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
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.database.FirebaseDatabase

class AdminOrdersAdapter(private val orders_list: MutableList<Pedido>, context: Context): RecyclerView.Adapter<AdminOrdersAdapter.AdminReservaCartaViewHolder>(),
    Filterable {
    private lateinit var contexto: Context
    private var lista_filtrada = orders_list
    private var db_ref = FirebaseDatabase.getInstance().getReference()


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AdminOrdersAdapter.AdminReservaCartaViewHolder {
        val vista_item = LayoutInflater.from(parent.context).inflate(R.layout.pedido_carta_admin,parent,false)
        contexto = parent.context
        return AdminReservaCartaViewHolder(vista_item)
    }

    override fun onBindViewHolder(holder: AdminOrdersAdapter.AdminReservaCartaViewHolder, position: Int) {
        val item_actual = lista_filtrada[position]



        holder.title.text = item_actual.nombre_carta
        holder.user.text = item_actual.nombre_usuario
        holder.date.text = Utilities.convertTimestampToDate(item_actual.time.toString()!!)
        holder.price.text = item_actual.precio!!.toInt().toFloat().toString()+"€"


        val URL: String? = when(item_actual.imagen){
            null -> null
            else -> item_actual.imagen!!
        }

        Glide.with(contexto)
            .load(URL)
            .apply(Utilities.opcionesGlide(contexto))
            .transition(Utilities.transicion)
            .into(holder.cover)

        if (item_actual.processed) {
            holder.approve.visibility = View.INVISIBLE
        }else{
            holder.approve.setOnClickListener {
                db_ref.child("tienda")
                    .child("reservaCarta")
                    .child(item_actual.id!!)
                    .child("processed")
                    .setValue(true)

                holder.approve.visibility = View.INVISIBLE

            }
        }


    }

    override fun getItemCount(): Int = lista_filtrada.size

    class AdminReservaCartaViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val cover: ImageView = itemView.findViewById(R.id.picture)
        val title: TextView = itemView.findViewById(R.id.titleLayout)
        val user: TextView = itemView.findViewById(R.id.userLayout)
        val price: TextView = itemView.findViewById(R.id.priceLayout)
        val date: TextView = itemView.findViewById(R.id.dateLayout)
        val approve : Button = itemView.findViewById(R.id.processButton)
    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val filterResults = FilterResults()
                val searchText = constraint?.toString()?.toLowerCase()

                if (searchText.isNullOrEmpty()) {
                    filterResults.values = orders_list
                } else {
                    val filteredList = orders_list.filter {
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