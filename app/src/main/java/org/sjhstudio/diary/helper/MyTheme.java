package org.sjhstudio.diary.helper;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatDelegate;

import org.sjhstudio.diary.R;

public class MyTheme {
    /** 상수 **/
    private static final String LOG = "MyTheme";
    public static final String SHARED_PREFERENCES_NAME = "pref";
    public static final String FONT_KEY = "font_key";
    public static final String MODE_KEY = "mode_key";
    public static final String PASSWORD = "password";
    public static final String ASK_LOCATION = "ask_location";

    public static void applyTheme(@NonNull Context context) {
        int fontIndex = -1;     // default (THE얌전해진언니체)
        int modeIndex = 0;      // default

        SharedPreferences pref = context.getSharedPreferences(SHARED_PREFERENCES_NAME, Activity.MODE_PRIVATE);
        if(pref != null) {
            fontIndex = pref.getInt(FONT_KEY, -1);
            modeIndex = pref.getInt(MODE_KEY, 0);
        }

        applyDarkmode(context, modeIndex);
        applyTheme(context, fontIndex);
    }

    public static void applyDarkmode(Context context, int modeIndex) {
        SharedPreferences pref = context.getSharedPreferences(SHARED_PREFERENCES_NAME, Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putInt(MODE_KEY, modeIndex);
        editor.commit();

        if(modeIndex == 0) {        // 시스템 설정에 따른 테마모드
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_AUTO_BATTERY);
            }
        } else if(modeIndex == 1){  // 라이트 모드
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        } else if(modeIndex == 2) { // 다크 모드
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        }
    }

    public static void applyTheme(@NonNull Context context, int fontIndex) {
        SharedPreferences pref = context.getSharedPreferences(SHARED_PREFERENCES_NAME, Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putInt(FONT_KEY, fontIndex);
        editor.commit();

        if(fontIndex == 100) {
            context.setTheme(R.style.Theme_BasicDiaryProject);
            return;
        }

        if (fontIndex == -1) {
            context.setTheme(R.style.Theme_DiaryProject);
            return;
        }

        if (fontIndex == 0) {
            context.setTheme(R.style.Theme_DiaryProject1);
            return;
        }

        if (fontIndex == 1) {
            context.setTheme(R.style.Theme_DiaryProject2);
            return;
        }

        if (fontIndex == 2) {
            context.setTheme(R.style.Theme_DiaryProject3);
            return;
        }

        if (fontIndex == 3) {
            context.setTheme(R.style.Theme_DiaryProject4);
            return;
        }

        if (fontIndex == 4) {
            context.setTheme(R.style.Theme_DiaryProject5);
            return;
        }

        if (fontIndex == 5) {
            context.setTheme(R.style.Theme_DiaryProject6);
            return;
        }

        if (fontIndex == 6) {
            context.setTheme(R.style.Theme_DiaryProject7);
            return;
        }

        if (fontIndex == 7) {
            context.setTheme(R.style.Theme_DiaryProject8);
            return;
        }

        if (fontIndex == 8) {
            context.setTheme(R.style.Theme_DiaryProject9);
            return;
        }

        if(fontIndex == 9) {
            context.setTheme(R.style.Theme_DiaryProject10);
            return;
        }
    }
}
