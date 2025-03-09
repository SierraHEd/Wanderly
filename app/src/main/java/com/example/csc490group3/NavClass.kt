package com.example.csc490group3

import UserProfileScreen
import android.content.Context
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.compose.runtime.Composable
import androidx.navigation.compose.rememberNavController

@Composable
fun Navigation(context: Context) {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "User_Login_Screen") {
        composable("splash_screen") {
            SplashScreen(navController)
        }
        composable("start_up_screen") {
            StartUpScreen(navController)
        }
        composable("User_Login_Screen") {
            UserLoginScreen(navController)
        }
        composable("sign_up_screen") {
            SignUpActivity(navController)
        }
        composable("Home_Screen") {
            HomeScreen(navController)
        }
        composable("search_screen") {
            SearchScreen(navController)
        }
        composable("register_event_screen") {
            RegisterEventScreen(navController)
        }
        composable("calendar_screen") {
            CalendarScreen(navController)
        }
        composable("settings_screen") {
            SettingsScreen(navController)
        }
        composable("profile_screen") {
            UserProfileScreen(navController)
        }
    }
}