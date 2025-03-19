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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.csc490group3.R
import com.example.csc490group3.model.Event

@Composable
fun EventCard(event: Event,
              onClick: () -> Unit,
              onBottomButtonClick: (Event) -> Unit,
              onEditEvent: (Event) -> Unit,
              showRegisterButton: Boolean = true,
              showUnregisterButton: Boolean = false,
              showOptionsButton: Boolean = false,
              isHorizontal: Boolean = false,
              modifier: Modifier = Modifier

) {
    Card(
        modifier = Modifier
            .then(
                if(isHorizontal) Modifier.width(250.dp)
                else Modifier.fillMaxWidth()
            )
            .wrapContentHeight()
            .padding(horizontal = 8.dp, vertical = 8.dp)
            .clip(RoundedCornerShape(12.dp))
            .clickable { onClick() },
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            //Placeholder for eventual image
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

            //Location and address
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.LocationOn, contentDescription = "Location", tint = Color.Gray)
                Spacer(modifier = Modifier.width(4.dp))
                Text(text = "${event.venue}, ${event.address} ${event.city} ${event.state} ${event.zipcode}",
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
                    Text(text = "${event.numAttendees} Attending")
                }
                Text(
                    text = "\$${String.format("%.2f", event.price)}",
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF388E3C) // Green for pricing
                )
            }

            Spacer(modifier = Modifier.height(8.dp))
            //Hides register button if needed
            if(showRegisterButton) {
                Button(
                    onClick = { onBottomButtonClick(event) },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1976D2)) // Blue color
                ) {
                    Text("Register", color = Color.White, fontWeight = FontWeight.Bold)
                }
            }
            else if(showUnregisterButton) {
                Button(
                    onClick = {onBottomButtonClick(event)},
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFA500)) // Red color
                ) {
                    Text("Unregister", color = Color.White, fontWeight = FontWeight.Bold)
                }
            }
            else if(showOptionsButton) {
                Row (
                    modifier = Modifier.fillMaxWidth()

                ){
                    Button (
                        onClick = {onBottomButtonClick(event)},
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD32F2F))
                    ) {
                        Text("Delete", color = Color.White, fontWeight = FontWeight.Bold)
                    }
                    Spacer(modifier = Modifier.width(4.dp))
                    Button (
                        onClick = {onEditEvent(event)},
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
    onDismiss: () -> Unit
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
                        modifier = Modifier.fillMaxSize().clip(RoundedCornerShape(8.dp))
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
            }
        },
        confirmButton = {
            Button(onClick = { onDismiss() }) {
                Text("Close")
            }
        }
    )
}