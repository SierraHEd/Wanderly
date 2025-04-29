package com.example.csc490group3.ui.components


import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.csc490group3.model.ConversationPreview
import com.example.csc490group3.model.IndividualUser
import com.example.csc490group3.model.User
import com.example.csc490group3.model.UserSession
import com.example.csc490group3.supabase.DatabaseManagement.getPrivateUser
import com.example.csc490group3.supabase.DatabaseManagement.isUserPublicById
import com.example.csc490group3.supabase.getUnreadCountBetween
import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.json.Json


@Composable
fun UserChatCard(
    conversation: ConversationPreview,
    navController: NavController
) {
    var friend by remember { mutableStateOf<IndividualUser?>(null) }
    val firstName = friend?.firstName ?: ""
    val lastName = friend?.lastName ?: ""
    var unreadCount by remember { mutableStateOf(0) }

    LaunchedEffect(conversation.otherUserID) {
        friend = getPrivateUser(conversation.otherUserID)
        unreadCount = UserSession.currentUser?.id?.let { getUnreadCountBetween(it, conversation.otherUserID) }!!
    }

    val latestMessage = conversation.lastMessage ?: ""
    val messageTime = conversation.lastMessageTime?.let {
        // Format LocalDateTime however you'd like — this is just an example:
        "${it.hour}:${it.minute.toString().padStart(2, '0')}"
    } ?: ""

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .clickable {
                navController.navigate("conversation_screen/${conversation.otherUserID}")
            },
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
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
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = latestMessage,
                        fontSize = 12.sp,
                        color = Color.DarkGray,
                        maxLines = 1
                    )
                }
            }
            if (unreadCount > 0) {
                Box(
                    Modifier
                        .padding(top = 4.dp)
                        .background(Color.Red, CircleShape)
                        .padding(horizontal = 6.dp, vertical = 6.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = unreadCount.toString(),
                        color = Color.White,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Text(
                text = messageTime,
                fontSize = 12.sp,
                color = Color.Gray
            )

        }
    }
}