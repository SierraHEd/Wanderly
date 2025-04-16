package com.example.csc490group3.viewModels

sealed class LocationViewModelEvent {
    data object OnZoomAll: LocationViewModelEvent()
    data object OnToggleAllPeaks: LocationViewModelEvent()
}