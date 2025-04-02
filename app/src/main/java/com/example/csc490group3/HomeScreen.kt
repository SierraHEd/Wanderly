package com.example.csc490group3


import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.csc490group3.data.BottomNavBar
import com.example.csc490group3.model.Event
import com.example.csc490group3.model.UserSession
import com.example.csc490group3.ui.components.EventCard
import com.example.csc490group3.ui.components.EventDetailDialog
import com.example.csc490group3.ui.theme.PurpleBKG
import com.example.csc490group3.viewModels.HomeScreenViewModel

@Composable
fun HomeScreen(navController: NavController, viewModel: HomeScreenViewModel = viewModel()) {

    val events by viewModel.events
    val isLoading by viewModel.isLoading
    val errorMessage by viewModel.errorMessage
    var context = LocalContext.current
    val selectedEvent = remember { mutableStateOf<Event?>(null) }
    val isRegistered = remember { mutableStateOf(false) }
    val isCheckingRegistration = remember { mutableStateOf(false) } // To track if registration is being checked

    // Check if the user is registered for the event when an event is selected
    selectedEvent.value?.let { event ->
        val currentUser = UserSession.currentUser
        if (currentUser != null) {
            val userID = currentUser.id
            // Use LaunchedEffect to perform the check asynchronously
            LaunchedEffect(userID, event.id) {
                isCheckingRegistration.value = true
                isRegistered.value = (userID != null && event.id != null) &&
                        viewModel.isUserRegisteredForEvent(userID, event.id)
                isCheckingRegistration.value = false
            }
        }
    }

    Scaffold(
        bottomBar = { BottomNavBar(navController) }
    ) { paddingValues ->
        Surface(
            modifier = Modifier
                .background(PurpleBKG)
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            Column(modifier = Modifier
                .background(PurpleBKG)
                .padding(16.dp)) {

                Text(text = "Home Page", style = MaterialTheme.typography.headlineMedium)
                Spacer(modifier = Modifier.height(20.dp))
                Row(modifier = Modifier
                    .fillMaxWidth()
                    .background(PurpleBKG)
                ) {
                    Button(modifier = Modifier.padding(horizontal = 20.dp),
                        onClick = { navController.navigate("start_up_screen") }) {
                        Text("Sign Out")
                    }
                    Button(modifier = Modifier.padding(horizontal = 50.dp),
                        onClick = { navController.navigate("register_event_screen") }) {
                        Text("Create Event")
                    }
                }
                Spacer(modifier = Modifier.height(20.dp))
                /* Add back if a test button is needed
                Button(onClick = { }) {
                    Text("TEST")
                }
                 */
                when {
                    isLoading -> {
                        Text("Loading events...", style = MaterialTheme.typography.bodyMedium)
                    }
                    errorMessage != null -> {
                        Text(
                            errorMessage!!,
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.Red
                        )
                    }
                    else -> {
                        LazyColumn {
                            items(events) { event ->
                                EventCard(
                                    onClick = {
                                        if (selectedEvent.value != event) {
                                            selectedEvent.value = event
                                        }
                                    },
                                    event = event, onBottomButtonClick = { selectedEvent ->
                                        viewModel.registerForEvent(
                                            selectedEvent,
                                            UserSession.currentUser
                                        )
                                        Toast.makeText(context,"REGISTERED!",Toast.LENGTH_SHORT).show()
                                    },
                                    onEditEvent = {}
                                )
                            }
                        }
                    }
                }

            }
        }

        // Show event detail popup when an event is selected
        selectedEvent.value?.let { event ->
            EventDetailDialog(
                event = event,
                onDismiss = { selectedEvent.value = null },
                showRegisterButton = if (isRegistered.value || isCheckingRegistration.value) false else true, // Hide if checking or already registered
                onRegister = {
                    // When the user clicks the register button, we manually trigger the registration
                    isRegistered.value = true // Mark the user as registered
                    viewModel.registerForEvent(
                        event,
                        UserSession.currentUser
                    ) // Perform registration
                    Toast.makeText(context, "Successfully Registered!", Toast.LENGTH_SHORT).show()
                },
                alreadyRegisteredText = if (isRegistered.value) "Already Registered" else null // Pass the message to the dialog
            )
        }
    }
}