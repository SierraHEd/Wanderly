package com.example.csc490group3

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import com.example.csc490group3.data.LocationScreenEvent
import com.example.csc490group3.viewModels.LocationScreenViewState
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMapOptions
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLngBounds
import com.google.maps.android.compose.CameraPositionState
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.GoogleMapComposable
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.rememberCameraPositionState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

// ...

@Composable
@GoogleMapComposable
fun LocationMap(
    navController: NavController,
    paddingValues: PaddingValues,
    viewState: LocationScreenViewState.MountainList,
    eventFlow: Flow<LocationScreenEvent>
) {
    var isMapLoaded by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(viewState.boundingBox.center, 5f)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
    ) {
        // Add GoogleMap here
        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            onMapLoaded = { isMapLoaded = true },
            cameraPositionState = cameraPositionState,
            googleMapOptionsFactory = {
                GoogleMapOptions().mapId("d12038b36cb9d4d3")
            }
        )

        LaunchedEffect(key1 = viewState.boundingBox) {
            zoomAll(scope, cameraPositionState, viewState.boundingBox)
        }

        LaunchedEffect(true) {
            eventFlow.collect { event ->
                when (event) {
                    LocationScreenEvent.OnZoomAll -> {
                        zoomAll(scope, cameraPositionState, viewState.boundingBox)
                    }
                }
            }
        }
    }
}

fun zoomAll(
    scope: CoroutineScope,
    cameraPositionState: CameraPositionState,
    boundingBox: LatLngBounds
) {
    scope.launch {
        cameraPositionState.animate(
            update = CameraUpdateFactory.newLatLngBounds(boundingBox, 64),
            durationMs = 1000
        )
    }
}

