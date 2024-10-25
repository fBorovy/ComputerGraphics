package com.fborowy.computergraphics.data
import com.fborowy.computergraphics.R

enum class Tool(val id: Int, val stringResource: Int, val painter: Int) {
    PRIMITIVES(1, R.string.primitives, R.drawable.primitives),
    PPM_JPEG(2, R.string.ppm_jpeg, R.drawable.pixels),
    FILE(3, R.string.files, R.drawable.save),
    COLORIZE(4, R.string.colorize, R.drawable.colorize),
}