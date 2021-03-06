package org.sjhstudio.diary.helper;

import android.content.Context;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatDelegate;

import org.sjhstudio.diary.utils.Pref;
import org.sjhstudio.diary.R;

public class MyTheme {

    public static void applyTheme(@NonNull Context context) {
        int font;  // THE얌전해진언니체
        int fontSize;   // 보통
        int mode;   // 시스템

        font = Pref.getPFontKey(context);
        mode = Pref.getPModeKey(context);
        fontSize = Pref.getPFontSize(context);
        applyDarkMode(context, mode);
        applyTheme(context, font, fontSize);
    }

    public static void applyDarkMode(Context context, int modeIndex) {
        Pref.setPModeKey(context, modeIndex);

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
        Pref.setPFontKey(context, font);
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
