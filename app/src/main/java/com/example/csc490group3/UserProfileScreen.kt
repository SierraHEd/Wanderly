import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Celebration
import androidx.compose.material.icons.filled.Remove
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
import com.example.csc490group3.R
import com.example.csc490group3.model.UserSession
import com.example.csc490group3.ui.theme.Purple40
import com.example.csc490group3.ui.theme.PurpleBKG
import com.example.csc490group3.ui.theme.PurpleContainer
import com.example.csc490group3.ui.theme.PurpleDarkBKG
import com.example.csc490group3.ui.theme.PurpleStart
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.csc490group3.model.UserSession.currentUserEmail
import com.example.csc490group3.viewModels.HomeScreenViewModel
import com.example.csc490group3.viewModels.UserProfileViewModel


@Composable
fun UserProfileScreen(navController: NavController) {
    var showSettings by remember { mutableStateOf(false) }
    val isCurrentUser = true // Make it so that we can tell if viewed user is the logged-in user

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(PurpleBKG)
            .padding(16.dp)
    ) {
        Row(Modifier.background(PurpleBKG)) {
            // Profile Picture
            Image(
                painter = painterResource(id = R.drawable.app_icon),
                contentDescription = null,
                modifier = Modifier
                    .size(50.dp)
                    .clip(CircleShape)
                    .clickable { navController.navigate("Home_Screen") }
                    .border(0.dp, Color.Transparent, CircleShape)
                    .padding(5.dp),
                contentScale = ContentScale.Crop
            )

            Text(
                text = "Welcome back, " + (currentUserEmail.toString().substringBefore("@")),
                modifier = Modifier.padding(10.dp),
                fontSize = 18.sp,
                color = Color.Black
            )

            // Settings Button (Only for current user)
            if (isCurrentUser) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Absolute.Right
                ) {
                    Button(
                        modifier = Modifier,
                        onClick = { showSettings = true },
                        colors = ButtonDefaults.buttonColors(containerColor = PurpleContainer)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = "Settings",
                            modifier = Modifier.size(20.dp),
                            tint = PurpleDarkBKG
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
        }

        // Saved Events (Only for current user)
        if (isCurrentUser) {
            Section1(title = "My Saved Events")
        }

        // Hosted Events (Visible to everyone)
        Section2(title = "My Hosted Events")
    }

    if (showSettings) {
        SettingsDialog(
            onDismiss = { showSettings = false },
            navController
        )
    }
}

@Composable
fun Section1(title: String, viewModel: UserProfileViewModel = viewModel()) {
    val events by viewModel.registeredEvents

    Row {
        Text(text = title)
    }

    LazyRow {
        items(events) { event ->
            com.example.csc490group3.ui.components.EventCard(
                event = event,
                onRegisterClick = {  }
            )
        }
    }
}

@Composable
fun Section2(title: String, viewModel: UserProfileViewModel = viewModel()) {
    val events by viewModel.createdEvents

    Row {
        Text(text = title)
    }

    LazyRow {
        items(events) { event ->
            com.example.csc490group3.ui.components.EventCard(
                event = event,
                onRegisterClick = {  }
            )
        }
    }
}

@Composable
fun SettingsDialog(onDismiss: () -> Unit, navController: NavController) {
    var isDarkMode by remember { mutableStateOf(false) }
    var isPublic by remember { mutableStateOf(true) }
    var showEventPrefs by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Account Settings") },
        containerColor = PurpleStart,
        icon = {
            Icon(
                Icons.Filled.Settings, "",
                tint = Purple40,
                modifier = Modifier.padding(horizontal = 30.dp)
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("Private Account")
                    Spacer(Modifier.weight(1f))
                    Switch(checked = isPublic, onCheckedChange = { isPublic = it })
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("Dark Mode")
                    Spacer(Modifier.weight(1f))
                    Switch(checked = isDarkMode, onCheckedChange = { isDarkMode = it })
                }

                Button(
                    colors = ButtonDefaults.buttonColors(containerColor = PurpleDarkBKG),
                    onClick = { /* Handle location preferences */ }
                ) {
                    Text("Location Preferences")
                }

                Button(
                    colors = ButtonDefaults.buttonColors(containerColor = PurpleDarkBKG),
                    onClick = { showEventPrefs = true }
                ) {
                    Text("Event Preferences")
                }

                Button(
                    colors = ButtonDefaults.buttonColors(containerColor = PurpleDarkBKG),
                    onClick = { /* Handle profile picture change */ }
                ) {
                    Text("Change Profile Picture")
                }
                Button(
                    colors = ButtonDefaults.buttonColors(containerColor = PurpleDarkBKG),
                    onClick = { navController.navigate("start_up_screen") }
                ) {
                    Text("Log Out")
                }
                Button(
                    colors = ButtonDefaults.buttonColors(containerColor = PurpleDarkBKG),
                    onClick = { /* Handle profile picture change */ }
                ) {
                    Text("Contact Us")
                }
                Button(
                    colors = ButtonDefaults.buttonColors(containerColor = PurpleDarkBKG),
                    onClick = { /* TOS stuff */ }
                ) {
                    Text("Terms of Service")
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Close")
            }
        }
    )

    if (showEventPrefs) {
        EventPreferencesDialog(onDismiss = { showEventPrefs = false })
    }
}

@Composable
fun EventPreferencesDialog(onDismiss: () -> Unit) {
    val preferences = listOf(
        "Music", "Food", "Comedy", "Theater", "Movies", "Festivals", "Performance", "Sports", "Charity Events",
        "Sightseeing", "Conferences", "Art", "Cars", "Gaming", "Networking", "BarHopping", "Local Parties"
    )
    val selectedPreferences = remember { mutableStateOf(setOf<String>()) }

    AlertDialog(
        containerColor = PurpleStart,
        icon = {
            Icon(
                Icons.Filled.Celebration, "",
                tint = Purple40,
                modifier = Modifier.padding(horizontal = 30.dp)
            )
        },
        onDismissRequest = onDismiss,
        title = { Text("Select Event Preferences") },
        text = {
            Column(
                modifier = Modifier.verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                preferences.forEach { preference ->
                    val isSelected = preference in selectedPreferences.value
                    Button(
                        onClick = {
                            selectedPreferences.value = if (isSelected) {
                                selectedPreferences.value - preference
                            } else {
                                selectedPreferences.value + preference
                            }
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isSelected) Color.Gray else PurpleDarkBKG
                        )
                    ) {
                        Icon(
                            imageVector = if (isSelected) Icons.Filled.Remove else Icons.Default.Add,
                            contentDescription = null
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(preference)
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Close")
            }
        }
    )
}
