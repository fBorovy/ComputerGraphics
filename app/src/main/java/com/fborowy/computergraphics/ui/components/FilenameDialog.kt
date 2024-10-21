package com.fborowy.computergraphics.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.fborowy.computergraphics.R
import com.fborowy.computergraphics.logic.FilesViewModel
import com.fborowy.computergraphics.ui.theme.Typography

@Composable
fun FilenameDialog(
    filesViewModel: FilesViewModel,
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit
) {
    val fileName by filesViewModel.newFilename.collectAsState()

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = stringResource(R.string.save_as), style = Typography.titleLarge) },
        text = {
            Column {
                Text(text = stringResource(R.string.enter_filename), style = Typography.bodySmall)
                CGTextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = fileName,
                    onValueChange = {
                        filesViewModel.editNewFilename(it)
                    },
                    onFocusChanged = {},
                    isActive = true
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    onConfirm(fileName)
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