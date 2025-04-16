package com.example.csc490group3

import android.content.Context
import com.example.csc490group3.data.Mountain
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.withContext
import java.io.InputStream

class MountainsRepository(@ApplicationContext val context: Context) {
    private val _mountains = MutableStateFlow(emptyList<Mountain>())
    val mountains: StateFlow<List<Mountain>> = _mountains
    private var loaded = false

    /**
     * Loads the list of mountains from the list of mountains from the raw resource.
     */
    suspend fun loadMountains(): StateFlow<List<Mountain>> {
        if (!loaded) {
            loaded = true
            _mountains.value = withContext(Dispatchers.IO) {
                context.resources.openRawResource(R.raw.top_peaks).use { inputStream ->
                    readMountains(inputStream)
                }
            }
        }
        return mountains
    }

    /**
     * Reads the [Waypoint]s from the given [inputStream] and returns a list of [Mountain]s.
     */
    private fun readMountains(inputStream: InputStream) =
        readWaypoints(inputStream).mapIndexed { index, waypoint ->
            waypoint.toMountain(index)
        }.toList()

    // ...
}