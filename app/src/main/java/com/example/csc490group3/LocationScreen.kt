package com.example.csc490group3

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.csc490group3.ui.theme.PurpleBKG
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback

private lateinit var fusedLocationClient: FusedLocationProviderClient
private lateinit var locationCallback: LocationCallback
private var locationRequired: Boolean = false

@Composable
fun LocationScreen(navController: NavController, context: Context) {
    Surface (modifier = Modifier.fillMaxSize()
        .background(PurpleBKG)
        .padding(28.dp)) {
        Box(modifier = Modifier.fillMaxSize()){
            Column(modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Bottom,
                horizontalAlignment = Alignment.CenterHorizontally){

                Text(text = "Your Location")
                Button(onClick = { /*TODO*/}) {
                    Text(text = "Get your location")
                }
            }
        }
    }
}