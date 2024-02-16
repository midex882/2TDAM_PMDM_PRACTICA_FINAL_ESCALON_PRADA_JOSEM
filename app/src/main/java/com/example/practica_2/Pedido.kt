package com.example.practica_2

import kotlinx.parcelize.Parcelize
import android.os.Parcelable
@Parcelize
class Pedido(var id: String?,
             var id_usuario: String?,
             var id_carta: String?,
             var nombre_carta : String?,
             var nombre_usuario: String?,
             var precio: Float?,
             var imagen : String?,
             var time: String?,
             var processed: Boolean) : Parcelable {
    constructor() : this(null,"","","","",0.0F,"","", false)
}