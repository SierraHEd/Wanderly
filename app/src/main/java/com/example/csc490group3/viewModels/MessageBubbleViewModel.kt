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
import com.example.csc490group3.supabase.DatabaseManagement.registerEvent
import com.example.csc490group3.supabase.addUserToWaitingList
import com.example.csc490group3.supabase.deleteAllNotificationsForUser
import com.example.csc490group3.supabase.getAllNotifications
import com.example.csc490group3.supabase.getUnreadNotifications
import com.example.csc490group3.supabase.insertNotification
import com.example.csc490group3.supabase.isUserOnWaitingList
import com.example.csc490group3.supabase.markAllNotificationsAsRead
import com.example.csc490group3.supabase.removeUserFromWaitingList
import com.example.csc490group3.supabase.updateNotificationAsReadInDatabase
import kotlinx.coroutines.launch

class MessageBubbleViewModel: ViewModel() {

    var errorMessage = mutableStateOf<String?>(null)
        private set

    var isUserOnWaitlist = mutableStateOf(false) // New state to hold waitlist status
        private set

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
                val message =
                    "You have joined the waitlist for the event: ${event.eventName}"
                val notification =
                    Notification(
                        user_id = user.id,
                        message = message,
                        is_read = false,
                        type = NotificationType.EVENT
                    )
                insertNotification(notification)

            } else {
                errorMessage.value = "Error adding to waiting list."
            }
        }
    }

    // Remove user from waitingList
    fun removeFromWaitingList(user: User?, event: Event) {
        viewModelScope.launch {
            if (user == null || event.id == null) {
                errorMessage.value = "User or event is missing ID."
                return@launch
            }

            try {
                val success = removeUserFromWaitingList(user.id!!, event.id)
                if (success) {
                    isUserOnWaitlist.value = false
                    val message =
                        "You have left the waitlist for the event: ${event.eventName}"
                    val notification =
                        Notification(
                            user_id = user.id,
                            message = message,
                            is_read = false,
                            type = NotificationType.EVENT
                        )
                    insertNotification(notification)
                } else {
                    errorMessage.value = "Error removing from waiting list."
                }
            } catch (e: Exception) {
                errorMessage.value = "Exception: ${e.localizedMessage}"
            }
        }
    }
    fun addNotificationForRegistration(event: Event) {
        viewModelScope.launch {
            try {
                val userId = UserSession.currentUser?.id
                if (userId != null) {
                    val message =
                        "You have successfully registered for the event: ${event.eventName}"
                    val notification =
                        Notification(
                            user_id = userId,
                            message = message,
                            is_read = false,
                            type = NotificationType.EVENT
                        )
                    insertNotification(notification)
                }
            } catch (e: Exception) {
                errorMessage.value = "Error creating notification: ${e.localizedMessage}"
            }
        }
    }

}