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

class ReservaCartaAdapter(private val game_list: MutableList<ReservaCarta>,context: Context): RecyclerView.Adapter<CartaAdapter.CartaViewHolder>(),
    Filterable {
    private lateinit var contexto: Context
    private var lista_filtrada = game_list
    private var db_ref = FirebaseDatabase.getInstance().getReference()


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartaAdapter.CartaViewHolder {
        val vista_item = LayoutInflater.from(parent.context).inflate(R.layout.pedido_carta,parent,false)
        contexto = parent.context
        return CartaViewHolder(vista_item)
    }

    override fun onBindViewHolder(holder: CartaAdapter.CartaViewHolder, position: Int) {
        val item_actual = lista_filtrada[position]



        holder.title.text = item_actual.
        holder.platform.text = item_actual.categoria
        holder.price.text = item_actual.precio!!.toInt().toFloat().toString()+"€"
        if(Utilities.checkAdminStatus(contexto))
        {
            holder.unavailable.visibility = View.VISIBLE
        }else{
            holder.unavailable.visibility = View.INVISIBLE
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

//        holder.unavailable.setOnClickListener {
//            val  db_ref = FirebaseDatabase.getInstance().getReference()
//            val sto_ref = FirebaseStorage.getInstance().getReference()
//            lista_filtrada.remove(item_actual)
//            sto_ref.child("tienda").child("carta").child(item_actual.id!!).delete()
//            db_ref.child("tienda").child("carta")
//                .child(item_actual.id!!).removeValue()
//
//            Toast.makeText(contexto,"Juego DESTRUIDO", Toast.LENGTH_SHORT).show()
//        }

        holder.buy.setOnClickListener {
            val id = db_ref.child("tienda").child("carta").push().key
            val creation = System.currentTimeMillis().toInt()

            val reservaCarta = ReservaCarta(id,  Utilities.getUserId(contexto),item_actual.id!!.toString(), creation)

            Utilities.writeOrder(db_ref, reservaCarta, id!!)
        }





    }

    override fun getItemCount(): Int = lista_filtrada.size

    class CartaViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val cover: ImageView = itemView.findViewById(R.id.picture)
        val title: TextView = itemView.findViewById(R.id.titleLayout)
        val platform: TextView = itemView.findViewById(R.id.platformLayout)
        val price: TextView = itemView.findViewById(R.id.priceLayout)
    }

//    override fun getFilter(): Filter {
//        return  object : Filter(){
//            override fun performFiltering(p0: CharSequence?): FilterResults {
//                val busqueda = p0.toString().lowercase()
//                if (busqueda.isEmpty()){
//                    lista_filtrada = game_list
//                }else {
//                    lista_filtrada = (game_list.filter {
//                        it.title.toString().lowercase().contains(busqueda)
//                    }) as MutableList<Game>
//                }
//
//                val filterResults = FilterResults()
//                filterResults.values = lista_filtrada
//                return filterResults
//            }
//
//            override fun publishResults(p0: CharSequence?, p1: FilterResults?) {
//                notifyDataSetChanged()
//            }
//
//        }
//    }

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