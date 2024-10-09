package com.fborowy.computergraphics.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.unit.dp
import com.fborowy.computergraphics.logic.Primitive
import com.fborowy.computergraphics.logic.PrimitiveToolsViewModel

@Composable
fun CanvasScreen(
    primitiveToolsViewModel: PrimitiveToolsViewModel
) {
    var startPoint by remember { mutableStateOf<Offset?>(null) }
    var endPoint by remember { mutableStateOf<Offset?>(null) }
    val selectedPrimitiveOffsets by primitiveToolsViewModel.selectedPrimitive.collectAsState()
    val selectedPrimitiveType by primitiveToolsViewModel.selectedPrimitiveType.collectAsState()
    val listOfPrimitives by primitiveToolsViewModel.primitivesList.collectAsState()
    val listOfPrimitivesOffsets by primitiveToolsViewModel.primitivesOffsetsList.collectAsState()
    val activeColor = MaterialTheme.colorScheme.secondary
    val inactiveColor = MaterialTheme.colorScheme.primary


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
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(Unit) {
                    detectDragGestures(
                        onDragStart = { offset ->
                            startPoint = offset // Zapisanie pierwszego punktu
                        },
                        onDragEnd = {
                            // Resetowanie po zakończeniu rysowania
                            startPoint = null
                            endPoint = null
                        },
                        onDrag = { change, _ ->
                            change.consume()
                            endPoint = change.position // Aktualizacja końcowego punktu
                        }
                    )
                }

        ) {
            if (startPoint != null && endPoint != null) {
                // Obliczanie promienia
                val radius = (startPoint!! - endPoint!!).getDistance() / 2
                // Obliczanie środka
                val center = Offset(
                    (startPoint!!.x + endPoint!!.x) / 2,
                    (startPoint!!.y + endPoint!!.y) / 2
                )
                drawCircle(
                    color = Color.White,
                    radius = radius,
                    center = center,
                    style = Stroke(width = 1f)
                )
            }
//            if (selectedPrimitiveOffset != null) {
//                drawCircle(
//                    color = activeColor,
//                    radius = 5f,
//                    center = selectedPrimitiveOffset!!.first,
//
//                )
//                drawCircle(
//                    color = activeColor,
//                    radius = 5f,
//                    center = selectedPrimitiveOffset!!.second
//                )
//                when (selectedPrimitiveType) {
//                    Primitive.CIRCLE.type -> {
//                        drawCircle(
//                            color = activeColor,
//                            radius = (selectedPrimitiveOffset!!.second - selectedPrimitiveOffset!!.first).getDistance() / 2,
//                            center = Offset(
//                                (selectedPrimitiveOffset!!.first.x + selectedPrimitiveOffset!!.second.x) / 2,
//                                (selectedPrimitiveOffset!!.first.y + selectedPrimitiveOffset!!.second.y) / 2
//                            ),
//                            alpha = 0.5f,
//                            style = Stroke(width = 2f)
//                        )
//                    }
//                    Primitive.RECTANGLE.type -> {
//
//                    }
//                    Primitive.LINE.type -> {
//
//                    }
//                    else -> {}
//                }
//            }
            if (listOfPrimitives.isNotEmpty()) {
                for ((index, primitive) in listOfPrimitives.withIndex()) {

                    val color = if (
                        selectedPrimitiveOffsets!!.first == listOfPrimitivesOffsets[index].first &&
                        selectedPrimitiveOffsets!!.second == listOfPrimitivesOffsets[index].second &&
                        selectedPrimitiveType == primitive
                    ) activeColor else inactiveColor

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
                                radius = (selectedPrimitiveOffsets!!.second - selectedPrimitiveOffsets!!.first).getDistance() / 2,
                                center = Offset(
                                    (selectedPrimitiveOffsets!!.first.x + selectedPrimitiveOffsets!!.second.x) / 2,
                                    (selectedPrimitiveOffsets!!.first.y + selectedPrimitiveOffsets!!.second.y) / 2
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