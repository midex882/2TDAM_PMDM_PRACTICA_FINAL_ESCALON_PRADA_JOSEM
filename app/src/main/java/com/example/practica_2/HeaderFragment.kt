package com.example.practica_2

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment

class HeaderFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.header, container, false)

        val userNameTextView = view.findViewById<TextView>(R.id.userName)
        val genericLogo = view.findViewById<ImageView>(R.id.genericLogo)

        userNameTextView.text = Utilities.getUserName(requireContext())

        userNameTextView.setOnClickListener {
            val intent = Intent(activity, UserArea::class.java)
            startActivity(intent)
        }

            genericLogo.setOnClickListener {
                val intent = Intent(activity, UserArea::class.java)
                startActivity(intent)
            }

            val imageView = view.findViewById<ImageView>(R.id.logo)
            imageView.setOnClickListener {
                val intent = Intent(activity, MainActivity::class.java)
                startActivity(intent)
            }
        return view
    }
}