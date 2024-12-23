package com.fborowy.computergraphics.logic

import android.graphics.Bitmap
import android.net.Uri
import android.util.Log
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.io.BufferedInputStream
import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader
import java.nio.ByteBuffer


class PpmJpegViewModel: ViewModel() {
    private var _imageBitmap = MutableStateFlow<ImageBitmap?>(null)
    val imageBitmap: StateFlow<ImageBitmap?> = _imageBitmap
    private var _jpegUri = MutableStateFlow<Uri?>(null)
    val jpegUri: StateFlow<Uri?> = _jpegUri
    private var _jpegDataToSave = MutableStateFlow<Pair<String, Int>?>(null)
    val jpegFileNameToSave = _jpegDataToSave

    fun loadJPEGImageUri(uri: Uri) {
        _jpegUri.value = uri
    }

    fun loadPPMImage(stream: InputStream) {
        viewModelScope.launch {
            _imageBitmap.value = readPPMFile(stream)
        }
    }

    private fun readPPMFile(stream: InputStream): ImageBitmap {
        val headerValues = mutableListOf<String>()
        var hLine: String?
        val reader = BufferedReader(InputStreamReader(stream))
        headerValues.add(reader.readLine())

        while (reader.readLine().also { hLine = it } != null) {
            Log.d("hLine", hLine.toString())
            val cleanedLine = hLine!!.substringBefore("#").trim()
            if (cleanedLine.isBlank()) continue
            cleanedLine.split(Regex("\\s+")).forEach { value ->
                Log.d("val", value)
                if (value.isNotEmpty() && headerValues.size < 4) {
                    headerValues.add(value)
                    Log.d("header values size", headerValues.size.toString())
                    if (headerValues.size == 4) return@forEach
                }
            }
            if (headerValues.size == 4) break
        }

        val width = headerValues[1].toInt()
        val height = headerValues[2].toInt()

        val maxColorValue = headerValues[3].toInt()

        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)

        when (headerValues[0]) {
            "P3" -> {
                val pixels = IntArray(width * height)
                val rgb = IntArray(3)
                val regex = Regex("\\s+")
                val allValues = StringBuilder()
                var widthCounter = 0
                var heightCounter = 0
                var colorCounter = 0

                reader.forEachLine { line ->
                    val cleanedLine = line.substringBefore("#").trim()
                    if (cleanedLine.isNotEmpty()) {
                        allValues.append(cleanedLine).append(" ")
                        if (allValues.length > 10000) {
                            val values = allValues.toString().split(regex)
                            allValues.clear()
                            for (value in values) {
                                if (value.isNotEmpty()) {
                                    rgb[colorCounter++] = (value.toFloat() * 255 / maxColorValue).toInt()

                                    if (colorCounter == 3) {
                                        pixels[heightCounter * width + widthCounter++] = android.graphics.Color.rgb(rgb[0], rgb[1], rgb[2])
                                        colorCounter = 0

                                        if (widthCounter == width) {
                                            widthCounter = 0
                                            heightCounter++
                                        }
                                        if (heightCounter == height) {
                                            return@forEachLine
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                val remainingValues = allValues.toString().split(regex)
                for (value in remainingValues) {
                    if (value.isNotEmpty()) {
                        rgb[colorCounter++] = (value.toFloat() * 255 / maxColorValue).toInt()

                        if (colorCounter == 3) {
                            pixels[heightCounter * width + widthCounter++] =
                                android.graphics.Color.rgb(rgb[0], rgb[1], rgb[2])
                            colorCounter = 0

                            if (widthCounter == width) {
                                widthCounter = 0
                                heightCounter++
                            }

                            if (heightCounter == height) {
                                break
                            }
                        }
                    }
                }

                bitmap.setPixels(pixels, 0, width, 0, 0, width, height)
            }
            "P6" -> {
//                val bytesPerPixel = 3
//                val byteArray = ByteArray(width * height * bytesPerPixel)
//                stream.read(byteArray)
//                val buffer = ByteBuffer.wrap(byteArray)
//                val pixelLine = IntArray(width)
//                for (y in 0 until height) {
//                    for (x in 0 until width) {
//                        val r = buffer.get().toInt() and 0xFF
//                        val g = buffer.get().toInt() and 0xFF
//                        val b = buffer.get().toInt() and 0xFF
//                        pixelLine[x] = android.graphics.Color.rgb(r, g, b)
//                    }
//                        bitmap.setPixels(pixelLine, 0, width, 0, y, width, 1)
//                }
                val bytesPerPixel = 3
                val bufferSize = width * bytesPerPixel * 10 // Adjust the block size as needed
                val byteBuffer = ByteArray(bufferSize)
                var currentRow = 0
                while (currentRow < height) {
                    val bytesRead = stream.read(byteBuffer)
                    if (bytesRead == -1) break

                    val buffer = ByteBuffer.wrap(byteBuffer)
                    val rowsToProcess = minOf(10, height - currentRow)

                    for (row in 0 until height) {
                        if (row >= rowsToProcess) break
                        val pixelLine = IntArray(width)
                        for (col in 0 until width) {
                            val r = buffer.get().toInt() and 0xFF
                            val g = buffer.get().toInt() and 0xFF
                            val b = buffer.get().toInt() and 0xFF
                            pixelLine[col] = android.graphics.Color.rgb(r, g, b)
                        }
                        bitmap.setPixels(pixelLine, 0, width, 0, currentRow, width, 1)
                        currentRow++
                    }
                }
            }
        }
        return bitmap.asImageBitmap()
    }
    

    fun resetBitmap() {
        _imageBitmap.value = null
    }

    fun resetJpegUri() {
        _jpegUri.value = null
    }

    fun switchJpegDataToSave(data: Pair<String, Int>?) {
        _jpegDataToSave.value = data
    }
}