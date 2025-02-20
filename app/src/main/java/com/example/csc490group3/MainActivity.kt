package com.example.csc490group3

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()
        setContent {
            // Initialize the NavController
            val navController = rememberNavController()

            // Set up the NavHost for navigation: Current default setting goes straight to sign up page
            NavHost(navController = navController, startDestination = "register event") {
                // Define the main screen as a composable
                composable("main") {
                    MainScreen(navController)
                }
                // Define the SignUp screen as a composable
                composable("register event") {
                    RegisterEventActivity(navController)
                }
            }
        }
    }
}
@Composable
fun MainScreen(navController: NavController) {
    // Main Screen content
    Button(onClick = { navController.navigate("register event") }) {
        Text("Go to register")

    }
}