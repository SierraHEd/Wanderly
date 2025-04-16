package com.example.csc490group3.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.csc490group3.MountainsRepository
import com.example.csc490group3.data.LocationScreenEvent
import com.example.csc490group3.data.is14er
import com.example.csc490group3.data.toLatLngBounds
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow

@HiltViewModel
class LocationViewModel
@Inject
constructor(
    mountainsRepository: MountainsRepository
) : ViewModel() {
    private val _eventChannel = Channel<LocationScreenEvent>()

    // Event channel to send events to the UI
    internal fun getEventChannel() = _eventChannel.receiveAsFlow()

    // Whether or not to show all of the high peaks
    private var showAllMountains = MutableStateFlow(false)

    val eventFlow = MutableSharedFlow<LocationScreenEvent>()
    val mountainsScreenViewState =
        mountainsRepository.mountains.combine(showAllMountains) { allMountains, showAllMountains ->
            if (allMountains.isEmpty()) {
                LocationScreenViewState.Loading
            } else {
                val filteredMountains =
                    if (showAllMountains) allMountains else allMountains.filter { it.is14er() }
                val boundingBox = filteredMountains.map { it.location }.toLatLngBounds()
                LocationScreenViewState.MountainList(
                    mountains = filteredMountains,
                    boundingBox = boundingBox,
                    showingAllPeaks = showAllMountains,
                )
            }
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = LocationScreenViewState.Loading
        )

    init {
        // Load the full set of mountains
        viewModelScope.launch {
            mountainsRepository.loadMountains()
        }
    }

    // Handle user events
    fun onEvent(event: LocationViewModelEvent) {
        when (event) {
            LocationViewModelEvent.OnZoomAll -> onZoomAll()
            LocationViewModelEvent.OnToggleAllPeaks -> toggleAllPeaks()
        }
    }

    private fun onZoomAll() {
        sendScreenEvent(LocationScreenEvent.OnZoomAll)
    }

    private fun toggleAllPeaks() {
        showAllMountains.value = !showAllMountains.value
    }

    // Send events back to the UI via the event channel
    private fun sendScreenEvent(event: LocationScreenEvent) {
        viewModelScope.launch { _eventChannel.send(event) }
    }
}