package com.example.csc490group3.viewModels

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.csc490group3.model.Event
import com.example.csc490group3.model.User
import com.example.csc490group3.model.UserSession
import com.example.csc490group3.supabase.DatabaseManagement.getUserCreatedEvents
import com.example.csc490group3.supabase.DatabaseManagement.getUserEvents
import com.example.csc490group3.supabase.DatabaseManagement.removeEvent
import com.example.csc490group3.supabase.DatabaseManagement.unregisterEvent
import kotlinx.coroutines.launch

class UserProfileViewModel: ViewModel() {
    var registeredEvents = mutableStateOf<List<Event>>(emptyList())
        private set
    var createdEvents = mutableStateOf<List<Event>>(emptyList())
        private set
    var isLoading = mutableStateOf(true)
        private set
    var errorMessage = mutableStateOf<String?>(null)
        private set

    init {
        fetchEvents()
    }

    private fun fetchEvents() {
        viewModelScope.launch {
            try {
                val currentUser = UserSession.currentUser

                val result1 = currentUser?.id?.let { getUserEvents(it) }
                if(result1 != null) {
                    registeredEvents.value = result1
                }

                val result2 = currentUser?.id?.let { getUserCreatedEvents(it) }
                if(result2 != null) {
                    createdEvents.value = result2
                }
            }catch(e: Exception) {
                errorMessage.value = "Error: ${e.localizedMessage}"
            }finally {
                isLoading.value = false
            }
        }
    }
    fun unregisterForEvent(event: Event, user: User?) {
        if (user == null) return

        viewModelScope.launch {
            try {
                unregisterEvent(event, user)
            } catch (e: Exception) {
                errorMessage.value = "Failed to Unregister: ${e.localizedMessage}"
            }
        }
    }
    fun deleteEvent(event: Event) {
        viewModelScope.launch {
            try {
                event.id?.let { removeEvent(it) }
                println("Event ${event.eventName} deleted")
            } catch (e: Exception) {
                errorMessage.value = "Failed to delete event: ${e.localizedMessage}"
            }
        }
    }
    fun editEvent(event: Event) {
        println("You are editing an event")
    }


}