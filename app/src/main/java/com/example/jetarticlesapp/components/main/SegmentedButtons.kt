package com.example.jetarticlesapp.components.main

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun <T>SingleChoiceSegmentedButtons(
    items: List<T>,
    buttonContent: @Composable (T) -> Unit,
    selectedIndex: Int,
    onSelectedIndexChange: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(modifier.fillMaxWidth(),
        contentAlignment = Alignment.Center) {
        SingleChoiceSegmentedButtonRow(Modifier.fillMaxWidth(.75f)) {
            items.forEachIndexed { index, item ->
                SegmentedButton(
                    shape = SegmentedButtonDefaults.itemShape(index = index, count = items.size),
                    onClick = {
                        onSelectedIndexChange(index)
                    },
                    selected = selectedIndex == index
                ) {
                    buttonContent(item)
                }
            }
        }
    }
}