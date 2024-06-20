package com.example.capstone_project.helper

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.ContextWrapper
import android.graphics.Color
import android.os.Build
import androidx.core.app.NotificationCompat
import com.example.capstone_project.R

class NotificationHelper(base: Context?) : ContextWrapper(base) {

    private val channelId = "channelID"
    private val channelName = "channelName"

    init {
        createChannel()
    }

    private fun createChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            var channel = NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_DEFAULT)
            channel.enableLights(true)
            channel.enableVibration(true)
            channel.lightColor = Color.GREEN
            channel.lockscreenVisibility = Notification.VISIBILITY_PRIVATE

            getManager().createNotificationChannel(channel)
        }
    }

    fun getManager() : NotificationManager {
        return getSystemService(NOTIFICATION_SERVICE) as NotificationManager
    }

    fun getChannelNotification(time: String?) : NotificationCompat.Builder {
        return NotificationCompat.Builder(applicationContext, channelId)
            .setContentTitle(getString(R.string.alarm_title))
            .setContentText(String.format("%s\n%s", getString(R.string.alarm_text),time))
            .setSmallIcon(R.drawable.outline_add_alert_24)
    }
}