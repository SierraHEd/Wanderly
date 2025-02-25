/*
    Sign Up Page: Users will enter an email and password, and a confirmed password.
    Users will then click the "create account" button to make a new account.
    The email can not already be in our database and the information entered must pass our validation.
    If validation passes users can hit the "create account" button and a new account will be added to the database.
    Users will then be directed to the Log In screen.
    Users can log in from the "Log In" screen using those credentials for as long as the account is active.
 */

package com.example.csc490group3

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.res.painterResource
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
import com.example.csc490group3.ui.theme.PurpleBKG
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.providers.builtin.Email
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


@Composable
fun SignUpActivity(navController: NavController) {
    var email by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    var retypePassword by rememberSaveable { mutableStateOf("") }
    var passwordErrorMessage by rememberSaveable { mutableStateOf("") }
    var passwordVisible by rememberSaveable { mutableStateOf(false) }
    var retypePasswordVisible by rememberSaveable { mutableStateOf(false) }
    var isFormValid by rememberSaveable { mutableStateOf(false) }
    val scrollState = rememberScrollState() // Create a scroll state
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current

    // For snackbar feedback
    val snackbarHostState = remember { SnackbarHostState() }

    //Create Supabase Client
    val supabase = createSupabaseClient(
        supabaseUrl = "https://bngtgtuhiycwahsknuqh.supabase.co/",
        supabaseKey = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6ImJuZ3RndHVoaXljd2Foc2tudXFoIiwicm9sZSI6ImFub24iLCJpYXQiOjE3Mzk5NDEwMDAsImV4cCI6MjA1NTUxNzAwMH0.9h6ZJ-sfIH6Le0_AuL3ExHM2E2gaJbEc95UWVV4k-d0"
    ) {
        install(Auth)
        install(Postgrest)
    }

    //Function to Call to the Supabase Client and make a new account
    suspend fun signUpNewUser(
        email: String,
        password: String,
        supabase: SupabaseClient,
        context: Context,
        navController: NavController
    ) {
        try {
            supabase.auth.signUpWith(Email) {
                this.email = email
                this.password = password
            }
            snackbarHostState.showSnackbar("Sign up successful!")
            navController.navigate("User_Login_Screen")

        } catch (e: Exception) {
            // Handle any exceptions that might occur during the sign-up process
            Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }

    // Validating the email field
    fun isEmailValid(email: String): Boolean {
        // Regex for validating a simple email structure
        val emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$"
        return email.matches(emailRegex.toRegex())
    }

    // Helper function to validate password length and complexity
    fun validatePasswordComplexityAndLength(password: String): Boolean {
        val hasUpperCase = password.any { it.isUpperCase() } // at least one capital letter
        val hasNumber = password.any { it.isDigit() } // at least one number
        return password.length >= 5 && hasUpperCase && hasNumber //Minimum 5 characters
    }

    // Helper function to check if passwords match
    fun checkPasswordsMatch(password: String, retypePassword: String): Boolean {
        return password == retypePassword
    }

    // LaunchedEffect for triggering snackbar message
    LaunchedEffect(passwordErrorMessage) {
        if (passwordErrorMessage.isNotEmpty()) {
            snackbarHostState.showSnackbar(passwordErrorMessage)
        }
    }

// LaunchedEffect to check that email and passwords pass validation
    LaunchedEffect(password, retypePassword, email) {
        // Trigger validation on password, retype password, and email fields
        val isPasswordValid = validatePasswordComplexityAndLength(password) && checkPasswordsMatch(
            password,
            retypePassword
        )
        val isEmailValid = isEmailValid(email)

        // Update isFormValid when all conditions are satisfied
        isFormValid = email.isNotEmpty() && password.isNotEmpty() && retypePassword.isNotEmpty() &&
                isPasswordValid && isEmailValid
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(PurpleBKG)
            .verticalScroll(scrollState), // Make the Column scrollable

        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    )
    {
        Spacer(modifier = Modifier.height(50.dp)) // Spacer to create space before the title

        //Logo
        Image(
            painter = painterResource(id = R.drawable.app_logo),
            contentDescription =""
        )
        //Activity/Page Identifier
        Text(
            text = "Sign Up",
            fontSize = 24.sp,
            modifier = Modifier.padding(10.dp)
        )

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
                    //Change color of outline if empty, correct, or incorrect
                    color = when {
                        email.isEmpty() -> Color.Black
                        isEmailValid(email) -> Color.Green
                        else -> Color.Red
                    }
                )
        )
        // Displaying error message only when email is invalid and when the user starts typing
        if (email.isNotEmpty() && !isEmailValid(email)) {
            Text(
                text = "Email must have a proper format EX: johndoe@email.com",
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
                    passwordErrorMessage =
                        if (validatePasswordComplexityAndLength(password)) "" else "Password must be at least 5 characters long, contain one capital letter, and one number."
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
                    color = when {
                        password.isEmpty() -> Color.Black
                        validatePasswordComplexityAndLength(password) -> Color.Green
                        else -> Color.Red
                    }
                ),
        )

        //Confirm password section
        TextField(
            value = retypePassword,
            onValueChange = {
                retypePassword = it
                coroutineScope.launch {
                    delay(500) // Delay for debouncing (e.g., 500ms)

                    // Check if passwords match
                    val passwordsAreMatching = checkPasswordsMatch(password, retypePassword)
                    passwordErrorMessage = when {
                        !passwordsAreMatching -> "Passwords do not match"
                        !validatePasswordComplexityAndLength(retypePassword) -> "Password must be at least 5 characters long, contain one capital letter, and one number."
                        else -> ""
                    }
                }
            },
            visualTransformation = if (retypePasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            trailingIcon = {
                val image = if (retypePasswordVisible)
                    Icons.Filled.Visibility
                else Icons.Filled.VisibilityOff

                // localized description for accessibility services
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
                    color = when {
                        retypePassword.isEmpty() -> Color.Black
                        password != retypePassword -> Color.Red
                        validatePasswordComplexityAndLength(retypePassword) -> Color.Green
                        else -> Color.Red
                    }
                ),
        )

        //Create Account button - Checks textfields for validation then submits to firebase
        Button(
            onClick = {
                if (email.isNotEmpty() && password.isNotEmpty() && retypePassword.isNotEmpty()) {
                    val isPasswordValid = validatePasswordComplexityAndLength(password)
                    val passwordsAreValid = checkPasswordsMatch(password, retypePassword)

                    // If password validation passes and passwords match
                    if (isPasswordValid && passwordsAreValid) {
                        // Proceed to sign up the user
                        coroutineScope.launch {
                            signUpNewUser(email, password, supabase, context, navController)
                        }
                    } else {
                        // Show failure toast if validation fails
                        Toast.makeText(
                            context,
                            "Sign up failed: Invalid form data",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                } else {
                    // Show failure toast if required fields are empty
                    Toast.makeText(context, "Please fill in all fields", Toast.LENGTH_LONG).show()
                }
            },
            modifier = Modifier
                .width(150.dp),
            enabled = isFormValid, // Button will be enabled only if the form is valid
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
//                 ready for nav bar or view change to go to Login activity
                   .clickable(onClick = { navController.navigate("User_Login_Screen") })
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