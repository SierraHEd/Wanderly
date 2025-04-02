package com.example.csc490group3.model

import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue

object UserSession {
    var currentUser: User? by mutableStateOf(null)
    var currentUserEmail: String? by mutableStateOf(null)
    var currentUserCategory: List<Category> by mutableStateOf(emptyList())
}