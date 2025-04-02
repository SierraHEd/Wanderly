package com.example.csc490group3

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.example.csc490group3.viewModels.LocationScreenViewState
import com.google.android.libraries.mapsplatform.transportation.consumer.model.MarkerType
import com.google.maps.android.compose.GoogleMap
import kotlinx.coroutines.flow.Flow

@Composable
fun LocationScreen(
    paddingValues: PaddingValues,
    viewState: LocationScreenViewState.MountainList,
    eventFlow: Flow<LocationScreenEvent>,
    selectedMarkerType: MarkerType,
) {
    var isMapLoaded by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
    ) {
        // Add GoogleMap here
        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            onMapLoaded = { isMapLoaded = true }
        ){
            when (selectedMarkerType) {
                MarkerType.Basic -> {
                    BasicMarkersMapContent(
                        mountains = viewState.mountains,
                    )
                }

                MarkerType.Advanced -> {
                    AdvancedMarkersMapContent(
                        mountains = viewState.mountains,
                    )
                }

                MarkerType.Clustered -> {
                    ClusteringMarkersMapContent(
                        mountains = viewState.mountains,
                    )
                }
            }
        }
    }
}