package com.example.csc490group3


import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.csc490group3.data.BottomNavBar
import com.example.csc490group3.ui.components.FriendRequestCard
import com.example.csc490group3.ui.theme.PurpleBKG

import com.example.csc490group3.viewModels.FriendRequestScreenViewModel


@Composable
fun FriendRequestScreen(
    navController: NavController,
    viewModel: FriendRequestScreenViewModel = viewModel()
) {

    val incomingRequests by viewModel.incomingRequests.collectAsState()
    val outgoingRequests by viewModel.outgoingRequests.collectAsState()

    Scaffold(
        bottomBar = { BottomNavBar(navController) },
        containerColor = PurpleBKG
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
        ) {
            Text(
                text = "Incoming Friend Requests",
                fontSize = 20.sp,
                modifier = Modifier.padding(16.dp)
            )

            incomingRequests.forEach {
                FriendRequestCard(
                    user = it,
                    isIncoming = true,
                    onAccept = { it.id?.let { it1 -> viewModel.acceptFriend(it1) } },
                    onDeclineOrCancel = { it.id?.let { it1 -> viewModel.declineFriend(it1) } }
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Outgoing Friend Requests",
                fontSize = 20.sp,
                modifier = Modifier.padding(16.dp)
            )

            outgoingRequests.forEach {
                FriendRequestCard(
                    user = it,
                    isIncoming = false,
                    onDeclineOrCancel = { it.id?.let { it1 -> viewModel.cancelRequest(it1) } }
                )
            }
        }
    }
}