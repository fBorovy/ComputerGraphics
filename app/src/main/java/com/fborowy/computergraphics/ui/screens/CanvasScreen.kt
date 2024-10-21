package com.fborowy.computergraphics.ui.screens

import android.util.Log
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.fborowy.computergraphics.logic.FilesViewModel
import com.fborowy.computergraphics.logic.PpmJpegViewModel
import com.fborowy.computergraphics.logic.Primitive
import com.fborowy.computergraphics.logic.PrimitiveToolsViewModel
import com.fborowy.computergraphics.ui.theme.Typography

@Composable
fun CanvasScreen(
    filesViewModel: FilesViewModel,
    primitiveToolsViewModel: PrimitiveToolsViewModel,
    ppmJpegViewModel: PpmJpegViewModel,
    currentFileName: String?,
) {
    val keyboardController = LocalSoftwareKeyboardController.current
    val listOfPrimitives by primitiveToolsViewModel.primitivesList.collectAsState()
    val listOfPrimitivesOffsets by primitiveToolsViewModel.primitivesOffsetsList.collectAsState()
    val selectedPrimitiveIndex by primitiveToolsViewModel.selectedPrimitiveIndex.collectAsState()
    val activeColor = MaterialTheme.colorScheme.secondary
    val inactiveColor = MaterialTheme.colorScheme.primary
    val selectedFirst by primitiveToolsViewModel.isFirstPointSelected.collectAsState()
    var isDragging by remember { mutableStateOf(false) }
    val bitmap by ppmJpegViewModel.imageBitmap.collectAsState()

    var scale by remember { mutableFloatStateOf(1f) }
    var offsetX by remember { mutableFloatStateOf(0f) }
    var offsetY by remember { mutableFloatStateOf(0f) }


    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 10.dp)
            .clip(RoundedCornerShape(7.dp, 7.dp, 7.dp, 7.dp))
            .background(MaterialTheme.colorScheme.tertiary)
            .onSizeChanged { size ->
                primitiveToolsViewModel.setCanvasStartingPointsOffsetsAndBottomRightCornerOffset(Offset(size.width.toFloat(), size.height.toFloat()))
            }
    ) {
        Text(
            text = currentFileName ?: "",
            style = Typography.labelSmall,
            modifier = Modifier.zIndex(1f).align(Alignment.TopEnd).padding(top = 3.dp, end = 3.dp)
        )
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(bitmap) {
                    if (bitmap != null && selectedPrimitiveIndex == null) {
                        detectTransformGestures { _, pan, zoom, _ ->
                            scale *= zoom
                            offsetX += pan.x
                            offsetY += pan.y
                        }
                    }
                }.pointerInput(selectedPrimitiveIndex) { //TODO
                    if (selectedPrimitiveIndex != null) {
                        detectDragGestures(
                            onDragStart = { offset ->
                                isDragging = true
                                primitiveToolsViewModel.selectPrimitiveByClick(offset)
                            },
                            onDragEnd = {
                                isDragging = false
                                primitiveToolsViewModel.changeSelectedPointOfSelectedPrimitive(null)
                            },
                            onDrag = { change, dragAmount ->
                                if (isDragging) {
                                    change.consume()
                                    if (selectedFirst != null) {
                                        primitiveToolsViewModel.changeSelectedPrimitiveOffset(
                                            a = selectedFirst!!,
                                            drag = dragAmount
                                        )
                                    }
                                }
                            }
                        )
                    }
                }.pointerInput(Unit) {
                    detectTapGestures { offset ->
                        keyboardController?.hide()
                        primitiveToolsViewModel.unselectPrimitive()
                        primitiveToolsViewModel.selectPrimitiveByClick(offset)
                    }
                }
        ) {
            if (bitmap != null) {
                val canvasWidth = size.width
                val aspectRatio = bitmap!!.width.toFloat() / bitmap!!.height.toFloat()
                val scaledHeight = canvasWidth / aspectRatio
                Log.d("ppm", "started drawing bitmap")
                Log.d("ppm", bitmap!!.width.toString() + " " + bitmap!!.height.toString())
                drawImage(
                    image = bitmap!!,
                    dstSize = IntSize(
                        (canvasWidth * scale).toInt(),
                        (scaledHeight * scale).toInt()
                    ),
                    dstOffset = IntOffset(offsetX.toInt(), offsetY.toInt()),
                )

            }
            if (listOfPrimitives.isNotEmpty()) {
                for ((index, primitive) in listOfPrimitives.withIndex()) {

                    val color = if (index == selectedPrimitiveIndex) activeColor else inactiveColor

                    drawCircle(
                        color = color,
                        radius = 5f,
                        center = listOfPrimitivesOffsets[index].first
                    )
                    drawCircle(
                        color = color,
                        radius = 5f,
                        center = listOfPrimitivesOffsets[index].second
                    )

                    when (primitive) {
                        Primitive.LINE.type -> {
                            drawLine(
                                color = color,
                                start = listOfPrimitivesOffsets[index].first,
                                end = listOfPrimitivesOffsets[index].second,
                                strokeWidth = 2f,
                            )
                        }
                        Primitive.CIRCLE.type -> {
                            drawCircle(
                                color = color,
                                radius = (listOfPrimitivesOffsets[index].second - listOfPrimitivesOffsets[index].first).getDistance() / 2,
                                center = Offset(
                                    (listOfPrimitivesOffsets[index].first.x + listOfPrimitivesOffsets[index].second.x) / 2,
                                    (listOfPrimitivesOffsets[index].first.y + listOfPrimitivesOffsets[index].second.y) / 2,
                                ),
                                style = Stroke(width = 2f)
                            )
                        }
                        Primitive.RECTANGLE.type -> {
                            drawRect(
                                color = color,
                                topLeft = listOfPrimitivesOffsets[index].first,
                                size = Size(width = listOfPrimitivesOffsets[index].second.x - listOfPrimitivesOffsets[index].first.x, height = listOfPrimitivesOffsets[index].second.y - listOfPrimitivesOffsets[index].first.y),
                                style = Stroke(width = 2f)
                            )
                        }
                    }
                }
            }
        }
    }
}