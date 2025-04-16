package com.example.csc490group3.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.People
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.csc490group3.model.Event
import com.example.csc490group3.model.UserSession
import com.example.csc490group3.supabase.DatabaseManagement
import com.example.csc490group3.supabase.DatabaseManagement.getPrivateUser
import kotlinx.coroutines.launch

@Composable
fun EventCard(
    event: Event,
    onClick: () -> Unit,
    onBottomButtonClick: (Event) -> Unit,
    onEditEvent: (Event) -> Unit,
    showUnregisterButton: Boolean = false,
    showOptionsButton: Boolean = false,
    isHorizontal: Boolean = false,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = Modifier
            .then(
                if (isHorizontal) Modifier.width(250.dp)
                else Modifier.fillMaxWidth()
            )
            .wrapContentHeight()
            .padding(horizontal = 8.dp, vertical = 8.dp)
            .clip(RoundedCornerShape(12.dp))
            .clickable { onClick() },
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Display event image if available, else show a placeholder
            if (event.photoUrl != null && event.photoUrl.isNotEmpty()) {
                Image(
                    painter = rememberAsyncImagePainter(event.photoUrl),
                    contentDescription = "Event Photo",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                        .clip(RoundedCornerShape(12.dp)),
                    contentScale = ContentScale.Crop
                )
            } else {
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
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = event.eventName,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(4.dp))

            // Location and address
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.LocationOn, contentDescription = "Location", tint = Color.Gray)
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "${event.venue}, ${event.address} ${event.city} ${event.state} ${event.zipcode}",
                    style = MaterialTheme.typography.bodyMedium
                )
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

                    val numAttendees = event.numAttendees
                    val maxAttendees = event.maxAttendees

                    if (numAttendees == null) {
                        Text(text = "Attendees data unavailable", color = Color.Gray)
                    } else {
                        Text(
                            text = if (numAttendees < maxAttendees)
                                "$numAttendees Out Of $maxAttendees"
                            else
                                "Max Attendees Reached!",
                            color = if (numAttendees >= maxAttendees) Color.Red else Color.Black
                        )
                    }
                }
                Text(
                    text = "\$${String.format("%.2f", event.price)}",
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF388E3C) // Green for pricing
                )
            }

            Spacer(modifier = Modifier.height(8.dp))
            // Display unregister or options buttons if required
            if (showUnregisterButton) {
                Button(
                    onClick = { onBottomButtonClick(event) },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFA500)) // Yellow color
                ) {
                    Text("Unregister", color = Color.White, fontWeight = FontWeight.Bold)
                }
            } else if (showOptionsButton) {
                Row(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Button(
                        onClick = { onBottomButtonClick(event) },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD32F2F))
                    ) {
                        Text("Delete", color = Color.White, fontWeight = FontWeight.Bold)
                    }
                    Spacer(modifier = Modifier.width(4.dp))
                    Button(
                        onClick = { onEditEvent(event) },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2196F3))
                    ) {
                        Text("Edit", color = Color.White, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
fun EventDetailDialog(
    event: Event,
    onDismiss: () -> Unit, // Close button action
    onRegister: (Event) -> Unit, // Click Register button actions
    showRegisterButton: Boolean, // Pass this flag to conditionally show the button
    isUserRegistered: Boolean = false, //Check user events table for match
    alreadyRegisteredText: String? = null, // New parameter for showing registered text
    navController: NavController
) {

    var firstName by remember { mutableStateOf("") }
    var lastName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var userEmail by remember { mutableStateOf("") }
    var showReportDialog by remember { mutableStateOf(false) }
    var hasReported by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(event.createdBy) {
        val user = getPrivateUser(event.createdBy)
        firstName = user?.firstName ?: ""
        lastName = user?.lastName ?: ""
        email = user?.email ?: ""
        userEmail = UserSession.currentUser?.email ?: ""
    }

    AlertDialog(
        onDismissRequest = { onDismiss() },
        title = { Text(text = event.eventName) },
        text = {
            Column {
                // Image at the top of the details popup
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                ) {
                    if (!event.photoUrl.isNullOrEmpty()) {
                        Image(
                            painter = rememberAsyncImagePainter(event.photoUrl),
                            contentDescription = "Event Photo",
                            modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(12.dp)),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        // placeholder if no photo URL is available
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
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Event details text
                Text(text = "Date: ${event.eventDate}")
                Text(text = "Venue: ${event.venue}")
                Text(text = "Location: ${event.address}, ${event.city}, ${event.state}, ${event.zipcode}")
                Text(text = "Country: ${event.country}")
                Text(text = "Description: ${event.description}")
                Text(text = "Categories: ${event.categories?.joinToString(", ") ?: "No categories"}")
                Text(text = "Max Attendees: ${event.maxAttendees}")
                Text(text = "Number of Attendees: ${event.numAttendees ?: 0}")
                Text(text = "Public: ${event.isPublic?.let { if (it) "Yes" else "No" } ?: "Unknown"}")
                Text(text = "Family Friendly: ${if (event.isFamilyFriendly) "Yes" else "No"}")
                Text(text = "Price: $${event.price ?: 0.0}")

                Text(
                    text = "Created by: $firstName $lastName",
                    modifier = Modifier.clickable {
                        if(email == userEmail) navController.navigate("profile_screen")
                        else navController.navigate("friends_profile_screen/${email}")
                    }
                )
            }
        },
        confirmButton = {
            Row(
                modifier = Modifier.fillMaxWidth().padding(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Button(
                    onClick = { showReportDialog = true },
                    colors = ButtonDefaults.buttonColors(containerColor = if(hasReported) Color.Gray else Color.Red),
                    enabled = !hasReported
                ) {
                    Text(if(hasReported) "Reported" else "Report", color = Color.White)
                }
                Spacer(modifier = Modifier.weight(1f))
                Button(onClick = { onDismiss() }) {
                    Text("Close")
                }
            }
        }
    )

    if (showReportDialog) {
        ReportEventDialog(
            onDismiss = { showReportDialog = false },
            onSubmit = { reason ->
                coroutineScope.launch {
                    val success = DatabaseManagement.reportEvent(event, reason)
                    if (success) hasReported = true
                    showReportDialog = false
                }
            }
        )
    }
}

// Dialog for reporting an event
@Composable
fun ReportEventDialog(
    onDismiss: () -> Unit,
    onSubmit: (String) -> Unit
) {
    var selectedReason by remember { mutableStateOf("Fake Event") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Report Event") },
        text = {
            Column {
                listOf("Fake Event", "Dangerous Event", "Spam event").forEach { reason ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { selectedReason = reason },
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(selected = selectedReason == reason, onClick = { selectedReason = reason })
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(reason)
                    }
                }
            }
        },
        confirmButton = {
            Button(onClick = { onSubmit(selectedReason) }) {
                Text("Submit Report")
            }
        },
        dismissButton = {
            Button(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
