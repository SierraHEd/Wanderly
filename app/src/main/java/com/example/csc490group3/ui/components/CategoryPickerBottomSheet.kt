package com.example.csc490group3.ui.components

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.ui.unit.max
import androidx.compose.ui.unit.sp
import com.example.csc490group3.model.Category
import com.example.csc490group3.model.User
import com.example.csc490group3.model.UserSession
import com.example.csc490group3.supabase.DatabaseManagement.addCategoryRelationship
import com.example.csc490group3.supabase.DatabaseManagement.deleteCategories
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoryPickerBottomSheet(
    showSheet: Boolean,
    onDismiss: () -> Unit,
    onSelectionDone:(List<Category>) -> Unit,
    maxSelections: Int = 25,
    initialSelectedCategories: List<Category> = emptyList(),
    updateCategories: Boolean = false,
    userCategories: Boolean = false,
    eventCategories:Boolean = false
) {
    val categories = listOf(
        Category(id = 1, name = "Music"),
        Category(id = 2, name = "Business & Professional"),
        Category(id = 3, name = "Food & Drink"),
        Category(id = 4, name = "Community & Culture"),
        Category(id = 5, name = "Performing & Visual Arts"),
        Category(id = 6, name = "Film, Media & Entertainment"),
        Category(id = 7, name = "Sports & Fitness"),
        Category(id = 8, name = "Health and Wellness"),
        Category(id = 9, name = "Science & Technology"),
        Category(id = 10, name = "Travel & Outdoor"),
        Category(id = 11, name = "Charity & Causes"),
        Category(id = 12, name = "Religion & Spirituality"),
        Category(id = 13, name = "Family & Education"),
        Category(id = 14, name = "Seasonal & Holiday"),
        Category(id = 15, name = "Government & Politics"),
        Category(id = 16, name = "Fashion & Beauty"),
        Category(id = 17, name = "Home & Lifestyle"),
        Category(id = 18, name = "Auto, Boat & Air"),
        Category(id = 19, name = "Hobbies & Special Interest"),
        Category(id = 20, name = "School Activities"),
        Category(id = 21, name = "Other")
    )
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current
    var selectedCategories by remember(initialSelectedCategories) {mutableStateOf(initialSelectedCategories.toList())}


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
                                        if(selectedCategories.size < maxSelections){
                                            selectedCategories + category
                                        }
                                        else{
                                            Toast
                                                .makeText(context, "Maximum of $maxSelections selections reached", Toast.LENGTH_SHORT)
                                                .show()
                                            selectedCategories
                                        }
                                    }
                                }
                                .padding(vertical = 8.dp),
                            verticalAlignment =  Alignment.CenterVertically
                        ){
                            Checkbox(
                                checked = selectedCategories.contains(category),
                                onCheckedChange = {isChecked ->
                                    selectedCategories = if(isChecked){
                                        if (selectedCategories.size < maxSelections){
                                            selectedCategories + category
                                        }
                                        else{
                                            Toast
                                                .makeText(context, "Maximum of $maxSelections selections reached", Toast.LENGTH_SHORT)
                                                .show()
                                            selectedCategories
                                        }
                                    }
                                    else{
                                        selectedCategories - category
                                    }

                                }
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = category.name,
                                fontSize = 20.sp,
                                fontFamily = FontFamily.Default
                            )
                        }
                    }
                }
                Button(
                    onClick = {
                        coroutineScope.launch {
                            if(updateCategories){
                                if(userCategories){
                                    UserSession.currentUser?.id?.let { deleteCategories(it,"user_categories") }
                                    UserSession.currentUser?.id?.let {
                                        addCategoryRelationship(selectedCategories, "user_categories",
                                            it
                                        )
                                    }
                                    UserSession.currentUserCategory = selectedCategories
                                }
                                if(eventCategories){

                                }
                            }
                            sheetState.hide() }
                        onSelectionDone(selectedCategories)
                        onDismiss()

                    },
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(top = 8.dp)
                ) {
                    Text("Done")
                }
            }

        }

    }
}