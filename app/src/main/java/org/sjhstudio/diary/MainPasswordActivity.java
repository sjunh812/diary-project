package org.sjhstudio.diary;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.biometric.BiometricPrompt;
import androidx.core.content.ContextCompat;

import org.sjhstudio.diary.helper.MyTheme;
import org.sjhstudio.diary.utils.BaseActivity;
import org.sjhstudio.diary.utils.Pref;
import org.sjhstudio.diary.utils.Utils;

public class MainPasswordActivity extends BaseActivity {

    private static final String TAG = "MainPasswordActivity";

    private TextView subTitleTextView;

    private ImageView pwImg1;
    private ImageView pwImg2;
    private ImageView pwImg3;
    private ImageView pwImg4;

    private String password = "";
    private String input = "";
    private Boolean canInput = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_password);

        password = Pref.getPPassword(this);
        init();
        setBiometric();
    }

    private void setBiometric() {
        BiometricPrompt biometricPrompt = new BiometricPrompt(this, ContextCompat.getMainExecutor(this), new BiometricPrompt.AuthenticationCallback() {
            @Override
            public void onAuthenticationError(int errorCode, @NonNull CharSequence errString) {
                super.onAuthenticationError(errorCode, errString);
                Log.e(TAG, "지문인증 에러");
            }

            @Override
            public void onAuthenticationSucceeded(@NonNull BiometricPrompt.AuthenticationResult result) {
                super.onAuthenticationSucceeded(result);
                Log.e(TAG, "지문인증 성공");
                Intent intent = new Intent(MainPasswordActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }

            @Override
            public void onAuthenticationFailed() {
                super.onAuthenticationFailed();
                Log.e(TAG, "지문인증 실패");
            }
        });

        BiometricPrompt.PromptInfo promptInfo = new BiometricPrompt.PromptInfo.Builder()
                .setTitle("잠금 해제")
                .setNegativeButtonText("비밀번호 사용하기")
                .build();

        if(Pref.getPFingerPrint(this)) biometricPrompt.authenticate(promptInfo);
    }

    private void init() {
        subTitleTextView = findViewById(R.id.sub_title);
        pwImg1 = findViewById(R.id.password_image_1);
        pwImg2 = findViewById(R.id.password_image_2);
        pwImg3 = findViewById(R.id.password_image_3);
        pwImg4 = findViewById(R.id.password_image_4);

        Button keypad0 = findViewById(R.id.keypad_0);
        Button keypad1 = findViewById(R.id.keypad_1);
        Button keypad2 = findViewById(R.id.keypad_2);
        Button keypad3 = findViewById(R.id.keypad_3);
        Button keypad4 = findViewById(R.id.keypad_4);
        Button keypad5 = findViewById(R.id.keypad_5);
        Button keypad6 = findViewById(R.id.keypad_6);
        Button keypad7 = findViewById(R.id.keypad_7);
        Button keypad8 = findViewById(R.id.keypad_8);
        Button keypad9 = findViewById(R.id.keypad_9);
        ImageButton eraseKeypad = findViewById(R.id.keypad_erase);

        keypad0.setOnClickListener(new NumberKeypadClickListener(0));
        keypad1.setOnClickListener(new NumberKeypadClickListener(1));
        keypad2.setOnClickListener(new NumberKeypadClickListener(2));
        keypad3.setOnClickListener(new NumberKeypadClickListener(3));
        keypad4.setOnClickListener(new NumberKeypadClickListener(4));
        keypad5.setOnClickListener(new NumberKeypadClickListener(5));
        keypad6.setOnClickListener(new NumberKeypadClickListener(6));
        keypad7.setOnClickListener(new NumberKeypadClickListener(7));
        keypad8.setOnClickListener(new NumberKeypadClickListener(8));
        keypad9.setOnClickListener(new NumberKeypadClickListener(9));
        eraseKeypad.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(input.length() > 0) {
                    erasePasswordImage(input.length() - 1);
                    input = input.substring(0, input.length() - 1);
                }
            }
        });
    }

    private void comparePassword() {
        if (input.equals(password)) {   // 인증성공
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            finish();
        } else {    // 인증실패
            subTitleTextView.setText("비밀번호가 일치하지 않습니다");
            Utils.INSTANCE.startVibrator(this, 500, 50, true);
            canInput = false;

            // 0.5초 delay
            new Handler().postDelayed(() -> {
                subTitleTextView.setText("비밀번호를 다시 입력해주세요");
                pwImg1.setImageResource(R.drawable.circle_light_purple);
                pwImg2.setImageResource(R.drawable.circle_light_purple);
                pwImg3.setImageResource(R.drawable.circle_light_purple);
                pwImg4.setImageResource(R.drawable.circle_light_purple);
                canInput = true;
                input = "";
            }, 500);
        }
    }

    private void setPasswordImages(String curInput) {
        switch (curInput.length()) {
            case 1:
                pwImg1.setImageResource(R.drawable.circle_purple);
                break;
            case 2:
                pwImg2.setImageResource(R.drawable.circle_purple);
                break;
            case 3:
                pwImg3.setImageResource(R.drawable.circle_purple);
                break;
            case 4:
                pwImg4.setImageResource(R.drawable.circle_purple);
                break;
        }
    }

    private void erasePasswordImage(int position) {
        switch (position) {
            case 0:
                pwImg1.setImageResource(R.drawable.circle_light_purple);
                break;
            case 1:
                pwImg2.setImageResource(R.drawable.circle_light_purple);
                break;
            case 2:
                pwImg3.setImageResource(R.drawable.circle_light_purple);
                break;
            case 3:
                pwImg4.setImageResource(R.drawable.circle_light_purple);
                break;
        }
    }

    private class NumberKeypadClickListener implements View.OnClickListener {
        private int number;

        public NumberKeypadClickListener(int number) {
            this.number = number;
        }

        @Override
        public void onClick(View v) {
            if(canInput && input.length() <= 3) {
                input += number;
                Log.d("LOG", "input : " + number);
                setPasswordImages(input);

                if(input.length() == 4) {
                    comparePassword();
                }
            }
        }
    }
}