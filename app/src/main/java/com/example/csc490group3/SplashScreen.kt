package com.example.csc490group3

import android.view.animation.OvershootInterpolator
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.navigation.NavController
import com.example.csc490group3.ui.theme.Purple40
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(navController : NavController) {
    val scale= remember {
        Animatable(0f, 1f)
    }
    LaunchedEffect(key1 = true) {
        scale.animateTo(
            targetValue = 0.5f,
            animationSpec = tween(durationMillis = 1000,0, easing = {
                OvershootInterpolator(2f).getInterpolation(it)
            }
            ))
        delay(3000)
        navController.navigate("login_screen")
    }
    Column (modifier = Modifier
        .fillMaxSize()
        .background(Purple40)
        .wrapContentSize(Alignment.Center)){
        Image(
            painter = painterResource(id = R.drawable.app_logo_w_bck),
            contentDescription =""
        )
    }
}