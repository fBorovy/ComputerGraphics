package com.fborowy.computergraphics.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.unit.dp
import com.fborowy.computergraphics.ui.theme.Typography

@Composable
fun CGTextField(
    value: String,
    onValueChange: (String) -> Unit
) {
    BasicTextField(
        value = value,
        onValueChange = { onValueChange(it) },
        modifier = Modifier
            .width(80.dp)
            .clip(RoundedCornerShape(3.dp))
            .background(MaterialTheme.colorScheme.tertiary)
            .padding(2.dp),
        cursorBrush = SolidColor(MaterialTheme.colorScheme.secondary),
        singleLine = true,
        textStyle = Typography.bodySmall,
    )
}