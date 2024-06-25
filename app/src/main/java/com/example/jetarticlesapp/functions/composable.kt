package com.example.jetarticlesapp.functions

import android.graphics.Color
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.SliderColors
import androidx.compose.material3.SliderDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import com.example.jetarticlesapp.datastores.SettingsDataStore
import kotlin.math.max
import kotlin.math.min

@Composable
fun LazyListState.isScrollingUp(): Boolean {
    var previousIndex by remember(this) { mutableIntStateOf(firstVisibleItemIndex) }
    var previousScrollOffset by remember(this) { mutableIntStateOf(firstVisibleItemScrollOffset) }
    return remember(this) {
        derivedStateOf {
            if (previousIndex != firstVisibleItemIndex) {
                previousIndex > firstVisibleItemIndex
            } else {
                previousScrollOffset >= firstVisibleItemScrollOffset
            }.also {
                previousIndex = firstVisibleItemIndex
                previousScrollOffset = firstVisibleItemScrollOffset
            }
        }
    }.value
}


@Composable
fun Icon(imageVector: ImageVector, modifier: Modifier = Modifier) = androidx.compose.material3.Icon(imageVector, null, modifier)

@Composable
fun IconButton(imageVector: ImageVector, enabled: Boolean = true, active: Boolean = false, onClick: () -> Unit) =
    androidx.compose.material3.IconButton(
        onClick = onClick,
        enabled = enabled,
        colors = if (active) IconButtonDefaults.filledIconButtonColors() else IconButtonDefaults.iconButtonColors()
    ) {
        Icon(imageVector = imageVector)
    }

@Composable
fun ComponentActivity.EnableEdgeToEdge() =
    with (
        SettingsDataStore(LocalContext.current).theme.collectAsState(initial = "system")
                to isSystemInDarkTheme()
    ) {
        enableEdgeToEdge(SystemBarStyle.auto(Color.TRANSPARENT, Color.TRANSPARENT) {
            when (first.value) {
                "light" -> false
                "dark" -> true
                else -> second
            }
        })
    }


val statusBarHeight
    @Composable
    get() = with(LocalDensity.current) { WindowInsets.systemBars.getTop(this).toDp() }

val navBarHeight
    @Composable
    get() = with(LocalDensity.current) { WindowInsets.systemBars.getBottom(this).toDp() }

@Composable
fun Slider(
    value: Float,
    onValueChange: (Float) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    valueRange: ClosedFloatingPointRange<Float> = 0f..1f,
    steps: Int = 0,
    onValueChangeFinished: (() -> Unit)? = null,
    colors: SliderColors = SliderDefaults.colors(),
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() }
) {
    androidx.compose.material3.Slider(
        value,
        { onValueChange(it.scaleTo((valueRange.endInclusive - valueRange.start) / max(steps, 1))) },
        modifier,
        enabled,
        valueRange,
        steps,
        onValueChangeFinished,
        colors,
        interactionSource
    )
}