package com.example.csc490group3.model

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
    val timeSent: LocalDateTime,
    @SerialName("is_read")
    val isRead: Boolean
)