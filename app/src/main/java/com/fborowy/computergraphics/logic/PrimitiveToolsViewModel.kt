package com.fborowy.computergraphics.logic

import androidx.compose.ui.geometry.Offset
import androidx.lifecycle.ViewModel
import com.fborowy.computergraphics.data.PrimitiveDataToSave
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlin.math.hypot

const val CLICK_DISTANCE_TOLERANCE = 25f
const val MINIMAL_OFFSET_COORDINATE_VALUE = 5f

class PrimitiveToolsViewModel: ViewModel() {

    private var canvasBottomRightPoint: Offset? = null
    private var _primitivesOffsetsList = MutableStateFlow<List<Pair<Offset, Offset>>>(emptyList())
    val primitivesOffsetsList: StateFlow<List<Pair<Offset, Offset>>> = _primitivesOffsetsList
    private var _primitivesList = MutableStateFlow<List<Int>>(emptyList())
    val primitivesList: StateFlow<List<Int>> = _primitivesList

    private var _selectedPrimitive = MutableStateFlow<Pair<Offset, Offset>?>(null)
    val selectedPrimitive: StateFlow<Pair<Offset, Offset>?> = _selectedPrimitive
    private var _selectedPrimitiveType = MutableStateFlow<Int?>(null)
    val selectedPrimitiveType: StateFlow<Int?> = _selectedPrimitiveType
    private var _selectedPrimitiveIndex: MutableStateFlow<Int?> = MutableStateFlow(null)
    val selectedPrimitiveIndex: StateFlow<Int?> = _selectedPrimitiveIndex
    private var _isFirstPointSelected: MutableStateFlow<Boolean?> = MutableStateFlow(null)
    val isFirstPointSelected: StateFlow<Boolean?> = _isFirstPointSelected


    var canvasStartingPoints: Pair<Offset, Offset>? = null


    fun addPrimitive(x: Offset, y: Offset, type: Int) {
        _primitivesOffsetsList.value += Pair(x, y)
        _primitivesList.value += type
        _selectedPrimitiveType.value = type
        _selectedPrimitive.value = Pair(x, y)
        _selectedPrimitiveIndex.value = primitivesOffsetsList.value.lastIndex
        _isFirstPointSelected.value = null
    }

    fun setCanvasStartingPointsOffsetsAndBottomRightCornerOffset(canvasBottomRightCorner: Offset) {
        canvasBottomRightPoint = canvasBottomRightCorner - Offset(MINIMAL_OFFSET_COORDINATE_VALUE, MINIMAL_OFFSET_COORDINATE_VALUE)
        canvasStartingPoints = Pair(
            first = Offset(x = canvasBottomRightCorner.x / 2 - canvasBottomRightCorner.x / 15, y = canvasBottomRightCorner.y / 2 - canvasBottomRightCorner.y / 35),
            second = Offset(x = canvasBottomRightCorner.x / 2 + canvasBottomRightCorner.x / 15, y = canvasBottomRightCorner.y / 2 + canvasBottomRightCorner.y / 35),
        )
    }

    fun changeSelectedPrimitiveOffsetCoordinate(a: Boolean, x: Boolean, value: Float) {
        var newValue = value
        if (x) {
            if (newValue > canvasBottomRightPoint!!.x) newValue = canvasBottomRightPoint!!.x
            if (newValue < MINIMAL_OFFSET_COORDINATE_VALUE) newValue = MINIMAL_OFFSET_COORDINATE_VALUE
        }
        else {
            if (newValue > canvasBottomRightPoint!!.y) newValue = canvasBottomRightPoint!!.y
            if (newValue < MINIMAL_OFFSET_COORDINATE_VALUE) newValue = MINIMAL_OFFSET_COORDINATE_VALUE
        }
        for ((index, primitive) in primitivesOffsetsList.value.withIndex()) {
            if (primitive.first == selectedPrimitive.value!!.first && primitive.second == selectedPrimitive.value!!.second && primitivesList.value[index] == selectedPrimitiveType.value) {
                if (a && x) {
                    _primitivesOffsetsList.value = primitivesOffsetsList.value.toMutableList().apply {
                        this[index] = Pair(first = Offset(x = newValue, y = selectedPrimitive.value!!.first.y), second = selectedPrimitive.value!!.second)
                    }
                    _selectedPrimitive.value = selectedPrimitive.value!!.copy(first = Offset(x = newValue, y = selectedPrimitive.value!!.first.y))
                    _isFirstPointSelected.value = true
                }
                if (a && !x) {
                    _primitivesOffsetsList.value = primitivesOffsetsList.value.toMutableList().apply {
                        this[index] = Pair(first = Offset(x = this[index].first.x, y = newValue), second = selectedPrimitive.value!!.second)
                    }
                    _selectedPrimitive.value = selectedPrimitive.value!!.copy(first = Offset(x = selectedPrimitive.value!!.first.x, y = newValue))
                    _isFirstPointSelected.value = true
                }
                if (!a && x) {

                    _primitivesOffsetsList.value = primitivesOffsetsList.value.toMutableList().apply {
                        this[index] = Pair(first = selectedPrimitive.value!!.first, second = Offset(x = newValue, y = this[index].second.y))
                    }
                    _selectedPrimitive.value = selectedPrimitive.value!!.copy(second = Offset(x = newValue, y = selectedPrimitive.value!!.second.y))
                    _isFirstPointSelected.value = false
                }
                if (!a && !x) {
                    _primitivesOffsetsList.value = primitivesOffsetsList.value.toMutableList().apply {
                        this[index] = Pair(first = selectedPrimitive.value!!.first, second = Offset(x = this[index].second.x, y = newValue))
                    }
                    _selectedPrimitive.value = selectedPrimitive.value!!.copy(second = Offset(x = selectedPrimitive.value!!.second.x, y = newValue))
                    _isFirstPointSelected.value = false
                }
            }
        }
    }

    fun selectPrimitiveByClick(offset: Offset): Boolean? {
        for ((index, primitive) in primitivesOffsetsList.value.withIndex()) {
            var distance = hypot(
                primitive.first.x - offset.x,
                primitive.first.y - offset.y
            )
            if (distance <= CLICK_DISTANCE_TOLERANCE) {
                _selectedPrimitive.value = primitive
                _selectedPrimitiveType.value = primitivesList.value[index]
                _selectedPrimitiveIndex.value = index
                _isFirstPointSelected.value = true
                return true
            }
            distance = hypot(
                primitive.second.x - offset.x,
                primitive.second.y - offset.y
            )
            if (distance <= CLICK_DISTANCE_TOLERANCE) {
                _selectedPrimitive.value = primitive
                _selectedPrimitiveType.value = primitivesList.value[index]
                _selectedPrimitiveIndex.value = index
                _isFirstPointSelected.value = false
                return false
            }
        }
        return null
    }

    fun changeSelectedPrimitiveOffset(a: Boolean, drag: Offset) {
        if (a) {
            var newX = selectedPrimitive.value!!.first.x + drag.x
            if (newX > canvasBottomRightPoint!!.x) newX = canvasBottomRightPoint!!.x
            if (newX < MINIMAL_OFFSET_COORDINATE_VALUE) newX = MINIMAL_OFFSET_COORDINATE_VALUE
            var newY = selectedPrimitive.value!!.first.y + drag.y
            if (newY > canvasBottomRightPoint!!.y) newY = canvasBottomRightPoint!!.y
            if (newY < MINIMAL_OFFSET_COORDINATE_VALUE) newY = MINIMAL_OFFSET_COORDINATE_VALUE
            _selectedPrimitive.value = selectedPrimitive.value!!.copy(first = Offset(x = newX, y = newY))
            _primitivesOffsetsList.value = primitivesOffsetsList.value.toMutableList().apply {
                this[selectedPrimitiveIndex.value!!] = Pair(first = Offset(newX, newY)/*this[selectedPrimitiveIndex.value!!].first + drag*/, second = this[selectedPrimitiveIndex.value!!].second)
            }
        } else {
            var newX = selectedPrimitive.value!!.second.x + drag.x
            if (newX > canvasBottomRightPoint!!.x) newX = canvasBottomRightPoint!!.x
            if (newX < MINIMAL_OFFSET_COORDINATE_VALUE) newX = MINIMAL_OFFSET_COORDINATE_VALUE
            var newY = selectedPrimitive.value!!.second.y + drag.y
            if (newY > canvasBottomRightPoint!!.y) newY = canvasBottomRightPoint!!.y
            if (newY < MINIMAL_OFFSET_COORDINATE_VALUE) newY = MINIMAL_OFFSET_COORDINATE_VALUE
            _selectedPrimitive.value = selectedPrimitive.value!!.copy(second = Offset(x = newX, y = newY))
            _primitivesOffsetsList.value =  primitivesOffsetsList.value.toMutableList().apply {
                this[_selectedPrimitiveIndex.value!!] = Pair(first = this[selectedPrimitiveIndex.value!!].first, second = Offset(newX, newY))
            }
        }
    }

    fun unselectPrimitive() {
        _selectedPrimitiveIndex.value = null
        _selectedPrimitive.value = null
        _selectedPrimitiveType.value = null
        _isFirstPointSelected.value = null
    }

    fun changeSelectedPointOfSelectedPrimitive(first: Boolean?) {
        _isFirstPointSelected.value = first
    }

    fun preparePrimitivesDataToSave(): PrimitiveDataToSave {
        if (primitivesList.value.isEmpty()) return PrimitiveDataToSave(emptyList(), emptyList())
        val offsets: MutableList<Pair<Pair<Float, Float>, Pair<Float, Float>>> = mutableListOf()
        for (primitive in primitivesOffsetsList.value) {
            offsets.add(Pair(Pair(primitive.first.x, primitive.first.y), Pair(primitive.second.x, primitive.second.y)))
        }
        return PrimitiveDataToSave(
            offsets = offsets,
            types = primitivesList.value,
        )

    }

    fun preparePrimitivesDataFromFile(primitives: PrimitiveDataToSave) {
        _primitivesList.value = primitives.types
        for (primitive in primitives.offsets) {
            _primitivesOffsetsList.value += Pair(first = Offset(x = primitive.first.first, y = primitive.first.second), second = Offset(x = primitive.second.first, y = primitive.second.second))
        }

    }

    fun clearCanvas() {
        _primitivesOffsetsList.value = emptyList()
        _primitivesList.value = emptyList()
        _selectedPrimitive.value = null
        _isFirstPointSelected.value = null
    }

}