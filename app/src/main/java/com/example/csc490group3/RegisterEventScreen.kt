package com.example.csc490group3


import android.net.Uri
import android.app.TimePickerDialog
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.Black
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Popup
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.csc490group3.model.Category
import com.example.csc490group3.model.Event
import com.example.csc490group3.model.UserSession
import com.example.csc490group3.supabase.DatabaseManagement.addCategoryRelationship
import com.example.csc490group3.supabase.DatabaseManagement.addEvent
import com.example.csc490group3.supabase.DatabaseManagement.addRecord
import com.example.csc490group3.supabase.StorageManagement
import com.example.csc490group3.supabase.DatabaseManagement.getCategories
import com.example.csc490group3.ui.theme.Purple40
import com.example.csc490group3.ui.theme.PurpleBKG
import com.example.csc490group3.ui.theme.PurpleContainer
import com.example.csc490group3.ui.theme.PurpleDarkBKG
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDate
import java.io.File
import java.util.UUID
import kotlinx.datetime.LocalTime
import java.time.format.DateTimeFormatter
import java.util.Calendar
import com.example.csc490group3.ui.components.CategoryPickerBottomSheet
import com.example.csc490group3.ui.components.StatePickerBottomSheet
import com.example.csc490group3.ui.components.CountryPickerBottomSheet


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterEventScreen(navController: NavController, initialEvent: Event? = null) {
    val coroutineScope = rememberCoroutineScope()
    var eventToAdd: Event
    var showCountryPicker by remember { mutableStateOf(false) }
    var showStatePicker by remember { mutableStateOf(false) }
    var showCategoryPicker by remember { mutableStateOf(false) }
    var price by remember { mutableStateOf(initialEvent?.price?.toString() ?: "") }
    var eventName by remember { mutableStateOf(initialEvent?.eventName ?: "") }
    var zipcode by remember { mutableStateOf(initialEvent?.zipcode ?: "") }
    var city by remember { mutableStateOf(initialEvent?.city ?: "") }
    var address by remember { mutableStateOf(initialEvent?.address ?: "") }
    var venue by remember { mutableStateOf(initialEvent?.venue ?: "") }
    var maxAttendees by remember { mutableStateOf(initialEvent?.maxAttendees?.toString() ?: "") }
    var description by remember { mutableStateOf(initialEvent?.description ?: "") }
    var isPublic by remember { mutableStateOf(initialEvent?.isPublic ?: true) }
    var isFamilyFriendly by remember { mutableStateOf(initialEvent?.isFamilyFriendly ?: false) }
    var selectedCategories by remember { mutableStateOf(emptyList<Category>()) }
    var eventTime by remember { mutableStateOf(initialEvent?.eventTime?.toString() ?: "00:00") }
    var selectedCountry by remember { mutableStateOf(initialEvent?.country ?: "Country") }
    var selectedState by remember { mutableStateOf(initialEvent?.state ?: "State") }
    var eventDateString by remember { mutableStateOf("") }
    var eventDate by remember { mutableStateOf(initialEvent?.eventDate) }
    var showDateErrorToast by remember { mutableStateOf(false) }
    var showRegisterSuccessToast by remember { mutableStateOf(false) }
    var eventImageUri by remember { mutableStateOf<Uri?>(null) }
    var eventImageFile by remember { mutableStateOf<File?>(null) }
    val context = LocalContext.current
    val localEventTime: LocalTime = LocalTime.parse("$eventTime:00")

    if (initialEvent != null) {
        LaunchedEffect(initialEvent.id) {
            val fetchedCategories = getCategories(initialEvent.id!!, "event_categories")
            if (fetchedCategories != null) {
                selectedCategories = fetchedCategories
            }
        }
    }

    val eventImagePicker = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        uri?.let {
            eventImageUri = it
            val inputStream = context.contentResolver.openInputStream(it)
            val file = File(context.cacheDir, "event_${UUID.randomUUID()}.jpg")
            inputStream?.use { input ->
                file.outputStream().use { output ->
                    input.copyTo(output)
                }
            }
            eventImageFile = file
        }
    }

    if (showDateErrorToast) {
        LaunchedEffect(Unit) {
            Toast.makeText(context, "Please select an event date.", Toast.LENGTH_SHORT).show()
        }
        showDateErrorToast = false
    }
    if (showRegisterSuccessToast) {
        LaunchedEffect(Unit) {
            Toast.makeText(context, "Event Created!", Toast.LENGTH_SHORT).show()
        }
        showRegisterSuccessToast = false
    }

    val datePickerState = rememberDatePickerState()
    var isDatePickerVisible by remember { mutableStateOf(false) }

    fun convertMillisToLocalDate(millis: Long): kotlinx.datetime.LocalDate {
        val offsetMillis = java.time.ZoneId.systemDefault()
            .rules
            .getOffset(java.time.Instant.ofEpochMilli(millis))
            .totalSeconds * 1000L
        val adjustedMillis = millis - offsetMillis
        val javaLocalDate = java.time.Instant.ofEpochMilli(adjustedMillis)
            .atZone(java.time.ZoneId.systemDefault())
            .toLocalDate()
        return kotlinx.datetime.LocalDate(
            javaLocalDate.year,
            javaLocalDate.monthValue,
            javaLocalDate.dayOfMonth
        )
    }

    fun convertMillisToLocalString(millis: Long): String {
        val formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy")
        val offsetMillis = java.time.ZoneId.systemDefault()
            .rules
            .getOffset(java.time.Instant.ofEpochMilli(millis))
            .totalSeconds * 1000L
        val adjustedMillis = millis - offsetMillis
        val localDate = java.time.Instant.ofEpochMilli(adjustedMillis)
            .atZone(java.time.ZoneId.systemDefault())
            .toLocalDate()
        return formatter.format(localDate)
    }

    fun convertStringToMillis(digits: String): Long? {
        if (digits.length != 8) return null
        val formattedDate = "${digits.substring(0, 2)}/${digits.substring(2, 4)}/${digits.substring(4, 8)}"
        return try {
            val formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy")
            val localDate = java.time.LocalDate.parse(formattedDate, formatter)
            localDate.atStartOfDay(java.time.ZoneId.systemDefault())
                .toInstant()
                .toEpochMilli()
        } catch (e: Exception) {
            null
        }
    }

    if (isDatePickerVisible) {
        Popup(
            alignment = Alignment.Center,
            onDismissRequest = { isDatePickerVisible = false }
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.8f)
                    .padding(16.dp)
                    .background(PurpleBKG)
                    .border(2.dp, Black)
            ) {
                DatePicker(
                    state = datePickerState,
                    colors = DatePickerDefaults.colors(containerColor = PurpleBKG),
                    modifier = Modifier.height(540.dp)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    horizontalArrangement = Arrangement.spacedBy(30.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    TextButton(
                        onClick = {
                            datePickerState.selectedDateMillis?.let { selectedDateMillis ->
                                eventDate = convertMillisToLocalDate(selectedDateMillis)
                                eventDateString = convertMillisToLocalString(selectedDateMillis)
                            }
                            isDatePickerVisible = false
                        },
                        modifier = Modifier
                            .weight(1f)
                            .background(PurpleDarkBKG)
                    ) {
                        Text("Confirm", color = White, modifier = Modifier.padding(8.dp))
                    }
                    TextButton(
                        onClick = { isDatePickerVisible = false },
                        modifier = Modifier
                            .weight(1f)
                            .background(Color.Red)
                    ) {
                        Text("Cancel", color = Black, modifier = Modifier.padding(8.dp))
                    }
                }
            }
        }
    }

    Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(PurpleBKG)
                .padding(16.dp)
        ) {
            Spacer(modifier = Modifier.height(36.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Image(
                    painter = painterResource(id = R.drawable.app_icon),
                    contentDescription = null,
                    modifier = Modifier
                        .size(50.dp)
                        .clip(CircleShape)
                        .clickable { navController.navigate("Home_Screen") }
                        .border(0.dp, Color.Transparent, CircleShape),
                    contentScale = ContentScale.Crop
                )
                Text(
                    "Your Wanderly Event",
                    textDecoration = TextDecoration.Underline,
                    fontSize = 28.sp,
                    color = Black,
                    fontFamily = FontFamily.Serif
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                "Event Registration Form",
                fontSize = 16.sp,
                color = Black,
                fontFamily = FontFamily.Serif
            )
            EventTextField("Event Name", eventName) { eventName = it }
            EventTextField("Description", description) { description = it }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = eventDateString,
                    onValueChange = { newText ->
                        eventDateString = newText.filter { it.isDigit() }.take(8)
                    },
                    label = { Text("Event Date") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    visualTransformation = DateInputVisualTransformation(),
                    readOnly = false,
                    modifier = Modifier
                        .fillMaxWidth(fraction = 0.6f)
                        .weight(1f)
                        .onGloballyPositioned {
                            convertStringToMillis(eventDateString)?.let { millis ->
                                datePickerState.selectedDateMillis = millis
                            }
                        },
                    trailingIcon = {
                        Icon(Icons.Filled.Create, contentDescription = "Select Date")
                    }
                )
                Spacer(modifier = Modifier.width(8.dp))
                IconButton(
                    onClick = { isDatePickerVisible = true },
                    modifier = Modifier
                        .size(48.dp)
                        .border(1.dp, Color.Gray, CircleShape)
                        .clip(CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.Filled.CalendarToday,
                        contentDescription = "Open Date Picker",
                        tint = Color.Black
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Box(modifier = Modifier.fillMaxWidth(0.4f)) {
                    OutlinedTextField(
                        value = eventTime,
                        onValueChange = { },
                        label = { Text("Event Time") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        readOnly = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Box(
                        modifier = Modifier
                            .matchParentSize()
                            .clickable {
                                val calendar = Calendar.getInstance()
                                val initialHour = calendar.get(Calendar.HOUR_OF_DAY)
                                val initialMinute = calendar.get(Calendar.MINUTE)
                                TimePickerDialog(
                                    context,
                                    { _, hour, minute ->
                                        eventTime = String.format("%02d:%02d", hour, minute)
                                    },
                                    initialHour,
                                    initialMinute,
                                    true
                                ).show()
                            }
                    )
                }
            }
            EventTextField("Venue", venue) { venue = it }
            EventTextField("Address", address) { address = it }
            EventTextField("City", city) { city = it }
            Button(
                onClick = { showStatePicker = true },
                colors = ButtonDefaults.buttonColors(containerColor = PurpleContainer),
                shape = RoundedCornerShape(20.dp),
                border = BorderStroke(1.dp, White),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 15.dp)
            ) {
                Text(
                    text = selectedState,
                    fontSize = 22.sp,
                    color = Black,
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Left,
                    fontFamily = FontFamily.Default
                )
            }
            StatePickerBottomSheet(
                showSheet = showStatePicker,
                onDismiss = { showStatePicker = false },
                onStateSelected = { selectedState = it }
            )
            EventTextField("Zipcode", zipcode) { zipcode = it }
            Button(
                onClick = { showCountryPicker = true },
                colors = ButtonDefaults.buttonColors(containerColor = PurpleContainer),
                shape = RoundedCornerShape(20.dp),
                border = BorderStroke(1.dp, White),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 15.dp)
            ) {
                Text(
                    text = selectedCountry,
                    fontSize = 22.sp,
                    color = Black,
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Left,
                    fontFamily = FontFamily.Default
                )
            }
            CountryPickerBottomSheet(
                showSheet = showCountryPicker,
                onDismiss = { showCountryPicker = false },
                onStateSelected = { selectedCountry = it }
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                EventTextField("Price", price, KeyboardType.Number, Modifier.weight(1f)) { price = it }
                Spacer(modifier = Modifier.width(4.dp))
                EventTextField("Guest Limit", maxAttendees, KeyboardType.Number, Modifier.weight(1f)) { maxAttendees = it }
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Is A Public Event", color = Black)
                    Switch(checked = isPublic, onCheckedChange = { isPublic = it })
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Is Family Friendly", color = Black)
                    Switch(checked = isFamilyFriendly, onCheckedChange = { isFamilyFriendly = it })
                }
            }
            Button(
                onClick = { showCategoryPicker = true },
                colors = ButtonDefaults.buttonColors(containerColor = PurpleContainer),
                shape = RoundedCornerShape(20.dp),
                border = BorderStroke(1.dp, White),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 15.dp)
            ) {
                Text(
                    text = if (selectedCategories.isEmpty()) "Select Categories"
                    else selectedCategories.joinToString(", ") { it.name },
                    fontSize = 22.sp,
                    color = Black,
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Left,
                    fontFamily = FontFamily.Default
                )
            }
            CategoryPickerBottomSheet(
                showSheet = showCategoryPicker,
                onDismiss = { showCategoryPicker = false },
                onSelectionDone = { selection -> selectedCategories = selection },
                maxSelections = 3
            )

            Button(
                onClick = { eventImagePicker.launch("image/*") },
                colors = ButtonDefaults.buttonColors(containerColor = PurpleContainer),
                modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)
            ) {
                Text(text = "Select Event Photo")
            }

            eventImageUri?.let { uri ->
                Image(
                    painter = rememberAsyncImagePainter(uri),
                    contentDescription = "Selected Event Photo",
                    modifier = Modifier
                        .size(150.dp)
                        .padding(8.dp)
                        .clip(RoundedCornerShape(8.dp)),
                    contentScale = ContentScale.Crop
                )
            }

            Button(
                colors = ButtonDefaults.buttonColors(containerColor = PurpleBKG),
                onClick = {
                    if (eventDate == null) {
                        showDateErrorToast = true
                    } else {
                        coroutineScope.launch {
                            // If an event image is selected, upload it first.
                            val eventPhotoUrl = eventImageFile?.let { file ->
                                StorageManagement.uploadEventPhoto(
                                    file,
                                    UUID.randomUUID().toString()
                                )
                            }

                            eventToAdd = UserSession.currentUser?.id?.let {
                                Event(
                                    eventName = eventName,
                                    zipcode = zipcode,
                                    city = city,
                                    address = address,
                                    venue = venue,
                                    maxAttendees = maxAttendees.toIntOrNull() ?: 0,
                                    description = description,
                                    isPublic = isPublic,
                                    isFamilyFriendly = isFamilyFriendly,
                                    price = price.toDoubleOrNull() ?: 0.0,
                                    country = selectedCountry,
                                    state = selectedState,
                                    createdBy = it,
                                    numAttendees = 0,
                                    eventDate = eventDate!!,
                                    eventTime = localEventTime,
                                    photoUrl = eventPhotoUrl
                                )
                            }!!
                            coroutineScope.launch {
                                val newEventID = addEvent(eventToAdd)
                                if (newEventID == -1) {
                                    println("Error adding event")
                                } else {
                                    addCategoryRelationship(
                                        selectedCategories,
                                        "event_categories",
                                        newEventID
                                    )
                                }
                            }
                            showRegisterSuccessToast = true
                            navController.navigate("Home_Screen")
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .size(50.dp)
                    .clip(CircleShape)
                    .border(2.dp, White, CircleShape)
            ) {
                Text(
                    "Register This Event",
                    fontSize = 22.sp,
                    color = Color(0xFF793CCB),
                    fontFamily = FontFamily.Default
                )
            }
            Spacer(modifier = Modifier.padding(8.dp))
            Button(
                colors = ButtonDefaults.buttonColors(containerColor = PurpleBKG),
                onClick = { navController.navigate("Home_Screen") },
                modifier = Modifier
                    .fillMaxWidth()
                    .size(50.dp)
                    .clip(CircleShape)
                    .border(2.dp, Color.Red, CircleShape)
            ) {
                Text(
                    "Go back to Home",
                    fontSize = 22.sp,
                    color = Color(0xFF793CCB),
                    fontFamily = FontFamily.Default
                )
            }
        }
    }
}

class DateInputVisualTransformation : VisualTransformation {
    override fun filter(text: AnnotatedString): TransformedText {
        val digits = text.text.filter { it.isDigit() }.take(8)
        val (formatted, mapping) = transformDigits(digits)
        val offsetMapping = object : OffsetMapping {
            override fun originalToTransformed(offset: Int): Int {
                return mapping.getOrElse(offset) { formatted.length }
            }
            override fun transformedToOriginal(offset: Int): Int {
                return mapping.indexOfLast { it <= offset }.coerceAtLeast(0)
            }
        }
        return TransformedText(AnnotatedString(formatted), offsetMapping)
    }
}

fun transformDigits(digits: String): Pair<String, List<Int>> {
    val mapping = mutableListOf<Int>()
    val sb = StringBuilder()
    mapping.add(0)
    for (i in digits.indices) {
        if (i == 2 || i == 4) {
            sb.append("/")
        }
        sb.append(digits[i])
        mapping.add(sb.length)
    }
    return Pair(sb.toString(), mapping)
}

@Composable
fun EventTextField(
    label: String,
    value: String,
    keyboardType: KeyboardType = KeyboardType.Text,
    modifier: Modifier = Modifier
        .fillMaxWidth()
        .padding(vertical = 10.dp),
    onValueChange: (String) -> Unit
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = {
            Text(
                text = label,
                fontSize = 22.sp,
                color = Black,
                fontFamily = FontFamily.Default
            )
        },
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        shape = RoundedCornerShape(20.dp),
        colors = TextFieldDefaults.colors(
            focusedLabelColor = Black,
            unfocusedContainerColor = PurpleContainer,
            cursorColor = Black,
            focusedContainerColor = PurpleContainer,
            unfocusedIndicatorColor = White,
            focusedIndicatorColor = White
        ),
        trailingIcon = {
            Icon(
                Icons.Filled.Create,
                "",
                tint = Purple40,
                modifier = Modifier.padding(horizontal = 30.dp)
            )
        },
        modifier = modifier
    )
}