package com.example.csc490group3

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp // Import this for 'sp'
import coil.compose.rememberAsyncImagePainter
import com.example.csc490group3.model.Message
import com.example.csc490group3.model.User
import com.example.csc490group3.model.UserSession
import com.example.csc490group3.ui.components.IncomingMessageBubble
import com.example.csc490group3.ui.components.OutgoingMessageBubble
import kotlinx.datetime.LocalDateTime
import java.util.UUID
import androidx.compose.foundation.Image
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Send
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.navigation.NavController
import com.example.csc490group3.model.IndividualUser
import com.example.csc490group3.supabase.DatabaseManagement.getPrivateUser
import kotlinx.datetime.Clock
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.TimeZone
import kotlinx.datetime.minus
import kotlinx.datetime.toLocalDateTime
import kotlin.time.DurationUnit

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewConversationScreen(
    friendEmail: String,
    navController: NavController
) {
    val messageText = remember { mutableStateOf("") }
    var friend by remember { mutableStateOf<IndividualUser?>(null) }

    // Fetch the friend (private user) details based on the friend's email
    LaunchedEffect(friendEmail) {
        friend = getPrivateUser(friendEmail)
    }

    // Set friend first and last name to empty string if null
    val firstName = friend?.firstName ?: ""
    val lastName = friend?.lastName ?: ""

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(8.dp)
    ) {
        // Back Button and Profile Header
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp),
                horizontalArrangement = Arrangement.Start
            ) {
                IconButton(
                    onClick = { navController.navigate("messages_screen") },  // Navigate back to the previous screen
                    modifier = Modifier.align(Alignment.CenterVertically)
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Back",
                        tint = Color.Black
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(Color.Gray.copy(alpha = 0.3f)),
                    contentAlignment = Alignment.Center
                ) {
                    if (!friend?.profile_picture_url.isNullOrEmpty()) {
                        Image(
                            painter = rememberAsyncImagePainter(friend?.profile_picture_url),
                            contentDescription = "Profile Picture",
                            modifier = Modifier
                                .size(48.dp)
                                .clip(CircleShape),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = "Profile Picture",
                            tint = Color.DarkGray,
                            modifier = Modifier.size(28.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                    Text(
                        text = "$firstName $lastName",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                }
            }
        }

        // No messages, so we skip the LazyColumn

        // Message Input Box and Send Button
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
                .align(Alignment.BottomCenter) // Align the text box to the bottom
        ) {
            TextField(
                value = messageText.value,
                onValueChange = { messageText.value = it },
                placeholder = { Text("Type a message...") },
                modifier = Modifier
                    .weight(1f)
                    .background(Color.LightGray, shape = RoundedCornerShape(12.dp))
                    .padding(16.dp)
            )
            IconButton(
                onClick = {
                    // TODO: Implement creating a new message logic (send to backend). =
                    // For now, just clear the input field
                    messageText.value = ""  // Clear the input after sending
                },
                modifier = Modifier
                    .align(Alignment.CenterVertically)
            ) {
                Icon(
                    imageVector = Icons.Default.Send,
                    contentDescription = "Send Message",
                    tint = Color.Blue
                )
            }
        }
    }
}