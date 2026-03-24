package com.huhx0015.gamecollection

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.huhx0015.gamecollection.ui.theme.GameCollectionTheme
import dagger.hilt.android.AndroidEntryPoint

/** Single-activity host; applies theme and shows [GameCollectionApp]. */
@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            GameCollectionTheme {
                GameCollectionApp()
            }
        }
    }
}
