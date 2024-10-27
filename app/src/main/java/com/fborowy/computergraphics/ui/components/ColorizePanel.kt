package com.fborowy.computergraphics.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.fborowy.computergraphics.R
import com.fborowy.computergraphics.logic.ColorizeViewModel
import com.fborowy.computergraphics.ui.theme.Typography

@Composable
fun ColorizePanel(
    colorizeViewModel: ColorizeViewModel,
) {
    val hue by colorizeViewModel.hue.collectAsState()
    val saturation by colorizeViewModel.saturation.collectAsState()
    val colorValue by colorizeViewModel.colorValue.collectAsState()
    val activeParameter by colorizeViewModel.activeParameter.collectAsState()
    val cyan by colorizeViewModel.cyan.collectAsState()
    val magenta by colorizeViewModel.magenta.collectAsState()
    val yellow by colorizeViewModel.yellow.collectAsState()
    val key by colorizeViewModel.key.collectAsState()


    Row(
        modifier = Modifier
            .fillMaxSize()
            .padding(10.dp, 5.dp)
            .horizontalScroll(rememberScrollState()),
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween,
        ) {
            Text(
                text = stringResource(R.string.hsv),
                style = Typography.bodyLarge
            )
            Slider(
                value = hue,
                onValueChange = {
                    colorizeViewModel.setActiveParameter(0)
                    colorizeViewModel.setHue(it)
                },
                valueRange = 0f..360f,
                steps = 119,
                modifier = Modifier.width(190.dp)
            )
            Row {
                Text(
                    text = stringResource(R.string.hue),
                    style = Typography.bodySmall,
                    modifier = Modifier.padding(end = 5.dp)
                )
                CGTextField(
                    modifier = Modifier.width(80.dp),
                    value = hue.toString(),
                    onValueChange = {
                        colorizeViewModel.setHue(it.toFloat())
                    },
                    isActive = activeParameter == 0,
                    onFocusChanged = { colorizeViewModel.setActiveParameter(if (it) 0 else null) }
                )
            }
            Slider(
                value = saturation,
                onValueChange = {
                    colorizeViewModel.setActiveParameter(1)
                    colorizeViewModel.setSaturation(it)
                },
                valueRange = 0f..1f,
                steps = 33,
                modifier = Modifier.width(190.dp)
            )
            Row {
                Text(
                    text = stringResource(R.string.saturation),
                    style = Typography.bodySmall,
                    modifier = Modifier.padding(end = 5.dp)
                )
                CGTextField(
                    modifier = Modifier.width(80.dp),
                    value = saturation.toString(),
                    onValueChange = {
                        colorizeViewModel.setSaturation(it.toFloat())
                    },
                    isActive = activeParameter == 1,
                    onFocusChanged = { colorizeViewModel.setActiveParameter(if(it) 1 else null) }
                )
            }

            Slider(
                value = colorValue,
                onValueChange = {
                    colorizeViewModel.setActiveParameter(2)
                    colorizeViewModel.setColorValue(it) },
                valueRange = 0f..1f,
                steps = 33,
                modifier = Modifier.width(190.dp)
            )
            Row {
                Text(
                    text = stringResource(R.string.color_value),
                    style = Typography.bodySmall,
                    modifier = Modifier.padding(end = 5.dp)
                )
                CGTextField(
                    modifier = Modifier.width(80.dp),
                    value = colorValue.toString(),
                    onValueChange = {
                        colorizeViewModel.setColorValue(it.toFloat())
                    },
                    isActive = activeParameter == 2,
                    onFocusChanged = { colorizeViewModel.setActiveParameter(if(it) 2 else null)}
                )
            }
        }
        Column (
            modifier = Modifier.verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween,
        ) {
            Text(
                text = stringResource(R.string.cmyk),
                style = Typography.bodyLarge
            )
            Slider(
                value = cyan,
                onValueChange = {
                    colorizeViewModel.setActiveParameter(3)
                    colorizeViewModel.setCyan(it)
                },
                valueRange = 0f..1f,
                steps = 33,
                modifier = Modifier.width(190.dp)
            )
            Row {
                Text(
                    text = stringResource(R.string.cyan),
                    style = Typography.bodySmall,
                    modifier = Modifier.padding(end = 5.dp)
                )
                CGTextField(
                    modifier = Modifier.width(80.dp),
                    value = cyan.toString(),
                    onValueChange = {
                        colorizeViewModel.setCyan(it.toFloat())
                    },
                    isActive = activeParameter == 3,
                    onFocusChanged = { colorizeViewModel.setActiveParameter(if (it) 3 else null) }
                )
            }
            Slider(
                value = magenta,
                onValueChange = {
                    colorizeViewModel.setActiveParameter(4)
                    colorizeViewModel.setMagenta(it)
                },
                valueRange = 0f..1f,
                steps = 33,
                modifier = Modifier.width(190.dp)
            )
            Row {
                Text(
                    text = stringResource(R.string.magenta),
                    style = Typography.bodySmall,
                    modifier = Modifier.padding(end = 5.dp)
                )
                CGTextField(
                    modifier = Modifier.width(80.dp),
                    value = magenta.toString(),
                    onValueChange = {
                        colorizeViewModel.setMagenta(it.toFloat())
                    },
                    isActive = activeParameter == 4,
                    onFocusChanged = { colorizeViewModel.setActiveParameter(if (it) 4 else null) }
                )
            }
            Slider(
                value = yellow,
                onValueChange = {
                    colorizeViewModel.setActiveParameter(5)
                    colorizeViewModel.setYellow(it) },
                valueRange = 0f..1f,
                steps = 33,
                modifier = Modifier.width(190.dp)
            )
            Row {
                Text(
                    text = stringResource(R.string.yellow),
                    style = Typography.bodySmall,
                    modifier = Modifier.padding(end = 5.dp)
                )
                CGTextField(
                    modifier = Modifier.width(80.dp),
                    value = yellow.toString(),
                    onValueChange = {
                        colorizeViewModel.setYellow(it.toFloat())
                    },
                    isActive = activeParameter == 5,
                    onFocusChanged = { colorizeViewModel.setActiveParameter(if(it) 5 else null)}
                )
            }
            Slider(
                value = key,
                onValueChange = {
                    colorizeViewModel.setActiveParameter(6)
                    colorizeViewModel.setKey(it) },
                valueRange = 0f..1f,
                steps = 33,
                modifier = Modifier.width(190.dp)
            )
            Row {
                Text(
                    text = stringResource(R.string.key),
                    style = Typography.bodySmall,
                    modifier = Modifier.padding(end = 5.dp)
                )
                CGTextField(
                    modifier = Modifier.width(80.dp),
                    value = key.toString(),
                    onValueChange = {
                        colorizeViewModel.setKey(it.toFloat())
                    },
                    isActive = activeParameter == 6,
                    onFocusChanged = { colorizeViewModel.setActiveParameter(if(it) 6 else null)}
                )
            }
        }
    }
}