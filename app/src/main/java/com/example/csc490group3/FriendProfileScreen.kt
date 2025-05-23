package com.example.csc490group3

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.QuestionMark
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.csc490group3.ui.theme.PurpleBKG
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.unit.TextUnit
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.rememberAsyncImagePainter
import com.example.csc490group3.model.Event
import com.example.csc490group3.model.IndividualUser
import com.example.csc490group3.model.UserSession
import com.example.csc490group3.supabase.DatabaseManagement.getPrivateUser
import com.example.csc490group3.supabase.DatabaseManagement.isUserPublicById
import com.example.csc490group3.supabase.checkFriendStatus
import com.example.csc490group3.supabase.friendRequest
import com.example.csc490group3.supabase.sendFriendNotification
import com.example.csc490group3.supabase.unfriend
import com.example.csc490group3.ui.components.EventCard
import com.example.csc490group3.ui.components.EventDetailDialog
import com.example.csc490group3.ui.theme.PurpleDarkBKG
import com.example.csc490group3.viewModels.UserProfileViewModel
import kotlinx.coroutines.launch

@Composable
fun FriendProfileScreen(navController: NavController, friendEmail: String) {
    var showSettings by remember { mutableStateOf(false) }
    var friend by remember { mutableStateOf<IndividualUser?>(null) }
    val scrollState = rememberScrollState()
    val firstName = friend?.firstName
    val lastName = friend?.lastName
    val profilePictureUrl = friend?.profile_picture_url

    var isPublic by remember { mutableStateOf(false) }

// TODO: Make a check here to set 'isFollowing' to whether or not CurrentUser is following this user. Change accordingly
    var isFriends by remember { mutableStateOf(false) }
    var isPendingRequest by remember { mutableStateOf(false) }
    // 🔁 Fetch the user info once when the screen loads
    LaunchedEffect(friendEmail) {
        friend = getPrivateUser(friendEmail)
        friend!!.id?.let {
            isPublic = isUserPublicById(it)
        }

        val status = UserSession.currentUser?.id?.let { friend?.id?.let { it1 ->
            checkFriendStatus(it,
                it1
            )
        } }
        print("Status: " + status)
        isFriends = (status == "accepted")
        isPendingRequest = (status == "pending")

    }
    val coroutineScope = rememberCoroutineScope()


    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(PurpleBKG)
            .padding(16.dp)
            .verticalScroll(scrollState)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            IconButton(onClick = { navController.navigate("home_screen") }) {
                Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back")
            }
        }


        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.End
        ) {

            Text( text = if (isFriends) "Remove Friend" else if (isPendingRequest) "Request Pending" else "Add Friend", fontSize = 20.sp,
                color = Color.Black,)

            Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End,
        ) {

            IconButton(
                onClick = { /* TODO: Add follow logic here */
                    coroutineScope.launch {
                        if(isFriends){
                            UserSession.currentUser?.id?.let { friend?.id?.let { it1 ->
                                unfriend(it,
                                    it1
                                )
                            } }
                            isFriends = false
                            isPendingRequest = false

                            UserSession.currentUser?.id?.let { currentUserId -> friend?.id?.let { friendId ->
                                // Send current user notification for removing a friend
                                sendFriendNotification(currentUserId, friendId, "unfriended")
                            } }
                        }
                        if(isPendingRequest){
                            UserSession.currentUser?.id?.let { friend?.id?.let { it1 ->
                                unfriend(it,
                                    it1
                                )
                            } }
                            isPendingRequest = false
                            UserSession.currentUser?.id?.let { currentUserId -> friend?.id?.let { friendId ->
                                // Send current user notification for canceling a friend request
                                sendFriendNotification(currentUserId, friendId, "canceled")
                            } }
                        }
                        else {
                            UserSession.currentUser?.id?.let { friend?.id?.let { it1 ->
                                friendRequest(it,
                                    it1
                                )
                            } }
                            isPendingRequest = true
                            UserSession.currentUser?.id?.let { currentUserId -> friend?.id?.let { friendId ->
                                // Send current user notification for canceling a friend request
                                sendFriendNotification(currentUserId, friendId, "requested")
                            } }
                        }
                    }

                },
                modifier = Modifier
                    .size(60.dp)
                    .clip(CircleShape)
                    .background(PurpleDarkBKG)
                    .border(2.dp, White, CircleShape)
            ) {
                Icon(
                    imageVector = if (isFriends) Icons.Default.Remove else if (isPendingRequest) Icons.Default.QuestionMark else Icons.Default.Add,
                    contentDescription = if (isFriends) "Remove Friend" else if (isPendingRequest) "Request Pending" else "Friend",
                    tint = White,
                    modifier = Modifier.size(50.dp)
                )
            }
        }
    }

        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            //pfp
            Image(
                painter = rememberAsyncImagePainter(profilePictureUrl ?: R.drawable.app_icon),
                contentDescription = "Profile Picture",
                modifier = Modifier
                    .size(120.dp)
                    .clip(CircleShape)
                    .border(2.dp, Color.Black, CircleShape),
                contentScale = ContentScale.Crop
            )
            Spacer(modifier = Modifier.height(8.dp))
            // Proper display name
            Text(
                text = "$firstName $lastName",
                fontSize = 24.sp,
                color = Color.Black
            )
        }

        Spacer(modifier = Modifier.height(16.dp))


        if(!isFriends && !isPublic){
            Text("This user is Private.", fontSize = 20.sp,
                color = Color.Black,)
        }

        Spacer(modifier = Modifier.height(16.dp))

        Section1(
            title = "$firstName's Saved Events", fontSize = 20.sp,
            friendEmail = friendEmail,
            navController = navController,
            isFriends = isFriends,
            isPublic = isPublic
        )
        Section2(
            title = "$firstName's Hosted Events", fontSize = 20.sp,
            navController = navController,
            friendEmail = friendEmail,
            isFriends = isFriends,
            isPublic = isPublic
        )
    }
}

@Composable
fun Section1(title: String, viewModel: UserProfileViewModel = viewModel(), fontSize: TextUnit, navController: NavController, friendEmail: String, isFriends: Boolean, isPublic: Boolean) {
    val events by viewModel.userRegisteredEvents
    val selectedEvent = remember { mutableStateOf<Event?>(null) }
    var isRegistered = remember { mutableStateOf(false) }

    var friend by remember { mutableStateOf<IndividualUser?>(null) }

    LaunchedEffect(friendEmail) {
        friend = getPrivateUser(friendEmail)
    }
    var id = friend?.id
    LaunchedEffect(id) {
        if (id != null) {
            viewModel.loadOtherUserEvents(id)
        }
    }

    if (isFriends || isPublic){
    Row() {
        Text(
            text = title,
        )
    }

    LazyRow {

        items(events) { event ->
            EventCard(event = event,
                onClick = { selectedEvent.value = event },
                onBottomButtonClick = { selectedEvent ->
                    viewModel.unregisterForEvent(selectedEvent, UserSession.currentUser)
                },
                onEditEvent = {},
                isHorizontal = true,
                showUnregisterButton = false
            )
        }
    }
    // Show event detail popup when an event is selected
    selectedEvent.value?.let { event ->
        EventDetailDialog(
            event = event,
            onDismiss = { selectedEvent.value = null },
            showRegisterButton = false,
            showWaitListButton = true,
            onJoinWaitlist = {},
            onRegister = { isRegistered.value = true },
            navController = navController
         )
        }
    }
}

@Composable
fun Section2(title: String, viewModel: UserProfileViewModel = viewModel(),fontSize: TextUnit, navController: NavController, friendEmail: String, isFriends: Boolean, isPublic: Boolean) {


    val events by viewModel.userCreatedEvents
    val selectedEvent = remember { mutableStateOf<Event?>(null) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var eventToDelete by remember { mutableStateOf<Event?>(null) }
    var isRegistered = remember { mutableStateOf(false) }
    var friend by remember { mutableStateOf<IndividualUser?>(null) }

    LaunchedEffect(friendEmail) {
        friend = getPrivateUser(friendEmail)
    }
    var id = friend?.id
    LaunchedEffect(id) {
        if (id != null) {
            viewModel.loadOtherUserEvents(id)
        }
    }
    if (isFriends || isPublic ) {
    Row() {
        Text(
            text = title,
        )
    }


    LazyRow {

        items(events) { event ->
            val isCurrentUserEvent = event.createdBy == UserSession.currentUser?.id
            EventCard(
                event = event,
                onBottomButtonClick = { selectedEvent ->
                    eventToDelete = event
                    showDeleteDialog = true
                },
                onEditEvent = { selectedEvent ->
                    viewModel.editEvent(selectedEvent)
                },
                onClick = { selectedEvent.value = event },
                isHorizontal = true,
                showOptionsButton = isCurrentUserEvent,
            )
        }
    }
    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Confirm Deletion") },
            text = { Text("Are you sure you want to delete this event? This action cannot be undone.") },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("Cancel")
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        eventToDelete?.let { viewModel.deleteEvent(it) } // Actually delete the event
                        showDeleteDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
                ) {
                    Text("Delete", color = White)
                }
            }
        )
    }

    // Show event detail popup when an event is selected
    selectedEvent.value?.let { event ->
        EventDetailDialog(event = event, onDismiss = { selectedEvent.value = null },
            showRegisterButton = false,
            showWaitListButton = false,
            onJoinWaitlist = {},
            navController = navController,
            onRegister = { isRegistered.value = true })

        }
    }
}
