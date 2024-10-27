package com.fborowy.computergraphics.ui.screens

import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.lifecycle.viewmodel.compose.viewModel
import com.fborowy.computergraphics.data.Tool
import com.fborowy.computergraphics.logic.ColorizeViewModel
import com.fborowy.computergraphics.logic.FilesViewModel
import com.fborowy.computergraphics.logic.PpmJpegViewModel
import com.fborowy.computergraphics.logic.PrimitiveToolsViewModel
import com.fborowy.computergraphics.ui.components.BottomPanel
import com.fborowy.computergraphics.ui.components.ColorizePanel
import com.fborowy.computergraphics.ui.components.FileOperationsPanel
import com.fborowy.computergraphics.ui.components.PpmJpegImagesPanel
import com.fborowy.computergraphics.ui.components.PrimitiveToolsPanel
import com.google.android.filament.Engine

@Composable
fun MainScreen(
    primitiveToolsViewModel: PrimitiveToolsViewModel = viewModel(),
    ppmJpegViewModel: PpmJpegViewModel = viewModel(),
    filesViewModel: FilesViewModel = viewModel(),
    colorizeViewModel: ColorizeViewModel = viewModel(),
    engine: Engine,
) {
    val context = LocalContext.current
    val focusManager = LocalFocusManager.current
    var content by rememberSaveable { mutableIntStateOf(1) }
    var selectedTool by rememberSaveable { mutableIntStateOf(Tool.PRIMITIVES.id) }
    val currentFilename by filesViewModel.currentFilename.collectAsState()

    when (content) {
        1 -> {
            StartScreen(
                onNewCanvasClick = {
                    content = 0
                },
                onLoadCanvasFromFileClick = {
                    content = 2
                }
            )
        }
        2 -> {
            FilesScreen(
                ppmJpegViewModel = ppmJpegViewModel,
                filesViewModel = filesViewModel,
                switchToCanvasScreenWithPrimitives = {
                    if (it != null) {
                        primitiveToolsViewModel.preparePrimitivesDataFromFile(it)
                        content = 0
                    }
                },
                switchToCanvasScreen = {
                    content = 0
                },
                onGoBack = {
                    content = 1
                }
            )
        }
        else -> {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .pointerInput(Unit) {
                        detectTapGestures {
                            colorizeViewModel.setActiveParameter(null)
                            focusManager.clearFocus()
                        }
                    }
            ) {
                Box(
                    modifier = Modifier.weight(if (selectedTool == Tool.COLORIZE.id) 5f else 15f)
                ) {
                    CanvasScreen(
                        primitiveToolsViewModel = primitiveToolsViewModel,
                        ppmJpegViewModel = ppmJpegViewModel,
                        colorizeViewModel = colorizeViewModel,
                        currentFileName = currentFilename,
                        selectedTool = selectedTool,
                        engine = engine,
                    )
                }
                Box(
                    modifier = Modifier.fillMaxWidth().weight(if (selectedTool == Tool.COLORIZE.id) 4f else 1f)
                ) {
                    when(selectedTool) {
                        Tool.PRIMITIVES.id -> PrimitiveToolsPanel(primitiveToolsViewModel)
                        Tool.FILE.id -> FileOperationsPanel(
                            filesViewModel = filesViewModel,
                            onSave = {
                                filesViewModel.savePrimitivesCborDataAs(
                                    context = context,
                                    data = primitiveToolsViewModel.preparePrimitivesDataToSave()
                                )
                            },
                            onSaveAs = {
                                filesViewModel.savePrimitivesCborDataAs(
                                    context = context,
                                    fileName = it,
                                    data = primitiveToolsViewModel.preparePrimitivesDataToSave(),
                                )
                            },
                            onSaveJpeg = { filename, quality ->
                                ppmJpegViewModel.switchJpegDataToSave(Pair(filename, quality))
                            },
                            onExit = {
                                ppmJpegViewModel.resetBitmap()
                                ppmJpegViewModel.resetJpegUri()
                                filesViewModel.setCurrentFilename(null)
                                primitiveToolsViewModel.clearCanvas()
                                content = 1
                            }
                        )
                        Tool.PPM_JPEG.id -> PpmJpegImagesPanel()
                        Tool.COLORIZE.id -> ColorizePanel(
                            colorizeViewModel = colorizeViewModel,
                        )
                    }
                }
                BottomPanel(
                    onToolSelect = { tool ->
                        selectedTool = tool
                    }
                )
            }
        }
    }
}
