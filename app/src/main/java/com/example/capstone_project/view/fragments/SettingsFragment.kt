package com.example.capstone_project.view.fragments

import android.Manifest
import android.app.AlarmManager
import android.app.PendingIntent
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import com.example.capstone_project.AlarmReceiver
import com.example.capstone_project.R
import com.example.capstone_project.databinding.FragmentSettingsBinding
import java.text.SimpleDateFormat
import java.util.Calendar

class SettingsFragment : Fragment() {

    companion object {
        const val DENIED = "denied"
        const val EXPLAINED = "explained"
    }

    private lateinit var binding: FragmentSettingsBinding
    private lateinit var calendar: Calendar
    private lateinit var alarmManager: AlarmManager
    private lateinit var pendingIntent: PendingIntent

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

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSettingsBinding.inflate(layoutInflater)

        binding.btnSetAlarm.setOnClickListener {
            showTimePicker()
        }

        binding.btnCancelAlarm.setOnClickListener {
            cancelAlarm()
        }

        return binding.root
    }

    private fun showTimePicker() {
        calendar = Calendar.getInstance()
        val timeSetListener = TimePickerDialog.OnTimeSetListener { timePicker, hour, minute ->
            calendar.set(Calendar.HOUR_OF_DAY, hour)
            calendar.set(Calendar.MINUTE, minute)
            calendar.set(Calendar.SECOND, 0)
            calendar.set(Calendar.MILLISECOND, 0)
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
        alarmManager = requireContext().getSystemService(Context.ALARM_SERVICE) as AlarmManager
        pendingIntent = PendingIntent.getBroadcast(
            requireContext(),
            0,
            Intent(requireContext(), AlarmReceiver::class.java),
            PendingIntent.FLAG_IMMUTABLE)

        alarmManager.cancel(pendingIntent)

        // Set the alarm for testing
        /*
        alarmManager.set(
            AlarmManager.ELAPSED_REALTIME_WAKEUP,
            SystemClock.elapsedRealtime() + 1000 * 5, // Get the alarm after 5 seconds.
            pendingIntent
        )
         */

        // Set the alarm
        alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.timeInMillis, pendingIntent)
        alarmManager.setRepeating(
            AlarmManager.RTC_WAKEUP,
            calendar.timeInMillis,
            AlarmManager.INTERVAL_DAY, // Repeat every day
            //1000 * 60, // Repeat test every 1 minute
            pendingIntent
        )

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            registerForActivityResult.launch(arrayOf(Manifest.permission.POST_NOTIFICATIONS))
        }

        binding.tvTime.text = String.format("Every Day at %s", SimpleDateFormat("HH:mm").format(calendar.time))
        Toast.makeText(requireContext(), String.format("Alarm Set : %s", SimpleDateFormat("HH:mm").format(calendar.time)), Toast.LENGTH_SHORT).show()
    }

    private fun cancelAlarm() {
        alarmManager = requireContext().getSystemService(Context.ALARM_SERVICE) as AlarmManager
        pendingIntent = PendingIntent.getBroadcast(
            requireContext(),
            0,
            Intent(requireContext(), AlarmReceiver::class.java),
            PendingIntent.FLAG_IMMUTABLE)

        alarmManager.cancel(pendingIntent)

        binding.tvTime.text = requireContext().getString(R.string.alarm_time)
        Toast.makeText(requireContext(), "Alarm Cancelled", Toast.LENGTH_SHORT).show()
    }
}