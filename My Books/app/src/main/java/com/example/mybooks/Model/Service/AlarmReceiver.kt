package com.example.mybooks.Model.Service

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.core.content.ContextCompat
import com.example.mybooks.Model.SharedPreferences.SharedPreferences
import java.util.*

class AlarmReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        val sharedPreferences = SharedPreferences.getInstance()
        var dia: Int


        if (context?.let { sharedPreferences.getActiveSincronized(it) } == true) {
            val today: Calendar = Calendar.getInstance()
   //         dia = today.get(Calendar.DAY_OF_MONTH) + 1
//            if (dia > getDaysByMonth(today.get(Calendar.MONTH) + 1))
//                dia = 1
           // today.set(Calendar.DAY_OF_MONTH, dia)
            //println("${today.get(Calendar.DAY_OF_MONTH)}")
            today.set(Calendar.HOUR_OF_DAY, 24)
            today.set(Calendar.MINUTE,  0)
            today.set(Calendar.SECOND, 0)
            println("${today.timeInMillis} ${today.get(Calendar.DAY_OF_MONTH)}")
            sharedPreferences.saveCountSincronized(context)
            AlarmService.getInstance().setAlarm(context, today.timeInMillis, 1)

            val service = Intent(context, Notificacion::class.java)
            service.setData(Uri.parse("custom://" + System.currentTimeMillis()))
            ContextCompat.startForegroundService(context, service)
            println("ALARMA RECIBIDAAAAAA")

        }

    }

            /**OBTENER LA CANTIDAD DE DIAS QUE CONTIENE ESE MES*/
    fun getDaysByMonth(month: Int): Int {
        when (month) {
            1, 3, 5, 7, 8, 10, 12 -> return 31
            4, 6, 9, 11 -> return 30
            2 -> {
                val date = Date()
                return if (isLeap(date.year)) 29 else 28
            }

            else -> return 0
        }
    }

    /**SABER SI EL AÑO ES BISIESTO*/
    fun isLeap(year: Int): Boolean {
        val calendar = GregorianCalendar()
        return calendar.isLeapYear(year)
    }
}