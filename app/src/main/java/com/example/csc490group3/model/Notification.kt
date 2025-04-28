package com.example.csc490group3.model

import kotlinx.serialization.Serializable

@Serializable
data class Notification(
    val id: Int? = null,
    val user_id: Int? = null,
    val message: String,
    val is_read: Boolean? = null,
    val type: NotificationType,
)

enum class NotificationType {
    EVENT,
    FRIEND_ACTION
}