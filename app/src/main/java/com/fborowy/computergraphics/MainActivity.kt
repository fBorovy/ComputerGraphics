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
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.fborowy.computergraphics.logic.PrimitiveToolsViewModel
import com.fborowy.computergraphics.ui.screens.MainScreen
import com.fborowy.computergraphics.ui.theme.ComputerGraphicsTheme
import io.github.sceneview.rememberEngine

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ComputerGraphicsTheme {
                val keyboardController = LocalSoftwareKeyboardController.current
                val engine = rememberEngine()
                Box(
                    modifier = Modifier.fillMaxSize()
                        .pointerInput(Unit) {
                            detectTapGestures {
                                keyboardController?.hide()
                            }
                        }
                        .background(MaterialTheme.colorScheme.background)
                        .systemBarsPadding()
                ){
                    MainScreen(engine = engine)
                }
            }
        }
    }
}
