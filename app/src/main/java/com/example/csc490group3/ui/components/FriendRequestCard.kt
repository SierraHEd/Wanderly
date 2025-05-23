package com.example.csc490group3.ui.components

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
import androidx.navigation.compose.rememberNavController
import coil.compose.rememberAsyncImagePainter
import com.example.csc490group3.model.IndividualUser
import com.example.csc490group3.model.User
import com.example.csc490group3.model.UserSession

@Composable
fun FriendRequestCard(
    user: IndividualUser,
    isIncoming: Boolean,
    onAccept: (() -> Unit)? = null,
    onDeclineOrCancel: () -> Unit,
    navController: NavController
) {

    Card(
        modifier = Modifier
            .fillMaxWidth() .clickable { onUserClick(
            user = user,
            navController = navController
        ) } // <- Click support here
            .padding(horizontal = 16.dp, vertical = 8.dp),

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
                    //CHECK HERE TO TEST FOR PROFILE
                    if (user.profile_picture_url != null && user.profile_picture_url!!.isNotEmpty()) {

                        androidx.compose.foundation.Image(//display photo blah blah blah
                            painter = rememberAsyncImagePainter(user.profile_picture_url),
                            contentDescription = "Event Photo",
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(180.dp)
                                .clip(RoundedCornerShape(28.dp)),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        // placeholder if no photo URL is available
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
                    //Text(text = UserSession.currentUser., fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    Text(text = user.email, fontSize = 14.sp, color = Color.Gray)
                }
            }

            Row {
                if (isIncoming) {
                    IconButton(onClick = { onAccept?.invoke() }) {
                        Icon(Icons.Default.Check, contentDescription = "Accept")
                    }
                    IconButton(onClick = { onDeclineOrCancel() }) {
                        Icon(
                            Icons.Default.Clear, //else Icons.Default.Cancel,
                            contentDescription = "Decline"
                        )
                    }
                }
                else{
                    IconButton(onClick = { onDeclineOrCancel() }) {
                        Icon(
                            Icons.Default.Cancel,
                            contentDescription = "Cancel"
                        )
                    }
                }

            }
        }
    }
}