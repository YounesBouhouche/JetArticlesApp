package com.example.jetarticlesapp.receiver

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.media.RingtoneManager
import androidx.core.app.NotificationCompat
import com.example.jetarticlesapp.R
import com.example.jetarticlesapp.activities.ArticleActivity

class NotificationUtils(base: Context, val text: String, val id: Int) : ContextWrapper(base) {
    private val channelId = "App Alert Notification ID"
    private val name = "App Alert Notification"
    private var manager : NotificationManager? = null
    private val channel = NotificationChannel(channelId, name, NotificationManager.IMPORTANCE_HIGH)
    init {
        channel.enableVibration(true)
        getManager().createNotificationChannel(channel)
    }
    fun getManager() : NotificationManager {
        if (manager == null) manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        return manager as NotificationManager
    }
    fun getNotificationBuilder(): NotificationCompat.Builder {
        val intent = Intent(this, ArticleActivity::class.java).apply {
            putExtra("article", id)
            flags = Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_IMMUTABLE)
        return NotificationCompat.Builder(applicationContext, channelId)
            .setContentTitle("Read article")
            .setContentText(text)
            .setSmallIcon(R.drawable.baseline_article_24)
            .setContentIntent(pendingIntent)
            .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
            .setAutoCancel(true)
    }
}