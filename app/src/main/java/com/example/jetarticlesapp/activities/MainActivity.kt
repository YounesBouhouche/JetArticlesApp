package com.example.jetarticlesapp.activities

import android.os.Build
import android.os.Bundle
import android.os.StrictMode
import android.os.StrictMode.ThreadPolicy
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.jetarticlesapp.components.main.App
import com.example.jetarticlesapp.functions.EnableEdgeToEdge
import com.example.jetarticlesapp.functions.parseSharedContent
import com.example.jetarticlesapp.ui.theme.AppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        StrictMode.setThreadPolicy(ThreadPolicy.Builder().permitAll().build())
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S)
            requestPermissions(arrayOf(android.Manifest.permission.SCHEDULE_EXACT_ALARM).apply {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
                        plus(android.Manifest.permission.POST_NOTIFICATIONS)
                }, 0)
        setContent {
            EnableEdgeToEdge()
            AppTheme {
                App(intent.parseSharedContent())
            }
        }
    }
}