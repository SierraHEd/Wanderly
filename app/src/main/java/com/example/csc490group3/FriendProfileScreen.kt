package com.example.csc490group3

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.csc490group3.ui.theme.PurpleBKG
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.unit.TextUnit
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.csc490group3.model.Event
import com.example.csc490group3.model.IndividualUser
import com.example.csc490group3.model.UserSession
import com.example.csc490group3.supabase.DatabaseManagement.getPrivateUser
import com.example.csc490group3.ui.components.EventCard
import com.example.csc490group3.viewModels.UserProfileViewModel

@Composable
fun FriendProfileScreen(navController: NavController, friendEmail: String) {
    var showSettings by remember { mutableStateOf(false) }
    var friend by remember { mutableStateOf<IndividualUser?>(null) }

    // 🔁 Fetch the user info once when the screen loads
    LaunchedEffect(friendEmail) {
        friend = getPrivateUser(friendEmail)
    }

    val firstName = friend?.firstName
    val lastName = friend?.lastName

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(PurpleBKG)
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            IconButton(onClick = { navController.navigate("home_screen") }) {
                Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back")
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Profile picture
            Image(
                painter = painterResource(id = R.drawable.app_icon),
                contentDescription = null,
                modifier = Modifier
                    .size(120.dp)
                    .clip(CircleShape)
                    .border(2.dp, Color.Black, CircleShape),
                contentScale = ContentScale.Crop
            )
            Spacer(modifier = Modifier.height(8.dp))
            // ✅ Proper display name
            Text(
                text = "$firstName $lastName",
                fontSize = 24.sp,
                color = Color.Black
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Section1(title = "$firstName's Saved Events", fontSize = 20.sp)
        Section2(title = "$firstName's Hosted Events", fontSize = 20.sp)
    }
}
@Composable

fun Section1(title: String, viewModel: UserProfileViewModel = viewModel(), fontSize: TextUnit) {
    val events by viewModel.registeredEvents
    Row() {
        Text(
            text = title,
        )
    }
    LazyRow {

        items(events) { event ->

        }
    }

}

@Composable
fun Section2(title: String, viewModel: UserProfileViewModel = viewModel(),fontSize: TextUnit) {
    val events by viewModel.createdEvents
    var showDeleteDialog by remember { mutableStateOf(false) }
    var eventToDelete by remember { mutableStateOf<Event?>(null) }

    Row() {
        Text(
            text = title,
        )
    }
    LazyRow {

        items(events) { event ->

        }
    }


}
