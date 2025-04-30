package com.example.csc490group3.model


import kotlinx.serialization.Serializable

@Serializable
data class Admin(
    val id: Int,
    val admin_email: String
)
