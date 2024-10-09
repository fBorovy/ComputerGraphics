package com.fborowy.computergraphics.ui.screens

import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.unit.dp
import com.fborowy.computergraphics.logic.PrimitiveToolsViewModel
import com.fborowy.computergraphics.ui.components.BottomPanel
import com.fborowy.computergraphics.ui.components.PrimitiveToolsPanel

@Composable
fun MainScreen(
    primitiveToolsViewModel: PrimitiveToolsViewModel = PrimitiveToolsViewModel()
) {
    var content by rememberSaveable { mutableIntStateOf(1) }

    if (content == 1) {
        StartScreen(
            onNewCanvasClick = {
                content = 2
            },
            onLoadCanvasFromFileClick = {

            }
        )
    }
    else if (content == 2){
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            Box(
                modifier = Modifier.weight(7f)
            ) {
                CanvasScreen(primitiveToolsViewModel)
            }
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Box(
                    modifier = Modifier.weight(1f)
                ) {
                    PrimitiveToolsPanel(primitiveToolsViewModel)
                }
                Box(
                    modifier = Modifier.weight(1f)
                ){
                    BottomPanel()
                }
            }
        }
    }
}