package com.example.csc490group3

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.csc490group3.data.ButtonComponent
import com.example.csc490group3.data.ImageCarousel
import com.example.csc490group3.data.ImageComponent
import com.example.csc490group3.data.NormalTextComponent
import com.example.csc490group3.ui.theme.PurpleStart

@Composable
fun StartUpScreen(navController: NavController) {
    Surface (modifier = Modifier.fillMaxSize()
        .background(PurpleStart)
        .padding(28.dp)){
        Column (modifier = Modifier.background(PurpleStart)){
            ImageComponent()
            ImageCarousel()
            ButtonComponent("Login", { navController.navigate("User_login_Screen") }, true);
            Spacer(modifier = Modifier.height(12.dp))
            NormalTextComponent("Don't Have an Account?")
            Spacer(modifier = Modifier.height(8.dp))
            ButtonComponent("Sign Up", {navController.navigate("sign_up_screen")}, true);
        }
    }
}

