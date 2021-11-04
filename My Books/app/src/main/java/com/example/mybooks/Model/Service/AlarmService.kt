package com.example.mybooks.Model.Service

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.net.Uri

class AlarmService {
    companion object {
        private var instance: AlarmService? = null
        fun getInstance(): AlarmService {
            if (instance == null) {
                instance = AlarmService()
            }

            return instance!!
        }
    }

    fun setAlarm(context: Context, times: Long, codeId: Int) {
        val alarmManager: AlarmManager =
            context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, AlarmReceiver::class.java)
        val pendingIntent =
            PendingIntent.getBroadcast(context, codeId, intent, PendingIntent.FLAG_ONE_SHOT)
        intent.data = Uri.parse("custom://" + System.currentTimeMillis())
        alarmManager.set(AlarmManager.RTC_WAKEUP, times, pendingIntent)
    }

}