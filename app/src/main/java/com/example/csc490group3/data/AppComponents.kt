package com.example.csc490group3.data

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.csc490group3.R


@Composable
fun ImageComponent(){
    val image = painterResource(R.drawable.app_logo)
    Image(
        painter = image,
        contentDescription = ""
    )
}

@Composable
fun NormalTextComponent(value:String) {
    Text(text = value,
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 40.dp),
        style = androidx.compose.ui.text.TextStyle(
            fontSize = 24.sp,
            fontWeight = FontWeight.Normal,
            fontStyle = FontStyle.Normal
        ),
        color = Color.White,
        textAlign = TextAlign.Center
    )
}

@Composable
fun ButtonComponent(value : String, onButtonClick : () -> Unit, isEnabled: Boolean) {
    Button(
        onClick = {onButtonClick.invoke()},
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(48.dp),
        contentPadding = PaddingValues(),
        colors = ButtonDefaults.buttonColors(Color.Transparent)
    ){
        Box(modifier = Modifier
            .fillMaxWidth()
            .heightIn(48.dp)
            .background(color = Color.Gray,
                shape = RoundedCornerShape(50.dp)
            ),
            contentAlignment = Alignment.Center
        ){
            Text(text = value,
                color = Color.Black,
                modifier = Modifier,
                style = androidx.compose.ui.text.TextStyle(
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Normal,
                    fontStyle = FontStyle.Normal
                ))
        }
    }
}

