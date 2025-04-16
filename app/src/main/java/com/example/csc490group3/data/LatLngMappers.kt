package com.example.csc490group3.data

import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds

fun Collection<LatLng>.toLatLngBounds() : LatLngBounds {
    if (isEmpty()) error("Cannot create a LatLngBounds from an empty list")

    return LatLngBounds.builder().apply {
        for (latLng in this@toLatLngBounds) {
            include(latLng)
        }
    }.build()
}