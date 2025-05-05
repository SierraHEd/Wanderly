/*
    Sign Up Page: Users will enter an email and password, confirmed password, first name, last name, and birthdate.
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
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
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
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.rememberDatePickerState
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
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Popup
import androidx.navigation.NavController
import com.example.csc490group3.model.IndividualUser
import com.example.csc490group3.supabase.DatabaseManagement.addRecord
import com.example.csc490group3.supabase.SupabaseManagement.AuthManagement.auth
import com.example.csc490group3.ui.theme.PurpleBKG
import com.example.csc490group3.ui.theme.PurpleDarkBKG
import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.auth.providers.builtin.Email
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import java.time.format.DateTimeFormatter
import java.time.LocalDate as JLocalDate


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SignUpActivity(navController: NavController) {

// --------------------------
// 1. State Management and Variables
// --------------------------

    var firstName by rememberSaveable { mutableStateOf("") }
    var lastName by rememberSaveable { mutableStateOf("") }
    var birthdate by remember { mutableStateOf<LocalDate?>(null) }
    var email by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    var retypePassword by rememberSaveable { mutableStateOf("") }
    var passwordVisible by rememberSaveable { mutableStateOf(false) }
    var retypePasswordVisible by rememberSaveable { mutableStateOf(false) }
    var isFormValid by rememberSaveable { mutableStateOf(false) }
    val scrollState = rememberScrollState() // Create a scroll state
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current
    val isLoading = remember { mutableStateOf(false) }  // Track the loading state
    // For snackbar feedback
    val snackbarHostState = remember { SnackbarHostState() }
    //Error messages
    var firstNameErrorMessage by rememberSaveable { mutableStateOf("") }
    var lastNameErrorMessage by rememberSaveable { mutableStateOf("") }
    var birthdateErrorMessage by rememberSaveable { mutableStateOf("") }
    var passwordErrorMessage by rememberSaveable { mutableStateOf("") }

// --------------------------
// 2. Validation Functions
// --------------------------

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

    // Check name fields to make sure they are not empty
    fun isNameValid(name: String): Boolean {
        return name.isNotEmpty()
    }

    // Check birthdate field to make sure it is not empty
    fun isBirthdateValid(birthdate: LocalDate?): Boolean {
        return birthdate != null
    }

    fun clearErrorMessages() {
        firstNameErrorMessage = ""
        lastNameErrorMessage = ""
        birthdateErrorMessage = ""
    }


// --------------------------
// 3. Helper Functions
// --------------------------

    //Function to Call to the Supabase Client and make a new account
    suspend fun signUpNewUser(
        email: String,
        password: String,
        supabase: Auth,
        context: Context,
        navController: NavController
    ) {
        try {
            auth.signUpWith(Email) {
                this.email = email
                this.password = password
            }
            val newUser = IndividualUser(

                email = email,
                firstName = firstName,
                lastName = lastName,
                birthday = birthdate!!,
                public = true
            )
            addRecord("private_users", newUser)
            snackbarHostState.showSnackbar("Sign up successful! Loading Log in Page...")
            navController.navigate("User_Login_Screen")

        } catch (e: Exception) {
            // Handle any exceptions that might occur during the sign-up process
            Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }

    //Function to format date to be more user friendly.
    val dateFormatter = DateTimeFormatter.ofPattern("MMMM d, yyyy")

    // Convert milliseconds to LocalDate so DatePicker can display date back to user
    fun convertMillisToLocalDate(millis: Long): LocalDate {
        return Instant.fromEpochMilliseconds(millis)
            .toLocalDateTime(TimeZone.UTC) // Convert to LocalDateTime using UTC
            .date // Extract the LocalDate part
    }


// --------------------------
// 4. DatePicker
// --------------------------
    // DatePicker Dialog state
    val datePickerState = rememberDatePickerState()
    var isDatePickerVisible by remember { mutableStateOf(false) }

    // Function to show the DatePicker dialog
    fun showDatePicker() {
        isDatePickerVisible = true
    }

    //DatePicker popup - when user clicks birthdate textfield -> isDatePickerVisible = true -> Popup shows
    if (isDatePickerVisible) {
        Popup(
            alignment = Alignment.Center,
            onDismissRequest = { isDatePickerVisible = false } //closes popup
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()  // Take full width
                    .fillMaxHeight(0.8f)  // height to 80% of screen
                    .padding(16.dp)  // Add padding inside the Popup
                    .verticalScroll(rememberScrollState())  // Make it scrollable vertically
                    .background(PurpleBKG) //Keep color theme to PurpleBKG
                    .border(2.dp, Color.Black)  // Border for visual clarity
            ) {
                DatePicker(
                    state = datePickerState,
                    colors = DatePickerDefaults.colors(
                        containerColor = PurpleBKG //Keep color theme to PurpleBKG
                    ),
                    modifier = Modifier.height(540.dp) // Add height so picker is easier to read
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(  // Row for Confirm and Cancel buttons side by side
                    horizontalArrangement = Arrangement.spacedBy(30.dp),  // Space between buttons
                    modifier = Modifier.fillMaxWidth()
                ) {
                    // Cancel Button
                    TextButton(
                        onClick = {
                            isDatePickerVisible = false
                        },
                        modifier = Modifier
                            .weight(1f)
                            .background(Color.Red)
                    ) {
                        Text(
                            text = "Cancel",
                            color = Color.Black,
                            modifier = Modifier.padding(8.dp)
                        )
                    }
                    // Confirm Button
                    TextButton(
                        onClick = {
                            //Convert selected date to a displayable format
                            datePickerState.selectedDateMillis?.let { selectedDateMillis ->
                                birthdate = convertMillisToLocalDate(selectedDateMillis)
                            }
                            isDatePickerVisible = false
                        },
                        modifier = Modifier
                            .weight(1f)
                            .background(PurpleDarkBKG)
                    ) {
                        Text(
                            text = "Confirm",
                            color = Color.White,
                            modifier = Modifier.padding(8.dp)
                        )
                    }
                }
            }
        }
    }

// --------------------------
// 5. Launched Effects Functions
// --------------------------

    // LaunchedEffect for triggering snackbar error message as user updates fields
    LaunchedEffect(passwordErrorMessage) {
        if (passwordErrorMessage.isNotEmpty()) {
            snackbarHostState.showSnackbar(passwordErrorMessage)
        }
    }

// LaunchedEffect to check that email, passwords, and other fields pass validation as user updates fields
    LaunchedEffect(password, retypePassword, email, firstName, lastName, birthdate) {
        // Trigger validation on password, retype password, and email fields
        val isPasswordValid =
            validatePasswordComplexityAndLength(password) && checkPasswordsMatch(
                password,
                retypePassword
            )
        val isEmailValid = isEmailValid(email)

        // Update isFormValid when all conditions are satisfied -> enables button to sign up
        isFormValid = email.isNotEmpty() && password.isNotEmpty() && retypePassword.isNotEmpty() &&
                isPasswordValid && isEmailValid
    }

// --------------------------
// 6. Main UI/Form
// --------------------------
    Box(modifier = Modifier.fillMaxSize()
        .padding(WindowInsets.navigationBars.asPaddingValues()))
        {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(PurpleBKG)
                .verticalScroll(rememberScrollState()), // Make the Column scrollable
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        )
        {
            //Logo
            Image(
                painter = painterResource(id = R.drawable.app_logo),
                contentDescription = ""
            )
            //Activity/Page Identifier
            Text(
                text = "Sign Up",
                fontSize = 24.sp,
                modifier = Modifier.padding(2.dp)
            )
            //password Rules
            Text(
                text = "Passwords must have a minimum 5 characters, at least one number, at least one capital letter",
                fontSize = 10.sp,
                modifier = Modifier.padding(2.dp)
            )

            //Email section
            TextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Enter your email...") },
                modifier = Modifier
                    .width(400.dp)
                    .padding(15.dp)
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
                    style = TextStyle(fontSize = 12.sp),
                    modifier = Modifier.padding(horizontal = 20.dp)
                )
            }

            //Password section
            TextField(
                value = password,
                onValueChange = {
                    password = it
                    coroutineScope.launch {
                        //Provide delay before evaluating user input
                        delay(500)
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
                    //Create visibility icon so users can choose to see or not see password
                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Icon(imageVector = image, description)
                    }
                },
                label = { Text("Enter your password...") },
                modifier = Modifier
                    .width(400.dp)
                    .padding(15.dp)
                    .border(
                        width = 3.dp,
                        color = when { //Change border colors if error
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
                        //Provide delay before evaluating user input
                        delay(500)
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
                    val description =
                        if (retypePasswordVisible) "Hide password" else "Show password"

                    IconButton(onClick = { retypePasswordVisible = !retypePasswordVisible }) {
                        Icon(imageVector = image, description)
                    }
                },
                label = { Text("Confirm your password...") },
                modifier = Modifier
                    .width(400.dp)
                    .padding(15.dp)
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

            // First Name Section
            TextField(
                value = firstName,
                onValueChange = { firstName = it },
                label = { Text("Enter your first name...") },
                modifier = Modifier
                    .width(400.dp)
                    .padding(15.dp)
                    .border(
                        width = 3.dp, color = Color.Black
                    )
            )

            if (firstNameErrorMessage.isNotEmpty()) {
                Text(
                    text = firstNameErrorMessage,
                    color = Color.Red,
                    modifier = Modifier.padding(horizontal = 20.dp)
                )
            }

            // Last Name Section
            TextField(
                value = lastName,
                onValueChange = { lastName = it },
                label = { Text("Enter your last name...") },
                modifier = Modifier
                    .width(400.dp)
                    .padding(15.dp)
                    .border(
                        width = 3.dp, color = Color.Black
                    )
            )
            if (lastNameErrorMessage.isNotEmpty()) {
                Text(
                    text = lastNameErrorMessage,
                    color = Color.Red,
                    modifier = Modifier.padding(horizontal = 20.dp)
                )
            }

            //Birthdate Section
            TextField(
                value = birthdate?.let {
                    // Convert LocalDate to java.time.LocalDate for formatting and user readability
                    val javaLocalDate = JLocalDate.of(it.year, it.monthNumber, it.dayOfMonth)
                    javaLocalDate.format(dateFormatter)
                } ?: "Select your birthdate",
                onValueChange = {},
                label = { Text("Click here to select your birthdate") },
                readOnly = true,
                modifier = Modifier
                    .width(400.dp)
                    .padding(15.dp)
                    .border(
                        width = 3.dp, color = Color.Black
                    )
                    .clickable {
                        showDatePicker()
                    },
                enabled = false
            )
            if (birthdateErrorMessage.isNotEmpty()) {
                Text(
                    text = birthdateErrorMessage,
                    color = Color.Red,
                    modifier = Modifier.padding(horizontal = 20.dp)
                )
            }

            //Create Account button - Checks textfields for validation then submits to supabase
            Button(
                onClick = {
                    //Clear errors if another attempt is made to submit
                    clearErrorMessages()

                    // Validate fields when the user tries to submit
                    val isFirstNameValid = isNameValid(firstName)
                    val isLastNameValid = isNameValid(lastName)
                    val isBirthdateValid = isBirthdateValid(birthdate)

                    //Display errors if fields are left empty
                    if (!(isFirstNameValid)) {
                        firstNameErrorMessage = "Please fill in first name"
                    }
                    if (!(isLastNameValid)) {
                        lastNameErrorMessage = "Please fill in last name"
                    }
                    if (!(isBirthdateValid)) {
                        birthdateErrorMessage = "Please fill in birthdate"
                    }

                    // Check if all fields are valid
                    if (isFirstNameValid && isLastNameValid && isBirthdateValid && email.isNotEmpty() && password.isNotEmpty() && retypePassword.isNotEmpty()) {
                        // Proceed with sign-up if Passwords meet requirements
                        val isPasswordValid = validatePasswordComplexityAndLength(password)
                        val passwordsAreValid = checkPasswordsMatch(password, retypePassword)

                        // If password validation passes and passwords match
                        if (isPasswordValid && passwordsAreValid) {
                            // Proceed to sign up the user
                            isLoading.value = true
                            coroutineScope.launch {
                                signUpNewUser(email, password, auth, context, navController)
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
                        Toast.makeText(context, "Please fill in all fields", Toast.LENGTH_LONG)
                            .show()
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
                        //nav bar or view change to go to Login activity
                        .clickable(onClick = {
                            println("Link clicked, going to login")
                            navController.navigate("User_Login_Screen")
                        })
                        .padding(start = 4.dp),
                )
            }
        }
        // Snackbar (Pop-up message) for feedback
        if (snackbarHostState.currentSnackbarData != null) {
            SnackbarHost(
                hostState = snackbarHostState,
                modifier = Modifier
                    .align(Alignment.BottomCenter) // Align Snackbar to the bottom center of the screen
            )
        }
    }
}