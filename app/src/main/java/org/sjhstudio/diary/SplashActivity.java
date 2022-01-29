package org.sjhstudio.diary;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;

import org.sjhstudio.diary.helper.MyTheme;

public class SplashActivity extends BaseActivity {
    private Handler handler = new Handler();    // 0.5초 딜레이를 위한 핸들러
    private String password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        SharedPreferences pref = getSharedPreferences(MyTheme.SHARED_PREFERENCES_NAME, Activity.MODE_PRIVATE);
        if(pref != null) {
            password = pref.getString(MyTheme.PASSWORD, "");
        }

        handler.postDelayed(()->{
            Intent intent;

            if(Pref.getPUsePw(this)) intent = new Intent(this, MainPasswordActivity.class);
            else intent = new Intent(this, MainActivity.class);

            startActivity(intent);
            finish();
        }, 800);    // 0.8초 뒤, 메인 액티비티로 전환
    }
}