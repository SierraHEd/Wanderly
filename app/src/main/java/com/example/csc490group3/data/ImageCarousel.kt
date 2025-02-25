package com.example.csc490group3.data

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.csc490group3.R
import kotlinx.coroutines.launch

@Composable
fun ImageCarousel() {
    val images = listOf(
        painterResource(id = R.drawable.concert),
        painterResource(id = R.drawable.hockey),
        painterResource(id = R.drawable.party)
    )

    val pagerState = rememberPagerState(pageCount = { images.size })
    val coroutineScope = rememberCoroutineScope()

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(16.dp)
    ) {
        HorizontalPager(
            state = pagerState,
            modifier = Modifier
                .fillMaxWidth()
                .height(250.dp)
        ) { page ->
            ImageCard(image = images[page])
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Page Indicator (Dots)
        Row(
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxWidth()
        ) {
            images.indices.forEach { index ->
                Box(
                    modifier = Modifier
                        .padding(4.dp)
                        .size(if (pagerState.currentPage == index) 12.dp else 8.dp)
                        .clip(CircleShape)
                        .background(
                            if (pagerState.currentPage == index) Color.Black else Color.Gray
                        )
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Navigation Buttons
        Row {
            Button(onClick = {
                coroutineScope.launch {
                    pagerState.animateScrollToPage((pagerState.currentPage - 1).coerceAtLeast(0))
                }
            }) {
                Text("Previous")
            }

            Spacer(modifier = Modifier.width(16.dp))

            Button(onClick = {
                coroutineScope.launch {
                    pagerState.animateScrollToPage((pagerState.currentPage + 1).coerceAtMost(images.size - 1))
                }
            }) {
                Text("Next")
            }
        }
    }
}

@Composable
fun ImageCard(image: Painter) {
    Card(
        shape = MaterialTheme.shapes.medium,
        modifier = Modifier.fillMaxWidth()
    ) {
        Image(
            painter = image,
            contentDescription = "Carousel Image",
            modifier = Modifier.fillMaxSize()
        )
    }
}