package com.example.csc490group3

import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.csc490group3.viewModels.MapViewModel
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.rememberCameraPositionState
import androidx.compose.ui.Modifier
import android.Manifest

@Composable
fun MapScreenWithLocation(mapViewModel: MapViewModel = viewModel()) {
    val context = LocalContext.current
    val userLocation by mapViewModel.userLocation.collectAsState()
    val fallback = LatLng(40.7128, -74.0060) // NYC as fallback

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { isGranted ->
            if (isGranted) {
                getCurrentLocation(context, mapViewModel)
            } else {
                Toast.makeText(context, "Location permission denied", Toast.LENGTH_SHORT).show()
            }
        }
    )

    LaunchedEffect(Unit) {
        val permission = Manifest.permission.ACCESS_FINE_LOCATION
        if (ContextCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
            permissionLauncher.launch(permission)
        } else {
            getCurrentLocation(context, mapViewModel)
        }
    }

    val cameraPositionState = rememberCameraPositionState()

    LaunchedEffect(userLocation) {
        userLocation?.let {
            cameraPositionState.position = CameraPosition.fromLatLngZoom(it, 14f)
        }
    }

    GoogleMap(
        modifier = Modifier.fillMaxSize(),
        cameraPositionState = cameraPositionState,
        properties = MapProperties(isMyLocationEnabled = true)
    )
}

fun getCurrentLocation(context: Context, viewModel: MapViewModel) {
    val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)

    try {
        fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
            if (location != null) {
                android.util.Log.d("MapScreen", "Got location: ${location.latitude}, ${location.longitude}")
                viewModel.updateUserLocation(location)
            } else {
                android.util.Log.d("MapScreen", "Location is null")
            }
        }
    } catch (e: SecurityException) {
        android.util.Log.e("MapScreen", "SecurityException getting location", e)
    }
}