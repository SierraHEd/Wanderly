package com.example.csc490group3.viewModels

import com.example.csc490group3.model.Event
import com.google.android.gms.maps.model.LatLngBounds

sealed class LocationScreenViewState {
    data object Loading : LocationScreenViewState()
    data class MountainList(
        // List of the mountains to display
        val mountains: List<Event>,

        // Bounding box that contains all of the mountains
        val boundingBox: LatLngBounds,

        // Switch indicating whether all the mountains or just the 14ers
        val showingAllPeaks: Boolean = false,
    ) : LocationScreenViewState()
}