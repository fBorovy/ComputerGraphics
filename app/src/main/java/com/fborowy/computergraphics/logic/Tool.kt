package com.fborowy.computergraphics.logic
import com.fborowy.computergraphics.R

enum class Tool(val stringResource: Int, val painter: Int) {
    PRIMITIVES(R.string.primitives, R.drawable.primitives),
    PPM_JPEG(R.string.ppm_jpeg, R.drawable.pixels),
    FILE(R.string.files, R.drawable.save)
}