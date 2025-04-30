package com.example.csc490group3

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircleOutline
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.csc490group3.data.BottomNavBar
import com.example.csc490group3.model.IndividualUser
import com.example.csc490group3.model.UserSession
import com.example.csc490group3.supabase.DatabaseManagement.getPrivateUser
import com.example.csc490group3.supabase.getFriends
import com.example.csc490group3.supabase.unfriend
import com.example.csc490group3.ui.components.UserChatCard
import com.example.csc490group3.ui.components.UserSearchCard
import com.example.csc490group3.ui.theme.Purple40
import com.example.csc490group3.ui.theme.PurpleBKG
import com.example.csc490group3.ui.theme.PurpleStart
import com.example.csc490group3.viewModels.HomeScreenViewModel
import com.example.csc490group3.viewModels.MessageScreenViewModel
import kotlinx.coroutines.launch

@Composable
fun MessagesScreen(navController: NavController, viewModel: MessageScreenViewModel = viewModel()) {

    val conversations by viewModel.conversations.collectAsState()

    var showFriends by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        UserSession.currentUser?.id?.let {
            viewModel.loadConversations(it)
        }
    }
    Scaffold(
        containerColor = PurpleBKG,
        bottomBar = { BottomNavBar(navController) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(PurpleBKG)
                .padding(paddingValues)
        ) {
            // Header section
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Messages",
                    style = MaterialTheme.typography.headlineSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                )
                val friendsList = remember { mutableStateOf<List<IndividualUser>?>(null) }
                LaunchedEffect(Unit) {
                    friendsList.value = getFriends(UserSession.currentUser?.id ?: return@LaunchedEffect)
                }
                Box {
                    //  val searchQuery = remember { mutableStateOf("")
                    IconButton(

                        onClick = {
                            showFriends = true
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.AddCircleOutline,
                            contentDescription = "New Conversation",
                            tint = Color.White
                        )
                    }
                }

            }

            // Scrollable chat list
            LazyColumn(
                modifier = Modifier.fillMaxSize()
            ) {
                items(conversations) { conversation ->
                    UserChatCard(
                        conversation = conversation,
                        navController = navController
                    )
                }
            }
        }

        if (showFriends) {
            NewConvoDialogue(onDismiss = { showFriends = false }, navController = navController)
        }
    }
}

@SuppressLint("CoroutineCreationDuringComposition")
@Composable
fun NewConvoDialogue(onDismiss: () -> Unit, navController: NavController) {

    val isCurrentUser = true
    val friendsList = remember { mutableStateOf<List<IndividualUser>?>(null) }
    val searchQuery = remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        friendsList.value = getFriends(UserSession.currentUser?.id ?: return@LaunchedEffect)
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Start a New Conversation") },
        containerColor = PurpleStart,
        icon = {
            Icon(
                Icons.Filled.People,
                contentDescription = "",
                tint = Purple40,
                modifier = Modifier.padding(horizontal = 30.dp)
            )
        },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.verticalScroll(rememberScrollState())
            ) {

                Text("Suggestions:", fontSize = 20.sp, color = Color.Black)
                val coroutineScope = rememberCoroutineScope()
                friendsList.value?.forEach { friend ->
                    //TODO: Check if there is an existing chat with friend. If so, do not include.
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "${friend.firstName} ${friend.lastName}",
                            fontSize = 16.sp,
                            modifier = Modifier.clickable {
                               navController.navigate("conversation_screen/${friend.id}")
                                //NAV TO NEW CHAT HERE.
                            }
                        )

                    }
                } ?: Text("Loading following...", fontSize = 16.sp)

                Spacer(modifier = Modifier.height(16.dp))
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Close")
            }
        }
    )
}