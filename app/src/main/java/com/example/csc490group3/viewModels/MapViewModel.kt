package com.example.csc490group3.viewModels

import android.app.Application
import android.location.Location
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class MapViewModel(application: Application) : AndroidViewModel(application) {

    private val _userLocation = MutableStateFlow<LatLng?>(null)
    val userLocation: StateFlow<LatLng?> = _userLocation.asStateFlow()

    fun updateUserLocation(location: Location) {
        viewModelScope.launch {
            android.util.Log.d("MapViewModel", "Updating userLocation: ${location.latitude}, ${location.longitude}")
            _userLocation.value = LatLng(location.latitude, location.longitude)
        }
    }
}