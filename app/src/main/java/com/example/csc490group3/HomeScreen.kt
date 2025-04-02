package com.example.csc490group3



import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll


import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.csc490group3.data.BottomNavBar
import com.example.csc490group3.model.UserSession
import com.example.csc490group3.supabase.DatabaseManagement.getCategories
import com.example.csc490group3.ui.components.EventCard
import com.example.csc490group3.ui.theme.PurpleBKG
import com.example.csc490group3.ui.theme.PurpleStart
import com.example.csc490group3.viewModels.HomeScreenViewModel
import kotlinx.coroutines.launch


@Composable
fun HomeScreen(navController: NavController, viewModel: HomeScreenViewModel = viewModel()) {
    val events by viewModel.events
    val suggestedEvents by viewModel.suggestedEvents
    val isLoading by viewModel.isLoading
    val errorMessage by viewModel.errorMessage
    val coroutineScope = rememberCoroutineScope()

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
                Text(
                    text = "Home Page",
                    style = MaterialTheme.typography.headlineMedium,
                    color = Color.White  // Adjust if needed
                )
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
                Button(onClick = {
                    coroutineScope.launch {
                        println(UserSession.currentUser?.id?.let { getCategories(it, "user_categories") })
                    }
                }) {
                    Text("TEST")
                }
                //Spacer(modifier = Modifier.height(4.dp))
            }
            // Scrollable content area using LazyColumn
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f)
                    .padding(horizontal = 16.dp)
            ) {
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
                                onBottomButtonClick = { selectedEvent ->
                                    viewModel.registerForEvent(selectedEvent, UserSession.currentUser)
                                },
                                onEditEvent = {},
                                isHorizontal = true
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))
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
                                event = event,
                                onBottomButtonClick = { selectedEvent ->
                                    viewModel.registerForEvent(selectedEvent, UserSession.currentUser)
                                },
                                onEditEvent = {}
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                        }
                    }
                }
            }
        }
    }
}