package com.example.csc490group3


import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.csc490group3.data.ButtonComponent
import com.example.csc490group3.model.Event
import com.example.csc490group3.model.UserSession
import com.example.csc490group3.supabase.DatabaseManagement.getAllEvents
import com.example.csc490group3.supabase.DatabaseManagement.getCurrentUserEvents
import com.example.csc490group3.supabase.DatabaseManagement.registerEvent
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

@Composable
fun EventCard(event: Event, onRegisterClick: (Event) -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = event.eventName,
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Address: ${event.address}",
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Venue: ${event.venue}",
            )
            Text(
                text = "Description: ${event.description}",
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Price Range: ${event.priceRange}",
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Number of Attendees: ${event.numAttendees}",
            )
            Spacer(modifier = Modifier.height(4.dp))
            Button(onClick = {onRegisterClick(event)}) {
                Text("Register")
            }

        }
    }

}

@Composable
fun HomeScreen(navController: NavController) {

    var events by remember { mutableStateOf<List<Event>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        try {
            val result = getAllEvents()
            println("Fetched Events: $result")

            if (result != null) {
                events = result
            }
        } catch (e: Exception) {
            errorMessage = "Error: ${e.localizedMessage}"
        } finally {
            isLoading = false
        }
    }

    Surface(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.padding(16.dp)) {

            Text(text = "Home Page", style = MaterialTheme.typography.headlineMedium)
            Spacer(modifier = Modifier.height(20.dp))
            Row(modifier = Modifier
                .fillMaxWidth()
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
            Button(onClick = {
                coroutineScope.launch {
                    UserSession.currentUser?.let { getCurrentUserEvents(it) }
                } }) {
                Text("TEST")
            }
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
                            EventCard(event = event, onRegisterClick = {selectedEvent ->
                                coroutineScope.launch {
                                    if(UserSession.currentUser != null) {
                                        registerEvent(selectedEvent, UserSession.currentUser)
                                    }
                                }
                            })
                        }
                    }
                }
            }

        }
    }
}