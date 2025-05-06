import android.widget.Toast
import com.example.csc490group3.model.Message
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.csc490group3.model.Event
import com.example.csc490group3.model.UserSession
import com.example.csc490group3.ui.components.EventCard
import com.example.csc490group3.supabase.DatabaseManagement.getEventById
import com.example.csc490group3.ui.components.EventDetailDialog
import com.example.csc490group3.viewModels.HomeScreenViewModel
import com.example.csc490group3.viewModels.MessageBubbleViewModel
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

@Composable
fun MessageBubble(navController: NavController,message: Message, isFromCurrentUser: Boolean, viewModel: MessageBubbleViewModel = viewModel()) {

    val context = LocalContext.current
    val selectedEvent = remember { mutableStateOf<Event?>(null) }
    val showDialog = remember { mutableStateOf(false) }

    val isRegistered = remember { mutableStateOf(false) }
    val isCheckingRegistration = remember { mutableStateOf(false) }

    val isOnWaitlist = remember { mutableStateOf(false) }
    val isCheckingWaitlist = remember { mutableStateOf(false) }

    val coroutineScope = rememberCoroutineScope()
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 4.dp),
        horizontalArrangement = if (isFromCurrentUser) Arrangement.End else Arrangement.Start
    ) {
        val bubbleColor = if (isFromCurrentUser) Color(0xFF9F7AEA) else Color.LightGray
        val textColor = if (isFromCurrentUser) Color.White else Color.Black

        Box(
            modifier = Modifier
                .background(bubbleColor, shape = RoundedCornerShape(16.dp))
                .padding(12.dp)
                .widthIn(max = 250.dp)
        ) {
            if (message.eventID != null) {
                val eventState = produceState<Event?>(null, message.eventID) {
                    value = getEventById(message.eventID)
                }
                println("Sending Event: ${eventState.value?.id ?: -1}")
                eventState.value?.let { event ->
                    Column {
                        Spacer(modifier = Modifier.height(4.dp))
                        EventCard(
                            event = event,
                            onBottomButtonClick = {},
                            onEditEvent = {},
                            isHorizontal = true,
                            onClick = { selectedEvent.value = event
                                showDialog.value = true

                                // Load registration/waitlist status
                                val userId = UserSession.currentUser?.id
                                if (userId != null && event.id != null) {
                                    isCheckingRegistration.value = true
                                    isCheckingWaitlist.value = true

                                    // Assuming you have a ViewModel passed in:
                                    coroutineScope.launch {
                                        isRegistered.value = viewModel.isUserRegisteredForEvent(userId, event.id)
                                        isOnWaitlist.value = viewModel.isUserWaitingForEvent(userId, event.id)
                                        isCheckingRegistration.value = false
                                        isCheckingWaitlist.value = false
                                    }
                                }}
                        )
                    }
                    if (showDialog.value && selectedEvent.value != null) {
                        EventDetailDialog(
                            event = event,
                            onDismiss = {
                                selectedEvent.value = null
                                showDialog.value = false
                            },
                            isUserRegistered = isRegistered.value,   // Pass isUserRegistered
                            isUserOnWaitList = isOnWaitlist.value,   // Pass isUserOnWaitList
                            showRegisterButton = !isRegistered.value && !isCheckingRegistration.value,  // Show register button only if not registered
                            onRegister = {
                                // When the user clicks the register button, we manually trigger the registration
                                isRegistered.value = true // Mark the user as registered
                                viewModel.registerForEvent(
                                    event,
                                    UserSession.currentUser
                                ) // Perform registration
                                Toast.makeText(context, "Successfully Registered!", Toast.LENGTH_SHORT)
                                    .show()
                            },
                            showWaitListButton = !isOnWaitlist.value && !isCheckingWaitlist.value,  // Show waitlist button only if not on waitlist
                            onJoinWaitlist = {
                                if (isOnWaitlist.value) {
                                    // Remove from waitlist
                                    isOnWaitlist.value = false
                                    viewModel.removeFromWaitingList(UserSession.currentUser, event)
                                    Toast.makeText(
                                        context,
                                        "You've been removed from the waiting list.",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                } else {
                                    // Add to waitlist
                                    isOnWaitlist.value = true
                                    viewModel.addToWaitingList(UserSession.currentUser, event)
                                    Toast.makeText(
                                        context,
                                        "You've been added to the waiting list.",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            },
                            navController = navController
                        )
                    }
                } ?: Text("Loading event...", fontSize = 12.sp, color = Color.Gray)

            } else {
                Text(
                    text = message.content,
                    color = textColor,
                    fontSize = 14.sp
                )
            }
        }


    }
}
