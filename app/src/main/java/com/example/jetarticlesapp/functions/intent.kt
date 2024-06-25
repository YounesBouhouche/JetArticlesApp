package com.example.jetarticlesapp.functions

import android.content.Intent

fun Intent.parseSharedContent(): String =
    if ((action == Intent.ACTION_SEND) and (type?.startsWith("text/") == true)) getStringExtra(
        Intent.EXTRA_TEXT) ?: ""
    else ""