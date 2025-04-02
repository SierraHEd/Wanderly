package com.example.csc490group3.model
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime
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
    val maxAttendees: Int,
    val description: String,
    @SerialName("is_public")
    val isPublic: Boolean? = true,
    @SerialName("is_family_friendly")
    val isFamilyFriendly: Boolean,
    val categories: List<Category>? = null,
    val price: Double? = 0.0,
    val country: String,
    val state: String,
    @SerialName("created_by")
    val createdBy: Int,
    @SerialName("num_attendees")
    val numAttendees: Int? = 0,
    @SerialName("date")
    val eventDate: LocalDate,
    @SerialName("time")
    val eventTime: LocalTime = LocalTime.parse("00:00:00")
)
