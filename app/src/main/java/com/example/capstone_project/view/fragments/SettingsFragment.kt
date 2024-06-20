package com.example.capstone_project.view.fragments

import android.Manifest
import android.app.AlarmManager
import android.app.PendingIntent
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import com.example.capstone_project.AlarmReceiver
import com.example.capstone_project.R
import com.example.capstone_project.databinding.FragmentSettingsBinding
import com.example.capstone_project.main.MainActivity
import java.text.SimpleDateFormat
import java.util.Calendar

class SettingsFragment : Fragment() {

    private lateinit var binding: FragmentSettingsBinding
    private lateinit var calendar: Calendar

    companion object {
        const val DENIED = "denied"
        const val EXPLAINED = "explained"
        const val ALARM_HOUR = "alarm_hour"
        const val ALARM_MIN = "alarm_min"
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSettingsBinding.inflate(layoutInflater)
        calendar = Calendar.getInstance()

        getAlarmTimeFromPreferences()

        binding.btnSetAlarm.setOnClickListener {
            showTimePicker()
        }

        binding.btnCancelAlarm.setOnClickListener {
            cancelAlarm()
        }

        return binding.root
    }

    private fun getAlarmTimeFromPreferences() {

        binding = FragmentSettingsBinding.inflate(layoutInflater)

        var alarmHour: String? = MainActivity.preferences.getString(ALARM_HOUR, null)
        var alarmMin: String? =  MainActivity.preferences.getString(ALARM_MIN, null)

        if (!alarmHour.isNullOrBlank() && !alarmMin.isNullOrBlank()) {
            calendar = Calendar.getInstance()
            calendar.set(Calendar.HOUR_OF_DAY, alarmHour.toInt())
            calendar.set(Calendar.MINUTE, alarmMin.toInt())
            calendar.set(Calendar.SECOND, 0)
            calendar.set(Calendar.MILLISECOND, 0)
            showAlarmTime()
        }
    }

    private fun setAlarmTimeToPreferences() {
        MainActivity.preferences.setString(ALARM_HOUR, calendar.get(Calendar.HOUR_OF_DAY).toString())
        MainActivity.preferences.setString(ALARM_MIN, calendar.get(Calendar.MINUTE).toString())
    }

    private fun showAlarmTime() {
        binding.tvTime.text = String.format("Every Day at %s", SimpleDateFormat("HH:mm").format(calendar.time))
    }

    private fun showTimePicker() {
        val timeSetListener = TimePickerDialog.OnTimeSetListener { _, hour, minute ->
            calendar.set(Calendar.HOUR_OF_DAY, hour)
            calendar.set(Calendar.MINUTE, minute)
            calendar.set(Calendar.SECOND, 0)
            calendar.set(Calendar.MILLISECOND, 0)

            showAlarmTime()
            setAlarmTimeToPreferences()
            setAlarm()
        }
        TimePickerDialog(
            this.requireContext(),
            timeSetListener,
            calendar.get(Calendar.HOUR_OF_DAY),
            calendar.get(Calendar.MINUTE),
            true
        ).show()
    }

    private fun setAlarm() {
        var alarmManager = requireContext().getSystemService(Context.ALARM_SERVICE) as AlarmManager
        var intent = Intent(requireContext(), AlarmReceiver::class.java)
        var pendingIntent = PendingIntent.getBroadcast(requireContext(), 1, intent, PendingIntent.FLAG_IMMUTABLE)

        alarmManager.cancel(pendingIntent)

        if (calendar.before(Calendar.getInstance())) {
            calendar.add(Calendar.DATE, 1)
        }

        // Set the alarm for Testing
        alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.timeInMillis, pendingIntent)

        // Set the alarm for Repeating
        alarmManager.setRepeating(
            AlarmManager.ELAPSED_REALTIME,
            calendar.timeInMillis,
            AlarmManager.INTERVAL_DAY, // Repeat every day
            //1000 * 60, // Repeat test every 1 minute
            pendingIntent
        )

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            registerForActivityResult.launch(arrayOf(Manifest.permission.POST_NOTIFICATIONS))
            registerForActivityResult.launch(arrayOf(Manifest.permission.SCHEDULE_EXACT_ALARM))
        }

        Toast.makeText(requireContext(), String.format("Alarm Set : %s", SimpleDateFormat("HH:mm").format(calendar.time)), Toast.LENGTH_SHORT).show()
    }

    private fun cancelAlarm() {
        var alarmManager = requireContext().getSystemService(Context.ALARM_SERVICE) as AlarmManager
        var intent = Intent(requireContext(), AlarmReceiver::class.java)
        var pendingIntent = PendingIntent.getBroadcast( requireContext(), 1, intent, PendingIntent.FLAG_IMMUTABLE)

        alarmManager.cancel(pendingIntent)

        binding.tvTime.text = requireContext().getString(R.string.alarm_time)
        Toast.makeText(requireContext(), "Alarm Cancelled", Toast.LENGTH_SHORT).show()
    }

    private val registerForActivityResult = registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
        val deniedPermissionList = permissions.filter { !it.value }.map { it.key }
        when {
            deniedPermissionList.isNotEmpty() -> {
                val map = deniedPermissionList.groupBy { permission ->
                    if (shouldShowRequestPermissionRationale(permission)) DENIED else EXPLAINED
                }
                map[DENIED]?.let {
                    // permission denied
                }
                map[EXPLAINED]?.let {
                    // permission explained
                }
            }
            else -> {
                // permission allowed
            }
        }
    }
}