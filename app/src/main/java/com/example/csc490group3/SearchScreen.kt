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

@Composable
fun SearchScreen(navController: NavHostController, viewModel: SearchScreenViewModel = viewModel()) {
    val scrollState = rememberScrollState()
    var searchTerm by rememberSaveable { mutableStateOf("") }
    val events by viewModel.events
    val errorMessage by viewModel.errorMessage
    val isLoading by viewModel.isLoading
    val keyboardController = LocalSoftwareKeyboardController.current
    var context = LocalContext.current
    val selectedEvent = remember { mutableStateOf<Event?>(null) }
    var isRegistered = remember { mutableStateOf(false) }


    Scaffold(
    bottomBar = { BottomNavBar(navController) }
    ) { paddingValues ->
        Surface(
            modifier = Modifier.fillMaxSize()
                .background(PurpleStart)
                .padding(paddingValues)
        ) {
          Column(
                  modifier = Modifier
                    .fillMaxSize()
                    .background(PurpleBKG),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Top
            )
            {
                Row (
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = {navController.popBackStack()}) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Go Back")
                    }
                }
                Spacer(modifier = Modifier.height(4.dp))
                TextField(
                    value = searchTerm,
                    onValueChange = { searchTerm = it },
                    label = { Text("Search") },
                    singleLine = true,
                    trailingIcon = {
                        IconButton(onClick = { viewModel.search(searchTerm) }) {
                            Icon(
                                imageVector = (Icons.Filled.Search),
                                contentDescription = "Search"
                            )
                        }
                    },
                    keyboardOptions = KeyboardOptions(
                        imeAction = ImeAction.Search
                    ),
                    keyboardActions = KeyboardActions(
                        onSearch = {
                            viewModel.search(searchTerm)
                            keyboardController?.hide()
                        }
                    ),
                    shape = RoundedCornerShape(16.dp),
                    colors = TextFieldDefaults.colors(
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 4.dp)
                )
                when {
                    isLoading -> {
                        Text("No Results", style = MaterialTheme.typography.bodyMedium)
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
                            items(events) { event ->
                                EventCard(
                                    onClick = { selectedEvent.value = event} ,
                                    event = event, onBottomButtonClick = { selectedEvent ->
                                        viewModel.registerForEvent(
                                            selectedEvent,
                                            UserSession.currentUser
                                        )
                                    },
                                    onEditEvent = {},
                                )
                            }
                        }
                    }
                }
            }
        }
        // Show event detail popup when an event is selected
        selectedEvent.value?.let { event ->
            EventDetailDialog(event = event,
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