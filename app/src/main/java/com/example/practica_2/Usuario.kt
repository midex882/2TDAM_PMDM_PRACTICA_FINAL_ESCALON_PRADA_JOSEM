package com.example.practica_2

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Usuario( var id: String? = null,
                    var nombre: String,
                    var username: String,
                    var correo: String,
                    var contrasena: String,
                    var admin : Boolean) : Parcelable {
                        constructor() : this(null,"","","","",false)
}