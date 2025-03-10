package com.example.csc490group3.viewModels

import android.util.Log
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

    // Gathers events from users who have created and/or registered for events
    fun fetchUserEvents() {
        viewModelScope.launch {
            isLoading.value = true
            try {
                val currentUser = UserSession.currentUser // Get the current user from the session
                Log.d("CalendarScreenViewModel", "Current User: $currentUser") // Debugging line

                if (currentUser != null) {
                    // Fetch events the user is registered for
                    val userRegisteredEvents = currentUser.id?.let { getUserEvents(it) }
                    Log.d("CalendarScreenViewModel", "User Registered Events: $userRegisteredEvents") // Debugging line

                    // Fetch events the user has created
                    val userCreatedEvents = currentUser.id?.let { getUserCreatedEvents(it) }
                    Log.d("CalendarScreenViewModel", "User Created Events: $userCreatedEvents") // Debugging line

                    // Combine both lists (if both are non-null)
                    if (userRegisteredEvents != null && userCreatedEvents != null) {
                        events.value = userRegisteredEvents + userCreatedEvents
                        Log.d("CalendarScreenViewModel", "Combined Events: ${events.value}") // Debugging line
                    } else {
                        errorMessage.value = "No events found for this user."
                    }
                } else {
                    errorMessage.value = "User not logged in."
                }
            } catch (e: Exception) {
                errorMessage.value = "Error: ${e.localizedMessage}"
                Log.e("CalendarScreenViewModel", "Error fetching events: ${e.localizedMessage}") // Debugging line
            } finally {
                isLoading.value = false
            }
        }
    }
}