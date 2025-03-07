package com.example.csc490group3


import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.People
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.csc490group3.data.BottomNavBar
import com.example.csc490group3.data.ButtonComponent
import com.example.csc490group3.model.Event
import com.example.csc490group3.model.UserSession
import com.example.csc490group3.supabase.DatabaseManagement.getAllEvents
import com.example.csc490group3.supabase.DatabaseManagement.getUserCreatedEvents
import com.example.csc490group3.supabase.DatabaseManagement.getUserEvents
import com.example.csc490group3.supabase.DatabaseManagement.registerEvent
import com.example.csc490group3.viewModels.HomeScreenViewModel
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

@Composable
fun EventCard(event: Event, onRegisterClick: (Event) -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .clip(RoundedCornerShape(12.dp)), // Rounded corners
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
                    .background(Color.Gray, shape = RoundedCornerShape(12.dp))
            ) {
                Text(
                    text = "Event Image",
                    color = Color.White,
                    modifier = Modifier.align(Alignment.Center)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = event.eventName,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(4.dp))

            // change this to be the address plus venue name
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.LocationOn, contentDescription = "Location", tint = Color.Gray)
                Spacer(modifier = Modifier.width(4.dp))
                Text(text = event.venue, style = MaterialTheme.typography.bodyMedium)
            }

            Spacer(modifier = Modifier.height(4.dp))

            // Attendees & Price
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.People, contentDescription = "Attendees", tint = Color.Gray)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = "${event.numAttendees} Attending")
                }
                Text(
                    text = "\$${String.format("%.2f", event.price)}",
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF388E3C) // Green for pricing
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = { onRegisterClick(event) },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1976D2)) // Blue color
            ) {
                Text("Register", color = Color.White, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun HomeScreen(navController: NavController, viewModel: HomeScreenViewModel = viewModel()) {

    val events by viewModel.events
    val isLoading by viewModel.isLoading
    val errorMessage by viewModel.errorMessage

    Scaffold(
        bottomBar = { BottomNavBar(navController) }
    ) { paddingValues ->
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
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
                Button(onClick = { }) {
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
                                    viewModel.registerForEvent(selectedEvent, UserSession.currentUser)
                                })
                            }
                        }
                    }
                }

            }
        }
    }
}