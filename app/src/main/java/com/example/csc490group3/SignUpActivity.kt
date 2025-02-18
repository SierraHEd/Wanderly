/*
    Sign Up Page: Users will enter an email and password, and a confirmed password.
    Users will then click the "create account" button to make a new account.
    The email can not already be in our database and the information entered must pass our validation.
    If validation passes users can hit the "create account" button and a new account will be added to the database.
    Users will then be directed to the Log In screen.
    Users can log in from the "Log In" screen using those credentials for as long as the account is active.
 */

package com.example.csc490group3

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.csc490group3.ui.theme.Purple40
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import com.google.firebase.auth.FirebaseAuth

@Composable
fun SignUpActivity(navController: NavController) {
//    var username by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var retypePassword by remember { mutableStateOf("") }
    var passwordsMatch by remember { mutableStateOf(true) }
    var passwordErrorMessage by remember { mutableStateOf("") }
    var passwordVisible by rememberSaveable { mutableStateOf(false) }
    var retypePasswordVisible by remember { mutableStateOf(false) }
    var isFormValid by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()
    val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()

    // For snackbar feedback
    val snackbarHostState = remember { SnackbarHostState() }

    // Helper function to validate password
    fun isPasswordValid(password: String): Boolean {
        val hasUpperCase = password.any { it.isUpperCase() } // at least one capital letter
        val hasNumber = password.any { it.isDigit() } // at least one number
        return password.length >= 5 && hasUpperCase && hasNumber //Minimum 5 characters
    }

    // Validating the email field
    fun isEmailValid(email: String): Boolean {
        return email.contains("@") // Simple check for an '@' symbol in the email
    }

    // Validating password on submit and when user types into password textfields
    fun validatePassword(): Boolean {
        return when {
            password != retypePassword -> {
                passwordsMatch = false
                passwordErrorMessage = "Passwords do not match"
                false
            }

            password.length < 5 -> {
                passwordsMatch = true
                passwordErrorMessage = "Password must be at least 5 characters long"
                false
            }

            !isPasswordValid(password) -> {
                passwordsMatch = true
                passwordErrorMessage =
                    "Password must be at least 5 characters long, contain one capital letter, and one number."
                false
            }

            else -> {
                passwordsMatch = true
                passwordErrorMessage = ""
                true
            }
        }
    }

    // LaunchedEffect for triggering snackbar message
    LaunchedEffect(passwordErrorMessage) {
        if (passwordErrorMessage.isNotEmpty()) {
            snackbarHostState.showSnackbar(passwordErrorMessage)
        }
    }
    // Launched effect to check that email and passwords pass validation
    LaunchedEffect(password, retypePassword, email) {
        isFormValid = password.isNotEmpty() && retypePassword.isNotEmpty() && email.isNotEmpty() &&
                validatePassword() && isEmailValid(email) && passwordsMatch
    }

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
        Text(
        //Can Swap in image for Logo or design
            text = "Do More", fontSize = 18.sp,
        )
        //Activity/Page Identifier
        Text(
            text = "Sign Up",
            fontSize = 24.sp,
            modifier = Modifier.padding(20.dp)
        )
        //DO WE NEED OR WANT A USERNAME ON SIGN UP?
//        //Username section
//        TextField(
//            value = username,
//            onValueChange = { username = it },
//            label = { Text("Enter your username...") },
//            modifier = Modifier
//                .width(400.dp)
//                .padding(20.dp)
//                .border(width = 3.dp, color = Color.Black),
//        )
        //Email section
        TextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Enter your email...") },
            modifier = Modifier
                .width(400.dp)
                .padding(20.dp)
                .border(
                    width = 3.dp,
                    color = if (email.isEmpty()) Color.Black else if (isEmailValid(email)) Color.Green else Color.Red
                ), // Change color logic
        )
        // Error message for password mismatch
        if (!isEmailValid(email)) {
            Text(
                text = "Email must have a proper format including @ symbol",
                color = Color.Red,
                modifier = Modifier.padding(horizontal = 20.dp)
            )
        }
        //Password section
        TextField(
            value = password,
            onValueChange = {
                password = it
                coroutineScope.launch {
                    delay(500) // Delay for debouncing (e.g., 500ms)
                    validatePassword() // Live Checking if password passes validation
                }
            },
            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            trailingIcon = {
                val image = if (passwordVisible)
                    Icons.Filled.Visibility
                else Icons.Filled.VisibilityOff

                // localized description for accessibility services
                val description = if (passwordVisible) "Hide password" else "Show password"

                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                    Icon(imageVector = image, description)
                }
            },
            label = { Text("Enter your password...") },
            modifier = Modifier
                .width(400.dp)
                .padding(20.dp)
                .border(
                    width = 3.dp,
                    color = if (password.isEmpty()) Color.Black else if (validatePassword()) Color.Green else Color.Red
                ),
        )

        if (!passwordsMatch || password.length < 5 || !isPasswordValid(password)) {
            Text(
                text = passwordErrorMessage,
                color = Color.Red,
                modifier = Modifier.padding(start = 20.dp)
            )
        }

        //Confirm password section
        TextField(
            value = retypePassword,
            onValueChange = {
                retypePassword = it
                coroutineScope.launch {
                    delay(500) // Delay for debouncing (e.g., 500ms)
                    validatePassword()// Live Checking if password passes validation
                }
            },
            visualTransformation = if (retypePasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            trailingIcon = {
                val image = if (retypePasswordVisible)
                    Icons.Filled.Visibility
                else Icons.Filled.VisibilityOff

                //localized description for accessibility services
                val description = if (retypePasswordVisible) "Hide password" else "Show password"

                IconButton(onClick = { retypePasswordVisible = !retypePasswordVisible }) {
                    Icon(imageVector = image, description)
                }
            },
            label = { Text("Confirm your password...") },
            modifier = Modifier
                .width(400.dp)
                .padding(20.dp)
                .border(
                    width = 3.dp,
                    color = if (password.isEmpty()) Color.Black else if (validatePassword()) Color.Green else Color.Red
                ),
            isError = !passwordsMatch,
        )
        // Error message for password mismatch
        if (!passwordsMatch) {
            Text(
                text = "Passwords do not match",
                color = Color.Red,
                modifier = Modifier.padding(horizontal = 20.dp)
            )
        }
        val context = LocalContext.current
        //Create Account button - Checks textfields for validation then submits to firebase
        Button(
            onClick = {
                if (email.isNotEmpty() && password.isNotEmpty()) {
                    val isValid = validatePassword()
                    if (isValid) {
                        //Continue validation
                        //If successful, bind and submit to Firebase DB then transfer to Next page
                        firebaseAuth.createUserWithEmailAndPassword(email, password)
                            .addOnCompleteListener { task ->
                                if (task.isSuccessful) {
                                    Toast.makeText(
                                        context,
                                        "Sign up Successful",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    // Proceed to the next screen (e.g., home page)
                                } else {
                                    Toast.makeText(
                                        context,
                                        "Sign up failed: ${task.exception?.message}",
                                        Toast.LENGTH_LONG
                                    ).show()
                                }
                            }
                    }else{
                        Toast.makeText(
                            context,"Sign up failed",Toast.LENGTH_LONG).show()
                    }
                }
            },
            modifier = Modifier
                .width(150.dp),
            //Button will not be clickable unless all fields are completed correctly.
            enabled = isFormValid,
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
        // Snackbar (Pop up message) for error feedback
        SnackbarHost(hostState = snackbarHostState)
    }
}

//Preview not currently working due to firebase/snackbar functions that can not be previewed.
@Composable
@Preview
fun SeePreview() {
    SignUpActivity(navController = rememberNavController())
}