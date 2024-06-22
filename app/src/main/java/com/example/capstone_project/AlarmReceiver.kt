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

        Log.d(this::class.simpleName, "Receive notification alarm")

        var calendar: Calendar = Calendar.getInstance()
        var notificationHelper: NotificationHelper = NotificationHelper(context)
        var nb: NotificationCompat.Builder = notificationHelper.getChannelNotification(calendar.time.toString())
        notificationHelper.getManager().notify(1, nb.build())


        var alarmManager = context?.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        var intent = Intent(context, AlarmReceiver::class.java)
        var pendingIntent = PendingIntent.getBroadcast(context, 1, intent, PendingIntent.FLAG_IMMUTABLE)

        alarmManager.cancel(pendingIntent)

        calendar.add(Calendar.DATE, 1)
        // for test
        //calendar.add(Calendar.MINUTE, 2)

        alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.timeInMillis, pendingIntent)
        Log.d(this::class.simpleName, String.format("Set next alarm : %s", calendar.time.toString()))
    }
}