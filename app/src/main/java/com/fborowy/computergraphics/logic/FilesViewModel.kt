package com.fborowy.computergraphics.logic

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import com.fborowy.computergraphics.data.PrimitiveDataToSave
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.cbor.Cbor
import kotlinx.serialization.decodeFromByteArray
import kotlinx.serialization.serializer
import java.io.File

const val MAX_FILENAME_SIZE = 40

class FilesViewModel: ViewModel() {

    private var _currentFilename = MutableStateFlow<String?>(null)
    val currentFilename: StateFlow<String?> = _currentFilename
    private var _shouldShowEnterFilenameDialog = MutableStateFlow(false)
    val shouldShowEnterFilenameDialog: StateFlow<Boolean> = _shouldShowEnterFilenameDialog
    private var _newFilename = MutableStateFlow("")
    val newFilename: StateFlow<String> = _newFilename
    private var _shouldShowExitDialog = MutableStateFlow(false)
    val shouldShowExitDialog: StateFlow<Boolean> = _shouldShowExitDialog
    private val filenameAllowedCharacters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789-_"


    @OptIn(ExperimentalSerializationApi::class, InternalSerializationApi::class)
    fun savePrimitivesCborDataAs(context: Context, fileName: String = currentFilename.value!!, data: PrimitiveDataToSave) {
        val cbor = Cbor.encodeToByteArray(PrimitiveDataToSave::class.serializer(), value = data)
        context.openFileOutput("$fileName.cbor", Context.MODE_PRIVATE).use {  output ->
            output.write(cbor)
        }
    }

    fun saveFileNameToFile(context: Context, newName: String, filename: String = "file_names.txt") {
        val file = File(context.filesDir, filename)
        file.appendText("\n$newName")
    }

    fun loadFileNamesFromFile(context: Context, filename: String = "file_names.txt"): List<String> {
        val file = File(context.filesDir, filename)
        return if (file.exists()) {
            file.readLines()
        } else {
            emptyList()
        }
    }

    fun switchEnterFilenameDialogVisibility(switch: Boolean){
        Log.d("new ilename", _newFilename.value)
        _newFilename.value = currentFilename.value?.substringBefore('.') ?: ""
        Log.d("new ilename", _newFilename.value)
        _shouldShowEnterFilenameDialog.value = switch
    }

    fun editNewFilename(newName: String) {
        val filteredNewName = newName.filter { it in filenameAllowedCharacters }
        if (newName.length > MAX_FILENAME_SIZE) {
            _newFilename.value = filteredNewName
        } else {
            _newFilename.value = filteredNewName.take(MAX_FILENAME_SIZE)
        }
    }

    fun switchExitDialogVisibility(switch: Boolean) {
        _shouldShowExitDialog.value = switch
    }

    @OptIn(ExperimentalSerializationApi::class)
    fun loadPrimitivesFromFile(context: Context, filename: String): PrimitiveDataToSave? {
        _currentFilename.value = filename
        return context.openFileInput("$filename.cbor")?.use { inputStream ->
            val byteArray = inputStream.readBytes()
            Cbor.decodeFromByteArray<PrimitiveDataToSave>(byteArray)
        }
    }

    fun setCurrentFilename(name: String?) {
        _currentFilename.value = name
    }

    fun getCurrentFilename(): String {
        return currentFilename.value?:""
    }


}
