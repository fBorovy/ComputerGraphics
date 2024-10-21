package com.fborowy.computergraphics.ui.screens

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
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
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.fborowy.computergraphics.logic.PpmJpegViewModel
import com.fborowy.computergraphics.logic.Primitive
import com.fborowy.computergraphics.logic.PrimitiveToolsViewModel
import com.fborowy.computergraphics.ui.theme.Typography

@Composable
fun CanvasScreen(
    primitiveToolsViewModel: PrimitiveToolsViewModel,
    ppmJpegViewModel: PpmJpegViewModel,
    currentFileName: String?,
) {
    val context = LocalContext.current
    val keyboardController = LocalSoftwareKeyboardController.current
    val listOfPrimitives by primitiveToolsViewModel.primitivesList.collectAsState()
    val listOfPrimitivesOffsets by primitiveToolsViewModel.primitivesOffsetsList.collectAsState()
    val selectedPrimitiveIndex by primitiveToolsViewModel.selectedPrimitiveIndex.collectAsState()
    val activeColor = MaterialTheme.colorScheme.secondary
    val inactiveColor = MaterialTheme.colorScheme.primary
    val selectedFirst by primitiveToolsViewModel.isFirstPointSelected.collectAsState()
    var isDragging by remember { mutableStateOf(false) }
    val bitmap by ppmJpegViewModel.imageBitmap.collectAsState()
    val jpegUri by ppmJpegViewModel.jpegUri.collectAsState()
    var jpegBitmap: Bitmap? = null


    var scale by remember { mutableFloatStateOf(1f) }
    var offsetX by remember { mutableFloatStateOf(0f) }
    var offsetY by remember { mutableFloatStateOf(0f) }

    if (jpegUri != null) {
        jpegBitmap = produceState<Bitmap?>(initialValue = null, jpegUri) {
            value = loadBitmapFromUri(context, jpegUri!!)
        }.value
    }

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
                .pointerInput(bitmap, jpegBitmap) {
                    if ((bitmap != null || jpegBitmap != null) && selectedPrimitiveIndex == null) {
                        detectTransformGestures { _, pan, zoom, _ ->
                            scale *= zoom
                            offsetX += pan.x
                            offsetY += pan.y
                        }
                    }
                }.pointerInput(selectedPrimitiveIndex) {
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
            bitmap?.let {
                val canvasWidth = size.width
                val aspectRatio = bitmap!!.width.toFloat() / bitmap!!.height.toFloat()
                val scaledHeight = canvasWidth / aspectRatio
                drawImage(
                    image = bitmap!!,
                    dstSize = IntSize(
                        (canvasWidth * scale).toInt(),
                        (scaledHeight * scale).toInt()
                    ),
                    dstOffset = IntOffset(offsetX.toInt(), offsetY.toInt()),
                )
            }

            jpegBitmap?. let {
                val canvasWidth = size.width
                val aspectRatio = jpegBitmap.width.toFloat() / jpegBitmap.height.toFloat()
                val scaledHeight = canvasWidth / aspectRatio
                drawImage(
                    image = jpegBitmap.asImageBitmap(),
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

fun loadBitmapFromUri(context: Context, jpegUri: Uri): Bitmap? {
    return try {
        val inputStream = context.contentResolver.openInputStream(jpegUri)
        BitmapFactory.decodeStream(inputStream)
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}
