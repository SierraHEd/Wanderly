package com.example.csc490group3.viewModels

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.csc490group3.model.Event
import com.example.csc490group3.model.User
import com.example.csc490group3.supabase.DatabaseManagement.registerEvent
import kotlinx.coroutines.launch

class SearchScreenViewModel: ViewModel() {
    var events = mutableStateOf<List<Event>>(emptyList())
        private set

    var isLoading = mutableStateOf(true)
        private set

    var errorMessage = mutableStateOf<String?>(null)
        private set

    fun search(query: String): List<Event>? {
        TODO()

    }
    fun registerForEvent(event: Event, user: User?) {
        if (user == null) return

        viewModelScope.launch {
            try {
                registerEvent(event, user)
            } catch (e: Exception) {
                errorMessage.value = "Registration failed: ${e.localizedMessage}"
            }
        }
    }
}