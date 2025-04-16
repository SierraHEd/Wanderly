package com.example.csc490group3.ui.admin

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
import androidx.navigation.NavController
import com.example.csc490group3.model.Report
import com.example.csc490group3.model.UserSession
import com.example.csc490group3.supabase.DatabaseManagement
import kotlinx.coroutines.launch

@Composable
fun AdminScreen(navController: NavController) {
    val coroutineScope = rememberCoroutineScope()
    var reportList by remember { mutableStateOf<List<Pair<Report, String>>>(emptyList()) }
    var isAdmin by remember { mutableStateOf(false) }
    val userEmail = UserSession.currentUser?.email ?: ""

    LaunchedEffect(Unit) {
        coroutineScope.launch {
            isAdmin = DatabaseManagement.isAdmin(userEmail)
            if (isAdmin) {
                reportList = DatabaseManagement.getReportedEventsWithNames()
            }
        }
    }

    if (!isAdmin) {
        Text("Access Denied: Admins Only", color = Color.Red, modifier = Modifier.padding(16.dp))
    } else {
        LazyColumn(modifier = Modifier.padding(16.dp)) {
            items(reportList) { (report, eventName) ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    elevation = CardDefaults.cardElevation(4.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Report ID: ${report.report_id}", fontWeight = FontWeight.Bold)
                        Text("Event: $eventName")
                        Text("Reported By: ${report.reported_By}")
                        Text("Report Type: ${reportTypeLabel(report.report_type)}")

                        Spacer(modifier = Modifier.height(8.dp))
                        Row {
                            Button(
                                onClick = {
                                    coroutineScope.launch {
                                        report.report_id?.let { DatabaseManagement.deleteEvent(it) }
                                        reportList = reportList.filterNot { it.first.report_id == report.report_id }
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
                            ) {
                                Text("Delete Event", color = Color.White)
                            }

                            Spacer(modifier = Modifier.width(8.dp))

                            Button(
                                onClick = {
                                    coroutineScope.launch {
                                        DatabaseManagement.dismissReport(report.report_id!!)
                                        reportList = reportList.filterNot { it.first.report_id == report.report_id }
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = Color.Gray)
                            ) {
                                Text("Dismiss Report", color = Color.White)
                            }
                        }
                    }
                }
            }
        }
    }
}

fun reportTypeLabel(type: Int): String = when (type) {
    1 -> "Fake Event"
    2 -> "Dangerous Event"
    3 -> "Spam Event"
    else -> "Unknown"
}
