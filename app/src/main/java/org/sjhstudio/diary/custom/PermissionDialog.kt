package org.sjhstudio.diary.custom

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import org.sjhstudio.diary.R

class PermissionDialog(context: Context): Dialog(context) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dialog_permission)

        findViewById<Button>(R.id.okButton).setOnClickListener { dismiss() }
    }

    fun setCancel(cancel: Boolean) = setCancelable(cancel)

    fun setOnOkBtnClickListener(listener: View.OnClickListener) {
        findViewById<Button>(R.id.okButton).setOnClickListener(listener)
    }
}