package com.fborowy.computergraphics.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import com.fborowy.computergraphics.logic.ColorizeViewModel
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun HSVConeCrossSectionCircle(
    colorizeViewModel: ColorizeViewModel,
    hue: Float,
    saturation: Float,
    colorValue: Float,
) {
    val keyboardController = LocalSoftwareKeyboardController.current
    val focusManager = LocalFocusManager.current
    val maxSaturation = 25
    Canvas(
        modifier = Modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                detectTapGestures {
                    focusManager.clearFocus()
                    colorizeViewModel.setActiveParameter(null)
                    keyboardController?.hide()
                }
            }
    ) {
        val centerX = size.width / 2f
        val centerY = size.height / 2f
        val maxRadius = minOf(centerX, centerY) * 0.8F
        drawCircle(
            color = if (colorValue > 0.03f) { Color.White.copy(alpha = colorValue) } else { Color.Black.copy(alpha = 1f - colorValue) },
            radius = 9f
        )
        for (r in 0..maxSaturation) {
            val currentRadius = maxRadius * (r + 1) / maxSaturation
            val saturationInCrossSection = r.toFloat() / maxSaturation
            for (i in 0..360 step 5) {
                val color = Color.hsv(i.toFloat(), saturationInCrossSection, colorValue)
                drawArc(
                    color = color,
                    startAngle = i.toFloat(),
                    sweepAngle = 5f,
                    useCenter = false,
                    topLeft = Offset(centerX - currentRadius, centerY - currentRadius),
                    size = Size(2 * currentRadius, 2 * currentRadius),
                    style = Stroke(width = maxRadius / maxSaturation)
                )
            }
        }

        val radiansAngle = Math.toRadians(hue.toDouble())
        val selectedXOffset = (maxRadius * saturation * cos(radiansAngle)).toFloat()
        val selectedYOffset = (maxRadius * saturation * sin(radiansAngle)).toFloat()
        drawCircle(
            color = if (colorValue > 0.5f) Color.Black else Color.White,
            radius = 8f,
            center = Offset(centerX + selectedXOffset, centerY + selectedYOffset),
            style = Stroke(width = 2f)
        )
    }
}
