package com.example.csc490group3.ui.admin

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.csc490group3.model.Report
import com.example.csc490group3.supabase.DatabaseManagement
import kotlinx.coroutines.launch
import com.example.csc490group3.ui.theme.Purple80

@Composable
fun AdminScreen(navController: NavController) {
    val coroutineScope = rememberCoroutineScope()
    var reportList by remember { mutableStateOf<List<Pair<Report, String>>?>(null) }
    var refreshing by remember { mutableStateOf(false) }

    LaunchedEffect(refreshing) {
        refreshing = false
        reportList = DatabaseManagement.getReportedEventsWithNames()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .background(Color.White)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Admin - Reported Events",
                style = MaterialTheme.typography.headlineMedium,
                color = Purple80
            )
            Button(
                onClick = { navController.navigate("start_up_screen") },
                colors = ButtonDefaults.buttonColors(containerColor = Purple80)
            ) {
                Text("Sign Out", color = Color.White)
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        if (reportList == null) {
            Text("Loading reports...", fontSize = 16.sp)
        } else if (reportList!!.isEmpty()) {
            Text("No reports found.", fontSize = 16.sp)
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                items(reportList!!) { (report, eventName) ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        elevation = CardDefaults.cardElevation(6.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text("Report ID: ${report.report_id}", fontSize = 14.sp)
                            Text("Event: $eventName", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                            Text("Reported By: ${report.reported_By}", fontSize = 14.sp)
                            Text(
                                "Report Type: ${when (report.report_type) {
                                    1 -> "Fake Event"
                                    2 -> "Dangerous Event"
                                    3 -> "Spam Event"
                                    else -> "Unknown"
                                }}",
                                fontSize = 14.sp
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Button(
                                    onClick = {
                                        coroutineScope.launch {
                                            DatabaseManagement.dismissReport(report.report_id ?: return@launch)
                                            refreshing = true
                                        }
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFA000))
                                ) {
                                    Text("Dismiss", color = Color.White)
                                }

                                Button(
                                    onClick = {
                                        coroutineScope.launch {
                                            DatabaseManagement.deleteEvent(report.reported_event_id)
                                            refreshing = true
                                        }
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD32F2F))
                                ) {
                                    Text("Delete Event", color = Color.White)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}


