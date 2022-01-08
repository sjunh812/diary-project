package org.sjhstudio.diary.helper

import android.content.Context
import org.sjhstudio.diary.custom.MsgDialog

class DialogUtils {

    companion object {
        /**
         * 주소록권한요청 다이얼로그
         * (구글드라이브)
         */
        fun showGoogleDrivePermissionDialog(context: Context, confirm: () -> Unit) {
            MsgDialog(context, "권한 안내", "구글 드라이브를 이용한 백업을 위해\n주소록 권한이 필요합니다.")
                .apply {
                    show();
                    onlyYesBtn();
                    setCancel(false);
                    setYesBtnText("확인");
                    setOnYesBtnClickListener {
                        confirm()
                        dismiss()
                    }
                }
        }
    }
}