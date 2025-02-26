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
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.csc490group3.data.ButtonComponent
import com.example.csc490group3.model.Event
import com.example.csc490group3.supabase.DatabaseManagement.getAllEvents

@Composable
fun EventCard(event: Event) {
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
                text = "Venue: ${event.venueName}",
            )
            Text(
                text = "Description: ${event.eventDescription}",
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Price: ${String.format("$%.2f", event.cost)}",
            )
        }
    }

}

@Composable
fun EventsScreen(events: List<Event>) {
    LazyColumn {
        items(events) { event ->
            EventCard(event = event)

        }
    }
}

@Composable
fun HomeScreen(navController: NavController) {

    var events by remember { mutableStateOf<List<Event>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

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
                            EventCard(event = event)
                        }
                    }
                }
            }

        }
    }
}