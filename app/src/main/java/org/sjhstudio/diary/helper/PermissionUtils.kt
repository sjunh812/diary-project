package org.sjhstudio.diary.helper

import android.content.Context
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat

class PermissionUtils {

    companion object {
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