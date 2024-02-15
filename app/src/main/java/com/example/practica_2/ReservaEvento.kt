package com.example.practica_2

class ReservaEvento(var id: String?, var id_usuario: String, var id_evento: String, var username_usuario: String?, var nombre_evento: String?, var fecha_evento: String?, var imagen_evento: String?) {
    override fun toString(): String {
        return "Reserva_evento(id=$id, id_usuario=$id_usuario, id_evento=$id_evento)"
    }

    constructor():this("", "", "", "", "", "", "")
}