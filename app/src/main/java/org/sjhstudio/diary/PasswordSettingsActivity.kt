package org.sjhstudio.diary

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.widget.Toolbar
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricManager.Authenticators.BIOMETRIC_STRONG
import org.sjhstudio.diary.helper.MyTheme

class PasswordSettingsActivity :
    BaseActivity(),
    CompoundButton.OnCheckedChangeListener {

    private lateinit var pwSwitch: Switch
    private lateinit var fpSwitch: Switch

    var setPW = false
    var toast: Toast? = null

    private fun showToast(message: String) {
        toast?.cancel()
        toast = Toast.makeText(this, message, Toast.LENGTH_SHORT)
            .apply { show() }
    }

    override fun onDestroy() {
        super.onDestroy()
        toast?.cancel()
    }

    override fun onResume() {
        super.onResume()
        val pref = getSharedPreferences(MyTheme.SHARED_PREFERENCES_NAME, Activity.MODE_PRIVATE)

        if(setPW) {
            setPW = false

            if(pref != null && pref.getString(MyTheme.PASSWORD, "")?.isNotEmpty() == true) {
                findViewById<Switch>(R.id.pw_switch).isChecked = true
            }
        } else {
            pwSwitch.isChecked = Pref.getPUsePw(this)

            if(!isSupportFingerPrint()) {
                findViewById<TextView>(R.id.support_fp_text).visibility = View.VISIBLE
                fpSwitch.isEnabled = false
                Pref.setPFingerPrint(this, false)
            } else {
                findViewById<TextView>(R.id.support_fp_text).visibility = View.GONE
                fpSwitch.isEnabled = true

                if(!pwSwitch.isChecked) {
                    Pref.setPFingerPrint(this, false)
                }
            }

            fpSwitch.isChecked = Pref.getPFingerPrint(this)
        }
    }

    override fun onPause() {
        super.onPause()
        Pref.setPUsePw(this, pwSwitch.isChecked)
        Pref.setPFingerPrint(this,  fpSwitch.isChecked)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_password_settings)
        init()
    }

    private fun init() {
        // 툴바 설정
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        toolbar.title = "잠금 설정"
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        pwSwitch = findViewById(R.id.pw_switch)
        fpSwitch = findViewById(R.id.finger_print_switch)

        // 비밀번호 변경
        findViewById<RelativeLayout>(R.id.change_pw_layout).setOnClickListener {
            startActivity(Intent(this, PasswordActivity::class.java))
        }

        findViewById<Switch>(R.id.pw_switch).setOnCheckedChangeListener(this)
        findViewById<Switch>(R.id.finger_print_switch).setOnCheckedChangeListener(this)
    }

    private fun isSupportFingerPrint(): Boolean {
        val biometricManager = BiometricManager.from(this)

        when(biometricManager.canAuthenticate(BIOMETRIC_STRONG)) {
            BiometricManager.BIOMETRIC_SUCCESS -> return true
            BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE -> return false
            BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE -> return false
            BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED -> return false
        }

        return false
    }

    override fun onCheckedChanged(buttonView: CompoundButton?, isChecked: Boolean) {
        when(buttonView?.id) {
            R.id.pw_switch -> {
                val pref = getSharedPreferences(MyTheme.SHARED_PREFERENCES_NAME, Activity.MODE_PRIVATE)

                if(!isChecked) {
                    findViewById<Switch>(R.id.finger_print_switch).isChecked = false
                } else {
                    if(pref != null && pref.getString(MyTheme.PASSWORD, "").isNullOrEmpty()) {
                        setPW = true
                        showToast("비밀번호를 먼저 생성해주세요.")
                        buttonView.isChecked = false
                        startActivity(Intent(this, PasswordActivity::class.java))
                    }
                }
            }

            R.id.finger_print_switch -> {
                if(findViewById<Switch>(R.id.pw_switch).isChecked && isSupportFingerPrint()) {
                } else {
                    buttonView.isChecked = false
                }
            }
        }
    }
}