package com.fborowy.computergraphics.ui.components

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.fborowy.computergraphics.R
import com.fborowy.computergraphics.logic.FilesViewModel
import com.fborowy.computergraphics.ui.theme.Typography

@Composable
fun FileOperationsPanel(
    filesViewModel: FilesViewModel,
    onSave: () -> Unit,
    onSaveAs: (String) -> Unit,
    onSaveJpeg: (String, Int) -> Unit,
    onExit: () -> Unit,
) {
    val context = LocalContext.current
    val shouldShowEnterFilenameDialog by filesViewModel.shouldShowEnterFilenameDialog.collectAsState()
    val shouldShowExitDialog by filesViewModel.shouldShowExitDialog.collectAsState()
    val currentFileName by filesViewModel.currentFilename.collectAsState()
    var jpegSaving by rememberSaveable { mutableStateOf(false) }

    Row(
        modifier = Modifier
            .fillMaxSize()
            .padding(10.dp, 5.dp)
            .horizontalScroll(rememberScrollState()),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (currentFileName != null) {
            Text(
                text = filesViewModel.currentFilename.value?: "",
                style = Typography.bodySmall,
                modifier = Modifier.padding(end = 5.dp)
            )
        }
        CGButton(
            textResource = R.string.save,
            onClick = {
                if (currentFileName != null) {
                    onSave()
                } else {
                    filesViewModel.switchEnterFilenameDialogVisibility(true)
                }
            }
        )
        CGButton(
            textResource = R.string.save_as,
            onClick = {
                filesViewModel.switchEnterFilenameDialogVisibility(true)
            }
        )
        CGButton(
            textResource = R.string.save_jpeg,
            onClick = {
                jpegSaving = true
                filesViewModel.switchEnterFilenameDialogVisibility(true)
            }
        )
        CGButton(
            textResource = R.string.exit,
            onClick = {
                filesViewModel.switchExitDialogVisibility(true)
            }
        )
    }

    if (shouldShowEnterFilenameDialog) {
        FilenameDialog(
            filesViewModel = filesViewModel,
            isJpeg = jpegSaving,
            onConfirm = {
                filesViewModel.saveFileNameToFile(context, it)
                filesViewModel.switchEnterFilenameDialogVisibility(false)
                filesViewModel.setCurrentFilename(it)
                onSaveAs(it)
            },
            onJpegConfirm = { filename, quality ->
                filesViewModel.switchEnterFilenameDialogVisibility(false)
                filesViewModel.setCurrentFilename(filename)
                onSaveJpeg(filename, quality)
            },
            onDismiss = {
                filesViewModel.switchEnterFilenameDialogVisibility(false)
            }
        )
    }
    if (shouldShowExitDialog) {
        ConfirmDialog(
            confirmStringResource = R.string.exit,
            onConfirm = {
                filesViewModel.switchExitDialogVisibility(false)
                onExit()
            },
            onDismiss = {
                filesViewModel.switchExitDialogVisibility(false)
            }
        )
    }
}