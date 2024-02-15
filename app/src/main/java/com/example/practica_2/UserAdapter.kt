package com.example.practica_2

import android.content.Context
import android.util.Log
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

class UserAdapter(private var user_list: MutableList<Usuario>, context: Context): RecyclerView.Adapter<UserAdapter.UserViewHolder>(),
    Filterable {
    private lateinit var lista_aux: MutableList<Usuario>
    private lateinit var contexto: Context
    private var lista_filtrada = user_list
    private var db_ref = FirebaseDatabase.getInstance().getReference()
    private var originalList: MutableList<Usuario> = user_list.toMutableList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserAdapter.UserViewHolder {
        val vista_item = LayoutInflater.from(parent.context).inflate(R.layout.usuario,parent,false)
        contexto = parent.context
        return UserViewHolder(vista_item)
    }

    override fun onBindViewHolder(holder: UserAdapter.UserViewHolder, position: Int) {
        val item_actual = lista_filtrada[position]

        holder.username.text = item_actual.username
        holder.email.text = item_actual.correo
        holder.name.text = item_actual.nombre

        if(item_actual.admin){
            holder.see.visibility = View.INVISIBLE
        }else{
            holder.see.visibility = View.VISIBLE

        }
//
        holder.see.setOnClickListener {
             val intent = android.content.Intent(contexto, UserArea::class.java)
                intent.putExtra("user_id", item_actual.id)
                intent.putExtra("username", item_actual.username)
                contexto.startActivity(intent)
        }

    }

    override fun getItemCount(): Int = lista_filtrada.size

    class UserViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val username: TextView = itemView.findViewById(R.id.usernameLayout)
        val email: TextView = itemView.findViewById(R.id.emailLayout)
        val name: TextView = itemView.findViewById(R.id.nameLayout)
        val see: Button = itemView.findViewById(R.id.buyButton)
    }

//    override fun getFilter(): Filter {
//        return object : Filter() {
//            override fun performFiltering(constraint: CharSequence?): FilterResults {
//                val filterResults = FilterResults()
//                val searchText = constraint?.toString()?.toLowerCase()
//
//                if (searchText.isNullOrEmpty()) {
//                    filterResults.values = user_list
//                } else {
//                    val filteredList = user_list.filter {
//                        it.nombre?.toLowerCase()?.contains(searchText) == true
//                    }
//                    filterResults.values = filteredList
//                }
//
//                return filterResults
//            }
//
//            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
//                notifyDataSetChanged()
//            }
//        }
//    }

    var userListFull: List<Usuario> = ArrayList(user_list)


    override fun getFilter(): Filter {
        return userFilter
    }

    private val userFilter: Filter = object : Filter() {
        override fun performFiltering(constraint: CharSequence?): FilterResults {
            Log.v("userlistfull", userListFull.toString())
            var filteredList: MutableList<Usuario> = ArrayList()
            if (constraint == null || constraint.isEmpty()) {
                filteredList.addAll(userListFull)
            } else {
                val filterPattern = constraint.toString().toLowerCase().trim { it <= ' ' }
                Log.v("filtering", "filtering ${filterPattern} ")
                for (item in userListFull) {
                    if (item.username.toLowerCase().contains(filterPattern)) {
                        filteredList.add(item)
                    }
                }
            }
            val results = FilterResults()
            results.values = filteredList
            return results
        }

        override fun publishResults(constraint: CharSequence?, results: FilterResults) {
            user_list = (results.values as List<Usuario>).toMutableList()
            Log.v("publishing results", "publishing results ${user_list} ")
            lista_filtrada = user_list
            notifyDataSetChanged()
        }
    }

}