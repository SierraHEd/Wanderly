package com.example.csc490group3

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity

import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Alignment

import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.lifecycleScope
import com.example.csc490group3.model.privateUser
import com.example.csc490group3.supabase.supabaseManagment
import com.example.csc490group3.ui.theme.CSC490Group3Theme
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDate


class MainActivity : ComponentActivity() {
    var context: Context = this
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        lifecycleScope.launch {
            Log.d("MainActivity", "Starting addRecord coroutine")
            Toast.makeText(this@MainActivity, "Launching retrieving record", Toast.LENGTH_SHORT)
                .show()

            val emailToTest = "alice@example.com"
            val newUser = supabaseManagment.getPrivateUser(emailToTest)
            if (newUser != null) {
                Log.d("MainActivity", "User fetched: $newUser")
                Toast.makeText(
                    this@MainActivity,
                    "User fetched: ${newUser.email}",
                    Toast.LENGTH_LONG
                ).show()
            } else {
                Log.d("MainActivity", "No user found with email: $emailToTest")
                Toast.makeText(this@MainActivity, "No user found", Toast.LENGTH_LONG).show()
            }

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
}
