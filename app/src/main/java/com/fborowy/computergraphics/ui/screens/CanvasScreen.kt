package com.fborowy.computergraphics.ui.screens

import android.graphics.Bitmap
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.Path
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.fborowy.computergraphics.R
import com.fborowy.computergraphics.data.Primitive
import com.fborowy.computergraphics.data.Tool
import com.fborowy.computergraphics.logic.ColorizeViewModel
import com.fborowy.computergraphics.logic.PpmJpegViewModel
import com.fborowy.computergraphics.logic.PrimitiveToolsViewModel
import com.fborowy.computergraphics.logic.functions.loadBitmapFromUri
import com.fborowy.computergraphics.logic.functions.saveBitmapToJpegToMediaStore
import com.fborowy.computergraphics.ui.components.HSVCone3D
import com.fborowy.computergraphics.ui.components.HSVConeCrossSectionCircle
import com.fborowy.computergraphics.ui.theme.Typography
import com.google.android.filament.Engine
import io.github.sceneview.SceneView

@Composable
fun CanvasScreen(
    primitiveToolsViewModel: PrimitiveToolsViewModel,
    ppmJpegViewModel: PpmJpegViewModel,
    colorizeViewModel: ColorizeViewModel,
    currentFileName: String?,
    selectedTool: Int,
    engine: Engine,
) {
    val context = LocalContext.current
    val focusManager = LocalFocusManager.current
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
    val jpegToSaveData by ppmJpegViewModel.jpegFileNameToSave.collectAsState()
    val hue by colorizeViewModel.hue.collectAsState()
    val saturation by colorizeViewModel.saturation.collectAsState()
    val colorValue by colorizeViewModel.colorValue.collectAsState()
    val start = Offset(100f, 400f)          // Punkt początkowy
    val control1 = Offset(200f, 100f)       // Pierwszy punkt kontrolny
    val control2 = Offset(500f, 100f)       // Drugi punkt kontrolny
    val end = Offset(600f, 400f)            // Punkt końcowy


    var scale by remember { mutableFloatStateOf(1f) }
    var offsetX by remember { mutableFloatStateOf(0f) }
    var offsetY by remember { mutableFloatStateOf(0f) }

    if (jpegUri != null) {
        jpegBitmap = produceState<Bitmap?>(initialValue = null, jpegUri) {
            value = loadBitmapFromUri(context, jpegUri!!)
        }.value
    }

    LaunchedEffect(jpegToSaveData) {
        jpegToSaveData?.let { data ->
            bitmap?.let { bitmap ->
                saveBitmapToJpegToMediaStore(context, bitmap.asAndroidBitmap(), data.first, data.second)
            }
            jpegBitmap?.let { saveBitmapToJpegToMediaStore(context, it, data.first, data.second) }
            ppmJpegViewModel.switchJpegDataToSave(null)
        }
    }

    when (selectedTool) {
        Tool.FILE.id, Tool.PPM_JPEG.id, Tool.PRIMITIVES.id -> {
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
                    modifier = Modifier.zIndex(1f).align(Alignment.TopEnd)
                        .padding(top = 3.dp, end = 3.dp)
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
                                        primitiveToolsViewModel.changeSelectedPointOfSelectedPrimitive(
                                            null
                                        )
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
                                focusManager.clearFocus()
                                colorizeViewModel.setActiveParameter(null)
                                keyboardController?.hide()
                                primitiveToolsViewModel.unselectPrimitive()
                                primitiveToolsViewModel.selectPrimitiveByClick(offset)
                            }
                        }
                ) {
                    val canvasWidth = size.width

                    bitmap?.let {
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

                    jpegBitmap?.let {
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

                    val path = androidx.compose.ui.graphics.Path().apply {
                        moveTo(start.x, start.y)
                        cubicTo(
                            control1.x, control1.y,
                            control2.x, control2.y,
                            end.x, end.y
                        )
                    }

                    drawPath(
                        path = path,
                        color = Color.Red,
                        style = Stroke(
                            width = 5f
                        )
                    )
                    drawCircle(Color.Blue, 8f, control1)
                    drawCircle(Color.Blue, 8f, control2)
                    drawCircle(Color.Green, 8f, start)
                    drawCircle(Color.Green, 8f, end)
                    if (listOfPrimitives.isNotEmpty()) {
                        for ((index, primitive) in listOfPrimitives.withIndex()) {

                            val color =
                                if (index == selectedPrimitiveIndex) activeColor else inactiveColor

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
                                        size = Size(
                                            width = listOfPrimitivesOffsets[index].second.x - listOfPrimitivesOffsets[index].first.x,
                                            height = listOfPrimitivesOffsets[index].second.y - listOfPrimitivesOffsets[index].first.y
                                        ),
                                        style = Stroke(width = 2f)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
        Tool.COLORIZE.id -> {
            Row(
                modifier = Modifier.fillMaxSize()
                    .padding(start = 20.dp)
            ) {
                Column(
                    modifier = Modifier
                        .weight(2f),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxSize()
                            .padding(vertical = 50.dp, horizontal = 108.dp)
                            .clip(RoundedCornerShape(3.dp))
                            .background(Color.hsv(hue, saturation, colorValue))
                    )
                    Box(
                        modifier = Modifier.weight(2f)
                    ) {
                        HSVCone3D(
                            engine = engine,
                            hue = hue,
                        )
                    }
                }
                Column(
                    modifier = Modifier
                        .weight(1f)
                ) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                    ) {
                        HSVConeCrossSectionCircle(
                            colorizeViewModel,
                            hue,
                            saturation,
                            colorValue,
                        )
                    }
                    BoxWithConstraints(
                        modifier = Modifier
                            .weight(2f)
                            .padding(bottom = 50.dp)
                    ) {
                        Icon(
                            modifier = Modifier
                                .offset { IntOffset(0, maxHeight.roundToPx() - (maxHeight * colorValue).roundToPx()) },
                            painter = painterResource(R.drawable.arrow_back),
                            contentDescription = stringResource(R.string.color_value_indicator),
                        )
                    }
                }
            }
        }
    }
}
