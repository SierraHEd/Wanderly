package com.example.csc490group3.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.csc490group3.model.IndividualUser
import com.example.csc490group3.model.User
import com.example.csc490group3.model.UserSession
import com.example.csc490group3.supabase.acceptDenyFriendRequest
import com.example.csc490group3.supabase.getPendingIncomingRequests
import com.example.csc490group3.supabase.unfriend
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDate

class FriendRequestScreenViewModel: ViewModel() {

    var incomingRequests = MutableStateFlow<List<IndividualUser>?>(emptyList())
    private set

    var outgoingRequests = MutableStateFlow<List<IndividualUser>?>(emptyList())
    private set

    init {
        viewModelScope.launch {
            UserSession.currentUser?.id?.let { userId ->
                incomingRequests.value = getPendingIncomingRequests(userId, true)
                outgoingRequests.value = getPendingIncomingRequests(userId, false)
            }
        }
    }

    fun acceptFriend(otherUser: Int) {
        viewModelScope.launch {
            UserSession.currentUser?.id?.let {
                acceptDenyFriendRequest(it, otherUser, true)
                refreshRequests()
            }
        }
    }

    fun declineFriend(otherUser: Int) {
        viewModelScope.launch {
            UserSession.currentUser?.id?.let {
                acceptDenyFriendRequest(it, otherUser, false)
                refreshRequests()
            }
        }
    }

    fun cancelRequest(toUserId: Int) {
        viewModelScope.launch {
            UserSession.currentUser?.id?.let {
                unfriend(it, toUserId)
                refreshRequests()
            }
        }
    }

    private suspend fun refreshRequests() {
        UserSession.currentUser?.id?.let { userId ->
            incomingRequests.value = getPendingIncomingRequests(userId, true)
            outgoingRequests.value = getPendingIncomingRequests(userId, false)
        }
    }
}