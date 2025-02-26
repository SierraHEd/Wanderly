package com.example.csc490group3

import android.content.Context
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.compose.runtime.Composable
import androidx.navigation.compose.rememberNavController

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
        composable("Register_Event_Screen"){
            RegisterEventActivity(navController)
        }

        composable("Home_Screen") {
            HomeScreen(navController)
        }

        composable("register_event_screen") {
            RegisterEventScreen(navController)
        }



    }
}