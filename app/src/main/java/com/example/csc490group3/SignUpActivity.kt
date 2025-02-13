package com.example.csc490group3

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.csc490group3.ui.theme.*

@Composable
fun SignUpActivity(modifier: Modifier = Modifier) {
    var username by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var retypePassword by remember { mutableStateOf("")}
    var passwordsMatch by remember { mutableStateOf(true)}

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Purple40),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    )
    {
        //TITLE Can Swap in image for Logo or design
        Text(
            text = "Wanderly", fontSize = 32.sp, modifier = Modifier.padding(5.dp),
        )
        //Catch Phrase
        Text(//Can Swap in image for Logo or design
                text = "Do More", fontSize = 18.sp,
        )
        //Activity Identifier
        Text(
            text = "Sign Up",
            fontSize = 24.sp,
            modifier = Modifier.padding(20.dp)
        )
        //Username section
        TextField(
            value = username,
            onValueChange = { username = it },
            label = { Text("Enter your username...") },
            modifier = Modifier
                .width(400.dp)
                .padding(20.dp)
        )
        //Email section
        TextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Enter your email...") },
            modifier = Modifier
                .width(400.dp)
                .padding(20.dp)
        )
        //Password section
        TextField(
            value = password,
            onValueChange = { password = it },
            visualTransformation = PasswordVisualTransformation(),
            label = { Text("Enter your password...") },
            modifier = Modifier
                .width(400.dp)
                .padding(20.dp)
        )
        //Confirm password section
        TextField(
                value = retypePassword,
        onValueChange = { retypePassword = it },
        visualTransformation = PasswordVisualTransformation(),
        label = { Text("Confirm your password...") },
        modifier = Modifier
            .width(400.dp)
            .padding(20.dp),
            isError = !passwordsMatch
        )
        // Error message for password mismatch
        if (!passwordsMatch) {
            Text(
                text = "Passwords do not match",
                color = Color.Red,
                modifier = Modifier.padding(horizontal = 20.dp)
            )
        }
        //Create Account button
        Button(
            onClick = {
                if (email.isNotEmpty() && password.isNotEmpty()) {
                    passwordsMatch = password == retypePassword
                    //Continue validation
                    //If successful bind and submit to Firebase DB then transfer to Next page
                }
            },
            modifier = Modifier
                .width(150.dp),
            // Can use a custom color defined in the theme for the button for uniformity
            colors = ButtonDefaults.buttonColors(containerColor = Color.Black),
        ) {
            Text("Create Account", color = Color.White)
        }
        //Link to return to Login Page if already registered
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(vertical = 8.dp)
        ) {
            Text(text = "Already have an account?", modifier = Modifier.padding(end = 4.dp))
            // Clickable Text for Login
            Text(
                text = "Login",
                color = Color.Blue,
                textDecoration = TextDecoration.Underline,
                modifier = Modifier
                //.clickable(onClick = { }) ready for nav bar or view change to go to Login activity
                    .padding(start = 4.dp),
            )
        }
    }
}

@Composable
@Preview
fun SeePreview() {
    SignUpActivity()
}