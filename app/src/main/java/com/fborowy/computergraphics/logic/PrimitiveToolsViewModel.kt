package com.fborowy.computergraphics.logic

import androidx.compose.ui.geometry.Offset
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class PrimitiveToolsViewModel: ViewModel() {

    private var _primitivesOffsetsList = MutableStateFlow<List<Pair<Offset, Offset>>>(emptyList())
    val primitivesOffsetsList: StateFlow<List<Pair<Offset, Offset>>> = _primitivesOffsetsList
    private var _primitivesList = MutableStateFlow<List<Int>>(emptyList())
    val primitivesList: StateFlow<List<Int?>> = _primitivesList

    private var _selectedPrimitive = MutableStateFlow<Pair<Offset, Offset>?>(null)
    val selectedPrimitive: StateFlow<Pair<Offset, Offset>?> = _selectedPrimitive
    private var _selectedPrimitiveType = MutableStateFlow<Int?>(null)
    val selectedPrimitiveType: StateFlow<Int?> = _selectedPrimitiveType

    var canvasStartingPoints: Pair<Offset, Offset>? = null
    private var canvasBottomRightPoint: Offset? = null

    fun findAndSelectPrimitive(point: Offset) {
        for ((index, primitive) in primitivesOffsetsList.value.withIndex()) {
            if (primitive.first == point || primitive.second == point) {
                _selectedPrimitive.value = primitive
                _selectedPrimitiveType.value = primitivesList.value[index]
            }
        }
    }

    fun addPrimitive(x: Offset, y: Offset, type: Int) {
        _primitivesOffsetsList.value += Pair(x, y)
        _primitivesList.value += type
        _selectedPrimitiveType.value = type
        _selectedPrimitive.value = Pair(x, y)
    }

    fun setCanvasStartingPointsOffsetsAndBottomRightCornerOffset(canvasBottomRightCorner: Offset) {
        canvasBottomRightPoint = canvasBottomRightCorner
        canvasStartingPoints = Pair(
            first = Offset(x = canvasBottomRightCorner.x / 2 - canvasBottomRightCorner.x / 15, y = canvasBottomRightCorner.y / 2 - canvasBottomRightCorner.y / 35),
            second = Offset(x = canvasBottomRightCorner.x / 2 + canvasBottomRightCorner.x / 15, y = canvasBottomRightCorner.y / 2 + canvasBottomRightCorner.y / 35),
        )
    }

    fun changeSelectedPrimitiveOffsetCoordinate(a: Boolean, x: Boolean, value: Float) {
        val newValue = if (x) {
            if (value <= canvasBottomRightPoint!!.x) value else canvasBottomRightPoint!!.x
        }
        else {
            if (value <= canvasBottomRightPoint!!.y) value else canvasBottomRightPoint!!.y
        }
        for ((index, primitive) in primitivesOffsetsList.value.withIndex()) {
            if (primitive.first == selectedPrimitive.value!!.first && primitive.second == selectedPrimitive.value!!.second && primitivesList.value[index] == selectedPrimitiveType.value) {
                if (a && x) {
                    _primitivesOffsetsList.value = primitivesOffsetsList.value.toMutableList().apply {
                        this[index] = Pair(first = Offset(x = newValue, y = selectedPrimitive.value!!.first.y), second = selectedPrimitive.value!!.second)
                    }
                    _selectedPrimitive.value = selectedPrimitive.value!!.copy(first = Offset(x = newValue, y = selectedPrimitive.value!!.first.y))
                }
                if (a && !x) {
                    _primitivesOffsetsList.value = primitivesOffsetsList.value.toMutableList().apply {
                        this[index] = Pair(first = Offset(x = this[index].first.x, y = newValue), second = selectedPrimitive.value!!.second)
                    }
                    _selectedPrimitive.value = selectedPrimitive.value!!.copy(first = Offset(x = selectedPrimitive.value!!.first.x, y = newValue))
                }
                if (!a && x) {
                    _primitivesOffsetsList.value = primitivesOffsetsList.value.toMutableList().apply {
                        this[index] = Pair(first = selectedPrimitive.value!!.first, second = Offset(x = newValue, y = this[index].second.y))
                    }
                    _selectedPrimitive.value = selectedPrimitive.value!!.copy(second = Offset(x = newValue, y = selectedPrimitive.value!!.second.y))
                }
                if (!a && !x) {
                    _primitivesOffsetsList.value = primitivesOffsetsList.value.toMutableList().apply {
                        this[index] = Pair(first = selectedPrimitive.value!!.first, second = Offset(x = this[index].second.x, y = newValue))
                    }
                    _selectedPrimitive.value = selectedPrimitive.value!!.copy(second = Offset(x = selectedPrimitive.value!!.second.x, y = newValue))
                }
            }
        }
    }



}