package com.example.csc490group3.model
import kotlinx.serialization.Serializable

@Serializable
sealed class User {
    abstract val id: Int?
    abstract val email: String
}