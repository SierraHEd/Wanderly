package com.example.csc490group3


import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Message
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.csc490group3.data.BottomNavBar
import com.example.csc490group3.model.Event
import com.example.csc490group3.model.Notification
import com.example.csc490group3.model.NotificationType
import com.example.csc490group3.model.UserSession
import com.example.csc490group3.ui.components.EventCard
import com.example.csc490group3.ui.components.EventDetailDialog
import com.example.csc490group3.ui.theme.PurpleBKG
import com.example.csc490group3.viewModels.HomeScreenViewModel
import java.time.LocalDateTime
import java.time.OffsetDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException

@Composable
fun HomeScreen(navController: NavController, viewModel: HomeScreenViewModel = viewModel()) {
    val hasMessages by viewModel.hasMessages
    val events by viewModel.events
    val suggestedEvents by viewModel.suggestedEvents
    val isLoading by viewModel.isLoading
    val errorMessage by viewModel.errorMessage
    var context = LocalContext.current
    val selectedEvent = remember { mutableStateOf<Event?>(null) }
    val isRegistered = remember { mutableStateOf(false) }
    val isCheckingRegistration =
        remember { mutableStateOf(false) } // To track if registration is being checked
    val isOnWaitlist = remember { mutableStateOf(false) }
    val isCheckingWaitlist = remember { mutableStateOf(false) }
    val showDialog = remember { mutableStateOf(false) }
    val showNotificationsDialog = remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()

    //Icon change for notifications
    LaunchedEffect(Unit) {
        UserSession.currentUser?.id?.let { userId ->
            viewModel.loadUnreadNotifications(userId)
        }
    }
    // Check if the user is registered or on the waitlist for the event when an event is selected
    selectedEvent.value?.let { event ->
        val currentUser = UserSession.currentUser
        if (currentUser != null) {
            val userID = currentUser.id

            LaunchedEffect(userID, event.id) {
                isCheckingRegistration.value = true
                isCheckingWaitlist.value = true

                // Check if user is registered for the event
                isRegistered.value =
                    (userID != null && event.id != null) && viewModel.isUserRegisteredForEvent(
                        userID,
                        event.id
                    )
                // Check if user is on the waiting list for the event
                isOnWaitlist.value =
                    (userID != null && event.id != null) && viewModel.isUserWaitingForEvent(
                        userID,
                        event.id
                    )
                isCheckingRegistration.value = false
                isCheckingWaitlist.value = false
                showDialog.value = true
            }
        }
    }

    Scaffold(
        containerColor = PurpleBKG,
        bottomBar = { BottomNavBar(navController) }
    ) { paddingValues ->
        // Use a Column as the root so that header and content are separate.
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(PurpleBKG)
                .padding(paddingValues)
        ) {
            // Header section (non-scrollable content at the top)
            Column(modifier = Modifier.padding(16.dp)) {

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Home Page",
                        style = MaterialTheme.typography.headlineMedium,
                        color = Color.White
                    )
                    Box {
                        IconButton(
                            onClick = {
                                // Handle message icon click here, e.g. navigate to messages screen
                                navController.navigate("messages_screen")
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Message, // You can use other icons if preferred
                                contentDescription = "Messages",
                                tint = Color.White
                            )
                        }
                        if (hasMessages) {
                            Box(
                                modifier = Modifier
                                    .size(10.dp)
                                    .background(Color.Red, shape = CircleShape)
                                    .align(Alignment.TopEnd)
                                    .offset(x = (-4).dp, y = 4.dp) // adjust for nicer positioning
                            )
                        }

                    }
                    Box{
                        IconButton(
                            onClick = {
                                // Handle message icon click here, e.g. navigate to messages screen
                                navController.navigate("map_screen")
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.LocationOn, // You can use other icons if preferred
                                contentDescription = "Map",
                                tint = Color.White
                            )
                        }
                    }
                    Box {
                        IconButton(onClick = {
                            UserSession.currentUser?.id?.let { userId ->
                                viewModel.loadAllNotifications(userId)
                                showNotificationsDialog.value = true
                            }
                        }) {
                            Icon(
                                imageVector = Icons.Default.Notifications,
                                contentDescription = "Notifications",
                                tint = Color.White
                            )
                        }
                        // Red dot if there are unread notifications
                        if (viewModel.hasUnreadNotifications.value) {
                            Box(
                                modifier = Modifier.size(8.dp)
                                    .background(Color.Red, shape = CircleShape)
                                    .align(Alignment.TopEnd)  // This will position the dot in the top-right corner of the icon
                            )
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(20.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(PurpleBKG)
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Button(
                        onClick = { navController.navigate("start_up_screen") },
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = "Sign Out",
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                    Button(
                        onClick = { navController.navigate("register_event_screen") },
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = "Create Event",
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.onSecondary
                        )
                    }
                }
                Spacer(modifier = Modifier.height(20.dp))
            }
            // Scrollable content area using LazyColumn
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f)
                    .padding(horizontal = 16.dp)
            ) {

                if (suggestedEvents.isNotEmpty()) {
                    item {
                        Text(
                            text = "Suggested Events",
                            style = MaterialTheme.typography.headlineSmall,
                            color = Color.White
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        LazyRow(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(PurpleBKG)
                        ) {
                            items(suggestedEvents) { event ->
                                EventCard(
                                    event = event,
                                    onBottomButtonClick = {},
                                    onEditEvent = {},
                                    isHorizontal = true,
                                    onClick = { selectedEvent.value = event }
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                    }
                }

                when {
                    isLoading -> {
                        item {
                            Text(
                                "Loading events...",
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color.White
                            )
                        }
                    }

                    errorMessage != null -> {
                        item {
                            Text(
                                errorMessage!!,
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color.Red
                            )
                        }
                    }

                    else -> {
                        items(events) { event ->
                            EventCard(
                                onClick = {
                                    if (selectedEvent.value != event) {
                                        selectedEvent.value = event
                                    }
                                },
                                event = event,
                                onBottomButtonClick = { selectedEvent ->
                                    viewModel.registerForEvent(
                                        selectedEvent,
                                        UserSession.currentUser
                                    )
                                    Toast.makeText(context, "REGISTERED!", Toast.LENGTH_SHORT)
                                        .show()
                                },
                                onEditEvent = {}
                            )
                        }
                    }
                }
            }
        }

        // Show event detail popup when an event is selected
        selectedEvent.value?.let { event ->
            if (showDialog.value && selectedEvent.value != null) {
                EventDetailDialog(
                    event = event,
                    onDismiss = {
                        selectedEvent.value = null
                        showDialog.value = false
                    },
                    isUserRegistered = isRegistered.value,   // Pass isUserRegistered
                    isUserOnWaitList = isOnWaitlist.value,   // Pass isUserOnWaitList
                    showRegisterButton = !isRegistered.value && !isCheckingRegistration.value,  // Show register button only if not registered
                    onRegister = {
                        // When the user clicks the register button, we manually trigger the registration
                        isRegistered.value = true // Mark the user as registered
                        viewModel.registerForEvent(
                            event,
                            UserSession.currentUser
                        ) // Perform registration
                        Toast.makeText(context, "Successfully Registered!", Toast.LENGTH_SHORT)
                            .show()
                    },
                    showWaitListButton = !isOnWaitlist.value && !isCheckingWaitlist.value,  // Show waitlist button only if not on waitlist
                    onJoinWaitlist = {
                        isOnWaitlist.value = true
                        viewModel.addToWaitingList(UserSession.currentUser, event)
                        Toast.makeText(
                            context,
                            "You've been added to the waiting list.",
                            Toast.LENGTH_SHORT
                        ).show()
                    },
                    navController = navController
                )
            }
        }
    // show notifications popup
        if (showNotificationsDialog.value) {
            AlertDialog(
                onDismissRequest = { showNotificationsDialog.value = false },
                confirmButton = {
                    Row(horizontalArrangement = Arrangement.End) {
                        TextButton(onClick = {
                            showNotificationsDialog.value = false
                        }) {
                            Text("Close")
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        TextButton(onClick = {
                            UserSession.currentUser?.id?.let {
                                viewModel.loadAllNotifications(it)
                            }
                        }) {
                            Text("Refresh")
                        }
                    }
                },
                title = {
                    Text("Notifications")
                },
                text = {
                    val notifications = viewModel.allNotifications.value
                    if (notifications.isEmpty()) {
                        Text("No notifications.")
                    } else {
                        Column(
                            modifier = Modifier
                                .heightIn(max = 400.dp)
                                .verticalScroll(rememberScrollState())
                        ) {
                            notifications.forEach { notification ->
                                NotificationCard(
                                    notification = notification,
                                    onClick = {
                                        viewModel.markNotificationAsReadInViewModel(notification)
                                    }
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                            }
                        }
                    }
                }
            )
        }
    }
}

// Notification card composable
@Composable
fun NotificationCard(
    notification: Notification,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .padding(horizontal = 8.dp, vertical = 8.dp)
            .clip(RoundedCornerShape(12.dp))
            .clickable { onClick() },
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Notification type handling
            when (notification.type) {
                NotificationType.EVENT -> {
                    // Display event-related notifications
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Event, contentDescription = "Event Notification", tint = Color.Gray)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = notification.message,  // Event-related message
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
                NotificationType.FRIEND_ACTION -> {
                    // Display friend action-related notifications
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.PersonAdd, contentDescription = "Friend Action Notification", tint = Color.Gray)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = notification.message,  // Friend action-related message
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Show date and time of notification
            notification.created_at?.let { createdAt ->
                val formattedDate = formatDate(createdAt)
                Text(
                    text = formattedDate,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )
            }

            //display read/unread state
            if (notification.is_read == false) {
                Text(
                    text = "New notification",
                    style = MaterialTheme.typography.bodySmall.copy(color = Color.Red)
                )
            }
        }
    }
}

// Custom date formatter for Supabase timestampz format
fun formatDate(raw: String): String {
    return try {
        // Parse timestampz
        val offsetDateTime = OffsetDateTime.parse(raw)

        // Format it to a readable string
        val displayFormatter = DateTimeFormatter.ofPattern("MMM d, yyyy • h:mm a")
        offsetDateTime.atZoneSameInstant(ZoneId.systemDefault()).format(displayFormatter)

    } catch (e: DateTimeParseException) {
        raw // just display regular timestamp if an error
    }
}