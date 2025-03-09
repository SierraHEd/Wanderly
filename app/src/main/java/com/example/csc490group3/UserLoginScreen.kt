package com.example.csc490group3

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.csc490group3.model.UserSession
import com.example.csc490group3.supabase.AuthManagement.accountValidation
import com.example.csc490group3.supabase.AuthManagement.getActiveUser
import com.example.csc490group3.supabase.DatabaseManagement.getPrivateUser
import com.example.csc490group3.ui.theme.PurpleBKG
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

@Composable
fun UserLoginScreen(navController: NavController) {

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    val coroutineScope = rememberCoroutineScope()

    Box(
        modifier = Modifier
            .fillMaxSize()
            //ARGB color code
            .background(PurpleBKG)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {//back button icon / logic
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(Icons.Filled.ArrowBack, contentDescription = "Back", tint = Color.Black)
                }
                Text("Back", color = Color.Black, fontSize = 16.sp)
            }

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = "Log in",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
            //maybe replace with actual logo & stuff later
            Text(
                text = "Wanderly",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
            Text(
                text = "DO MORE",
                fontSize = 16.sp,
                color = Color.Black
            )

            Spacer(modifier = Modifier.height(24.dp))

            Card(
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Text fields missing shadow/ stroke/ outline
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Text("Email", fontSize = 14.sp, color = Color.Black)
                        OutlinedTextField(
                            value = email,
                            onValueChange = { email = it },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Text("Password", fontSize = 14.sp, color = Color.Black)
                        OutlinedTextField(
                            value = password,
                            onValueChange = { password = it },
                            visualTransformation = PasswordVisualTransformation(),
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    //sign up button once clicked should go to home page
                    Button(

                        onClick = {
                            coroutineScope.launch {
                                val validAccount = accountValidation(email, password)
                                if(validAccount) {
                                    UserSession.currentUser = getPrivateUser(email)
                                    navController.navigate("Home_Screen")
                                }
                                else {
                                    println("Authentication error")
                                }
                            }

                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Black)
                    ) {
                        Text("Sign In", color = Color.White, fontSize = 16.sp)
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            /*doesn't do anything yet needs to have a forgot password page first*/
            Text(
                text = "Forgot password?",
                color = Color.Black,
                fontSize = 14.sp,
                modifier = Modifier.clickable {/*forgot password page goes here */  }
            )
        }
    }
}
