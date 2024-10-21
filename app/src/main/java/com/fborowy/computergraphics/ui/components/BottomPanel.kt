package com.fborowy.computergraphics.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.fborowy.computergraphics.logic.Tool

@Composable
fun BottomPanel(
    onToolSelect: (Int) -> Unit
) {
    val listOfTools = listOf(
        Tool.PRIMITIVES,
        Tool.FILE
    )
    var currentTool by rememberSaveable { mutableIntStateOf(1) }

    Spacer(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 10.dp)
            .height(1.dp)
            .background(MaterialTheme.colorScheme.primary)
    )
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
//        for (i in 1..5) {
//            Box(
//                modifier = Modifier.weight(1f),
//                contentAlignment = Alignment.Center
//            ) {
//                Icon(
//                    painter = painterResource(Tool.PRIMITIVE_TOOLS.painter),
//                    contentDescription = stringResource(Tool.PRIMITIVE_TOOLS.stringResource),
//                    tint = MaterialTheme.colorScheme.primary,
//                    modifier = Modifier.size(35.dp)
//                )
//            }
//        }
        Box(
            modifier = Modifier.weight(1f),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painterResource(Tool.PPM_JPEG.painter),
                contentDescription = stringResource(Tool.PPM_JPEG.stringResource),
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier
                    .size(45.dp)
                    .clickable {
                        onToolSelect(2)
                    }
            )
        }
        Box(
            modifier = Modifier.weight(1f),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painterResource(Tool.PRIMITIVES.painter),
                contentDescription = stringResource(Tool.PRIMITIVES.stringResource),
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier
                    .size(45.dp)
                    .clickable {
                        onToolSelect(0)
                    }
            )
        }
        Box(
            modifier = Modifier.weight(1f),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painterResource(Tool.FILE.painter),
                contentDescription = stringResource(Tool.FILE.stringResource),
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier
                    .size(45.dp)
                    .clickable {
                        onToolSelect(1)
                    }
            )
        }
    }
}