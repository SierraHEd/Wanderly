package com.example.csc490group3.model

import kotlinx.serialization.Serializable

@Serializable
data class Report(
    val report_id: Int? = null,
    val reported_By: String,
    val reported_Event: String,
    val report_type: Int,
    val reported_event_id: Int
)
