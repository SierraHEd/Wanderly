package com.example.csc490group3

import androidx.navigation.NavController
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.KeyboardArrowDown
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.csc490group3.ui.theme.Purple40



@Composable
fun RegisterEventScreen(navController: NavController) {


    val categories = listOf("Music", "Food", "Entertainment", "Sports")
    val priceRanges = listOf("Free", "$1 - $20", "$21 - $50", "$51 - $100", "$100+")
    val countries = listOf("USA", "Canada", "UK", "Germany", "France")

    val states = listOf("New York", "California", "Texas", "Florida", "Illinois")

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

    Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
        /** ^^ This guy holds everything. Assumably, if making bottom bar, this should
            probably go inside of it. like embedded. This way we can have scrolling functional
            without the bottom bar moving **/
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFF793CCB))
                .padding(16.dp)
        ) {

            // My header for the form w/ logo and all that.
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                Image(
                    painter = painterResource(id = R.drawable.wanderlyicon),
                    contentDescription = null,
                    modifier = Modifier
                        .size(50.dp)
                        .clip(CircleShape)
                        .clickable {  navController.navigate("Home_Screen") }
                        .border(0.dp, Color.Transparent, CircleShape),
                    contentScale = ContentScale.Crop
                )
                Text(
                    "Your Wanderly Event",
                    textDecoration = TextDecoration.Underline,
                    fontSize = 28.sp,
                    color = White,
                    fontFamily = FontFamily.Serif
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                "Event Registration Form",
                fontSize = 16.sp,
                color = White,
                fontFamily = FontFamily.Serif
            )

            //Fields
            EventTextField("Event Name", eventName) { eventName = it }
            DropdownMenuExample(categories, selectedCategory) { selectedCategory = it }
            EventTextField("Venue", venue) { venue = it }
            DropdownMenuExample(priceRanges, selectedPrice) { selectedPrice = it }
            EventTextField("Description", description) { description = it }
            EventTextField("Guest Limit", maxAttendees, KeyboardType.Number) { maxAttendees = it }
            EventTextField("Address", address) { address = it }
            DropdownMenuExample(countries, selectedCountry) { selectedCountry = it }
            DropdownMenuExample(states, selectedState) { selectedState = it }
            EventTextField("City", city) { city = it }
            EventTextField("Zipcode", zipcode) { zipcode = it }

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                Text("Is A Public Event", color = White)
                Switch(checked = isPublic, onCheckedChange = { isPublic = it })
            }
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                Text("Is Family Friendly", color = White)
                Switch(checked = isFamilyFriendly, onCheckedChange = { isFamilyFriendly = it })
            }

            Button(
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE3BEFC)),
                onClick = {
                    //EVENT HANDLING FOR REGISTRATION GOES HERE.
                    },
                modifier = Modifier.fillMaxWidth().size(50.dp).clip(CircleShape).border(2.dp, White, CircleShape)
            ) {
                Text(
                    "Register This Event",
                    fontSize = 22.sp,
                    color = Color(0xFF793CCB),
                    fontFamily = FontFamily.Default
                )
            }
        }
    }
}

//Dropdown Menu Composable Function

@Composable
fun DropdownMenuExample(options: List<String>, selectedItem: String, onItemSelected: (String) -> Unit) {
    var expanded by remember { mutableStateOf(false) }

    Button(

        onClick = { expanded = true },
        colors = ButtonDefaults.buttonColors(
            containerColor = Color(0xFFE3BEFC)

        ),
        shape = RoundedCornerShape(20.dp),
        border = BorderStroke(width = 1.dp, color = White),
        modifier = Modifier.fillMaxWidth().padding(vertical = 15.dp)
    ) {
        Text(text = selectedItem, fontSize = 18.sp, color = Black, modifier = Modifier.weight(1f), textAlign = TextAlign.Left)
        Row(
            horizontalArrangement = Arrangement.End
        ) {
            Icon(
                Icons.Filled.KeyboardArrowDown,
                contentDescription = null,
                tint = Purple40,
                modifier = Modifier.size(40.dp) // Adjust the size as needed
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
fun EventTextField(label: String, value: String, keyboardType: KeyboardType = KeyboardType.Text, onValueChange: (String) -> Unit) {
    OutlinedTextField(
        value = value, 
        onValueChange = onValueChange,
        label = { Text(text = label, fontSize = 22.sp, color = Black, fontFamily = FontFamily.Default) },
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        shape = RoundedCornerShape(20.dp),
        colors = TextFieldDefaults.colors(
            focusedLabelColor = Black,
            unfocusedContainerColor = Color(0xFFE3BEFC),
            cursorColor = Black,
            focusedContainerColor = Color(0xFFE3BEFC),
            unfocusedIndicatorColor = White,
            focusedIndicatorColor = White
        ),
        trailingIcon = {
            Icon(Icons.Filled.Create, "", tint = Purple40, modifier = Modifier.padding(horizontal = (30.dp)))
        },
        modifier = Modifier.fillMaxWidth().padding(vertical = 10.dp)

    )
}