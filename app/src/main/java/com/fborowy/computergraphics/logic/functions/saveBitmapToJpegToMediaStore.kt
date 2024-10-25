package com.fborowy.computergraphics.logic.functions

import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import java.io.File

fun saveBitmapToJpegToMediaStore(context: Context, bitmap: Bitmap, filename: String, quality: Int) {
    val contentResolver = context.contentResolver

    val contentValues = ContentValues().apply {
        put(MediaStore.MediaColumns.DISPLAY_NAME, "$filename.jpeg")
        put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
    }

    val imageUri = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) { // for API 29 and above (Android 10+)
        contentValues.put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS)
        contentResolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, contentValues)
    } else {
        val downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
        val file = File(downloadsDir, "$filename.jpeg")
        contentValues.put(MediaStore.MediaColumns.DATA, file.absolutePath)
        contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
    }

    imageUri?.let { uri ->
        contentResolver.openOutputStream(uri)?.use { outputStream ->
            bitmap.compress(
                Bitmap.CompressFormat.JPEG,
                quality,
                outputStream
            )
        }
    }
}