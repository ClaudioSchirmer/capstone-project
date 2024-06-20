package com.example.capstone_project

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.app.NotificationCompat
import com.example.capstone_project.helper.NotificationHelper
import java.util.Calendar

class AlarmReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {

        Log.d(this::class.simpleName, "Notification Alarm Start")

        var calendar: Calendar = Calendar.getInstance()
        var notificationHelper: NotificationHelper = NotificationHelper(context)
        var nb: NotificationCompat.Builder = notificationHelper.getChannelNotification(calendar.time.toString())
        notificationHelper.getManager().notify(1, nb.build())

        Log.d(this::class.simpleName, "Notification Alarm End")
    }
}