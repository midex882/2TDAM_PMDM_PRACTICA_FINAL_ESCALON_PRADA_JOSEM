package com.example.practica_2

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Filter
import android.widget.Filterable
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.util.Util
import com.google.firebase.database.FirebaseDatabase

class CartaAdapter(private var game_list: MutableList<Carta>,private val activity: AppCompatActivity): RecyclerView.Adapter<CartaAdapter.CartaViewHolder>(), Filterable {
    private lateinit var contexto: Context
    private var lista_filtrada = game_list
    private var db_ref = FirebaseDatabase.getInstance().getReference()


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartaAdapter.CartaViewHolder {
        val vista_item = LayoutInflater.from(parent.context).inflate(R.layout.carta,parent,false)
        contexto = parent.context
        return CartaViewHolder(vista_item)
    }

    override fun onBindViewHolder(holder: CartaAdapter.CartaViewHolder, position: Int) {
        val item_actual = lista_filtrada[position]

        Log.v("item_actual", item_actual.toString())
        holder.title.text = item_actual.nombre
        holder.platform.text = item_actual.categoria

        if(Utilities.getCurrencyPreference(contexto)) {
            Log.v("converting to dollars","converting to dollars")

            var precio = Math.round(item_actual.precio!!.toInt().toFloat() * Utilities.getSavedCurrencyRate(
                contexto
            ))

            holder.price.text = precio.toString() + "$"

        }else{
            holder.price.text = item_actual.precio!!.toInt().toFloat().toString()+"€"
        }

        if(Utilities.checkAdminStatus(contexto))
        {
            holder.edit.visibility = View.VISIBLE
        }else{
            holder.edit.visibility = View.INVISIBLE
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
            Log.v("item_actual",item_actual.toString())
            val id = db_ref.child("tienda").child("reservaCarta").push().key
            val creation = System.currentTimeMillis().toString()

            Log.v("userId", Utilities.getUserId(contexto))
            Log.v("username", Utilities.getUserName(contexto))
            Log.v("lista_users", Utilities.getUsers(db_ref).toString())

            val reservaCarta = Pedido(
                id,
                Utilities.getUserId(contexto),
                item_actual.id,  // This assumes item_actual.id is the ID of the carta
                null,
                null,
                null,
                null,
                creation,
                false
            )

            Utilities.writeOrder(db_ref, reservaCarta, id!!, activity)
        }

        holder.edit.setOnClickListener {
            val activity = Intent(contexto,EditCarta::class.java)
            activity.putExtra("carta", item_actual)
            contexto.startActivity(activity)
        }






    }

    override fun getItemCount(): Int = lista_filtrada.size

    class CartaViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val cover: ImageView = itemView.findViewById(R.id.picture)
        val title: TextView = itemView.findViewById(R.id.titleLayout)
        val platform: TextView = itemView.findViewById(R.id.platformLayout)
        val price: TextView = itemView.findViewById(R.id.priceLayout)
        val buy : Button = itemView.findViewById(R.id.buyButton)
        val edit: ImageView = itemView.findViewById(R.id.edit)
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
        return cartaFilter
    }

    var cardListFull: MutableList<Carta> = game_list


    private val cartaFilter: Filter = object : Filter() {
        override fun performFiltering(constraint: CharSequence?): FilterResults {
            Log.v("game_list", game_list.toString())
            Log.v("cardlistfull", cardListFull.toString())
            var filteredList: MutableList<Carta> = ArrayList()
            if (constraint == null || constraint.isEmpty()) {
                filteredList.addAll(cardListFull)
            } else {
                val filterPattern = constraint.toString().toLowerCase().trim { it <= ' ' }
                Log.v("filtering", "filtering ${filterPattern} ")
                for (item in cardListFull) {
                    if (item.nombre.toLowerCase().contains(filterPattern)) {
                        filteredList.add(item)
                    }
                }
            }
            val results = FilterResults()
            results.values = filteredList
            return results
        }

        override fun publishResults(constraint: CharSequence?, results: FilterResults) {
            game_list = (results.values as List<Carta>).toMutableList()
            Log.v("publishing results", "publishing results ${game_list} ")
            lista_filtrada = game_list
            notifyDataSetChanged()
        }
    }





}