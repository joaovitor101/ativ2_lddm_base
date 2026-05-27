package com.fatec.at2_base.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
@Serializable
data class Valorant(
    val id: Int,
    val agent: String,
    val description: String? = null,
    val country: String? = null,
)