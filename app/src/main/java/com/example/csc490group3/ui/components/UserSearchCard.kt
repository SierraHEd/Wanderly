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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.csc490group3.model.IndividualUser
import com.example.csc490group3.model.User
import com.example.csc490group3.model.UserSession


fun onUserClick(user: User,

                navController: NavController) {
    if(user == UserSession.currentUser){
        navController.navigate("profile_screen")
    }
    else {
        navController.navigate("friends_profile_screen/${user.email}")
    }
}

@Composable
fun UserSearchCard(
    user: User,
    navController: NavController
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .clickable { onUserClick(
                user = user,
                navController = navController
            ) }, // ← Clickable behavior here
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
                    if (!user.profile_picture_url.isNullOrEmpty()) {
                        Image(
                            painter = rememberAsyncImagePainter(user.profile_picture_url),
                            contentDescription = "Profile Picture",
                            modifier = Modifier
                                .size(48.dp) // fixed size inside Box
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
                        text = user.email,
                        fontSize = 14.sp,
                        color = Color.Gray
                    )
                }
            }
        }
    }
}