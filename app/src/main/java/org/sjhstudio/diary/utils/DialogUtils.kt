package org.sjhstudio.diary.utils

import android.content.Context
import org.sjhstudio.diary.custom.CustomGPSDialog
import org.sjhstudio.diary.custom.MsgDialog
import org.sjhstudio.diary.Pref
import org.sjhstudio.diary.custom.CustomDialog
import org.sjhstudio.diary.custom.CustomStopWriteDialog

/**
 * 다이얼로그 관리
 */
class DialogUtils {

    companion object {
        /**
         * GPS 요청 다이얼로그
         */
        fun showGPSDialog(context: Context, yes: () -> Unit) {
            val can = Pref.getPAskLocation(context) // 다시묻지않기.

            if(!can) return

            CustomGPSDialog(context).apply {
                show()
                isChecked = !can
                setYesButtonOnClickListener {
                    dismiss()
                    yes()
                    Pref.setPAskLocation(context, !isChecked)
                }
            }
        }

        /**
         * 저장공간권한 요청 다이얼르그
         */
        fun showStoragePermissionDialog(context: Context, yes: () -> Unit) {
            MsgDialog(context, "권한 안내", "사진을 추가하기 위해\n저장공간 권한이 필요합니다.")
                .apply {
                    show()
                    setYesBtnText("허용")
                    setNoBtnText("취소")
                    setOnYesBtnClickListener {
                        dismiss()
                        yes()
                    }
                }
        }

        /**
         * 주소록권한 요청 다이얼로그
         * (구글드라이브)
         */
        fun showGoogleDrivePermissionDialog(context: Context, confirm: () -> Unit) {
            MsgDialog(context, "권한 안내", "구글 드라이브를 이용한 백업을 위해\n주소록 권한이 필요합니다.")
                .apply {
                    show()
                    onlyYesBtn()
                    setCancel(false)
                    setYesBtnText("확인")
                    setOnYesBtnClickListener {
                        dismiss()
                        confirm()
                    }
                }
        }

        /**
         * 사진추가 다이얼로그
         */
        fun showAddPhotoDialog(context: Context, camara: () -> Unit, album: () -> Unit) {
            CustomDialog(context).apply {
                show()
                setCameraButtonOnClickListener {
                    dismiss()
                    camara()
                }
                setAlbumButtonOnClickListener {
                    dismiss()
                    album()
                }
            }
        }

        /**
         * 일기작성 그만두기 다이얼로그
         */
        fun showStopWriteDialog(context: Context, back: () -> Unit) {
            CustomStopWriteDialog(context).apply {
                show()
                setBackButtonOnClickListener {
                    dismiss()
                    back()
                }
            }
        }
    }
}