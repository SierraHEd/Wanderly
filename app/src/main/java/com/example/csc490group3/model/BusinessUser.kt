package com.example.csc490group3.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("business")
data class BusinessUser (
    override val id: Int? = null,
    override val email: String,
    val name: String,
    val address: String
) : User()
