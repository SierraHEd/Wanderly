package com.example.csc490group3

import android.content.Context
import android.util.Xml
import com.example.csc490group3.data.Mountain
import com.example.csc490group3.ui.components.meters
import com.google.android.gms.maps.model.LatLng
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.withContext
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserException
import java.io.IOException
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

    private data class WaypointBuilder(
        var latitude: Double? = null,
        var longitude: Double? = null,
        var name: String? = null,
        var elevation: Double? = null,
    ) {
        fun build(): Waypoint? {
            val latitude = latitude ?: return null
            val longitude = longitude ?: return null
            val elevation = elevation ?: return null
            val name = name ?: return null

            return Waypoint(
                name = name,
                location = LatLng(latitude, longitude),
                elevationMeters = elevation,
            )
        }
    }

    /**
     * Read all of the waypoints from a GPX file.
     */
    private fun readWaypoints(inputStream: InputStream): Sequence<Waypoint> = sequence {
        // We don't use namespaces
        val ns: String? = null
        val parser = Xml.newPullParser()
        parser.setInput(inputStream, null)

        try {
            var eventType = parser.eventType

            var builder: WaypointBuilder? = null

            while (eventType != XmlPullParser.END_DOCUMENT) {
                when (eventType) {
                    XmlPullParser.START_TAG -> {
                        if (parser.name == "wpt") {
                            builder = WaypointBuilder(
                                latitude = parser.getAttributeValue(ns, "lat").toDouble(),
                                longitude = parser.getAttributeValue(ns, "lon").toDouble(),
                            )
                        } else if (builder != null) {
                            when (parser.name) {
                                "name" -> builder.name = parser.nextText()
                                "ele" -> builder.elevation = parser.nextText().toDouble()
                            }
                        }
                    }

                    XmlPullParser.END_TAG -> {
                        if (parser.name == "wpt" && builder != null) {
                            builder.build()?.let { waypoint -> yield(waypoint) }
                            builder = null
                        }
                    }
                }
                eventType = parser.next()
            }

        } catch (e: XmlPullParserException) {
            // Handle parsing errors
        } catch (e: IOException) {
            // Handle IO errors
        }
    }
}

private data class Waypoint(
    val name: String,
    val location: LatLng,
    val elevationMeters: Double,
)

private fun Waypoint.toMountain(id: Int): Mountain {
    return Mountain(
        id = id,
        name = name,
        location = location,
        elevation = elevationMeters.meters
    )
}