package com.example.csc490group3.model
import kotlinx.serialization.Serializable

@Serializable
data class Event (
    val id: Int,
    val eventName: String,
    val address: String,
    val cost: Double,
    val categories: Set<Category> = emptySet(),
    val venueName: String,
    val eventDescription: String,
    val maxAttendees: Int
)
