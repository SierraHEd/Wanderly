package com.example.csc490group3

import android.location.Geocoder
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.csc490group3.data.BottomNavBar
import com.example.csc490group3.model.Event
import com.example.csc490group3.supabase.DatabaseManagement.getCategoriesForEvent
import com.example.csc490group3.ui.components.EventCard
import com.example.csc490group3.ui.components.EventDetailDialog
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState
import kotlinx.io.IOException
import java.util.Locale
const val HUE_PINK = 350f
class AllEventsMapScreen
@Composable
fun AllEventsMapScreen(
    navController: NavController,
    eventRepo: suspend () -> List<Event>?
) {
    val context = LocalContext.current
    var latLngList by remember { mutableStateOf<List<Pair<Event, LatLng>>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var selectedEvent by remember { mutableStateOf<Event?>(null) }
    var selectedCategory by remember { mutableStateOf("All") }

    val allCategories by remember(latLngList) {
        derivedStateOf {
            latLngList.flatMap { it.first.categories.orEmpty() }
                .map { it.name }
                .distinct()
                .sorted()
        }
    }

    val filteredEvents = if (selectedCategory == "All") {
        latLngList
    } else {
        latLngList.filter { (event, _) ->
            event.categories?.any { it.name == selectedCategory } == true
        }
    }

    LaunchedEffect(Unit) {
        val events = eventRepo()
        val geocoder = Geocoder(context, Locale.getDefault())
        val enrichedLocations = mutableListOf<Pair<Event, LatLng>>()

        events?.forEach { event ->
            // DEBUG: Log event ID
            Log.d("CATEGORY DEBUG", "Fetching categories for event ID: ${event.id}")

            val categories = event.id?.let { getCategoriesForEvent(it) }?.toSet()

            // DEBUG: Log categories received
            if (categories != null) {
                Log.d("CATEGORY DEBUG", "Categories for event ${event.id}:")
                categories.forEach { category ->
                    Log.d("CATEGORY DEBUG", "- ${category.name}")
                }
                Log.d("CATEGORY DEBUG", "Fetched ${categories.size} categories for event ${event.id}")

            }

            val enrichedEvent = event.copy(categories = categories)

            val fullAddress = "${event.address}, ${event.city}, ${event.state}, ${event.zipcode}"
            try {
                val result = geocoder.getFromLocationName(fullAddress, 1)
                val loc = result?.firstOrNull()
                if (loc != null) {
                    enrichedLocations.add(enrichedEvent to LatLng(loc.latitude, loc.longitude))
                } else {
                    Log.d("MAPS EXCEPTION", "No coordinates found for address: $fullAddress")
                }
            } catch (e: IOException) {
                Log.d("MAPS EXCEPTION", "Geocoding error for $fullAddress: ${e.localizedMessage}")
            }
        }

        latLngList = enrichedLocations
        isLoading = false
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.primary,
        bottomBar = { BottomNavBar(navController) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.primary)
                .padding(paddingValues)
        ) {
            Row(
                modifier = Modifier
                    .horizontalScroll(rememberScrollState())
                    .padding(8.dp)
            ) {
                (listOf("All") + allCategories).forEach { category ->
                    val backgroundColor = if (category != "All") getCategoryColor(category) else MaterialTheme.colorScheme.secondary
                    FilterChip(
                        selected = selectedCategory == category,
                        onClick = { selectedCategory = category },
                        label = { Text(category) },
                        colors = FilterChipDefaults.filterChipColors(
                            containerColor = backgroundColor,
                            selectedContainerColor = backgroundColor,
                            labelColor = Color.White
                        ),
                        modifier = Modifier.padding(end = 8.dp)
                    )
                }
            }

            Box(modifier = Modifier.fillMaxSize()) {
                when {
                    isLoading -> Text(
                        text = "Loading event locations...",
                        modifier = Modifier.align(Alignment.Center),
                        fontSize = 18.sp
                    )
                    latLngList.isEmpty() -> Text(
                        text = "No valid locations found.",
                        modifier = Modifier.align(Alignment.Center),
                        fontSize = 18.sp
                    )
                    else -> {
                        val cameraPositionState = rememberCameraPositionState {
                            position = CameraPosition.fromLatLngZoom(latLngList.first().second, 5f)
                        }

                        GoogleMap(
                            modifier = Modifier.fillMaxSize(),
                            cameraPositionState = cameraPositionState
                        ) {
                            filteredEvents.forEach { (event, location) ->
                                val categoryName = event.categories?.firstOrNull()?.name
                                val icon = when (categoryName) {
                                    "Music" -> BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)
                                    "Business & Professional" -> BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)
                                    "Food & Drink" -> BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE)
                                    "Community & Culture" -> BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA)
                                    "Performing & Visual Arts" -> BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_VIOLET)
                                    "Film, Media & Entertainment" -> BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ROSE)
                                    "Sports & Fitness" -> BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)
                                    "Health and Wellness" -> BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_CYAN)
                                    "Science & Technology" -> BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)
                                    "Travel & Outdoor" -> BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW)
                                    "Charity & Causes" -> BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE)
                                    "Religion & Spirituality" -> BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA)
                                    "Family & Education" -> BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_VIOLET)
                                    "Seasonal & Holiday" -> BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ROSE)
                                    "Government & Politics" -> BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)
                                    "Fashion & Beauty" -> BitmapDescriptorFactory.defaultMarker(HUE_PINK)
                                    "Home & Lifestyle" -> BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW)
                                    "Auto, Boat & Air" -> BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_CYAN)
                                    "Hobbies & Special Interest" -> BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)
                                    "School Activities" -> BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)
                                    "Other" -> BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE)
                                    else -> BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)
                                }

                                Marker(
                                    state = MarkerState(position = location),
                                    title = event.eventName,
                                    snippet = "${event.venue}, ${event.city}, ${event.state}",
                                    onInfoWindowClick = { selectedEvent = event },
                                    icon = icon
                                )
                            }
                        }

                        selectedEvent?.let { event ->
                            EventDetailDialog(
                                event = event,
                                onDismiss = { selectedEvent = null },
                                showRegisterButton = false,
                                showWaitListButton = false,
                                onJoinWaitlist = {},
                                navController = navController,
                                onRegister = {}
                            )
                        }
                    }
                }
            }
        }
    }
}

fun getCategoryColor(category: String): Color {
    return when (category) {
        "Music" -> Color.Blue
        "Business & Professional" -> Color.Red
        "Food & Drink" -> Color(0xFFFFA500) // Orange
        "Community & Culture" -> Color.Magenta
        "Performing & Visual Arts" -> Color(0xFF8A2BE2) // Violet
        "Film, Media & Entertainment" -> Color(0xFFFF007F) // Rose
        "Sports & Fitness" -> Color.Green
        "Health and Wellness" -> Color.Cyan
        "Science & Technology" -> Color(0xFF007FFF) // Azure
        "Travel & Outdoor" -> Color.Yellow
        "Charity & Causes" -> Color(0xFFFFA500)
        "Religion & Spirituality" -> Color.Magenta
        "Family & Education" -> Color(0xFF8A2BE2)
        "Seasonal & Holiday" -> Color(0xFFFF007F)
        "Government & Politics" -> Color.Red
        "Fashion & Beauty" -> Color(0xFFFF69B4) // Pink
        "Home & Lifestyle" -> Color.Yellow
        "Auto, Boat & Air" -> Color.Cyan
        "Hobbies & Special Interest" -> Color.Green
        "School Activities" -> Color.Blue
        "Other" -> Color(0xFFFFA500)
        else -> Color.Gray
    }
}

