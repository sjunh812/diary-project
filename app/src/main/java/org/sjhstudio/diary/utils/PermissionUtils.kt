package org.sjhstudio.diary.utils

import android.content.Context
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat

object PermissionUtils {

    // 위치
    fun checkLocationPermission(context: Context): Boolean {
        return (ContextCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_FINE_LOCATION)
            == PackageManager.PERMISSION_GRANTED
            || ContextCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_COARSE_LOCATION)
            == PackageManager.PERMISSION_GRANTED)
    }

    // 카메라
    fun checkCameraPermission(context: Context): Boolean {
        return (ContextCompat.checkSelfPermission(context, android.Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_GRANTED)
    }

    // 저장공간
    fun checkStoragePermission(context: Context): Boolean {
        return (ContextCompat.checkSelfPermission(context, android.Manifest.permission.READ_EXTERNAL_STORAGE)
            == PackageManager.PERMISSION_GRANTED
            && ContextCompat.checkSelfPermission(context, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
            == PackageManager.PERMISSION_GRANTED)
    }

    // 주소록
    fun checkGoogleDrivePermission(context: Context): Boolean {
        return (ContextCompat.checkSelfPermission(context, android.Manifest.permission.GET_ACCOUNTS)
                == PackageManager.PERMISSION_GRANTED)
    }

}