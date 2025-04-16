package com.example.csc490group3.data

sealed class LocationScreenEvent {
    data object OnZoomAll: LocationScreenEvent()
}