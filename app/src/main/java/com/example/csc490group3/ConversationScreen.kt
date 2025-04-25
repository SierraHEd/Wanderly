package com.example.csc490group3

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
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
fun ConversationScreen(
    otherUser: IndividualUser,
    navController: NavController
) {
    val currentUserId = UserSession.currentUser?.id ?: return
    val messagesState = remember { mutableStateOf<List<Message>>(emptyList()) }
    val messageText = remember { mutableStateOf("") }
    var friend by remember { mutableStateOf<IndividualUser?>(null) }
    val firstName = friend?.firstName ?: ""
    val lastName = friend?.lastName ?: ""
    LaunchedEffect(otherUser.email) {
        friend = getPrivateUser(otherUser.email)
    }

    // Fetch messages when the screen is launched
    LaunchedEffect(otherUser.id) {
        messagesState.value = getConversationWithUser(otherUser)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(8.dp)
    ) {
        // Back Button and Profile Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp),
            horizontalArrangement = Arrangement.Start
        ) {
            IconButton(
                onClick = {  navController.navigate("messages_screen") },  // Navigate back to the previous screen
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
                if (!otherUser.profile_picture_url.isNullOrEmpty()) {
                    Image(
                        painter = rememberAsyncImagePainter(otherUser.profile_picture_url),
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

        // Message List (LazyColumn)
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            reverseLayout = true
        ) {
            val messages = messagesState.value
            val reversedMessages = messages.reversed()  // Reverse the messages manually
            items(reversedMessages) { message ->
                if (message.senderID == currentUserId) {
                    OutgoingMessageBubble(message)
                } else {
                    IncomingMessageBubble(message)
                }
            }
        }

        // Message Input Box and Send Button
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
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
                    // Handle sending the message (add the logic here)
                    otherUser.id?.let { sendMessage(messageText.value, currentUserId, it) }
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

suspend fun getConversationWithUser(otherUser: User): List<Message> {

    //TODO: Fetch list of Message object from real data instead. Currently using dummy data.


    // Fake messages for demonstration purposes
    val fakeMessages = listOf(
        Message(
            id = UUID.randomUUID().toString(),
            senderID = otherUser.id ?: 1,  // Assuming 1 is the ID for the other user
            receiverID = UserSession.currentUser?.id ?: 2,  // Assuming 2 is the current user's ID
            content = "Hello, how are you?",
            timeSent = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()),
            isRead = false
        ),
        Message(
            id = UUID.randomUUID().toString(),
            senderID = UserSession.currentUser?.id ?: 2,
            receiverID = otherUser.id ?: 1,
            content = "I'm doing well! How about you?",
            timeSent = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()),
            isRead = true
        ),
        Message(
            id = UUID.randomUUID().toString(),
            senderID = otherUser.id ?: 1,
            receiverID = UserSession.currentUser?.id ?: 2,
            content = "That's great to hear! Let's catch up soon.",
            timeSent = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()),
            isRead = false
        ),
        Message(
            id = UUID.randomUUID().toString(),
            senderID = UserSession.currentUser?.id ?: 2,
            receiverID = otherUser.id ?: 1,
            content = "Sure! Looking forward to it.",
            timeSent = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()),
            isRead = true
        )
    )


    return fakeMessages
}

fun sendMessage(content: String, senderID: Int, receiverID: Int) {
    val currentInstant = Clock.System.now()
    // TODO: Add logic to send the message to the backend or database.
    val message = Message(
        id = UUID.randomUUID().toString(),
        senderID = senderID,
        receiverID = receiverID,
        content = content,
        timeSent = currentInstant.toLocalDateTime(TimeZone.currentSystemDefault()),
        isRead = false
    )

    // Append the new message to the list (this is a temporary placeholder)
    // Update your messages list or UI accordingly.
}

