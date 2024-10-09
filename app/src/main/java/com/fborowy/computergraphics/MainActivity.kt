package com.fborowy.computergraphics

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.unit.dp
import com.fborowy.computergraphics.ui.screens.MainScreen
import com.fborowy.computergraphics.ui.theme.ComputerGraphicsTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ComputerGraphicsTheme {
                val keyboardController = LocalSoftwareKeyboardController.current

                Box(
                    modifier = Modifier.fillMaxSize()
                        .pointerInput(Unit) {
                            detectTapGestures {
                                keyboardController?.hide()
                            }
                        }
                        .background(MaterialTheme.colorScheme.background)
                        .padding(vertical = 25.dp)
                ){
                    MainScreen()
                }
            }
        }
    }
}
