package com.example.csc490group3.viewModels

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.csc490group3.model.Event
import com.example.csc490group3.model.IndividualUser
import com.example.csc490group3.model.User
import com.example.csc490group3.model.UserSession
import com.example.csc490group3.supabase.DatabaseManagement
import com.example.csc490group3.supabase.DatabaseManagement.getPrivateUser
import com.example.csc490group3.supabase.DatabaseManagement.getUserCreatedEvents
import com.example.csc490group3.supabase.DatabaseManagement.getUserEvents
import com.example.csc490group3.supabase.DatabaseManagement.removeEvent
import com.example.csc490group3.supabase.DatabaseManagement.unregisterEvent
import com.example.csc490group3.supabase.StorageManagement

import com.example.csc490group3.supabase.getPendingIncomingRequests
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import java.io.File

class UserProfileViewModel: ViewModel() {
    var registeredEvents = mutableStateOf<List<Event>>(emptyList())
        private set
    var createdEvents = mutableStateOf<List<Event>>(emptyList())
        private set
    var userRegisteredEvents = mutableStateOf<List<Event>>(emptyList())
        private set
    var userCreatedEvents = mutableStateOf<List<Event>>(emptyList())
        private set
    var isLoading = mutableStateOf(true)
        private set
    var errorMessage = mutableStateOf<String?>(null)
        private set
    var incomingRequests = mutableStateOf<List<IndividualUser>?>(emptyList())
        private set

    init {
        fetchEvents()
        loadFriendRequests()
    }

    private fun loadFriendRequests() {
        viewModelScope.launch {
            UserSession.currentUser?.id?.let { userId ->
                incomingRequests.value = getPendingIncomingRequests(userId, true)
            }
        }
    }

    fun loadOtherUserEvents(id: Int) {
        fetchOtherUserEvents(id)
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

    private fun fetchOtherUserEvents(id: Int) {
        viewModelScope.launch {
            try {
                val currentUser = getPrivateUser(id)

                val result1 = currentUser?.id?.let { getUserEvents(it) }
                if(result1 != null) {
                    userRegisteredEvents.value = result1
                }

                val result2 = currentUser?.id?.let { getUserCreatedEvents(it) }
                if(result2 != null) {
                    userCreatedEvents.value = result2
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
                val result = unregisterEvent(event, user)
                if (result) {
                    fetchEvents() // refresh UI
                }
            } catch (e: Exception) {
                errorMessage.value = "Failed to unregister: ${e.localizedMessage}"
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

    fun uploadAndSetEventPhoto(file: File, eventId: Int) {
        viewModelScope.launch {
            val photoUrl = StorageManagement.uploadEventPhoto(file, eventId.toString())
            photoUrl?.let {
                val success = DatabaseManagement.updateEventPhoto(eventId, it)
                if (success) {
                    // Optionally update the local event object so the UI reflects the change
                    println("Event photo updated.")
                }
            } ?: run {
                println("Failed to upload event photo.")
            }
        }
    }

    fun uploadAndSetProfilePicture(file: File, userId: Int) {
        viewModelScope.launch {
            val photoUrl = StorageManagement.uploadPhoto(file, userId.toString())
            photoUrl?.let {
                val success = DatabaseManagement.updateUserProfilePicture(userId, it)
                if (success) {
                    UserSession.currentUser?.profile_picture_url = photoUrl
                }
            } ?: run {
                println("Failed to upload and set profile picture.")
            }
        }
    }
}