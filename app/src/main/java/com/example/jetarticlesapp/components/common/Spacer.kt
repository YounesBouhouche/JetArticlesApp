package com.example.jetarticlesapp.components.common

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun NavigationBarSpacer(modifier: Modifier = Modifier) {
    Spacer(modifier.navigationBarsPadding())
}