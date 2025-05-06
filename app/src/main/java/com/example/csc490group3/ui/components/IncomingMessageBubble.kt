package com.example.csc490group3.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import com.example.csc490group3.model.Message

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.produceState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.csc490group3.model.Event
import com.example.csc490group3.supabase.DatabaseManagement.getEventById

@Composable
fun IncomingMessageBubble(message: Message) {
    Row(
        modifier = Modifier.fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 4.dp),
        horizontalArrangement = Arrangement.Start
    ) {
        Box(
            modifier = Modifier
                .background(Color.LightGray, shape = RoundedCornerShape(16.dp))
                .padding(12.dp)
                .widthIn(max = 250.dp)
        ) {
            if (message.eventID != null) {
                val eventState = produceState<Event?>(null, message.eventID) {
                    value = getEventById(message.eventID)
                }
                println("Sending Event:" + (eventState.value?.id ?: -1 ))
                eventState.value?.let { event ->
                    Column {
                        Spacer(modifier = Modifier.height(4.dp))
                        EventCard(
                            event = event,
                            onBottomButtonClick = {},
                            onEditEvent = {},
                            isHorizontal = true,
                            onClick = { }
                        )
                    }
                } ?: Text("Loading event...", fontSize = 12.sp, color = Color.Gray)

            } else {
                Text(
                    text = message.content,
                    color = Color.Black,
                    fontSize = 14.sp
                )
            }
        }
    }
}