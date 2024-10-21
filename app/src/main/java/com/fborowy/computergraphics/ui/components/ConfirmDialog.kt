package com.fborowy.computergraphics.ui.components

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.fborowy.computergraphics.R
import com.fborowy.computergraphics.ui.theme.Typography

@Composable
fun ConfirmDialog(
    confirmStringResource: Int,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {

    AlertDialog(
        onDismissRequest = onDismiss,
        text = {
            Text(text = stringResource(R.string.exit_ask), style = Typography.bodySmall)
        },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text(text = stringResource(confirmStringResource), style = Typography.bodySmall)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(text = stringResource(R.string.cancel), style = Typography.bodySmall)
            }
        }
    )
}