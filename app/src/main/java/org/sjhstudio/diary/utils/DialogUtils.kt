package org.sjhstudio.diary.utils

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.widget.EditText
import org.sjhstudio.diary.Pref
import org.sjhstudio.diary.custom.*

/**
 * 다이얼로그 관리
 */
class DialogUtils {

    companion object {
        /**
         * 접근권한안내 다이얼로그
         */
        fun showPermissionGuideDialog(context: Context, ok: () -> Unit) {
            PermissionDialog(context).apply {
                show()
                setCancel(false)
                setOnOkBtnClickListener {
                    dismiss()
                    ok()
                }
            }
        }

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
         * 카메라 및 저장공간권한 요청 다이얼르그
         */
        fun showStoragePermissionDialog(context: Context, yes: () -> Unit) {
            MsgDialog(context, "권한 안내", "사진 추가를 위해 카메라 및 저장공간\n권한이 필요합니다.")
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
        fun showGoogleDrivePermissionDialog(context: Context, yes: () -> Unit) {
            MsgDialog(context, "권한 안내", "구글 드라이브를 이용한 백업을 위해\n주소록 권한이 필요합니다.")
                .apply {
                    show()
                    onlyYesBtn()
                    setCancel(false)
                    setYesBtnText("확인")
                    setOnYesBtnClickListener {
                        dismiss()
                        yes()
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

        /**
         * 일기검색 다이얼로그(키워드)
         */
        fun showSearchKeywordDialog(context: Context) {
//            AlertDialog.Builder(context)
//                .setTitle("일기 검색")
//                .setMessage("검색할 내용을 입력해주세요. ex) 여행")
//                .setView(EditText(context))
//                .setPositiveButton("검색", positiveListener)
//                .setNegativeButton("취소", negativeListener)
//                .show()
        }
    }
}