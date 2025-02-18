package com.example.csc490group3

// Import necessary Android and Jetpack Compose components for UI and functionality
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Create
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.Black
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.csc490group3.ui.theme.CSC490Group3Theme
import com.example.csc490group3.ui.theme.Purple40


/*
TODO
Needs database implementation.
Needs logic for retrieving info from TextFields upon Register button click.
Required variables for database entry included below - (fun EventRegistrationScreen)
Comprehensive colors between classes required team-wide
Comprehensive fonts between classes required team-wide
I'd also like to make the logo look nicer. :)
*/



// MainActivity class - Entry point of the application
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CSC490Group3Theme { EventRegistrationScreen() }
        }
    }
}

// Composable function defining the event registration screen UI
@Composable
fun EventRegistrationScreen() {
    // Mutable state variables for event details
    var eventName by remember { mutableStateOf("") }
    var zipcode by remember { mutableStateOf("") }
    var state by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("") }
    var city by remember { mutableStateOf("") }
    var venue by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("") }
    var price by remember { mutableStateOf("") }
    var maxAttendees by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var isPublic by remember { mutableStateOf(true) }
    var isFamilyFriendly by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF793CCB))
            .padding(16.dp)
    ) {
        // Title with logo
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
            Image(
                painter = painterResource(id = R.drawable.wanderlyicon),
                contentDescription = null,
                modifier = Modifier
                    .size(50.dp)
                    .clip(CircleShape)
                    .border(0.dp, Transparent, CircleShape),
                contentScale = ContentScale.Crop
            )
            Text("Your Wanderly Event",textDecoration = TextDecoration.Underline, fontSize = 28.sp, color = Color.White, fontFamily = FontFamily.Serif)
        }
        Spacer(modifier = Modifier.height(16.dp))
        Text("Event Registration Form", fontSize = 16.sp, color = White, fontFamily = FontFamily.Serif)

        // Input fields grouped into rows
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
            EventTextField("Event Name", eventName) { eventName = it }
            EventTextField("Category", category) { category = it }
        }
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
            EventTextField("Venue", venue) { venue = it }
            EventTextField("Price", price, KeyboardType.Number) { price = it }
        }
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
            EventTextField("Description", description) { description = it }
            EventTextField("Guest Limit", maxAttendees, KeyboardType.Number) { maxAttendees = it }
        }
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
            EventTextField("Address", address) { address = it }
            EventTextField("City", city) { city = it }
        }
        Row(modifier = Modifier.fillMaxWidth(),  horizontalArrangement = Arrangement.SpaceEvenly) {
            EventTextField("State", state, ) { state = it }
            EventTextField("Zipcode", zipcode) { zipcode = it }
        }

        // Public Event Toggle
        Row(
            verticalAlignment = androidx.compose.ui.Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Text("Is a Public Event", color = White)
            Switch(checked = isPublic, onCheckedChange = { isPublic = it })
        }

        // Family Friendly Toggle
        Row(
            verticalAlignment = androidx.compose.ui.Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Text("Is Family Friendly", color = White, )
            Switch(checked = isFamilyFriendly, onCheckedChange = { isFamilyFriendly = it })
        }

        // Register Button
        Button(
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE3BEFC)),
            onClick = { /* Handle event registration */ },
            modifier = Modifier.fillMaxWidth()
                .size(50.dp)
                .clip(CircleShape)
                .border(2.dp, White, CircleShape)
        ) {
            Text("Register This Event", fontSize = 22.sp, color = Color(0xFF793CCB), fontFamily = FontFamily.Default)

        }
    }
}

// Composable function for reusable text input fields
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EventTextField(label: String, value: String, keyboardType: KeyboardType = KeyboardType.Text, onValueChange: (String) -> Unit) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        shape = RoundedCornerShape(20.dp),
        colors = TextFieldDefaults.textFieldColors(
            containerColor = Color(0xFFE3BEFC),
            cursorColor = Black,
            disabledLabelColor = Black,
            focusedLabelColor = Black,
            unfocusedIndicatorColor = White,
        ),
        trailingIcon = {
            Icon(Icons.Filled.Create, "", tint = Purple40)
        },
        modifier = Modifier.background(Color(0xFF793CCB)).width(180.dp).padding(vertical = 20.dp, horizontal = 3.dp,)
    )
}

// Preview function for UI testing in Android Studio
@Preview(showBackground = true)
@Composable
fun PreviewEventRegistrationScreen() {
    CSC490Group3Theme { EventRegistrationScreen() }
}