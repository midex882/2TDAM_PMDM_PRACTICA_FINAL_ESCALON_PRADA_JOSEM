package com.example.practica_2

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Carta(var id: String,var nombre: String, var disponible: Boolean, var categoria: String, var precio: Float, var imagen: String?) :
    Parcelable {
    constructor() : this("","",false,"",0.0f,"")
}