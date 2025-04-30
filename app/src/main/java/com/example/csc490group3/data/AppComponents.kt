package com.example.csc490group3.data

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.csc490group3.R
import com.example.csc490group3.ui.theme.PurpleContainer


@Composable
fun ImageComponent(){
    val image = painterResource(R.drawable.app_logo)
    Image(
        painter = image,
        contentDescription = ""
    )
}

@Composable
fun NormalTextComponent(value:String) {
    Text(text = value,
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 40.dp),
        style = androidx.compose.ui.text.TextStyle(
            fontSize = 24.sp,
            fontWeight = FontWeight.Normal,
            fontStyle = FontStyle.Normal
        ),
        color = Color.White,
        textAlign = TextAlign.Center
    )
}

@Composable
fun ButtonComponent(value : String, onButtonClick : () -> Unit, isEnabled: Boolean) {
    Button(
        onClick = {onButtonClick.invoke()},
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(48.dp),
        contentPadding = PaddingValues(),
        colors = ButtonDefaults.buttonColors(Color.Transparent)
    ){
        Box(modifier = Modifier
            .fillMaxWidth()
            .heightIn(48.dp)
            .background(color = PurpleContainer,
                shape = RoundedCornerShape(50.dp)
            ),
            contentAlignment = Alignment.Center
        ){
            Text(text = value,
                color = Color.Black,
                modifier = Modifier,
                style = androidx.compose.ui.text.TextStyle(
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Normal,
                    fontStyle = FontStyle.Normal
                ))
        }
    }
}

@Composable
fun BottomNavBar(navController: NavController) {
    NavigationBar(
        containerColor = MaterialTheme.colorScheme.primaryContainer, // Background color
        contentColor = MaterialTheme.colorScheme.onSurface // Text and icon color
    ) {
        NavigationBarItem(
            selected = false,
            onClick = {navController.navigate("Home_Screen")},
            icon = { Icon(imageVector = Icons.Filled.Home, contentDescription = "Home", tint = MaterialTheme.colorScheme.onSurface) }, // Icon as a composable
            label = { Text("Home", color = MaterialTheme.colorScheme.onSurface) }, // Label as a composable
            alwaysShowLabel = true // Ensure label is always shown
        )
        NavigationBarItem(
            selected = false,
            onClick = { navController.navigate("search_screen") },
            icon = { Icon(imageVector = Icons.Filled.Search, contentDescription = "Search", tint = MaterialTheme.colorScheme.onSurface) },
            label = { Text("Search", color = MaterialTheme.colorScheme.onSurface) },
            alwaysShowLabel = true
        )
        NavigationBarItem(
            selected = false,
            onClick = { navController.navigate("calendar_screen") },
            icon = { Icon(imageVector = Icons.Filled.CalendarMonth, contentDescription = "Calendar", tint = MaterialTheme.colorScheme.onSurface) },
            label = { Text("Calendar", color = MaterialTheme.colorScheme.onSurface) },
            alwaysShowLabel = true
        )
        NavigationBarItem(
            selected = false,
            onClick = { navController.navigate("profile_screen") },
            icon = { Icon(imageVector = Icons.Filled.AccountCircle, contentDescription = "Profile", tint = MaterialTheme.colorScheme.onSurface) },
            label = { Text("Profile", color = MaterialTheme.colorScheme.onSurface) },
            alwaysShowLabel = true
        )
    }
}