package com.fborowy.computergraphics.ui.screens

import android.net.Uri
import android.provider.OpenableColumns
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.fborowy.computergraphics.R
import com.fborowy.computergraphics.data.PrimitiveDataToSave
import com.fborowy.computergraphics.logic.FilesViewModel
import com.fborowy.computergraphics.logic.PpmJpegViewModel
import com.fborowy.computergraphics.ui.theme.Typography

@Composable
fun FilesScreen(
    ppmJpegViewModel: PpmJpegViewModel,
    filesViewModel: FilesViewModel,
    switchToCanvasScreenWithPrimitives: (PrimitiveDataToSave?) -> Unit,
    switchToCanvasScreen: () -> Unit,
    onGoBack: () -> Unit,
) {
    val context = LocalContext.current
    var skipFirstSeparatorLine = true
    val wrongFileNameErrorMessage = stringResource(R.string.wrong_file)

    val files = filesViewModel.loadFileNamesFromFile(context)

    var selectedFileUri by remember { mutableStateOf<Uri?>(null) }
    val selectedFileName by filesViewModel.currentFilename.collectAsState()


    val filePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri?.scheme == "content") {
            val cursor = context.contentResolver.query(uri, null, null, null, null)
            cursor?.use {
                if (it.moveToFirst()) {
                    val nameIndex = it.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                    val fileName = it.getString(nameIndex)
                    if (fileName.endsWith(".ppm")) {
                        val inputStream = context.contentResolver.openInputStream(uri)
                        inputStream?.let { stream ->
                            ppmJpegViewModel.loadPPMImage(stream)
                            filesViewModel.setCurrentFilename(fileName)
                            selectedFileUri = uri
                            switchToCanvasScreen()
                        }
                    } else if (fileName.endsWith(".jpeg") || fileName.endsWith("jpg")) {
                        ppmJpegViewModel.loadJPEGImageUri(uri)
                        filesViewModel.setCurrentFilename(fileName)
                        selectedFileUri = uri
                        switchToCanvasScreen()
                    } else {
                        Toast.makeText(
                            context,
                            wrongFileNameErrorMessage,
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        }
    }

    Column(
        modifier = Modifier.fillMaxSize().padding(horizontal = 20.dp, vertical = 3.dp)
    ) {
        Row(

            modifier = Modifier
                .fillMaxWidth().padding(bottom = 7.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                modifier = Modifier
                    .clickable {
                        onGoBack()
                    },
                painter = painterResource(R.drawable.arrow_back),
                contentDescription = stringResource(R.string.go_back),
                tint = MaterialTheme.colorScheme.primary
            )
            Row(
                modifier = Modifier
                    .padding(start = 15.dp)
                    .clip(RoundedCornerShape(5.dp))
                    .clickable {
                        filePickerLauncher.launch("*/*")
                    },
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    modifier = Modifier.padding(end = 5.dp).size(30.dp),
                    painter = painterResource(R.drawable.search_files),
                    contentDescription = stringResource(R.string.search_file),
                    tint = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = selectedFileName?: stringResource(R.string.search_file),
                    style = Typography.bodySmall,
                    maxLines = 1,
                )
            }
        }
        Text(
            text = stringResource(R.string.shapes),
            style = Typography.bodySmall
        )
        Text(
            text = stringResource(R.string.shapes_hint),
            style = Typography.labelSmall
        )
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 7.dp)
        ) {
            items(files) { file ->
                if (skipFirstSeparatorLine) {
                    skipFirstSeparatorLine = false
                } else {
                    Spacer(modifier = Modifier.fillMaxWidth()
                        .height(1.dp)
                        .background(MaterialTheme.colorScheme.primary)
                        .padding(vertical = 2.dp))
                    Text(
                        text = file,
                        modifier = Modifier
                            .clickable {
                                switchToCanvasScreenWithPrimitives(filesViewModel.loadPrimitivesFromFile(context, file))
                            }
                            .padding(vertical = 3.dp),
                        style = Typography.bodySmall
                    )
                }
            }
        }
    }
}