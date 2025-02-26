package com.example.csc490group3.model
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Event (
    val id: Int,
    @SerialName("event_name")
    val eventName: String,
    val address: String,
    val cost: Double,
    val categories: Set<Category> = emptySet(),
    @SerialName("venue_name")
    val venueName: String,
    @SerialName("event_description")
    val eventDescription: String,
    val maxAttendees: Int? = null
)
