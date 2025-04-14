package com.example.csc490group3.model

import kotlinx.serialization.Serializable

@Serializable
data class WaitList(
    val user_id: Int,
    val event_id: Int,
    val created_at: String? = null
)