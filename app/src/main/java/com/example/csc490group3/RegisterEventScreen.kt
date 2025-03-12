package com.example.csc490group3

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.Black
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Popup
import androidx.navigation.NavController
import com.example.csc490group3.model.Event
import com.example.csc490group3.model.UserSession
import com.example.csc490group3.supabase.DatabaseManagement.addRecord
import com.example.csc490group3.ui.theme.Purple40
import com.example.csc490group3.ui.theme.PurpleBKG
import com.example.csc490group3.ui.theme.PurpleContainer
import com.example.csc490group3.ui.theme.PurpleDarkBKG
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDate

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterEventScreen(navController: NavController) {
    var eventToAdd: Event
    val categories = listOf("Music", "Food", "Entertainment", "Sports")
    val countries = listOf("USA", "Canada", "UK", "Germany", "France")
    val states = listOf("New York", "California", "Texas", "Florida", "Illinois")
    var price by remember { mutableStateOf("") }
    var eventName by remember { mutableStateOf("") }
    var zipcode by remember { mutableStateOf("") }
    var city by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("") }
    var venue by remember { mutableStateOf("") }
    var maxAttendees by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var isPublic by remember { mutableStateOf(true) }
    var isFamilyFriendly by remember { mutableStateOf(false) }
    var selectedCategory by remember { mutableStateOf("Category") }
    var selectedPrice by remember { mutableStateOf("Price Range") }
    //TODO: make it so that the states will change depending on which country is selected.
    var selectedCountry by remember { mutableStateOf("Country") }
    var selectedState by remember { mutableStateOf("State") }
    var eventDate by remember { mutableStateOf<LocalDate?>(null) } // Store the event date input
    var showDateErrorToast by remember { mutableStateOf(false) } // State to trigger toast
    var showRegisterSuccessToast by remember { mutableStateOf(false) } // State to trigger toast
    //the coroutine is to call the fun from database mgmt
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current

////////////////
// Error Handling
///////////////

    if (showDateErrorToast) {
        // Show the toast when `showToast` becomes true
        LaunchedEffect(Unit) {
            Toast.makeText(context, "Please select an event date.", Toast.LENGTH_SHORT).show()
        }
        // Reset the showToast state to false to avoid showing it multiple times
        showDateErrorToast = false
    }

    if (showRegisterSuccessToast) {
        // Show the toast when `showToast` becomes true
        LaunchedEffect(Unit) {
            Toast.makeText(context, "Event Created!", Toast.LENGTH_SHORT).show()
        }
        // Reset the showToast state to false to avoid showing it multiple times
        showDateErrorToast = false
    }

////////////////
// Date Picker
///////////////

    // Date Picker Dialog state
    val datePickerState = rememberDatePickerState()
    var isDatePickerVisible by remember { mutableStateOf(false) }

    // Utility to convert millis to LocalDate
    fun convertMillisToLocalDate(millis: Long): LocalDate {
        val javaLocalDate = java.time.LocalDate.ofEpochDay(millis / 86400000)
        return LocalDate(javaLocalDate.year, javaLocalDate.monthValue, javaLocalDate.dayOfMonth)
    }

    // Function to show the DatePicker dialog
    fun showDatePicker() {
        isDatePickerVisible = true
    }

    // Handle the DatePicker dialog visibility and selection
    if (isDatePickerVisible) {
        Popup(
            alignment = Alignment.Center,
            onDismissRequest = { isDatePickerVisible = false }
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.8f)
                    .padding(16.dp)
                    .background(PurpleBKG)
                    .border(2.dp, Black)
            ) {
                DatePicker(
                    state = datePickerState,
                    colors = DatePickerDefaults.colors(
                        containerColor = PurpleBKG
                    ),
                    modifier = Modifier.height(540.dp)
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    horizontalArrangement = Arrangement.spacedBy(30.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    // Confirm Button
                    TextButton(
                        onClick = {
                            datePickerState.selectedDateMillis?.let { selectedDateMillis ->
                                eventDate = convertMillisToLocalDate(selectedDateMillis)
                            }
                            isDatePickerVisible = false
                        },
                        modifier = Modifier
                            .weight(1f)
                            .background(PurpleDarkBKG)
                    ) {
                        Text(
                            text = "Confirm",
                            color = White,
                            modifier = Modifier.padding(8.dp)
                        )
                    }
                    // Cancel Button
                    TextButton(
                        onClick = { isDatePickerVisible = false },
                        modifier = Modifier
                            .weight(1f)
                            .background(Color.Red)
                    ) {
                        Text(
                            text = "Cancel",
                            color = Black,
                            modifier = Modifier.padding(8.dp)
                        )
                    }
                }
            }
        }
    }

////////////////
// Main UI
///////////////

    Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
        /** ^^ This guy holds everything. Assumably, if making bottom bar, this should
        probably go inside of it. like embedded. This way we can have scrolling functional
        without the bottom bar moving **/
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(PurpleBKG)
                .padding(16.dp)
        ) {
            // Spacer to bring title and logo down a little
            Spacer(modifier = Modifier.height(36.dp))
            // My header for the form w/ logo and all that.
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                Image(
                    painter = painterResource(id = R.drawable.app_icon),
                    contentDescription = null,
                    modifier = Modifier
                        .size(50.dp)
                        .clip(CircleShape)
                        .clickable { navController.navigate("Home_Screen") }
                        .border(0.dp, Color.Transparent, CircleShape),
                    contentScale = ContentScale.Crop
                )
                Text(
                    "Your Wanderly Event",
                    textDecoration = TextDecoration.Underline,
                    fontSize = 28.sp,
                    color = Black,
                    fontFamily = FontFamily.Serif
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                "Event Registration Form",
                fontSize = 16.sp,
                color = Black,
                fontFamily = FontFamily.Serif
            )

            //Fields
            // Event Date Field (Using a Button to show DatePicker)
            Button(
                onClick = { showDatePicker() },
                enabled = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF6A1B9A),
                    contentColor = White
                )
            ) {
                Text(text = eventDate?.toString() ?: "Select Event Date")
            }
            // Event Name Field
            EventTextField("Event Name", eventName) { eventName = it }
            DropdownMenuExample(categories, selectedCategory) { selectedCategory = it }
            EventTextField("Venue", venue) { venue = it }
            EventTextField("Price", price) { price = it }
            EventTextField("Description", description) { description = it }
            EventTextField("Guest Limit", maxAttendees, KeyboardType.Number) { maxAttendees = it }
            EventTextField("Address", address) { address = it }
            DropdownMenuExample(countries, selectedCountry) { selectedCountry = it }
            DropdownMenuExample(states, selectedState) { selectedState = it }
            EventTextField("City", city) { city = it }
            EventTextField("Zipcode", zipcode) { zipcode = it }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Text("Is A Public Event", color = Black)
                Switch(checked = isPublic, onCheckedChange = { isPublic = it })
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Text("Is Family Friendly", color = Black)
                Switch(checked = isFamilyFriendly, onCheckedChange = { isFamilyFriendly = it })
            }

            Button(
                colors = ButtonDefaults.buttonColors(containerColor = PurpleBKG),
                onClick = {
                    // Check if eventDate is null
                    if (eventDate == null) {
                        // Trigger the toast by setting the state
                        showDateErrorToast = true
                    } else {
                        eventToAdd = UserSession.currentUser?.id?.let {
                            Event(
                                eventName = eventName,
                                zipcode = zipcode,
                                city = city,
                                address = address,
                                venue = venue,
                                maxAttendees = maxAttendees.toIntOrNull() ?: 0,
                                description = description,
                                isPublic = isPublic,
                                isFamilyFriendly = isFamilyFriendly,
                                price = price.toDoubleOrNull() ?: 0.0,
                                country = selectedCountry,
                                state = selectedState,
                                createdBy = it,
                                numAttendees = 0,
                                eventDate = eventDate!!
                            )
                        }!!
                        coroutineScope.launch {
                            if (!addRecord("events", eventToAdd)) {
                                println("Error adding event")
                            }
                        }
                        showRegisterSuccessToast = true
                        navController.navigate("Home_Screen")
                    }

                },
                modifier = Modifier
                    .fillMaxWidth()
                    .size(50.dp)
                    .clip(CircleShape)
                    .border(2.dp, White, CircleShape)
            ) {
                Text(
                    "Register This Event",
                    fontSize = 22.sp,
                    color = Color(0xFF793CCB),
                    fontFamily = FontFamily.Default
                )
            }

            Spacer(Modifier.padding(8.dp))

            Button(colors = ButtonDefaults.buttonColors(containerColor = PurpleBKG),
                onClick = {
                    navController.navigate("Home_Screen")
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .size(50.dp)
                    .clip(CircleShape)
                    .border(2.dp, Color.Red, CircleShape)
            ) {
                Text(
                    "Go back to Home",
                    fontSize = 22.sp,
                    color = Color(0xFF793CCB),
                    fontFamily = FontFamily.Default
                )
            }
        }
    }
}

///////////////
// Composable Functions
///////////////

//Dropdown Menu Composable Function
@Composable
fun DropdownMenuExample(
    options: List<String>,
    selectedItem: String,
    onItemSelected: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Button(
        onClick = { expanded = true },
        colors = ButtonDefaults.buttonColors(
            containerColor = PurpleContainer

        ),
        shape = RoundedCornerShape(20.dp),
        border = BorderStroke(width = 1.dp, color = White),
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 15.dp)
    ) {
        Text(
            text = selectedItem,
            fontSize = 18.sp,
            color = Black,
            modifier = Modifier.weight(1f),
            textAlign = TextAlign.Left
        )
        Row(
            horizontalArrangement = Arrangement.End
        ) {
            Icon(
                Icons.Filled.KeyboardArrowDown,
                contentDescription = null,
                tint = Purple40,
                modifier = Modifier.size(40.dp)
            )
        }
    }

    DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
        options.forEach { option ->
            DropdownMenuItem(
                text = { Text(option, fontSize = 16.sp) },
                onClick = {
                    onItemSelected(option)
                    expanded = false
                }
            )
        }
    }
}

//Textfield Composable Function
@Composable
fun EventTextField(
    label: String,
    value: String,
    keyboardType: KeyboardType = KeyboardType.Text,
    onValueChange: (String) -> Unit
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = {
            Text(
                text = label,
                fontSize = 22.sp,
                color = Black,
                fontFamily = FontFamily.Default
            )
        },
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        shape = RoundedCornerShape(20.dp),
        colors = TextFieldDefaults.colors(
            focusedLabelColor = Black,
            unfocusedContainerColor = PurpleContainer,
            cursorColor = Black,
            focusedContainerColor = PurpleContainer,
            unfocusedIndicatorColor = White,
            focusedIndicatorColor = White
        ),
        trailingIcon = {
            Icon(
                Icons.Filled.Create,
                "",
                tint = Purple40,
                modifier = Modifier.padding(horizontal = (30.dp))
            )
        },
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 10.dp)

    )
}