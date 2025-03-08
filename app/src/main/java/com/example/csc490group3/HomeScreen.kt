package com.example.csc490group3



import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items




import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.csc490group3.data.BottomNavBar
import com.example.csc490group3.model.UserSession
import com.example.csc490group3.ui.components.EventCard
import com.example.csc490group3.viewModels.HomeScreenViewModel


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