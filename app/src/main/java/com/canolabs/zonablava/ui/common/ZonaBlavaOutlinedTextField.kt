package com.canolabs.zonablava.ui.common

import androidx.compose.runtime.Composable
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import com.canolabs.zonablava.R
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.canolabs.zonablava.ui.robotoFamily

@Composable
fun ZonaBlavaOutlinedTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    placeholder: String? = null
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        textStyle = TextStyle(
            fontSize = 24.sp,
            fontFamily = robotoFamily,
        ),
        colors = OutlinedTextFieldDefaults.colors(
            focusedLabelColor = colorResource(id = R.color.md_theme_light_primary),
            focusedBorderColor = colorResource(id = R.color.md_theme_light_primary),
            unfocusedBorderColor = colorResource(id = R.color.md_theme_light_onSurface),
            cursorColor = colorResource(id = R.color.md_theme_light_primary),
        ),
        singleLine = true,
        modifier = modifier
            .padding(16.dp)
            .fillMaxWidth()
            .height(80.dp),
        shape = RoundedCornerShape(16.dp),
        placeholder = {
            if (placeholder != null) {
                Text(
                    text = placeholder,
                    fontSize = 24.sp,
                    textAlign = TextAlign.Center
                )
            }
        }
    )
}