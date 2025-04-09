package com.example.csc490group3.viewModels

import androidx.lifecycle.ViewModel
import com.example.csc490group3.model.IndividualUser
import com.example.csc490group3.model.User
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.datetime.LocalDate

class FriendRequestScreenViewModel: ViewModel() {

    val incomingRequests = MutableStateFlow<List<IndividualUser>>(emptyList())
    val outgoingRequests = MutableStateFlow<List<IndividualUser>>(emptyList())



    fun acceptFriend(userId: Int){

    }
    fun declineFriend(userId: Int){

    }
    fun cancelRequest(userId: Int){

    }
}