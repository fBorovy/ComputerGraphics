package com.fborowy.computergraphics.logic

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.lang.Float.max
import kotlin.math.abs
import kotlin.math.min

class ColorizeViewModel: ViewModel() {

    private var _activeParameter = MutableStateFlow<Int?>(null)
    val activeParameter = _activeParameter
    private var _hue = MutableStateFlow(190f)
    val hue: StateFlow<Float> = _hue
    private var _saturation = MutableStateFlow(0.68f)
    val saturation: StateFlow<Float> = _saturation
    private var _colorValue = MutableStateFlow(1f)
    val colorValue: StateFlow<Float> = _colorValue

    private var _cyan = MutableStateFlow(0.68f)
    val cyan = _cyan
    private var _magenta = MutableStateFlow(0.1183f)
    val magenta = _magenta
    private var _yellow = MutableStateFlow(0f)
    val yellow = _yellow
    private var _key = MutableStateFlow(0f)
    val key = _key

    fun setHue(hue: Float) {
        when {
            hue < 0f -> _hue.value = 0f
            hue > 360f -> _hue.value = 360f
            else -> _hue.value = hue
        }
        updateCMYKValues()
    }
    fun setSaturation(saturation: Float) {
        when {
            saturation < 0f -> _saturation.value = 0f
            saturation > 1f -> _saturation.value = 1f
            else -> _saturation.value = saturation
        }
        updateCMYKValues()
    }
    fun setColorValue(value: Float) {
        when {
            value < 0f -> _colorValue.value = 0f
            value > 1f -> _colorValue.value = 1f
            else -> _colorValue.value = value
        }
        updateCMYKValues()
    }
    fun setActiveParameter(parameter: Int?) {
        _activeParameter.value = parameter
    }
    fun setCyan(cyan: Float) {
        when {
            cyan < 0 -> _cyan.value = 0f
            cyan > 1 -> _cyan.value = 1f
            else -> _cyan.value = cyan
        }
        updateHSVValues()
    }
    fun setMagenta(magenta: Float) {
        when {
            magenta < 0 -> _magenta.value = 0f
            magenta > 1 -> _magenta.value = 1f
            else -> _magenta.value = magenta
        }
        updateHSVValues()
    }
    fun setYellow(yellow: Float) {
        when {
            yellow < 0 -> _yellow.value = 0f
            yellow > 1 -> _yellow.value = 1f
            else -> _yellow.value = yellow
        }
        updateHSVValues()
    }
    fun setKey(key: Float) {
        when {
            key < 0 -> _key.value = 0f
            key > 1 -> _key.value = 1f
            else -> _key.value = key
        }
        updateHSVValues()
    }

    private fun updateCMYKValues() {
        val c = colorValue.value * saturation.value
        val x = c * (1f - abs((hue.value/60) % 2 - 1))
        val m = colorValue.value - c
        var rPrime = 0f
        var gPrime = 0f
        var bPrime = 0f
        when {
            hue.value < 60 -> {
                rPrime = c
                gPrime = x
                bPrime = 0f
            }
            60 <= hue.value && hue.value < 120 -> {
                rPrime = x
                gPrime = c
                bPrime = 0f
            }
            120 <= hue.value && hue.value < 180 -> {
                rPrime = 0f
                gPrime = c
                bPrime = x
            }
            180 <= hue.value && hue.value < 240 -> {
                rPrime = 0f
                gPrime = x
                bPrime = c
            }
            240 <= hue.value && hue.value < 300 -> {
                rPrime = x
                gPrime = 0f
                bPrime = c
            }
            300 <= hue.value -> {
                rPrime = c
                gPrime = 0f
                bPrime = x
            }
        }

        val r = rPrime + m
        val g = gPrime + m
        val b = bPrime + m

        _key.value = 1 - maxOf(r, g, b)
        if (key.value == 1f) {
            _cyan.value = 0f
            _magenta.value = 0f
            _yellow.value = 0f
        } else {
            _cyan.value = (1 - r - key.value) / (1 - key.value)
            _magenta.value = (1 - g - key.value) / (1 - key.value)
            _yellow.value = (1 - b - key.value) / (1 - key.value)
        }
    }

    private fun  updateHSVValues() {
        val r = 1f - min(1f, cyan.value * (1 - key.value) + key.value)
        val g = 1f - min(1f, magenta.value * (1 - key.value) + key.value)
        val b = 1f - min(1f, yellow.value * (1 - key.value) + key.value)
        val maxRGB = maxOf(r, g, b)
        _colorValue.value = maxRGB
        val minRGB = minOf(r, g, b)

        _saturation.value = if (maxRGB == 0f) 0f else (maxRGB-minRGB) / maxRGB
        var hue = when {
            saturation.value == 0f -> 0f
            maxRGB == r -> 60 * ((g - b) / (maxRGB - minRGB) + 6)
            maxRGB == g -> 60 * ((b - r) / (maxRGB - minRGB) + 2)
            maxRGB == b -> 60 * ((r - g) / (maxRGB - minRGB) + 4)
            else -> 0f
        }
        if (hue < 0f) hue += 360f
        if (hue > 360f) hue -= 360
        _hue.value = hue

    }
}