package com.fborowy.computergraphics.ui.components

import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.fborowy.computergraphics.R
import com.fborowy.computergraphics.logic.Primitive
import com.fborowy.computergraphics.logic.PrimitiveToolsViewModel

@Composable
fun PrimitiveToolsPanel(
    primitiveToolsViewModel: PrimitiveToolsViewModel
) {
    val selectedPrimitiveType by primitiveToolsViewModel.selectedPrimitiveType.collectAsState()
    val selectedPrimitiveOffsets by primitiveToolsViewModel.selectedPrimitive.collectAsState()

    LaunchedEffect(selectedPrimitiveOffsets) {
        Log.d("selected", selectedPrimitiveOffsets.toString())
    }
    Row(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp, 5.dp)
            .horizontalScroll(rememberScrollState()),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            painter = painterResource(R.drawable.line),
            contentDescription = stringResource(R.string.add_line),
            tint = if (selectedPrimitiveType == Primitive.LINE.type) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.primary,
            modifier = Modifier
                .padding(end = 5.dp)
                .clickable {
                    primitiveToolsViewModel.addPrimitive(primitiveToolsViewModel.canvasStartingPoints!!.first, primitiveToolsViewModel.canvasStartingPoints!!.second, Primitive.LINE.type)
                }
        )
        Icon(
            painter = painterResource(R.drawable.rectangle),
            contentDescription = stringResource(R.string.add_rectangle),
            tint = if (selectedPrimitiveType == Primitive.RECTANGLE.type) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.primary,
            modifier = Modifier
                .padding(end = 5.dp)
                .clickable {
                    primitiveToolsViewModel.addPrimitive(primitiveToolsViewModel.canvasStartingPoints!!.first, primitiveToolsViewModel.canvasStartingPoints!!.second, Primitive.RECTANGLE.type)
                }
        )
        Icon(
            painter = painterResource(R.drawable.circle),
            contentDescription = stringResource(R.string.add_circle),
            tint = if (selectedPrimitiveType == Primitive.CIRCLE.type) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.primary,
            modifier = Modifier
                .padding(end = 5.dp)
                .clickable {
                    primitiveToolsViewModel.addPrimitive(primitiveToolsViewModel.canvasStartingPoints!!.first, primitiveToolsViewModel.canvasStartingPoints!!.second, Primitive.CIRCLE.type)
                }
        )

        if (selectedPrimitiveOffsets != null) {
            Text(
                text = "A: x:",
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(start = 7.dp)
            )
            CGTextField(
                value = selectedPrimitiveOffsets!!.first.x.toString(),
                onValueChange = {
                    primitiveToolsViewModel.changeSelectedPrimitiveOffsetCoordinate(a = true, x = true, value = it.toFloat())
                    Log.d("onVC", it)
                }
            )
            Text(
                text = "y:",
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(start = 7.dp)
            )
            CGTextField(
                value = selectedPrimitiveOffsets!!.first.y.toString(),
                onValueChange = {
                    primitiveToolsViewModel.changeSelectedPrimitiveOffsetCoordinate(a = true, x = false, value = it.toFloat())
                },
            )
            Text(
                text = "B: x:",
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(start = 7.dp)
            )
            CGTextField(
                value = selectedPrimitiveOffsets!!.second.x.toString(),
                onValueChange = {
                    primitiveToolsViewModel.changeSelectedPrimitiveOffsetCoordinate(a = false, x = true, value = it.toFloat())
                },
            )
            Text(
                text = "y:",
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(start = 7.dp)
            )
            CGTextField(
                value = selectedPrimitiveOffsets!!.second.y.toString(),
                onValueChange = {
                    primitiveToolsViewModel.changeSelectedPrimitiveOffsetCoordinate(a = false, x = false, value = it.toFloat())
                },
            )
        }
    }
}