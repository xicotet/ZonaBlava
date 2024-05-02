package com.canolabs.zonablava.ui.common

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.dp
import com.canolabs.zonablava.R

@Composable
fun ZonaBlavaErrorText(text: String) {
    Row(
        modifier = Modifier.padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Filled.Warning,
            contentDescription = "Caution icon",
            tint = colorResource(id = R.color.md_theme_light_error),
            modifier = Modifier.padding(8.dp)
        )
        Text(
            text = text,
            color = colorResource(id = R.color.md_theme_light_error),
            modifier = Modifier.padding()
        )
    }
}