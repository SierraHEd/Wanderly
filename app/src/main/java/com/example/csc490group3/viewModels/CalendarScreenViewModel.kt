package com.example.csc490group3.viewModels

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.csc490group3.model.Event
import com.example.csc490group3.model.UserSession
import com.example.csc490group3.supabase.DatabaseManagement.getUserCreatedEvents
import com.example.csc490group3.supabase.DatabaseManagement.getUserEvents
import kotlinx.coroutines.launch

class CalendarScreenViewModel : ViewModel() {
    var events = mutableStateOf<List<Event>>(emptyList()) // List of events to display on the calendar
        private set

    var isLoading = mutableStateOf(true)
        private set

    var errorMessage = mutableStateOf<String?>(null)
        private set

    init {
        fetchUserEvents()
    }

    fun fetchUserEvents() {
        viewModelScope.launch {
            isLoading.value = true
            try {
                val currentUser = UserSession.currentUser // Get the current user from the session

                if (currentUser != null) {
                    // Fetch events the user is registered for
                    val userRegisteredEvents = currentUser.id?.let { getUserEvents(it) }

                    // Fetch events the user has created
                    val userCreatedEvents = currentUser.id?.let { getUserCreatedEvents(it) }

                    // Combine both lists (if both are non-null)
                    if (userRegisteredEvents != null && userCreatedEvents != null) {
                        events.value = userRegisteredEvents + userCreatedEvents
                    } else {
                        errorMessage.value = "No events found for this user."
                    }
                } else {
                    errorMessage.value = "User not logged in."
                }
            } catch (e: Exception) {
                errorMessage.value = "Error: ${e.localizedMessage}"
            } finally {
                isLoading.value = false
            }
        }
    }
}
