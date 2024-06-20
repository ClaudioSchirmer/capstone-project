package com.example.capstone_project

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.app.NotificationCompat
import com.example.capstone_project.helper.NotificationHelper
import java.util.Calendar

class AlarmReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {

        Log.d("y_song", "Notification Alarm Start")

        var calendar: Calendar = Calendar.getInstance()
        var notificationHelper: NotificationHelper = NotificationHelper(context)
        var nb: NotificationCompat.Builder = notificationHelper.getChannelNotification(calendar.time.toString())
        notificationHelper.getManager().notify(1, nb.build())

        Log.d("y_song", "Notification Alarm End")
        Log.d(this::class.simpleName, "Notification Alarm")
    }
}