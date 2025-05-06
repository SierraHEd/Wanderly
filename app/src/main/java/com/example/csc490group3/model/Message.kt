package com.example.csc490group3.model

import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Message (
    val id: String,
    @SerialName("sender_id")
    val senderID: Int,
    @SerialName("receiver_id")
    val receiverID: Int,
    val content: String,
    @SerialName("sent_at")
    val timeSent: Instant,
    @SerialName("is_read")
    val isRead: Boolean,
    @SerialName("event_id")
    val eventID: Int? = null
)