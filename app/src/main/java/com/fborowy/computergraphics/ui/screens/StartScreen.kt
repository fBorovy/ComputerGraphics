package com.fborowy.computergraphics.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.fborowy.computergraphics.R

@Composable
fun StartScreen(
    onNewCanvasClick: () -> Unit,
    onLoadCanvasFromFileClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxSize(),
        verticalAlignment = Alignment.CenterVertically
    ) {


        Box(
            modifier = Modifier.weight(1f),
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = Modifier
                    .clickable {
                        onNewCanvasClick()
                    },
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    painter = painterResource(R.drawable.add_canvas),
                    contentDescription = stringResource(R.string.add_canva),
                    tint = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(7.dp))
                Text(
                    text = stringResource(R.string.add_canva),
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }


        Box(
            modifier = Modifier.weight(1f),
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = Modifier.clickable {
                    onLoadCanvasFromFileClick()
                },
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    painter = painterResource(R.drawable.upload_file),
                    contentDescription = stringResource(R.string.upload_canva_file),
                    tint = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(7.dp))
                Text(
                    text = stringResource(R.string.upload_canva_file),
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}