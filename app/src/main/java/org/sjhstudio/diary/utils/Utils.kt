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

class Utils {

    companion object {
        @SuppressLint("SimpleDateFormat")
        val yearFormat = SimpleDateFormat("yyyy")
        @SuppressLint("SimpleDateFormat")
        val monthFormat = SimpleDateFormat("MM")

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

        fun getCurrentYear(): Int {
            return Integer.parseInt(yearFormat.format(Date()))
        }

        fun getCurrentMonth(): Int {
            return Integer.parseInt(monthFormat.format(Date()))
        }
    }

}