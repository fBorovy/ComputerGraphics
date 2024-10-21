package com.fborowy.computergraphics.ui.screens

import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import com.fborowy.computergraphics.logic.FilesViewModel
import com.fborowy.computergraphics.logic.PpmJpegViewModel
import com.fborowy.computergraphics.logic.PrimitiveToolsViewModel
import com.fborowy.computergraphics.ui.components.BottomPanel
import com.fborowy.computergraphics.ui.components.FileOperationsPanel
import com.fborowy.computergraphics.ui.components.PpmJpegImagesPanel
import com.fborowy.computergraphics.ui.components.PrimitiveToolsPanel

@Composable
fun MainScreen(
    primitiveToolsViewModel: PrimitiveToolsViewModel = viewModel(),
    ppmJpegViewModel: PpmJpegViewModel = viewModel(),
    filesViewModel: FilesViewModel = viewModel()
) {
    val context = LocalContext.current
    var content by rememberSaveable { mutableIntStateOf(1) }
    var selectedTool by rememberSaveable { mutableIntStateOf(0) }
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
                switchToCanvasScreenWithPPM3 = {
                    content = 0
                },
                onGoBack = {
                    content = 1
                }
            )
        }
        else -> {
            Column(
                modifier = Modifier.fillMaxSize()
            ) {
                Box(
                    modifier = Modifier.weight(7f)
                ) {
                    CanvasScreen(
                        filesViewModel = filesViewModel,
                        primitiveToolsViewModel = primitiveToolsViewModel,
                        ppmJpegViewModel = ppmJpegViewModel,
                        currentFileName = currentFilename,
                    )
                }
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Box(
                        modifier = Modifier.weight(1f)
                    ) {
                        when(selectedTool) {
                            0 -> PrimitiveToolsPanel(primitiveToolsViewModel)
                            1 -> FileOperationsPanel(
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
                                onExit = {
                                    ppmJpegViewModel.resetBitmap()
                                    filesViewModel.setCurrentFilename(null)
                                    primitiveToolsViewModel.clearCanvas()
                                    content = 1
                                }
                            )
                            2 -> PpmJpegImagesPanel()
                        }
                    }
                    Box(
                        modifier = Modifier.weight(1f)
                    ){
                        BottomPanel { tool ->
                            selectedTool = tool
                        }
                    }
                }
            }
        }
    }
}
