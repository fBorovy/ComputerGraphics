package com.fborowy.computergraphics.logic.functions

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri

fun loadBitmapFromUri(context: Context, jpegUri: Uri): Bitmap? {
    return try {
        val inputStream = context.contentResolver.openInputStream(jpegUri)
        BitmapFactory.decodeStream(inputStream)
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}