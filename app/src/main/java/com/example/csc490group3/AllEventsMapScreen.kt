package com.example.csc490group3

import android.location.Geocoder
import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.sp
import com.example.csc490group3.model.Event
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState
import kotlinx.io.IOException
import java.util.Locale

class AllEventsMapScreen
@Composable
fun AllEventsMapScreen(eventRepo: suspend () -> List<Event>?) {
    val context = LocalContext.current
    var latLngList by remember { mutableStateOf<List<Pair<Event, LatLng>>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        val events = eventRepo()
        val geocoder = Geocoder(context, Locale.getDefault())
        val locations = mutableListOf<Pair<Event, LatLng>>()

        events?.forEach { event ->
            val fullAddress = "${event.address}, ${event.city}, ${event.state}, ${event.zipcode}"
            try {
                val result = geocoder.getFromLocationName(fullAddress, 1)
                val loc = result?.firstOrNull()
                if (loc != null) {
                    locations.add(event to LatLng(loc.latitude, loc.longitude))
                } else {
                    Log.d("MAPS EXCEPTION", "No coordinates found for address: $fullAddress")
                }
            } catch (e: IOException) {
                Log.d("MAPS EXCEPTION", "Geocoding error for $fullAddress: ${e.localizedMessage}")
            }
        }

        latLngList = locations
        isLoading = false
    }

    Box(modifier = Modifier.fillMaxSize()) {
        if (isLoading) {
            Text(
                text = "Loading event locations...",
                modifier = Modifier.align(Alignment.Center),
                fontSize = 18.sp
            )
        } else if (latLngList.isEmpty()) {
            Text(
                text = "No valid locations found.",
                modifier = Modifier.align(Alignment.Center),
                fontSize = 18.sp
            )
        } else {
            val cameraPositionState = rememberCameraPositionState {
                position = CameraPosition.fromLatLngZoom(latLngList.first().second, 5f)
            }

            GoogleMap(
                modifier = Modifier.fillMaxSize(),
                cameraPositionState = cameraPositionState
            ) {
                latLngList.forEach { (event, location) ->
                    Marker(
                        state = MarkerState(position = location),
                        title = event.eventName,
                        snippet = "${event.venue}, ${event.city}, ${event.state}"
                    )
                }
            }
        }
    }
}
