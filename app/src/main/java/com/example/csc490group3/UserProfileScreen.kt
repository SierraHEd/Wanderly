import android.annotation.SuppressLint
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Celebration
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.csc490group3.R
import com.example.csc490group3.data.BottomNavBar
import com.example.csc490group3.model.Event
import com.example.csc490group3.model.IndividualUser
import com.example.csc490group3.model.UserSession
import com.example.csc490group3.supabase.DatabaseManagement.getFriends
import com.example.csc490group3.ui.components.CategoryPickerBottomSheet
import com.example.csc490group3.ui.components.EventCard
import com.example.csc490group3.ui.theme.Purple40
import com.example.csc490group3.ui.theme.PurpleBKG
import com.example.csc490group3.ui.theme.PurpleDarkBKG
import com.example.csc490group3.ui.theme.PurpleStart
import com.example.csc490group3.viewModels.UserProfileViewModel
import java.io.File
import java.util.UUID

@Composable
fun UserProfileScreen(navController: NavController) {
    var showSettings by remember { mutableStateOf(false) }
    var showFriends by remember { mutableStateOf(false) }
    val isCurrentUser = true // Make it so that we can tell if viewed user is the logged in user
    val profilePictureUrl = UserSession.currentUser?.profile_picture_url
    // val CurrentUser = UserSession.currentUser?.email.?


    /*
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(PurpleBKG)
            .padding(16.dp)

    ) {

        Row(Modifier.background(PurpleBKG))

        {
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
                fontSize = 24.sp,
                color = Color.Black,

                )
            if (isCurrentUser) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                    IconButton(onClick = { showSettings = true }) {
                        Icon(imageVector = Icons.Default.Settings, contentDescription = "Settings")
                    }
                }
            }
            // Settings Button (Only for current user)

            if (isCurrentUser) {
                Row(modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Absolute.Right) {
                    Button(
                        modifier = Modifier,
                        onClick = { showSettings = true },
                        colors = ButtonDefaults.buttonColors(containerColor = PurpleContainer),
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

             */
    Scaffold(
        bottomBar = { BottomNavBar(navController) }
    ) { paddingValues ->
        Surface(
            modifier = Modifier
                .background(PurpleBKG)
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
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
                    IconButton(onClick = { showFriends = true }) {
                        Icon(imageVector = Icons.Default.People, contentDescription = "Friends")
                    }
                    if (isCurrentUser) {
                        IconButton(onClick = { showSettings = true }) {
                            Icon(
                                imageVector = Icons.Default.Settings,
                                contentDescription = "Settings"
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))


                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    //pfp
                    Image(
                        painter = rememberAsyncImagePainter(profilePictureUrl ?: R.drawable.app_icon),
                        contentDescription = "Profile Picture",
                        modifier = Modifier
                            .size(120.dp)
                            .clip(CircleShape)
                            .border(2.dp, Color.Black, CircleShape),
                        contentScale = ContentScale.Crop
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Welcome back, " + (UserSession.currentUserEmail.toString()
                            .substringBefore("@")),
                        fontSize = 24.sp,
                        color = Color.Black
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Saved Events (seen by current user only)
                if (isCurrentUser) {
                    Section1(title = "My Saved Events", fontSize = 20.sp)
                }

                // Hosted Events (seen by non current users)
                Section2(title = "My Hosted Events", fontSize = 20.sp)
            }

            if (showSettings) {
                SettingsDialog(
                    onDismiss = { showSettings = false },
                    navController
                )
            }

            if (showFriends) {
                FriendsDialog(onDismiss = { showFriends = false },
                    navController
                )
            }
        }
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
            EventCard(event = event, onBottomButtonClick = { selectedEvent ->
                viewModel.unregisterForEvent(selectedEvent, UserSession.currentUser)
            },
            onEditEvent = {},
                onClick = {},
                isHorizontal = true,
                showUnregisterButton = true
            )
        }
    }

}

@OptIn(ExperimentalMaterial3Api::class)
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
            EventCard(
                event = event,
                onBottomButtonClick = {selectedEvent ->
                    eventToDelete = event
                    showDeleteDialog = true
                },
                onEditEvent = {selectedEvent ->
                    viewModel.editEvent(selectedEvent)
                },
                onClick = {},
                isHorizontal = true,
                showOptionsButton = true,
            )
        }
    }
    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Confirm Deletion") },
            text = { Text("Are you sure you want to delete this event? This action cannot be undone.") },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("Cancel")
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        eventToDelete?.let { viewModel.deleteEvent(it) } // Actually delete the event
                        showDeleteDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
                ) {
                    Text("Delete", color = Color.White)
                }
            }
        )
    }

}

@Composable
fun SettingsDialog(onDismiss: () -> Unit, navController: NavController, viewModel: UserProfileViewModel = viewModel()) {
    val context = LocalContext.current

    val filePicker = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        uri?.let {
            val inputStream = context.contentResolver.openInputStream(uri)
            val file = File(context.cacheDir, "profile_${UUID.randomUUID()}.jpg")
            inputStream.use { input ->
                file.outputStream().use { output ->
                    input?.copyTo(output)
                }
            }
            UserSession.currentUser?.id?.let { userId ->
                viewModel.uploadAndSetProfilePicture(file, userId)
            }
        }
    }
    var isDarkMode by remember { mutableStateOf(false) }
    var isPublic by remember { mutableStateOf(true) }
    var showEventPrefs by remember { mutableStateOf(false) }
    var showCategoryPicker by remember { mutableStateOf(false) }
    var selectedCategories by remember { mutableStateOf(UserSession.currentUserCategory) }

    AlertDialog(

        onDismissRequest = onDismiss,
        title = { Text("Account Settings") },
        containerColor = PurpleStart,
        icon = { Icon(Icons.Filled.Settings, "", tint = Purple40, modifier = Modifier.padding(horizontal = (30.dp))) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {


                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("Private Account")
                    Spacer(Modifier.weight(1f))
                    Switch(checked = isPublic, onCheckedChange = {isPublic = it }
                        //Make it so that account will remember change
                    )
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("Dark Mode")
                    Spacer(Modifier.weight(1f))
                    Switch(checked = isDarkMode, onCheckedChange = {isDarkMode = it }
                        //Make it so that account will remember change
                    )
                }

                Button(
                    colors = ButtonDefaults.buttonColors(containerColor = PurpleDarkBKG),
                    onClick = { /* Handle location preferences */ }) {
                    Text("Location Preferences")
                }

                Button(
                    onClick = { showCategoryPicker = true },
                    colors = ButtonDefaults.buttonColors(containerColor = PurpleDarkBKG),
                    shape = RoundedCornerShape(20.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 15.dp)
                ) {
                    Text(
                        text = selectedCategories.joinToString(", ") { it.name },
                        fontSize = 22.sp,
                        color = White,
                        modifier = Modifier.weight(1f),
                        textAlign = TextAlign.Left,
                        fontFamily = FontFamily.Default
                    )
                }
                //calls to bring up category selection bottom sheet
                CategoryPickerBottomSheet(
                    showSheet = showCategoryPicker,
                    onDismiss = {showCategoryPicker = false},
                    onSelectionDone = {selection ->
                        selectedCategories = selection
                    },
                    maxSelections = 3,
                    initialSelectedCategories = UserSession.currentUserCategory,
                    userCategories = true,
                    updateCategories = true
                )


                Button(
                    colors = ButtonDefaults.buttonColors(containerColor = PurpleDarkBKG),
                    onClick = { filePicker.launch("image/*") }) {
                    Text("Change Profile Picture")
                }
                Button(
                    colors = ButtonDefaults.buttonColors(containerColor = PurpleDarkBKG),
                    onClick = { navController.navigate("start_up_screen") }) {
                    Text("Log Out")
                }
                Button(
                    colors = ButtonDefaults.buttonColors(containerColor = PurpleDarkBKG),
                    onClick = { /* Handle profile picture change */ },
                ) {
                    Text("Contact Us")
                }
                Button(
                    colors = ButtonDefaults.buttonColors(containerColor = PurpleDarkBKG),
                    onClick = { /* TOS stuff */ })
                {
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

@SuppressLint("CoroutineCreationDuringComposition")
@Composable
fun FriendsDialog(onDismiss: () -> Unit, navController: NavController) {

    // Change from dummy data to database data.
    // Make buttons change in database.
    // Make scrollable.
    // Only put buttons if viewing own account.
    // Make names clickable to view their account.

    val isCurrentUser = true // Modify based on current user logic
    val friendsList = remember { mutableStateOf<List<IndividualUser>?>(null) } // Store the friends list

    // Fetch the friends list asynchronously
    LaunchedEffect(Unit) {
        friendsList.value = getFriends(UserSession.currentUser?.id ?: return@LaunchedEffect)
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Manage Friends") },
        containerColor = PurpleStart,
        icon = {
            Icon(
                Icons.Filled.Celebration, contentDescription = "",
                tint = Purple40, modifier = Modifier.padding(horizontal = 30.dp)
            )
        },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.verticalScroll(rememberScrollState())
            ) {
                Text("Friends List", fontSize = 18.sp)

                friendsList.value?.forEach { friend ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Make friend's name clickable
                        Text(
                            text = "${friend.firstName} ${friend.lastName}",
                            fontSize = 16.sp,
                            modifier = Modifier.clickable {
                                // Navigate to friend's profile screen
                                navController.navigate("friends_profile_screen/${friend.email}")
                            }
                        )
                        if (isCurrentUser) {
                            IconButton(onClick = {
                            /* Unfriend logic */
                            }) {
                                Icon(Icons.Filled.Remove, "Unfriend")
                            }
                        }
                    }
                } ?: Text("Loading friends...", fontSize = 16.sp)

                Spacer(modifier = Modifier.height(16.dp))


            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Close")
            }
        }
    )
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
        icon = { Icon(Icons.Filled.Celebration, "", tint = Purple40, modifier = Modifier.padding(horizontal = (30.dp))) },
        onDismissRequest = onDismiss,
        title = { Text("Select Event Preferences") },
        text = {
            Column(modifier = Modifier.verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(8.dp)) {
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
                        ),
                        modifier = Modifier
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