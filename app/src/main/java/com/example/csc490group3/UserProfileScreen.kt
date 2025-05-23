import android.annotation.SuppressLint
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
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
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
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
import androidx.navigation.compose.rememberNavController
import coil.compose.rememberAsyncImagePainter
import com.example.csc490group3.R
import com.example.csc490group3.data.AppStorage
import com.example.csc490group3.data.BottomNavBar
import com.example.csc490group3.model.Event
import com.example.csc490group3.model.IndividualUser
import com.example.csc490group3.model.UserSession



import com.example.csc490group3.supabase.DatabaseManagement.getPrivateUser
import com.example.csc490group3.supabase.DatabaseManagement.isUserPublicById
import com.example.csc490group3.supabase.DatabaseManagement.setUserPrivacy
import com.example.csc490group3.supabase.getFriends
import com.example.csc490group3.supabase.unfriend

import com.example.csc490group3.ui.components.CategoryPickerBottomSheet
import com.example.csc490group3.ui.components.EventCard
import com.example.csc490group3.ui.components.EventDetailDialog
import com.example.csc490group3.ui.theme.Purple40
import com.example.csc490group3.ui.theme.PurpleBKG
import com.example.csc490group3.ui.theme.PurpleDarkBKG
import com.example.csc490group3.ui.theme.PurpleStart
import com.example.csc490group3.viewModels.UserProfileViewModel
import kotlinx.coroutines.launch
import java.io.File
import java.util.UUID

@Composable
fun UserProfileScreen(navController: NavController, appStorage: AppStorage, viewModel: UserProfileViewModel = viewModel()) {
    var showSettings by remember { mutableStateOf(false) }
    var showFriends by remember { mutableStateOf(false) }
    val isCurrentUser = true // Make it so that we can tell if viewed user is the logged in user
    val profilePictureUrl = UserSession.currentUser?.profile_picture_url
    var user by remember { mutableStateOf<IndividualUser?>(null) }
    val isDarkMode by appStorage.isDarkMode.collectAsState(initial = false)

    MaterialTheme(
        colorScheme = if (isDarkMode) lightColorScheme() else darkColorScheme()
    ) {

        LaunchedEffect(UserSession.currentUserEmail) {
            user = UserSession.currentUserEmail?.let { getPrivateUser(it) }
        }
        val firstName = user?.firstName


        // val CurrentUser = UserSession.currentUser?.email.?
        var context = LocalContext.current
        val selectedEvent = remember { mutableStateOf<Event?>(null) }
        var isRegistered = remember { mutableStateOf(false) }

        Scaffold(
            bottomBar = { BottomNavBar(navController) }
        ) { paddingValues ->
            Surface(
                modifier = Modifier
                    .background(MaterialTheme.colorScheme.primary)
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .background(MaterialTheme.colorScheme.primary)
                        .padding(16.dp)
                ) {

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        IconButton(onClick = { navController.navigate("home_screen") }) {
                            Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back", tint = MaterialTheme.colorScheme.onSecondary)
                        }
                        IconButton(onClick = { showFriends = true }) {
                            Icon(imageVector = Icons.Default.People, contentDescription = "Friends", tint = MaterialTheme.colorScheme.onSecondary)
                        }
                        if (isCurrentUser) {
                            Box {
                                IconButton(onClick = { navController.navigate("friend_requests_screen") }) {
                                    Icon(
                                        imageVector = Icons.Default.PersonAdd,
                                        contentDescription = "Friend Requests",
                                        tint = MaterialTheme.colorScheme.onSecondary
                                    )
                                }
                                if (viewModel.incomingRequests.value?.isNotEmpty() == true) {
                                    Box(
                                        modifier = Modifier
                                            .size(10.dp)
                                            .background(Color.Red, shape = CircleShape)
                                            .align(Alignment.TopEnd)
                                            .offset(
                                                x = (-4).dp,
                                                y = 4.dp
                                            ) // adjust for nicer positioning
                                    )
                                }
                            }
                            IconButton(onClick = { showSettings = true }) {
                                Icon(
                                    imageVector = Icons.Default.Settings,
                                    contentDescription = "Settings",
                                    tint = MaterialTheme.colorScheme.onSecondary
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
                            painter = rememberAsyncImagePainter(
                                profilePictureUrl ?: R.drawable.app_icon
                            ),
                            contentDescription = "Profile Picture",
                            modifier = Modifier
                                .size(120.dp)
                                .clip(CircleShape)
                                .border(2.dp, MaterialTheme.colorScheme.onSecondary, CircleShape),
                            contentScale = ContentScale.Crop
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Welcome back, $firstName",
                            fontSize = 24.sp,
                            color = MaterialTheme.colorScheme.onSecondary
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Saved Events (seen by current user only)
                    if (isCurrentUser) {
                        Section1(
                            title = "My Saved Events",
                            fontSize = 20.sp,
                            navController = navController
                        )
                    }

                    // Hosted Events (seen by non current users)
                    Section2(
                        title = "My Hosted Events",
                        fontSize = 20.sp,
                        navController = navController,
                    )
                }

                if (showSettings) {
                    SettingsDialog(
                        onDismiss = { showSettings = false },
                        navController
                    )
                }

                if (showFriends) {
                    FriendsDialog(
                        onDismiss = { showFriends = false },
                        navController
                    )
                }
            }
        }
    }
}

@Composable
fun Section1(title: String, viewModel: UserProfileViewModel = viewModel(), fontSize: TextUnit, navController: NavController) {
    val events by viewModel.registeredEvents
    val selectedEvent = remember { mutableStateOf<Event?>(null) }
    var isRegistered = remember { mutableStateOf(false) }

    Row() {
        Text(
            text = title,
            color = MaterialTheme.colorScheme.onSecondary
        )
    }
    LazyRow {

        items(events) { event ->
            EventCard(event = event,
                onClick = { selectedEvent.value = event} ,
                onBottomButtonClick = { selectedEvent ->
                    viewModel.unregisterForEvent(selectedEvent, UserSession.currentUser)
                },
                onEditEvent = {},
                isHorizontal = true,
                showUnregisterButton = true
            )
        }
    }
    // Show event detail popup when an event is selected
    selectedEvent.value?.let { event ->
        EventDetailDialog(event = event,
            onDismiss = { selectedEvent.value = null },
            showRegisterButton = false,
            showWaitListButton = false,
            onJoinWaitlist = {},
            onRegister = { isRegistered.value = true },
            navController = navController
        )
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Section2(title: String, viewModel: UserProfileViewModel = viewModel(), fontSize: TextUnit, navController: NavController) {
    val events by viewModel.createdEvents
    val selectedEvent = remember { mutableStateOf<Event?>(null) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var eventToDelete by remember { mutableStateOf<Event?>(null) }
    var isRegistered = remember { mutableStateOf(false) }

    Row() {
        Text(
            text = title,
            color = MaterialTheme.colorScheme.onSecondary
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
                onClick = {selectedEvent.value = event},
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
                    Text("Delete", color = White)
                }
            }
        )
    }

    // Show event detail popup when an event is selected
    selectedEvent.value?.let { event ->
        EventDetailDialog(event = event, onDismiss = { selectedEvent.value = null },
            showRegisterButton = false,
            navController = navController,
            showWaitListButton = false,
            onJoinWaitlist = {},
            onRegister = { isRegistered.value = true })

    }
}

@Composable
fun SettingsDialog(onDismiss: () -> Unit, navController: NavController, viewModel: UserProfileViewModel = viewModel()) {
    val context = LocalContext.current
    val store = AppStorage(LocalContext.current)
    var darkModeEnabled by remember { mutableStateOf(false) }

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
    val coroutineScope = rememberCoroutineScope()
    LaunchedEffect(UserSession.currentUser?.id) {
        UserSession.currentUser?.id?.let {
            isPublic = isUserPublicById(it)
        }
        store.isDarkMode.collect { enabled ->
            darkModeEnabled = enabled
        }
    }
    AlertDialog(

        onDismissRequest = onDismiss,
        title = { Text("Account Settings", color = MaterialTheme.colorScheme.onSecondary) },
        containerColor = MaterialTheme.colorScheme.primary,
        icon = { Icon(Icons.Filled.Settings, "", tint = MaterialTheme.colorScheme.onSecondary, modifier = Modifier.padding(horizontal = (30.dp))) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {


                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("Public Account", color = MaterialTheme.colorScheme.onSecondary)
                    Spacer(Modifier.weight(1f))
                    Switch(checked = isPublic, onCheckedChange = {

                        coroutineScope.launch {
                            UserSession.currentUser?.id?.let { it1 -> setUserPrivacy(
                                it1,
                                isPublic
                            )}
                        }





                        isPublic = it },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Purple40,
                            uncheckedThumbColor = Color.Gray,
                            checkedTrackColor = Color.LightGray,
                            uncheckedTrackColor = Color.DarkGray
                        )
                        //Make it so that account will remember change
                    )
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("Dark Mode", color = MaterialTheme.colorScheme.onSecondary)
                    Spacer(Modifier.weight(1f))
                    Switch(checked = darkModeEnabled,
                        onCheckedChange = { isChecked ->
                            coroutineScope.launch {
                                store.saveDarkMode(isChecked)
                            }
                        },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Purple40,
                            uncheckedThumbColor = Color.Gray,
                            checkedTrackColor = Color.LightGray,
                            uncheckedTrackColor = Color.DarkGray
                        )
                        //Make it so that account will remember change
                    )
                }

                Button(
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                    onClick = { /* Handle location preferences */ }) {
                    Text("Location Preferences", color = MaterialTheme.colorScheme.onSurface)
                }

                Button(
                    onClick = { showCategoryPicker = true },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                    shape = RoundedCornerShape(20.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 15.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Edit,
                            contentDescription = "Edit Icon",
                            tint = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.padding(end = 8.dp)
                        )
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Event Suggestion Preferences:",
                                fontSize = 18.sp,
                                color = MaterialTheme.colorScheme.onSurface,
                                textAlign = TextAlign.Left,
                                fontFamily = FontFamily.Default
                            )
                            Text(
                                text = selectedCategories.joinToString(", ") { it.name },
                                fontSize = 22.sp,
                                color = MaterialTheme.colorScheme.onSurface,
                                textAlign = TextAlign.Left,
                                fontFamily = FontFamily.Default
                            )
                        }
                    }
                }
                //calls to bring up category selection bottom sheet
                CategoryPickerBottomSheet(
                    showSheet = showCategoryPicker,
                    onDismiss = {showCategoryPicker = false},
                    onSelectionDone = {selection ->
                        selectedCategories = selection
                    },
                    maxSelections = 5,
                    initialSelectedCategories = UserSession.currentUserCategory,
                    userCategories = true,
                    updateCategories = true
                )


                Button(
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                    onClick = { filePicker.launch("image/*") }) {
                    Text("Change Profile Picture", color = MaterialTheme.colorScheme.onSurface)
                }
                Button(
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                    onClick = { navController.navigate("start_up_screen") }) {
                    Text("Log Out", color = MaterialTheme.colorScheme.onSurface)
                }
                Button(
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                    onClick = { /* Handle profile picture change */ },
                ) {
                    Text("Contact Us", color = MaterialTheme.colorScheme.onSurface)
                }
                Button(
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                    onClick = { /* TOS stuff */ })
                {
                    Text("Terms of Service", color = MaterialTheme.colorScheme.onSurface)
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Close", color = MaterialTheme.colorScheme.onSecondary)
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

    val isCurrentUser = true
    val friendsList = remember { mutableStateOf<List<IndividualUser>?>(null) }
    val searchQuery = remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        friendsList.value = getFriends(UserSession.currentUser?.id ?: return@LaunchedEffect)
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Manage Following") },
        containerColor = PurpleStart,
        icon = {
            Icon(
                Icons.Filled.People,
                contentDescription = "",
                tint = Purple40,
                modifier = Modifier.padding(horizontal = 30.dp)
            )
        },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.verticalScroll(rememberScrollState())
            ) {

                Text("Followed Users", fontSize = 18.sp)
                val coroutineScope = rememberCoroutineScope()
                friendsList.value?.forEach { friend ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "${friend.firstName} ${friend.lastName}",
                            fontSize = 16.sp,
                            modifier = Modifier.clickable {
                                navController.navigate("friends_profile_screen/${friend.email}")
                            }
                        )
                        if (isCurrentUser) {
                            IconButton(onClick = {
                                coroutineScope.launch {
                                UserSession.currentUser?.id?.let { friend.id?.let { it1 ->
                                    unfriend(it,
                                        it1
                                    )
                                } }}

                                /* Unfriend logic */
                            }) {
                                Icon(Icons.Filled.Remove, "Unfollow")
                            }
                        }
                    }
                } ?: Text("Loading following...", fontSize = 16.sp)

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