import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.csc490group3.R
import com.example.csc490group3.data.BottomNavBar
import com.example.csc490group3.model.Event
import com.example.csc490group3.model.UserSession
import com.example.csc490group3.ui.components.EventCard
import com.example.csc490group3.ui.components.EventDetailDialog
import com.example.csc490group3.ui.theme.Purple40
import com.example.csc490group3.ui.theme.PurpleBKG
import com.example.csc490group3.ui.theme.PurpleDarkBKG
import com.example.csc490group3.ui.theme.PurpleStart
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.Color.Companion.Black
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.TextUnit
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.csc490group3.data.BottomNavBar
import com.example.csc490group3.model.Category
import com.example.csc490group3.model.Event
import com.example.csc490group3.ui.components.CategoryPickerBottomSheet
import com.example.csc490group3.ui.components.EventCard
import com.example.csc490group3.ui.theme.PurpleContainer
import com.example.csc490group3.viewModels.HomeScreenViewModel
import com.example.csc490group3.viewModels.UserProfileViewModel
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.ui.platform.LocalContext
import io.ktor.http.ContentDisposition.Companion.File
import java.io.File
import java.util.*
import coil.compose.rememberAsyncImagePainter


@Composable
fun UserProfileScreen(navController: NavController) {
    var showSettings by remember { mutableStateOf(false) }
    val isCurrentUser = true // Make it so that we can tell if viewed user is the logged in user
    val profilePictureUrl = UserSession.currentUser?.profile_picture_url
    // val CurrentUser = UserSession.currentUser?.email.?
    var context = LocalContext.current
    val selectedEvent = remember { mutableStateOf<Event?>(null) }
    var isRegistered = remember { mutableStateOf(false) }

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
        }

    }
}

@Composable
fun Section1(title: String, viewModel: UserProfileViewModel = viewModel(), fontSize: TextUnit) {
    val events by viewModel.registeredEvents
    val selectedEvent = remember { mutableStateOf<Event?>(null) }
    var isRegistered = remember { mutableStateOf(false) }

    Row() {
        Text(
            text = title,
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
            onRegister = { isRegistered.value = true }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Section2(title: String, viewModel: UserProfileViewModel = viewModel(),fontSize: TextUnit) {
    val events by viewModel.createdEvents
    val selectedEvent = remember { mutableStateOf<Event?>(null) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var eventToDelete by remember { mutableStateOf<Event?>(null) }
    var isRegistered = remember { mutableStateOf(false) }

    Row() {
        Text(
            text = title,
        )
    }
    LazyRow {

        items(events) { event ->
            EventCard(
                event = event,
                onClick = { selectedEvent.value = event} ,
                onBottomButtonClick = {selectedEvent ->
                    eventToDelete = event
                    showDeleteDialog = true
                },
                onEditEvent = {selectedEvent ->
                    viewModel.editEvent(selectedEvent)
                },
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
    // Show event detail popup when an event is selected
    selectedEvent.value?.let { event ->
        EventDetailDialog(event = event, onDismiss = { selectedEvent.value = null },
            showRegisterButton = false,
            onRegister = { isRegistered.value = true })
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
    var isPublic by remember { mutableStateOf(true) } // <-- ADD THIS LINE BACK
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
                    maxSelections = 6,
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
}

