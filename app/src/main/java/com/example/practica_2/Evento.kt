package com.example.practica_2

class Evento(var nombre: String,
             var descripcion: String,
             var precio: Float,
             var aforo_max: Int,
             var aforo_ocupado: Int,
             var fecha: String,
             var hora: String){
    override fun toString(): String {
        return "Evento(nombre='$nombre', descripcion='$descripcion', precio=$precio, aforo_max=$aforo_max, aforo_ocupado=$aforo_ocupado, fecha='$fecha', hora='$hora')"
    }
}