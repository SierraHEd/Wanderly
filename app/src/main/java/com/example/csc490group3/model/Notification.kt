package com.example.csc490group3.model

import kotlinx.serialization.Serializable

@Serializable
data class Notification(
    val id: Int? = null,
    val user_id: Int? = null,
    val message: String? = null,
    val is_read: Boolean? = null,
    val created_at: String? = null // or `LocalDateTime` if you're using Java time
)