package org.sjhstudio.diary.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class Pref {

    public static final String PREF_NAME = "pref";
    public static final String ASK_LOCATION = "ask_location";   // GPS 알림
    public static final String FONT_SIZE = "font_size"; // 폰트크기
    public static final String PERMISSION_GUIDE = "permission_guide";   // 권한안내(최초 1회만)
    public static final String USE_PW = "use_pw";   // 비밀번호 사용
    public static final String FINGER_PRINT = "finger_print";   // 지문 사용
    public static final String SKIP_NOTE = "skip_note"; // 일기요약(3줄)

    public static Boolean getPAskLocation(Context context) {
        SharedPreferences pref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        if(pref != null) return pref.getBoolean(ASK_LOCATION, true);

        return true;
    }

    public static void setPAskLocation(Context context, boolean flag) {
        SharedPreferences pref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        if(pref != null) pref.edit().putBoolean(ASK_LOCATION, flag).apply();
    }

    public static int getPFontSize(Context context) {
        SharedPreferences pref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        if(pref != null) return pref.getInt(FONT_SIZE, 2);  // 2가 보통 사이즈

        return 0;
    }

    public static void setPFontSize(Context context, int size) {
        SharedPreferences pref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        if(pref != null) pref.edit().putInt(FONT_SIZE, size).apply();
    }

    public static Boolean getPPermissionGuide(Context context) {
        SharedPreferences pref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        if(pref != null) return pref.getBoolean(PERMISSION_GUIDE, false);

        return false;
    }

    public static void setPPermissionGuide(Context context, Boolean flag) {
        SharedPreferences pref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        if(pref != null) pref.edit().putBoolean(PERMISSION_GUIDE, flag).apply();
    }

    public static Boolean getPUsePw(Context context) {
        SharedPreferences pref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        if(pref != null) return pref.getBoolean(USE_PW, false);

        return false;
    }

    public static void setPUsePw(Context context, Boolean flag) {
        SharedPreferences pref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        if(pref != null) pref.edit().putBoolean(USE_PW, flag).apply();
    }

    public static Boolean getPFingerPrint(Context context) {
        SharedPreferences pref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        if(pref != null) return pref.getBoolean(FINGER_PRINT, false);

        return false;
    }

    public static void setPFingerPrint(Context context, Boolean flag) {
        SharedPreferences pref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        if(pref != null) pref.edit().putBoolean(FINGER_PRINT, flag).apply();
    }

    public static Boolean getPSkipNote(Context context) {
        SharedPreferences pref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        if(pref != null) return pref.getBoolean(SKIP_NOTE, true);

        return true;
    }

    public static void setPSkipNote(Context context, Boolean flag) {
        SharedPreferences pref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        if(pref != null) pref.edit().putBoolean(SKIP_NOTE, flag).apply();
    }

}