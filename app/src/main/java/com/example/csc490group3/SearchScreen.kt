package com.example.csc490group3

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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.example.csc490group3.ui.theme.PurpleBKG
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.csc490group3.data.BottomNavBar
import com.example.csc490group3.model.Event
import com.example.csc490group3.model.UserSession
import com.example.csc490group3.ui.components.EventCard
import com.example.csc490group3.ui.components.EventDetailDialog
import com.example.csc490group3.ui.theme.PurpleStart
import com.example.csc490group3.viewModels.SearchScreenViewModel
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import com.example.csc490group3.data.AppStorage
import com.example.csc490group3.model.User
import com.example.csc490group3.supabase.DatabaseManagement.getPrivateUser
import com.example.csc490group3.ui.components.UserSearchCard
import com.example.csc490group3.ui.theme.Purple40
import com.example.csc490group3.ui.theme.PurpleDarkBKG
import com.example.csc490group3.viewModels.UserSeachViewModel


@Composable
fun SearchScreen(navController: NavHostController, appStorage: AppStorage, viewModel: SearchScreenViewModel = viewModel(), viewModel2: UserSeachViewModel = viewModel()) {
    var selectedTabIndex by remember { mutableStateOf(0) }

    val tabTitles = listOf("Search Events", "Search Users")
    val isDarkMode by appStorage.isDarkMode.collectAsState(initial = false)

    MaterialTheme(
        colorScheme = if (isDarkMode) lightColorScheme() else darkColorScheme()
    ) {

        Scaffold(
            bottomBar = { BottomNavBar(navController) }
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .background(MaterialTheme.colorScheme.primary)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Go Back", tint = MaterialTheme.colorScheme.onSecondary)
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Search", style = MaterialTheme.typography.titleLarge, color = MaterialTheme.colorScheme.onSecondary)
                }

                TabRow(
                    selectedTabIndex = selectedTabIndex,
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    contentColor = MaterialTheme.colorScheme.onSurface
                ) {
                    tabTitles.forEachIndexed { index, title ->
                        Tab(
                            selected = selectedTabIndex == index,
                            onClick = { selectedTabIndex = index },
                            text = { Text(title) }
                        )
                    }
                }

                when (selectedTabIndex) {
                    0 -> EventSearchTab(navController, viewModel)
                    1 -> UserSearchTab(navController, viewModel2)
                }
            }
        }
    }
}

@Composable
fun EventSearchTab(navController: NavHostController, viewModel: SearchScreenViewModel) {
    var searchTerm by rememberSaveable { mutableStateOf("") }
    val events by viewModel.events
    val errorMessage by viewModel.errorMessage
    val isLoading by viewModel.isLoading
    val keyboardController = LocalSoftwareKeyboardController.current
    val selectedEvent = remember { mutableStateOf<Event?>(null) }
    val isRegistered = remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(8.dp)
    ) {
        TextField(
            value = searchTerm,
            onValueChange = { searchTerm = it },
            label = { Text("Search Events") },
            singleLine = true,
            trailingIcon = {
                IconButton(onClick = { viewModel.search(searchTerm) }) {
                    Icon(Icons.Filled.Search, contentDescription = "Search")
                }
            },
            keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Search),
            keyboardActions = KeyboardActions(onSearch = {
                viewModel.search(searchTerm)
                keyboardController?.hide()
            }),
            shape = RoundedCornerShape(16.dp),
            colors = TextFieldDefaults.colors(
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent
            ),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp)
        )

        Spacer(modifier = Modifier.height(8.dp))

        when {
            isLoading -> {
                Text("Loading...", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSecondary)
            }
            errorMessage != null -> {
                Text(
                    errorMessage!!,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Red
                )
            }
            else -> {
                LazyColumn {
                    if (events.isNotEmpty()){
                        items(events) { event ->
                            EventCard(
                                event = event,
                                onClick = { selectedEvent.value = event },
                                onBottomButtonClick = {
                                    viewModel.registerForEvent(it, UserSession.currentUser)
                                },
                                onEditEvent = {}
                            )
                        }
                }else{
                        item {
                            ShowText2()
                        }
                    }
                }
            }
        }

        selectedEvent.value?.let { event ->
            EventDetailDialog(
                event = event,
                onDismiss = { selectedEvent.value = null },
                showRegisterButton = true,
                showWaitListButton = false,
                onJoinWaitlist = {},
                onRegister = { isRegistered.value = true},
                navController = navController
            )
        }
    }
}

@Composable
fun UserSearchTab(navController: NavHostController, viewModel: UserSeachViewModel) {
    var searchTerm by rememberSaveable { mutableStateOf("") }
    val users by viewModel.users
    val errorMessage by viewModel.errorMessage
    val isLoading by viewModel.isLoading
    val keyboardController = LocalSoftwareKeyboardController.current
    val selectedEvent = remember { mutableStateOf<Event?>(null) }
    val isRegistered = remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(8.dp)
    ) {
        TextField(
            value = searchTerm,
            onValueChange = { searchTerm = it },
            label = { Text("Search Users") },
            singleLine = true,
            trailingIcon = {
                IconButton(onClick = {


                    viewModel.search(searchTerm)


                }) {
                    Icon(Icons.Filled.Search, contentDescription = "Search Users")
                }
            },
            keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Search),
            keyboardActions = KeyboardActions(onSearch = {


                viewModel.search(searchTerm)



                keyboardController?.hide()
            }),
            shape = RoundedCornerShape(16.dp),
            colors = TextFieldDefaults.colors(
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent
            ),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp)
        )

        Spacer(modifier = Modifier.height(8.dp))

        when {
            isLoading -> {
                Text("Loading...", style = MaterialTheme.typography.bodyMedium)
            }

            errorMessage != null -> {
                Text(
                    errorMessage!!,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Red
                )
            }

            else -> {
                LazyColumn {
                    if (users.isNotEmpty()) {
                        items(users) { user ->
                            UserSearchCard(
                                user = user,
                                navController = navController
                            )
                        }
                    } else {
                        item {
                            ShowText()
                        }
                    }
                }
            }

        }



        // Placeholder content

    }
}

@Composable
fun ShowText() {
    Text("No users found!", style = MaterialTheme.typography.bodyMedium)
}

@Composable
fun ShowText2() {
    Text("No events found!", style = MaterialTheme.typography.bodyMedium)
}


