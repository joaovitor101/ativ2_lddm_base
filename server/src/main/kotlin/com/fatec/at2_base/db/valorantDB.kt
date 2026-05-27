package com.fatec.at2_base.db

import kotlinx.serialization.Serializable

@Serializable
data class Valorant(
    val id: Long,
    val agent: String,
    val description: String,
    val country: String
)

object ValorantDatabase {

    val agents = mutableListOf(

        Valorant(
            id = 1,
            agent = "Jett",
            description = "Especialista em mobilidade e ataques rápidos",
            country = "Coreia do Sul"
        ),

        Valorant(
            id = 2,
            agent = "Phoenix",
            description = "Controla fogo e possui habilidades de cura",
            country = "Reino Unido"
        ),

        Valorant(
            id = 3,
            agent = "Sova",
            description = "Especialista em reconhecimento e rastreamento",
            country = "Rússia"
        )
    )
}