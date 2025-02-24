package com.example.csc490group3.model

import kotlinx.datetime.LocalDate
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("business")
data class businessUser (
    override val id: Int? = null,
    override val email: String,
    val name: String,
    val address: String
) : user()
