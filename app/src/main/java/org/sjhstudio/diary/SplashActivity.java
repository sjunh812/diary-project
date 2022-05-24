package org.sjhstudio.diary;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import org.sjhstudio.diary.helper.MyTheme;
import org.sjhstudio.diary.utils.BaseActivity;
import org.sjhstudio.diary.utils.Pref;

@SuppressLint("CustomSplashScreen")
public class SplashActivity extends BaseActivity {

    private static final String TAG = "SplashActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        String password = Pref.getPPassword(this);
        Log.e(TAG, "password : " + password);

        new Handler().postDelayed(()->{
            Intent intent;

            if(Pref.getPUsePw(this)) intent = new Intent(this, MainPasswordActivity.class);
            else intent = new Intent(this, MainActivity.class);

            startActivity(intent);
            finish();
        }, 800);    // 0.8초 뒤, 메인 액티비티로 전환
    }

}