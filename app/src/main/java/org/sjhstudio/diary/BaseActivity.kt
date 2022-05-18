package org.sjhstudio.diary

import android.content.Context
import android.content.res.Configuration
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.MenuItem
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import org.sjhstudio.diary.helper.MyTheme
import org.sjhstudio.diary.utils.Pref

open class BaseActivity: AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        MyTheme.applyTheme(this)
    }

    override fun attachBaseContext(newBase: Context?) {
        val newOverride = Configuration(newBase?.resources?.configuration)

        // default 0.9f
        when(Pref.getPFontSize(newBase)) {
            0 -> newOverride.fontScale = 0.6f
            1 -> newOverride.fontScale = 0.75f
            2 -> newOverride.fontScale = 0.9f
            3 -> newOverride.fontScale = 1.05f
            4 -> newOverride.fontScale = 1.2f
        }

        applyOverrideConfiguration(newOverride)
        super.attachBaseContext(newBase)
    }

    private fun adjustFontScale(configuration: Configuration?) {
        configuration?.let {
            it.fontScale = 1.0F
            val metrics: DisplayMetrics = resources.displayMetrics
            val wm: WindowManager = getSystemService(Context.WINDOW_SERVICE) as WindowManager
            wm.defaultDisplay.getMetrics(metrics)
            metrics.scaledDensity = configuration.fontScale * metrics.density

            baseContext.applicationContext.createConfigurationContext(it)
            baseContext.resources.displayMetrics.setTo(metrics)

        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            android.R.id.home -> {
                finish()
                return true
            }
        }

        return super.onOptionsItemSelected(item)
    }
}