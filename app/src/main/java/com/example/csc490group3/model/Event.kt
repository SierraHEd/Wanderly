package com.example.csc490group3.model
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Event (
    val id: Int? = null,
    @SerialName("event_name")
    val eventName: String,
    val zipcode: String,
    val city: String,
    val address: String,
    val venue: String,
    @SerialName("max_attendees")
    val maxAttendees: String,
    val description: String,
    @SerialName("is_public")
    val isPublic: Boolean? = true,
    @SerialName("is_family_friendly")
    val isFamilyFriendly: Boolean,
    val categories: Set<Category>? = null,
    @SerialName("price_range")
    val priceRange: String,
    val country: String,
    val state: String,
    @SerialName("created_by")
    val createdBy: Int
)
