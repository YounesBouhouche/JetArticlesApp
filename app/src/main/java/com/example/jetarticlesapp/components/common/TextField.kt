package com.example.jetarticlesapp.components.common

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextOverflow

@Composable
fun TextField(
    value: String,
    onValueChange: ((String) -> Unit)?,
    modifier: Modifier = Modifier,
    readOnly: Boolean = false,
    enabled: Boolean = true,
    isError: Boolean = false,
    singleLine: Boolean = true,
    label: String? = null,
    supportingText: String? = null,
    placeholder: String? = null,
    icon: ImageVector? = null
) {
    val source = remember { MutableInteractionSource() }
    val focused = source.collectIsFocusedAsState()
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange?: {},
        modifier = modifier,
        interactionSource = source,
        readOnly = readOnly,
        enabled = enabled,
        isError = isError,
        supportingText = supportingText?.run {
            {
                Text(this)
            }
        },
        label = label?.run {
            {
                Text(
                    text = this,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        },
        placeholder = placeholder?.run {
            {
                Text(
                    text = this,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        },
        leadingIcon = icon?.run {
            {
                Icon(icon, null)
            }
        },
        trailingIcon = {
            if (focused.value and value.isNotEmpty())
                IconButton(onClick = { onValueChange?.run { this("") } }) {
                    Icon(Icons.Default.Clear, null)
                }
        },
        singleLine = singleLine
    )
}