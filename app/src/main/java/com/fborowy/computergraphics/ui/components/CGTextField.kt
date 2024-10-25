package com.fborowy.computergraphics.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.traceEventEnd
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.unit.dp
import com.fborowy.computergraphics.ui.theme.Typography

@Composable
fun CGTextField(
    modifier: Modifier = Modifier,
    value: String,
    isActive: Boolean,
    onValueChange: (String) -> Unit,
    onFocusChanged: (Boolean) -> Unit = {},
) {
    val style = if (isActive) Typography.bodySmall.copy(color = MaterialTheme.colorScheme.secondary) else Typography.bodySmall

    BasicTextField(
        value = value,
        onValueChange = { onValueChange(it) },
        modifier = modifier
            .clip(RoundedCornerShape(3.dp))
            .background(MaterialTheme.colorScheme.tertiary)
            .padding(2.dp)
            .onFocusChanged { focused ->
                if (focused.isFocused) {
                    onFocusChanged(true)
                }
            },
        cursorBrush = SolidColor(MaterialTheme.colorScheme.secondary),
        singleLine = true,
        textStyle = style,
    )
}