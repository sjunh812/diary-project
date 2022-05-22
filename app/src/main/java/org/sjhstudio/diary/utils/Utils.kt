package org.sjhstudio.diary.utils

import android.annotation.SuppressLint
import android.content.Context
import android.location.LocationManager
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import java.text.SimpleDateFormat
import java.util.*

object Utils {

    val calendar = Calendar.getInstance()

    @SuppressLint("SimpleDateFormat") val yearFormat = SimpleDateFormat("yyyy")
    @SuppressLint("SimpleDateFormat") val monthFormat = SimpleDateFormat("MM")
    @SuppressLint("SimpleDateFormat") val dayFormat = SimpleDateFormat("dd")
    @SuppressLint("SimpleDateFormat") val dateFormat = SimpleDateFormat("yyyy년 MM월 dd일")
    @SuppressLint("SimpleDateFormat") val dateFormat2 = SimpleDateFormat("yyyy-MM-dd")
    @SuppressLint("SimpleDateFormat") val dateFormat3 = SimpleDateFormat("yyyyMMdd")
    @SuppressLint("SimpleDateFormat") val timeFormat = SimpleDateFormat("a HH:mm")
    @SuppressLint("SimpleDateFormat") val timeFormat2 = SimpleDateFormat("HH:mm:SS")

    fun getYear(date: Date): Int {
        calendar.time = date
        return calendar.get(Calendar.YEAR)
    }

    fun getMonth(date: Date): Int {
        calendar.time = date
        return calendar.get(Calendar.MONTH) + 1
    }

    fun getDay(date: Date): Int {
        calendar.time = date
        return calendar.get(Calendar.DAY_OF_MONTH)
    }

    fun getCurrentYear(): Int {
        return Integer.parseInt(yearFormat.format(Date()))
    }

    fun getCurrentMonth(): Int {
        return Integer.parseInt(monthFormat.format(Date()))
    }

    // GPS 체크
    fun checkGPS(context: Context): Boolean {
        val lm = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        if(lm.isProviderEnabled(LocationManager.GPS_PROVIDER)) return true
        return false
    }

    // 진동
    fun startVibrator(context: Context, time: Long, amplitude: Int, underOreo: Boolean = false) {
        val vibrator =
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                val vm = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
                vm.defaultVibrator
            } else {
                context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
            }

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrator.vibrate(VibrationEffect.createOneShot(time, amplitude))
        } else {
            if(underOreo) vibrator.vibrate(time)
        }
    }

}