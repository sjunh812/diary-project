package org.sjhstudio.diary;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import org.sjhstudio.diary.extensions.ViewExtensionKt;
import org.sjhstudio.diary.utils.BaseActivity;
import org.sjhstudio.diary.utils.Pref;

@SuppressLint("CustomSplashScreen")
public class SplashActivity extends BaseActivity {

    private static final String TAG = "SplashActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        ViewExtensionKt.enableSystemBarPadding(findViewById(R.id.root));

        new Handler().postDelayed(() -> {
            Intent intent;

            if (Pref.getPUsePw(this)) intent = new Intent(this, MainPasswordActivity.class);
            else intent = new Intent(this, MainActivity.class);

            startActivity(intent);
            finish();
        }, 800);    // 0.8초 뒤, 메인 액티비티로 전환
    }
}