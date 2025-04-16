package com.example.csc490group3.model

import kotlinx.datetime.LocalDateTime

data class ConversationPreview (
    val otherUserID: Int,
    val lastMessage: String,
    val lastMessageTime: LocalDateTime
)