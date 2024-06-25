package com.example.jetarticlesapp.functions

import kotlin.math.roundToInt

fun Float.scaleTo(x: Float): Float = (this / x).roundToInt() * x