package com.example.practica_2

import kotlinx.parcelize.Parcelize
import android.os.Parcelable
@Parcelize
class Evento(
            var id: String,
            var nombre: String,
             var descripcion: String,
             var aforo_max: Int,
             var aforo_ocupado: Int,
             var imagen: String?,
             var fecha: Int): Parcelable{
    constructor():this("","", "", 0, 0, "", 0)
    override fun toString(): String {
        return "Evento(nombre='$nombre', descripcion='$descripcion', aforo_max=$aforo_max, aforo_ocupado=$aforo_ocupado, fecha='$fecha', imagen=$imagen)"
    }
}