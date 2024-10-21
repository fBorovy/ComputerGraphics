package com.fborowy.computergraphics.data

import kotlinx.serialization.Serializable

@Serializable
data class PrimitiveDataToSave(
    val offsets: List<Pair<Pair<Float, Float>, Pair<Float, Float>>>,
    val types: List<Int>
)
