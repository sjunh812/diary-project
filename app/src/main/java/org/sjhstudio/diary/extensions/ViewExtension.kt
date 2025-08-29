package org.sjhstudio.diary.extensions

import android.view.View
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.marginEnd
import androidx.core.view.marginStart
import androidx.core.view.updatePadding

/**
 * 시스템 바 (상태 바, 네비게이션 바) 패딩 적용
 */
fun View.enableSystemBarPadding() {
    ViewCompat.setOnApplyWindowInsetsListener(this) { view, insets ->
        val systemBarsInsets = insets.getInsets(WindowInsetsCompat.Type.systemBars())
        view.updatePadding(
            left = view.marginStart,
            top = systemBarsInsets.top,
            right = view.marginEnd,
            bottom = systemBarsInsets.bottom
        )
        WindowInsetsCompat.CONSUMED
    }
}