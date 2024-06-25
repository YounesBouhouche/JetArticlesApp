package com.example.jetarticlesapp.components.common

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import com.example.jetarticlesapp.functions.Icon
import com.example.jetarticlesapp.functions.IconButton

data class RadiosGroup(
    val buttons: List<Pair<ImageVector, String>>,
    val selected: Int,
    val onSelectedChange: (Int) -> Unit,
)

@Composable
fun TopAppBarActions(
    expanded: Boolean,
    onExpandedChange: (Boolean) -> Unit,
    icon: ImageVector,
    dismissWhenClick: Boolean = false,
    groups: List<RadiosGroup>
) {
    Box(
        modifier = Modifier
            .wrapContentSize(Alignment.TopStart)
    ) {
        IconButton(icon) {
            onExpandedChange(true)
        }
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { onExpandedChange(false) }
        ) {
            groups.forEachIndexed { index, group ->
                group.buttons.forEachIndexed { i, btn ->
                    DropdownMenuItem(
                        text = { Text(btn.second) },
                        onClick = {
                            group.onSelectedChange(i)
                            if (dismissWhenClick) onExpandedChange(false)
                        },
                        leadingIcon = {
                            RadioButton(
                                selected = group.selected == i,
                                onClick = {
                                    group.onSelectedChange(i)
                                    if (dismissWhenClick) onExpandedChange(false)
                                }
                            )
                        },
                        trailingIcon = {
                            Icon(btn.first)
                        }
                    )
                }
                if (index < groups.size - 1) HorizontalDivider()
            }
        }
    }
}