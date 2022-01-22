package org.sjhstudio.diary.utils

import android.content.Context
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat

/**
 * 권한관리
 */
class PermissionUtils {

    companion object {
        /**
         * 위치 권한확인
         */
        fun checkLocationPermission(context: Context): Boolean {
            if(ContextCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_COARSE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
                return true
            }

            return false
        }

        /**
         * 카메라 권한확인
         */
        fun checkCameraPermission(context: Context): Boolean {
            return (ContextCompat.checkSelfPermission(context, android.Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_GRANTED)
        }

        /**
         * 저장공간 권한확인
         */
        fun checkStoragePermission(context: Context): Boolean {
            if(ContextCompat.checkSelfPermission(context, android.Manifest.permission.READ_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(context, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED) {
                return true
            }

            return false
        }

        /**
         * 구글드라이브 권한확인
         * (주소록 - GET_ACCOUNTS)
         */
        fun checkGoogleDrivePermission(context: Context): Boolean {
            if(ContextCompat.checkSelfPermission(context, android.Manifest.permission.GET_ACCOUNTS)
                == PackageManager.PERMISSION_GRANTED) {
                return true
            }

            return false
        }
    }
}