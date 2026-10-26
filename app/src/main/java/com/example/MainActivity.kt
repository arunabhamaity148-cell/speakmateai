package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.example.ai.configureAppCheck
import com.example.ui.navigation.SpeakMateApp
import com.example.ui.theme.SpeakMateTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // STEP 1: App Check with launch intent before any UI or Firebase operations
        configureAppCheck(this, intent)

        // STEP 2: Edge to edge display
        enableEdgeToEdge()

        // STEP 3: Compose UI
        setContent {
            SpeakMateTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    SpeakMateApp()
                }
            }
        }
    }
}
