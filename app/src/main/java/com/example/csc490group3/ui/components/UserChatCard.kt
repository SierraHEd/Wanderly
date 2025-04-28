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
import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.json.Json


@Composable
fun UserChatCard(
    conversation: ConversationPreview,
    navController: NavController
) {
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
            Column {
                Text(
                    text = "User ID: ${conversation.otherUserID}", // later replace with name
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = conversation.lastMessage,
                    fontSize = 12.sp,
                    color = Color.DarkGray,
                    maxLines = 1
                )
            }

            Text(
                text = conversation.lastMessageTime.toString(), // format it prettier later
                fontSize = 12.sp,
                color = Color.Gray
            )
        }
    }
}