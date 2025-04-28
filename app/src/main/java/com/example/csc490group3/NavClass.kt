package com.example.csc490group3

import UserProfileScreen
import android.content.Context
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import com.example.csc490group3.model.IndividualUser
import com.example.csc490group3.model.User
import com.example.csc490group3.supabase.DatabaseManagement.getPrivateUser
import com.example.csc490group3.ui.admin.AdminScreen
import com.example.csc490group3.viewModels.MessageScreenViewModel
import kotlinx.serialization.json.Json

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
        composable("messages_screen") {
            MessagesScreen(navController)
        }
        composable("conversation_screen/{userId}") { backStackEntry ->
            val userId = backStackEntry.arguments?.getString("userId")?.toIntOrNull() ?: -1
            var user by remember { mutableStateOf<IndividualUser?>(null) }

            LaunchedEffect(userId) {
                if (userId != -1) {
                    user = getPrivateUser(userId) // <-- new function to fetch by ID
                }
            }

            user?.let { ConversationScreen(otherUser = it, navController = navController) }
        }
        composable("new_conversation_screen/{friendEmail}") { backStackEntry ->
            val friendEmail = backStackEntry.arguments?.getString("friendEmail") ?: ""
            NewConversationScreen(navController = navController, friendEmail = friendEmail)
        }
    }
}