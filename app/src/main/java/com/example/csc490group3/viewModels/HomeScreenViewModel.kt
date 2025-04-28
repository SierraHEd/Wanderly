package com.example.csc490group3.viewModels

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.csc490group3.model.Event
import com.example.csc490group3.model.Notification
import com.example.csc490group3.model.NotificationType
import com.example.csc490group3.model.User
import com.example.csc490group3.model.UserSession
import com.example.csc490group3.supabase.DatabaseManagement
import com.example.csc490group3.supabase.DatabaseManagement.getAllEvents
import com.example.csc490group3.supabase.DatabaseManagement.getAllSuggestedEvents
import com.example.csc490group3.supabase.DatabaseManagement.registerEvent
import com.example.csc490group3.supabase.addUserToWaitingList
import com.example.csc490group3.supabase.getAllNotifications
import com.example.csc490group3.supabase.getUnreadNotifications
import com.example.csc490group3.supabase.insertNotification
import com.example.csc490group3.supabase.isUserOnWaitingList
import com.example.csc490group3.supabase.updateNotificationAsReadInDatabase
import kotlinx.coroutines.launch

class HomeScreenViewModel: ViewModel() {
    var events = mutableStateOf<List<Event>>(emptyList())
        private set

    var suggestedEvents = mutableStateOf<List<Event>>(emptyList())
        private set

    var isLoading = mutableStateOf(true)
        private set

    var errorMessage = mutableStateOf<String?>(null)
        private set

    var isUserOnWaitlist = mutableStateOf(false) // New state to hold waitlist status
        private set

    var unreadNotifications = mutableStateOf<List<Notification>>(emptyList())
        private set

    var allNotifications = mutableStateOf<List<Notification>>(emptyList())
        private set

    var hasUnreadNotifications = mutableStateOf(false)
        private set

    init {
        fetchEvents()
        fetchSuggestedEvents()
    }

    private fun fetchSuggestedEvents() {
        viewModelScope.launch {
            try {
                val result = UserSession.currentUser?.id?.let { getAllSuggestedEvents(it) }
                if (result != null) {
                    suggestedEvents.value = result
                }
            } catch (e: Exception) {
                errorMessage.value = "Error: ${e.localizedMessage}"
            } finally {
                isLoading.value = false
            }

        }
    }

    private fun fetchEvents() {
        viewModelScope.launch {
            try {
                val result = getAllEvents()
                if (result != null) {
                    events.value = result
                }
            } catch (e: Exception) {
                errorMessage.value = "Error: ${e.localizedMessage}"
            } finally {
                isLoading.value = false
            }

        }
    }

    // Register for an event
    fun registerForEvent(event: Event, user: User?) {
        if (user == null) return
        viewModelScope.launch {
            try {
                // Check if both user and event IDs are non-null before proceeding
                val userId = user.id
                val eventId = event.id

                if (userId == null || eventId == null) {
                    errorMessage.value = "User or event is missing ID."
                    return@launch
                }

                val isRegistered = isUserRegisteredForEvent(userId, eventId)
                if (isRegistered) {
                    errorMessage.value = "You are already registered for this event."
                    return@launch
                }
                registerEvent(event, user) // Register the user

                // After successful registration, add a notification
                addNotificationForRegistration(event)
                // Load the unread notifications after registration
                loadUnreadNotifications(userId)
            } catch (e: Exception) {
                errorMessage.value = "Registration failed: ${e.localizedMessage}"
            }
        }
    }

    suspend fun isUserRegisteredForEvent(userID: Int, eventID: Int): Boolean {
        return try {
            // Query user_events table to see if the user is already registered for this event
            val userEvents = DatabaseManagement.getUserEvents(userID)
            userEvents?.any { it.id == eventID } == true
        } catch (e: Exception) {
            errorMessage.value = "Error checking registration: ${e.localizedMessage}"
            false
        }
    }

    suspend fun isUserWaitingForEvent(userID: Int, eventID: Int): Boolean {
        return try {
            val isWaiting = isUserOnWaitingList(userID, eventID)
            return isWaiting
        } catch (e: Exception) {
            errorMessage.value = "Error checking waiting list: ${e.localizedMessage}"
            false
        }
    }

    // Add to waitlist
    fun addToWaitingList(user: User?, event: Event) {
        viewModelScope.launch {
            if (user == null || event.id == null) {
                errorMessage.value = "User or event is missing ID."
                return@launch
            }

            val success = user.id?.let { addUserToWaitingList(it, event.id) }
            if (success == true) {
                isUserOnWaitlist.value = true
            } else {
                errorMessage.value = "Error adding to waiting list."
            }
        }
    }

    fun markNotificationAsReadInViewModel(notification: Notification) {
        viewModelScope.launch {
            try {
                if (notification.is_read != true) {
                    // Safely unwrap notification.id and call the database update
                    notification.id?.let { notificationId ->
                        val success = updateNotificationAsReadInDatabase(notificationId)
                        if (success) {
                            // If successful, refresh the notification lists
                            UserSession.currentUser?.id?.let { userId ->
                                loadAllNotifications(userId)
                                loadUnreadNotifications(userId)
                            }
                        } else {
                            errorMessage.value = "Failed to update notification."
                        }
                    } ?: run {
                        // Handle the case when notification.id is null
                        errorMessage.value = "Notification ID is null, cannot mark as read."
                    }
                }
            } catch (e: Exception) {
                errorMessage.value = "Error marking notification as read: ${e.localizedMessage}"
            }
        }
    }

    //load all unread notifications
    fun loadUnreadNotifications(userId: Int) {
        viewModelScope.launch {
            val notifications = getUnreadNotifications(userId)
            unreadNotifications.value = notifications
            hasUnreadNotifications.value = notifications.isNotEmpty()
        }
    }

    // load all notifications
    fun loadAllNotifications(userId: Int) {
        viewModelScope.launch {
            val notifications = getAllNotifications(userId)
            allNotifications.value = notifications
        }
    }

    //Add registration to notification table
    fun addNotificationForRegistration(event: Event) {
        viewModelScope.launch {
            try {
                val userId = UserSession.currentUser?.id
                if (userId != null) {
                    val message =
                        "You have successfully registered for the event: ${event.eventName}"
                    val notification =
                        Notification(user_id = userId, message = message, is_read = false, type = NotificationType.EVENT)
                    insertNotification(notification)
                    loadUnreadNotifications(userId)
                }
            } catch (e: Exception) {
                errorMessage.value = "Error creating notification: ${e.localizedMessage}"
            }
        }
    }
}