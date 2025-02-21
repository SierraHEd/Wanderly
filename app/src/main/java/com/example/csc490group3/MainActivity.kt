package com.example.csc490group3

import android.net.http.HttpResponseCache.install
import android.os.Bundle
import androidx.activity.ComponentActivity

import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable

import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.csc490group3.ui.theme.CSC490Group3Theme
import io.github.jan.supabase.BuildConfig as SupabaseBuildConfig
import io.github.jan.supabase.*
import io.github.jan.supabase.postgrest.Postgrest
import com.example.csc490group3.BuildConfig as AppBuildConfig

val supabase = createSupabaseClient(
    supabaseUrl = AppBuildConfig.SUPABASE_URL,
    supabaseKey = AppBuildConfig.SUPABASE_ANON_KEY
) {
    install(Postgrest)
}


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
