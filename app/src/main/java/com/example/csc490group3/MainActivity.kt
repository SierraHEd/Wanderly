package com.example.csc490group3

import android.net.http.HttpResponseCache.install
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.csc490group3.ui.theme.CSC490Group3Theme
import io.github.jan.supabase.BuildConfig as SupabaseBuildConfig
import io.github.jan.supabase.*
import io.github.jan.supabase.postgrest.Postgrest
import com.example.csc490group3.BuildConfig as AppBuildConfig

val supabase = createSupabaseClient(
    supabaseUrl = AppBuildConfig.SUPABASE_URL,
    supabaseKey = AppBuildConfig.SUPABASE_ANON_KEY
) {
    install(Postgrest)
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)
    }
}
/*
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CSC490Group3Theme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Greeting(
                        name = "Android",
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    CSC490Group3Theme {
        Greeting("Android")
    }
}

 */