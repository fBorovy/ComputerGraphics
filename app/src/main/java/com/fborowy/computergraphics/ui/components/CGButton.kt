package com.fborowy.computergraphics.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.fborowy.computergraphics.R
import com.fborowy.computergraphics.ui.theme.Typography

@Composable
fun CGButton(
    textResource: Int,
    onClick: () -> Unit,
) {
    Box(
        modifier = Modifier
            .padding(end = 10.dp)
            .clip(RoundedCornerShape(3.dp))
            .background(MaterialTheme.colorScheme.tertiary)
            .padding(horizontal = 9.dp, vertical = 3.dp)
            .clickable { onClick() }
    ) {
        Text(
            text = stringResource(textResource),
            style = Typography.bodySmall
        )

    }
}