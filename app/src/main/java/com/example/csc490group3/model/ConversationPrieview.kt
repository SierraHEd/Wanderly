package com.example.csc490group3.model

import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ConversationPreview (
    @SerialName("other_user_id")
    val otherUserID: Int,

    @SerialName("last_message")
    val lastMessage: String,

    @SerialName("last_message_time")
    val lastMessageTime: LocalDateTime
)