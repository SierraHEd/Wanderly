package com.example.csc490group3.data

import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.LatLngBounds
import com.google.maps.android.compose.CameraPositionState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

fun LocationMap(
    // ...
) {
    // ...
    val scope = rememberCoroutineScope()

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