package com.fborowy.computergraphics.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.fborowy.computergraphics.R
import com.fborowy.computergraphics.logic.FilesViewModel
import com.fborowy.computergraphics.ui.theme.Typography

@Composable
fun FilenameDialog(
    filesViewModel: FilesViewModel,
    isJpeg: Boolean = false,
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit,
    onJpegConfirm: (String, Int) -> Unit,
) {
    val fileName by filesViewModel.newFilename.collectAsState()
    var quality by remember { mutableFloatStateOf(100f) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = stringResource(R.string.save_as), style = Typography.titleLarge) },
        text = {
            Column {
                Text(
                    text = stringResource(R.string.enter_filename),
                    style = Typography.bodySmall
                )
                CGTextField(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 7.dp),
                    value = fileName,
                    onValueChange = {
                        filesViewModel.editNewFilename(it)
                    },
                    onFocusChanged = {},
                    isActive = true
                )
                if (isJpeg) {
                    Column(
                        modifier = Modifier.padding(top = 20.dp)
                    ) {
                        Text(
                            text = stringResource(R.string.compression_ratio) + ": " + quality.toInt(),
                            style = Typography.bodySmall
                        )
                        Slider(
                            value = quality,
                            onValueChange = {quality = it},
                            valueRange = 0f..100f,
                            steps = 99,
                        )
                    }
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    if (isJpeg) {
                        onJpegConfirm(fileName, quality.toInt())
                    } else {
                        onConfirm(fileName)
                    }
                },
            ) {
                Text(text = stringResource(R.string.save), style = Typography.bodySmall)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(text = stringResource(R.string.cancel), style = Typography.bodySmall)
            }
        }
    )
}