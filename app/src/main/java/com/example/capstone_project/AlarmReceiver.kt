package com.example.capstone_project

import android.Manifest
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.capstone_project.main.MainActivity

class AlarmReceiver : BroadcastReceiver() {

    companion object{
        const val CHANNEL_ID = "channel"
    }

    override fun onReceive(context: Context?, intent: Intent?) {

        intent!!.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        val pendingIntent = PendingIntent.getActivity(context, 0,
            Intent(context, MainActivity::class.java),
            PendingIntent.FLAG_IMMUTABLE)

        val builder = NotificationCompat.Builder(context!!, CHANNEL_ID)
            .setSmallIcon(R.drawable.outline_add_alert_24)
            .setContentTitle(context.getString(R.string.alarm_title))
            .setContentText(context.getString(R.string.alarm_text))
            .setAutoCancel(true)
            .setDefaults(NotificationCompat.DEFAULT_ALL)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)

        val notificationManager = NotificationManagerCompat.from(context)

        if (ActivityCompat.checkSelfPermission( context, Manifest.permission.POST_NOTIFICATIONS ) != PackageManager.PERMISSION_GRANTED ) {
            return
        }

        notificationManager.notify(1, builder.build())

        Log.d(this::class.simpleName, "Notification Alarm")
    }
}