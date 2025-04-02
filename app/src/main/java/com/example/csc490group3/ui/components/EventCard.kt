package com.example.csc490group3.ui.components

import androidx.compose.foundation.Image
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.People
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.example.csc490group3.R
import com.example.csc490group3.model.Event

@Composable
fun EventCard(
    event: Event,
    onClick: () -> Unit,
    onBottomButtonClick: (Event) -> Unit,
    onEditEvent: (Event) -> Unit,
    showUnregisterButton: Boolean = false,
    showOptionsButton: Boolean = false,
    isHorizontal: Boolean = false,
    modifier: Modifier = Modifier
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

            //if available, else show a placeholder
            if (event.photoUrl != null && event.photoUrl.isNotEmpty()) {

                androidx.compose.foundation.Image(//display photo blah blah blah
                    painter = rememberAsyncImagePainter(event.photoUrl),
                    contentDescription = "Event Photo",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                        .clip(RoundedCornerShape(12.dp)),
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

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = event.eventName,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(4.dp))

            // location and address
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.LocationOn, contentDescription = "Location", tint = Color.Gray)
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "${event.venue}, ${event.address} ${event.city} ${event.state} ${event.zipcode}",
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            // attendees & Price
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.People, contentDescription = "Attendees", tint = Color.Gray)
                    Spacer(modifier = Modifier.width(4.dp))

                    // Handle nullable numAttendees
                    val numAttendees = event.numAttendees
                    val maxAttendees = event.maxAttendees

                    // Check if numAttendees is null and handle accordingly
                    if (numAttendees == null) {
                        // Handle the case when numAttendees is null (e.g., show a placeholder)
                        Text(text = "Attendees data unavailable", color = Color.Gray)
                    } else {
                        Text(
                            text = if (numAttendees < maxAttendees) "$numAttendees Out Of $maxAttendees" else "Max Attendees Reached!",
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
            //Hides register button if needed
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

//Shows all event details in a popup
@Composable
fun EventDetailDialog(
    event: Event,
    onDismiss: () -> Unit, // Close button action
    onRegister: (Event) -> Unit, // Click Register button actions
    showRegisterButton: Boolean, // Pass this flag to conditionally show the button
    isUserRegistered: Boolean = false, //Check user events table for match
    alreadyRegisteredText: String? = null // New parameter for showing registered text
) {
    androidx.compose.material3.AlertDialog(
        onDismissRequest = { onDismiss() },
        title = { Text(text = event.eventName) },
        text = {
            Column {
                // Image at the top of the details popup
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(100.dp) // Adjust size as needed
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.app_logo),
                        contentDescription = "Event Image",
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(RoundedCornerShape(8.dp))
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

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

                // Display "Already Registered" text if user is registered
                if (alreadyRegisteredText != null) {
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = alreadyRegisteredText,
                        color = Color.Magenta,
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Check if the user is already registered or if the button should be shown
                if (isUserRegistered) {
                    // Show "Already Registered" message at the bottom
                    Text(
                        text = alreadyRegisteredText ?: "You are already registered for this event!",
                        color = Color.Green,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center
                    )
                } else if (showRegisterButton) {
                    // Register Button - Only show if the user is not registered
                    if ((event.numAttendees ?: 0) < event.maxAttendees) {
                        Button(
                            onClick = { onRegister(event) },
                            modifier = Modifier.fillMaxWidth(),
                            enabled = (event.numAttendees ?: 0) < event.maxAttendees,
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1976D2)) // Blue color
                        ) {
                            Text(
                                "Register for Event",
                                color = Color.White,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    } else {
                        Text(
                            "Registration Full",
                            color = Color.Red,
                            modifier = Modifier.fillMaxWidth(),
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(onClick = { onDismiss() }) {
                Text("Close")
            }
        }
    )
}
