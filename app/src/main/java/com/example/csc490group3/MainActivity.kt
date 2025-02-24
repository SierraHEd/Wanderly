package com.example.csc490group3

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.example.csc490group3.ui.theme.CSC490Group3Theme
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest
import com.example.csc490group3.BuildConfig as AppBuildConfig

val supabase = createSupabaseClient(
    supabaseUrl = AppBuildConfig.SUPABASE_URL,
    supabaseKey = AppBuildConfig.SUPABASE_ANON_KEY
) {
    install(Postgrest)
}


class MainActivity : ComponentActivity() {
    var context: Context = this
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {

            CSC490Group3Theme {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.fillMaxSize().background(
                        Color(0xFFE0E0E0)
                    )
                ) {
                    Navigation(context)
                }

            }
        }
    }
}
