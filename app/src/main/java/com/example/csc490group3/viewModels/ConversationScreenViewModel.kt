package com.example.csc490group3.viewModels

import android.service.autofill.Validators.and
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.csc490group3.model.IndividualUser
import com.example.csc490group3.model.Message
import com.example.csc490group3.model.User
import com.example.csc490group3.model.UserSession
import com.example.csc490group3.supabase.DatabaseManagement
import com.example.csc490group3.supabase.DatabaseManagement.getPrivateUser
import com.example.csc490group3.supabase.SupabaseManagement
import com.example.csc490group3.supabase.SupabaseManagement.RealtimeManagment.realtime
import com.example.csc490group3.supabase.SupabaseManagement.supabase
import com.example.csc490group3.supabase.getConversationWithUser
import com.example.csc490group3.supabase.markRead
import com.example.csc490group3.supabase.sendChatMessage
import com.google.common.base.Predicates.or
import io.github.jan.supabase.annotations.SupabaseExperimental
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.query.filter.FilterOperation
import io.github.jan.supabase.postgrest.query.filter.FilterOperator
import io.github.jan.supabase.realtime.PrimaryKey
import io.github.jan.supabase.realtime.RealtimeChannel
import io.github.jan.supabase.realtime.selectAsFlow
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ConversationScreenViewModel: ViewModel() {
    private val _messages = MutableStateFlow<List<Message>>(emptyList())
    val messages: StateFlow<List<Message>> = _messages

    private val _otherUser = MutableStateFlow<IndividualUser?>(null)
    val otherUser: StateFlow<IndividualUser?> = _otherUser

    var messageText = MutableStateFlow("")

    private var messageFlowJob: Job? = null


    fun loadConversation(userId: Int) {
        viewModelScope.launch {
            val user = getPrivateUser(userId)
            _otherUser.value = user

            if (user != null) {

                user.id?.let { UserSession.currentUser?.id?.let { it1 -> markRead(it1, it) } }

                _messages.value = user.id?.let { getConversationWithUser(it) }!!

                subscribeToMessages()

            }
        }
    }

    @OptIn(SupabaseExperimental::class)
    fun subscribeToMessages() {

        messageFlowJob?.cancel()

        val flow: Flow<List<Message>> = supabase
            .from("messages")
            .selectAsFlow(
                Message::id,
                filter = UserSession.currentUser?.id?.let {
                    FilterOperation(
                        "receiver_id",
                        FilterOperator.EQ,
                        it
                    )
                }
            )
        messageFlowJob = viewModelScope.launch {
            flow.collect { incomingList ->
                val newFromPartner = incomingList
                    .filter { it.senderID == otherUser.value?.id ?: 0 }

                if (newFromPartner.isNotEmpty()) {
                    _messages.update { old ->
                        old + newFromPartner.sortedBy { it.timeSent }
                    }
                }
            }
        }

    }

    fun sendMessage() {
        viewModelScope.launch {
            val other = _otherUser.value ?: return@launch
            sendChatMessage(messageText.value, other.id!!)
            messageText.value = ""
            _messages.value =
                other.id?.let { getConversationWithUser(it) }!!
        }
    }

    override fun onCleared() {
        super.onCleared()
        messageFlowJob?.cancel()
    }

}