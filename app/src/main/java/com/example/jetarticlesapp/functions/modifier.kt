package com.example.jetarticlesapp.functions

import androidx.compose.foundation.gestures.detectDragGesturesAfterLongPress
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.round
import androidx.compose.ui.unit.toIntRect

private fun LazyGridState.gridItemKeyAtPosition(hitPoint: Offset): Int? =
    layoutInfo.visibleItemsInfo.find { itemInfo ->
        itemInfo.size.toIntRect().contains(hitPoint.round() - itemInfo.offset)
    }?.key as? Int

private fun LazyGridState.indexOf(key: Int): Int? =
    layoutInfo.visibleItemsInfo.find {
        it.key == key
    }?.index

private fun LazyGridState.keyOf(index: Int): Int = layoutInfo.visibleItemsInfo[index].key as Int

private fun LazyGridState.keysOf(indices: IntRange) = indices.map { keyOf(it) }

private fun LazyGridState.setKeysOf(indices: IntRange) = keysOf(indices).toSet()

fun Modifier.articlesDragHandler(
    lazyGridState: LazyGridState,
    startKey: Int,
    selectedIds: MutableState<Set<Int>>
) = this.pointerInput(Unit) {
    var initialKey: Int? = null
    var currentKey: Int? = null
    detectDragGesturesAfterLongPress(
        onDragStart = {
            initialKey = startKey
            currentKey = startKey
            selectedIds.value += startKey
        },
        onDragCancel = { initialKey = null },
        onDragEnd = { initialKey = null },
        onDrag = { change, _ ->
            if (initialKey != null) {
                lazyGridState.gridItemKeyAtPosition(change.position)?.let { key ->
                    if (currentKey != key) {
                        selectedIds.value = selectedIds.value
                            .minus(
                                lazyGridState.setKeysOf(lazyGridState.indexOf(initialKey!!)!!..lazyGridState.indexOf(currentKey!!)!!)
                            )
                            .minus(
                                lazyGridState.setKeysOf(lazyGridState.indexOf(currentKey!!)!!..lazyGridState.indexOf(initialKey!!)!!)
                            )
                            .plus(
                                lazyGridState.setKeysOf(lazyGridState.indexOf(initialKey!!)!!..lazyGridState.indexOf(key)!!)
                            )
                            .plus(
                                lazyGridState.setKeysOf(lazyGridState.indexOf(key)!!..lazyGridState.indexOf(initialKey!!)!!)
                            )
                        currentKey = key
                    }
                }
            }
        }
    )
}