package org.sjhstudio.diary

import android.annotation.SuppressLint
import android.os.Bundle
import android.widget.Switch
import androidx.appcompat.widget.Toolbar
import org.sjhstudio.diary.utils.Pref

class SettingsActivity: BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
        initUi()
    }

    @SuppressLint("UseSwitchCompatOrMaterialCode")
    private fun initUi() {
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        toolbar.title = "기타 설정"
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val skipNoteSwitch = findViewById<Switch>(R.id.skip_note_switch)
        val skip = Pref.getPSkipNote(this)
        skipNoteSwitch.apply {
            println("xxx 일기 3줄 보기(최초) : $skip")
            isChecked = skip
            setOnCheckedChangeListener { buttonView, isChecked ->
                println("xxx 일기 3줄 보기 : $isChecked")
                Pref.setPSkipNote(this@SettingsActivity, isChecked)
            }
        }
    }

}