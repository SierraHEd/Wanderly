package com.example.csc490group3.data

import com.google.android.gms.maps.model.LatLng

data class Mountain(
    val id: Int,
    val name: String,
    val location: LatLng,
    val elevation: Meters,
)

fun Mountain.is14er() = elevation >= 14_000.feet