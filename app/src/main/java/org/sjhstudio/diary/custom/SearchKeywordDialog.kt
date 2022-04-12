package org.sjhstudio.diary.custom

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import org.sjhstudio.diary.R

class SearchKeywordDialog(context: Context): Dialog(context) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dialog_search_keyword)

        findViewById<Button>(R.id.search_btn).setOnClickListener { dismiss() }
        findViewById<Button>(R.id.cancel_btn).setOnClickListener { dismiss() }
    }

    fun setCancel(cancel: Boolean) = setCancelable(cancel)

    fun getKeyword(): String {
        return findViewById<EditText>(R.id.search_et).text.toString()
    }

    fun setOnSearchBtnClickListener(listener: View.OnClickListener) {
        findViewById<Button>(R.id.search_btn).setOnClickListener(listener)
    }

    fun setOnCancelBtnClickListener(listener: View.OnClickListener) {
        findViewById<Button>(R.id.cancel_btn).setOnClickListener(listener)
    }
}