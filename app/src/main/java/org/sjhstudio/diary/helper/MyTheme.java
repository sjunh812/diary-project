package org.sjhstudio.diary.helper;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatDelegate;

import org.sjhstudio.diary.Pref;
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

        int font = -1;  // THE얌전해진언니체
        int fontSize = 2;   // 보통
        int mode = 0;

        SharedPreferences pref = context.getSharedPreferences(SHARED_PREFERENCES_NAME, Activity.MODE_PRIVATE);
        if(pref != null) {
            font = pref.getInt(FONT_KEY, -1);
            mode = pref.getInt(MODE_KEY, 0);
        }
        fontSize = Pref.getPFontSize(context);

        applyDarkmode(context, mode);
        applyTheme(context, font, fontSize);
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

    public static void applyTheme(@NonNull Context context, int font, int fontSize) {
        SharedPreferences pref = context.getSharedPreferences(SHARED_PREFERENCES_NAME, Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putInt(FONT_KEY, font);
        editor.commit();

        Pref.setPFontSize(context, fontSize);

        switch(fontSize) {
            case 0:
                break;
            case 1:
                break;
            case 2:
                break;
            case 3:
                break;
            case 4:
                break;
        }

        switch(font) {
            case 100:
                context.setTheme(R.style.Theme_BasicDiaryProject);
                break;
            case -1:
                context.setTheme(R.style.Theme_DiaryProject);
                break;
            case 0:
                context.setTheme(R.style.Theme_DiaryProject1);
                break;
            case 1:
                context.setTheme(R.style.Theme_DiaryProject2);
                break;
            case 2:
                context.setTheme(R.style.Theme_DiaryProject3);
                break;
            case 3:
                context.setTheme(R.style.Theme_DiaryProject4);
                break;
            case 4:
                context.setTheme(R.style.Theme_DiaryProject5);
                break;
            case 5:
                context.setTheme(R.style.Theme_DiaryProject6);
                break;
            case 6:
                context.setTheme(R.style.Theme_DiaryProject7);
                break;
            case 7:
                context.setTheme(R.style.Theme_DiaryProject8);
                break;
            case 8:
                context.setTheme(R.style.Theme_DiaryProject9);
                break;
            case 9:
                context.setTheme(R.style.Theme_DiaryProject10);
                break;
            case 10:
                context.setTheme(R.style.Theme_DiaryProject11);
                break;
            case 11:
                context.setTheme(R.style.Theme_DiaryProject12);
                break;
        }
    }
}
