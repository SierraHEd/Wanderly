package com.example.csc490group3

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.csc490group3.data.BottomNavBar
import com.example.csc490group3.model.Event
import com.example.csc490group3.ui.theme.PurpleBKG
import com.example.csc490group3.viewModels.CalendarScreenViewModel
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDate
import kotlinx.datetime.Month
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import java.time.YearMonth
import java.util.Locale
import com.example.csc490group3.ui.components.EventDetailDialog

@Composable
fun CalendarScreen(
    navController: NavController,
    viewModel: CalendarScreenViewModel = viewModel() // Correctly instantiates the ViewModel
) {
    // Get the current date using Clock.System.now() and convert it to LocalDate
    val currentDate = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date
    // Use the currentDate to initialize selectedDay, selectedMonth, and selectedYear
    val selectedDay = remember { mutableStateOf(currentDate.dayOfMonth) }
    val selectedMonth = remember { mutableStateOf(currentDate.monthNumber) }
    val selectedYear = remember { mutableStateOf(currentDate.year) }

    // Get data from ViewModel
    val events by viewModel.events
    val isLoading by viewModel.isLoading
    val errorMessage by viewModel.errorMessage

    // Track selected event for dialog and events for the selected day
    val selectedEvent = remember { mutableStateOf<Event?>(null) }
    val eventsForSelectedDay = remember { mutableStateOf<List<Event>>(emptyList()) }
    val showEventsPopup = remember { mutableStateOf(false) }

    var isRegistered = remember { mutableStateOf(false) }

    // Sort events by date and filter out past events
    val upcomingEvents = events
        .filter { it.eventDate >= currentDate }  // Filter out events that have passed
        .sortedBy { it.eventDate }  // Sort events by their eventDate

////////////////
// Launched Effects
///////////////

    // LaunchedEffect only when the month or year changes update events
    LaunchedEffect(selectedMonth.value, selectedYear.value) {
        viewModel.fetchUserEvents() // Fetch events only when month or year changes
    }

////////////////
// Main UI
///////////////

    Scaffold(
        bottomBar = { BottomNavBar(navController) } // Adding BottomNavBar
    ) { paddingValues ->
        Surface(
            modifier = Modifier
                .background(PurpleBKG)
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                //Spacer for clearance from front facing camera
                Spacer(modifier = Modifier.height(32.dp))

                // Month navigation buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    //Button to move month backward by 1
                    Button(
                        onClick = {
                            if (selectedMonth.value == 1) {
                                selectedMonth.value = 12
                                selectedYear.value -= 1
                            } else {
                                selectedMonth.value -= 1
                            }
                            selectedDay.value = 0
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF6650a4),
                            contentColor = Color.White
                        )
                    ) {
                        Text("<")
                    }

                    // Month and Year display
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "${
                                Month.of(selectedMonth.value).name.lowercase()
                                    .replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.ROOT) else it.toString() }
                            } - ${selectedYear.value}",
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp
                        )
                        //Button to redirect calendar to today's date
                        Button(
                            onClick = {
                                val today =
                                    Clock.System.now()
                                        .toLocalDateTime(TimeZone.currentSystemDefault()).date
                                selectedYear.value = today.year
                                selectedMonth.value = today.monthNumber
                                selectedDay.value = today.dayOfMonth
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF6650a4),
                                contentColor = Color.White
                            )
                        ) {
                            Text("Today")
                        }
                    }
                    //Button to move month forward by 1
                    Button(
                        onClick = {
                            if (selectedMonth.value == 12) {
                                selectedMonth.value = 1
                                selectedYear.value += 1
                            } else {
                                selectedMonth.value += 1
                            }
                            selectedDay.value = 0
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF6650a4),
                            contentColor = Color.White
                        )
                    ) {
                        Text(">")
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Show a loading spinner when events are being fetched
                if (isLoading) {
                    Text(text = "Loading...", color = Color.Gray)
                    return@Surface
                }

                // Show error message if there's any issue fetching events
                if (!errorMessage.isNullOrEmpty()) {
                    Text(
                        text = "Error: $errorMessage",
                        color = Color.Red,
                        modifier = Modifier.padding(16.dp)
                    )
                    return@Surface
                }

                // Calendar view
                CalendarView(
                    year = selectedYear.value,
                    month = selectedMonth.value,
                    eventDates = events.map { it.eventDate },
                    selectedDay = selectedDay.value,
                    currentDay = if (selectedYear.value == currentDate.year &&
                        selectedMonth.value == currentDate.monthNumber
                    )
                        currentDate.dayOfMonth else -1,
                    onDateClick = { day -> //saves day user clicks and compares to current events
                        selectedDay.value = day
                        eventsForSelectedDay.value = events.filter { it.eventDate.dayOfMonth == day }
                        //If more than one event on a given day show popup list of events
                        if (eventsForSelectedDay.value.size > 1) {
                            showEventsPopup.value = true
                        }
                        //If event then show all details
                        else if (eventsForSelectedDay.value.isNotEmpty()) {
                            selectedEvent.value = eventsForSelectedDay.value.first()
                        }
                    }
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Selected Date Details Title
                if (eventsForSelectedDay.value.isNotEmpty()) {
                    Text(
                        text = "Selected Date Details",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = Color.Black,
                        modifier = Modifier.padding(start = 16.dp, bottom = 8.dp)
                    )

                    // Show the list of events for the selected day
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                    ) {
                        items(eventsForSelectedDay.value) { event -> // Using eventsForSelectedDay.value
                            // Display each event as a clickable item
                            CalendarEventCard(
                                event = event,
                                onClick = {
                                    selectedEvent.value = event // Show event details dialog
                                }
                            )
                        }
                    }
                } else if (selectedDay.value > 0) {
                    Text(
                        text = "No events recorded for this date",
                        modifier = Modifier.padding(16.dp),
                        color = Color.Gray
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Upcoming Events Title
                Text(
                    text = "Upcoming Events",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = Color.Black,
                    modifier = Modifier.padding(start = 16.dp, bottom = 8.dp)
                )

                // Display Event cards for all upcoming events
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                ) {
                    items(upcomingEvents) { event ->
                        // Display event card
                        CalendarEventCard(
                            event = event,
                            onClick = {
                                selectedEvent.value = event // Show event details dialog
                            }
                        )
                    }
                }
            }

            // Show event detail popup when an event is selected
            selectedEvent.value?.let { event ->
                EventDetailDialog(event = event, onDismiss = { selectedEvent.value = null },
                    showRegisterButton = false,
                    navController = navController,
                    onRegister =  {
                        isRegistered.value = true;
                }
                )
            }

            // Show the popup dialog with the list of events for the selected day
            if (showEventsPopup.value) {
                androidx.compose.material3.AlertDialog(
                    onDismissRequest = { showEventsPopup.value = false },
                    title = { Text(text = "Events on ${selectedDay.value}") },
                    text = {
                        LazyColumn {
                            items(eventsForSelectedDay.value) { event ->
                                Text(
                                    text = event.eventName,
                                    modifier = Modifier
                                        .padding(8.dp)
                                        .clickable {
                                            // Show the details of the selected event
                                            selectedEvent.value = event
                                            showEventsPopup.value = false // Close popup
                                        }
                                )
                            }
                        }
                    },
                    confirmButton = {
                        Button(
                            onClick = { showEventsPopup.value = false }  // Close popup
                        ) {
                            Text("Close")
                        }
                    }
                )
            }
        }
    }
}

////////////////
// Composable Functions
///////////////

//Event cards for brief details under the calendar
@Composable
fun CalendarEventCard(
    event: Event,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .clickable { onClick() },
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
        ) {
            Text(
                text = event.eventName,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(text = "Time: ${event.eventDate}")
                    Text(text = "Type: ${event.categories?.joinToString(", ")}")
                }
                Column {
                    Text(text = "Location: ${event.address}")
                }
            }
        }
    }
}
//Custom view for the calendar
@Composable
fun CalendarView(
    year: Int,
    month: Int,
    eventDates: List<LocalDate>, // A list of event dates as LocalDate
    selectedDay: Int,
    currentDay: Int,
    onDateClick: (Int) -> Unit
) {
    val daysInMonth = YearMonth.of(year, month).lengthOfMonth()
    val firstDayOfWeek =
        LocalDate(year, month, 1).dayOfWeek.value % 7 // Get the first day of the month

    Column {
        WeekHeader()
        // Create the calendar grid
        LazyVerticalGrid(columns = GridCells.Fixed(7)) {
            // Add empty cells for the first day of the week
            repeat(firstDayOfWeek) {
                item { Spacer(modifier = Modifier.size(40.dp)) }
            }

            // Add cells for each day of the month
            for (day in 1..daysInMonth) {
                item {
                    DayCell(
                        day = day,
                        isEventDay = eventDates.contains(
                            LocalDate(
                                year,
                                month,
                                day
                            )
                        ), // Direct comparison with LocalDate
                        isSelected = day == selectedDay,
                        currentDay = day == currentDay,
                        onClick = { onDateClick(day) }
                    )
                }
            }
        }
    }
}
//Custom cells for the days -> holds data for if it has event -> handles colors, font, and onClick.
@Composable
fun DayCell(
    day: Int,
    isEventDay: Boolean,
    isSelected: Boolean,
    currentDay: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .size(40.dp)
            .padding(4.dp)
            .clip(CircleShape)
            .background(
                when {
                    isSelected -> Color(0xFF6650a4) // Purple for selected day
                    isEventDay -> Color(0x8A7CA8FF) // Light Blue for event day
                    currentDay -> Color(0xFF969696) // Grey for current day
                    else -> Color.Transparent
                }
            )
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = day.toString(),
            color = if (isSelected) Color.White else Color.Black,
            style = TextStyle(
                fontSize = 14.sp,
                fontWeight = if (currentDay) FontWeight.Bold else FontWeight.Normal
            )
        )
    }
}

//Weekday view
@Composable
fun WeekHeader() {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        listOf("Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat").forEach { day ->
            Text(text = day, textAlign = TextAlign.Center, modifier = Modifier.weight(1f))
        }
    }
}
