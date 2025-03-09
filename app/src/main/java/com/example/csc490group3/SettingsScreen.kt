package com.example.csc490group3

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import com.example.csc490group3.data.BottomNavBar
import com.example.csc490group3.data.ImageComponent
import com.example.csc490group3.data.NormalTextComponent
import com.example.csc490group3.ui.theme.PurpleStart

@Composable
fun SettingsScreen(navController: NavController) {
    Scaffold(
        bottomBar = { BottomNavBar(navController) }
    ) { paddingValues ->
        Surface(
            modifier = Modifier.fillMaxSize()
                .background(PurpleStart)
                .padding(paddingValues)
        ) {
            Column(modifier = Modifier.background(PurpleStart)) {
                ImageComponent()
                NormalTextComponent("This is the Settings Page")
            }
        }
    }
}