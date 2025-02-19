package com.example.csc490group3

import android.view.animation.OvershootInterpolator
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.csc490group3.data.ButtonComponent
import com.example.csc490group3.data.ImageComponent
import com.example.csc490group3.data.NormalTextComponent
import com.example.csc490group3.ui.theme.Purple40
import kotlinx.coroutines.delay

@Composable
fun StartUpScreen(navController: NavController) {
    Surface (modifier = Modifier.fillMaxSize()
        .background(Purple40)
        .padding(28.dp)){
        Column (modifier = Modifier.background(Purple40)){
            ImageComponent()
            Spacer(modifier = Modifier.height(20.dp))
            ButtonComponent("Login", { navController.navigate("login_screen") }, true);
            Spacer(modifier = Modifier.height(12.dp))
            NormalTextComponent("Don't Have an Account?")
            Spacer(modifier = Modifier.height(8.dp))
            ButtonComponent("Sign Up", {navController.navigate("sign_up_screen")}, true);
        }
    }
}

