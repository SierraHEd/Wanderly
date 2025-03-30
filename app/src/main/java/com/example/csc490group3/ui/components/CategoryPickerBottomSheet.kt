package com.example.csc490group3.ui.components

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoryPickerBottomSheet(
    showSheet: Boolean,
    onDismiss: () -> Unit,
    onSelectionDone:(List<String>) -> Unit
) {
    val categories = arrayOf("Music","Business & Professional","Food & Drink",
        "Community & Culture","Performing & Visual Arts","Film, Media & Entertainment","Sports & Fitness","Health and Wellness",
        "Science & Technology","Travel & Outdoor","Charity & Causes","Religion & Spirituality","Family & Education","Seasonal & Holiday","Government & Politics",
        "Fashion & Beauty", "Home & Lifestyle", "Auto, Boat & Air", "Hobbies & Special Interest","School Activities", "Other")
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current

    var selectedCategories by remember { mutableStateOf(emptyList<String>()) }

    if (showSheet) {
        ModalBottomSheet(
            onDismissRequest = onDismiss,
            sheetState = sheetState
        ) {
            Box(
                modifier = Modifier.fillMaxWidth()
                    .heightIn(min = 200.dp, max = 400.dp)
                    .padding(16.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                        .verticalScroll(rememberScrollState())
                ) {
                    Text("Select Your Categories", style = MaterialTheme.typography.headlineSmall)
                    Spacer(modifier = Modifier.height(8.dp))

                    categories.forEach { category ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    selectedCategories = if(selectedCategories.contains(category)) {
                                        selectedCategories - category
                                    }else {
                                        selectedCategories + category
                                    }
                                }
                                .padding(vertical = 8.dp),
                            verticalAlignment =  Alignment.CenterVertically
                        ){
                            Checkbox(
                                checked = selectedCategories.contains(category),
                                onCheckedChange = {isChecked ->
                                    selectedCategories = if(isChecked){
                                        selectedCategories + category
                                    }else{
                                        selectedCategories - category
                                    }

                                }
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = category,
                                fontSize = 20.sp,
                                fontFamily = FontFamily.Default
                            )
                        }
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Button(
                        onClick = {
                            Toast.makeText(context, "Selected: $selectedCategories", Toast.LENGTH_SHORT).show()
                            coroutineScope.launch {sheetState.hide()}
                            onSelectionDone(selectedCategories)
                            onDismiss()
                        },
                        modifier = Modifier.align(Alignment.End)
                    ){
                        Text("Done")
                    }
                }
            }

        }

    }
}