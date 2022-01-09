package org.sjhstudio.diary;

import android.content.Context;
import android.content.SharedPreferences;

public class Pref {

    public static final String PREF_NAME = "pref";              // SharedPreferences 이름
    public static final String ASK_LOCATION = "ask_location";   // 위치설정 알림 여부

    public static Boolean getPAskLocation(Context context) {
        SharedPreferences pref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        if(pref != null) return pref.getBoolean(ASK_LOCATION, true);

        return true;
    }

    public static void setPAskLocation(Context context, boolean flag) {
        SharedPreferences pref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        if(pref != null) pref.edit().putBoolean(ASK_LOCATION, flag).apply();
    }
}
