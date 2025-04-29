package com.example.csc490group3.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.csc490group3.model.IndividualUser
import com.example.csc490group3.model.Message
import com.example.csc490group3.model.UserSession
import com.example.csc490group3.supabase.DatabaseManagement.getPrivateUser
import com.example.csc490group3.supabase.getConversationWithUser
import com.example.csc490group3.supabase.markRead
import com.example.csc490group3.supabase.sendChatMessage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ConversationScreenViewModel: ViewModel() {
    private val _messages = MutableStateFlow<List<Message>>(emptyList())
    val messages: StateFlow<List<Message>> = _messages

    private val _otherUser = MutableStateFlow<IndividualUser?>(null)
    val otherUser: StateFlow<IndividualUser?> = _otherUser

    var messageText = MutableStateFlow("")

    fun loadConversation(userId: Int) {
        viewModelScope.launch {
            val user = getPrivateUser(userId)
            _otherUser.value = user
            if (user != null) {

                user.id?.let { UserSession.currentUser?.id?.let { it1 -> markRead(it1, it) } }

                _messages.value = user.id?.let { getConversationWithUser(it) }!!
            }
        }
    }

    fun sendMessage(currentUserId: Int) {
        viewModelScope.launch {
            val other = _otherUser.value ?: return@launch
            sendChatMessage(messageText.value, other.id!!)
            messageText.value = "" // Clear input
            _messages.value =
                other.id?.let { getConversationWithUser(it) }!! // Refresh messages after sending
        }
    }

}