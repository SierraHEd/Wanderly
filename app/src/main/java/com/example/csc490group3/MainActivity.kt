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
import com.example.csc490group3.data.AppStorage
import com.example.csc490group3.model.Event
import com.example.csc490group3.supabase.SupabaseManagement.DatabaseManagement.postgrest
import com.example.csc490group3.ui.theme.CSC490Group3Theme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext


class MainActivity : ComponentActivity() {
    var context: Context = this
    private lateinit var appStorage: AppStorage
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        appStorage = AppStorage(applicationContext)
        enableEdgeToEdge()


            setContent {

                CSC490Group3Theme {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier.fillMaxSize().background(
                            Color(0xFFE0E0E0)
                        )
                    ) {
                        Navigation(context, appStorage)
                    }

                }
            }
        }
    }
