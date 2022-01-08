package org.sjhstudio.diary.custom

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import org.sjhstudio.diary.R

class MsgDialog(context: Context, val title: String, val contents: String): Dialog(context) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.msg_dialog)

        findViewById<TextView>(R.id.title_text).text = title
        findViewById<TextView>(R.id.contents_text).text = contents

        findViewById<Button>(R.id.yes_btn).setOnClickListener { dismiss() }
        findViewById<Button>(R.id.no_btn).setOnClickListener { dismiss() }
    }

    fun setCancel(cancel: Boolean) = setCancelable(cancel)

    fun onlyYesBtn() {
        findViewById<Button>(R.id.no_btn).visibility = View.GONE
    }

    fun setYesBtnText(text: String) {
        findViewById<Button>(R.id.yes_btn).text = text
    }

    fun setOnYesBtnClickListener(listener: View.OnClickListener) {
        findViewById<Button>(R.id.yes_btn).setOnClickListener(listener)
    }
}