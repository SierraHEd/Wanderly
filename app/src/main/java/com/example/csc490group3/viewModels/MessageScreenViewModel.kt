package com.example.csc490group3.viewModels

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.csc490group3.model.ConversationPreview
import com.example.csc490group3.model.UserSession
import com.example.csc490group3.supabase.getConversations
import com.example.csc490group3.supabase.getTotalUnread
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class MessageScreenViewModel: ViewModel() {



    private val _conversations = MutableStateFlow<List<ConversationPreview>>(emptyList())
    val conversations: StateFlow<List<ConversationPreview>> = _conversations

    fun loadConversations(userId: Int) {
        viewModelScope.launch {
            val results = getConversations(userId)
            if (results != null) {

                _conversations.value = results
            }
        }
    }
}