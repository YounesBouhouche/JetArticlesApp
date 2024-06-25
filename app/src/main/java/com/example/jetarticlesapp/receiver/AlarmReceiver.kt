package com.example.jetarticlesapp.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class AlarmReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val notificationUtils =
            NotificationUtils(
                context,
                intent.getStringExtra("text") ?: "Some article",
                intent.getIntExtra("id", 0)
            )
        val notification = notificationUtils.getNotificationBuilder().build()
        notificationUtils.getManager().notify(150, notification)
    }
}