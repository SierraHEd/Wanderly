package com.example.csc490group3.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UnreadCount(
    @SerialName("count") val count: Int
)