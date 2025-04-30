package com.example.csc490group3

import UserProfileScreen
import android.content.Context
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.compose.runtime.Composable
import androidx.navigation.compose.rememberNavController
import com.example.csc490group3.ui.admin.AdminScreen

@Composable
fun Navigation(context: Context) {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "splash_screen") {
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

        composable("friend_requests_screen") {
            FriendRequestScreen(navController)
        }
        composable("friends_profile_screen/{friendEmail}") { backStackEntry ->
            val friendEmail = backStackEntry.arguments?.getString("friendEmail") ?: ""
            FriendProfileScreen(navController = navController, friendEmail = friendEmail)
        }
        composable("Admin_Screen") {
            AdminScreen(navController)
        }
    }
}