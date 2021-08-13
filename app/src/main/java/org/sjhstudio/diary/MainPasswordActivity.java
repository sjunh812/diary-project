package org.sjhstudio.diary;

import androidx.appcompat.app.AppCompatActivity;

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

import org.sjhstudio.diary.helper.MyTheme;

public class MainPasswordActivity extends AppCompatActivity {
    private TextView subTitleTextView;

    /** passwrod image **/
    private ImageView passwordImage1;
    private ImageView passwordImage2;
    private ImageView passwordImage3;
    private ImageView passwordImage4;

    /** data **/
    private String password = "";
    private String input = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MyTheme.applyTheme(this);
        setContentView(R.layout.activity_main_password);

        SharedPreferences pref = getSharedPreferences(MyTheme.SHARED_PREFERENCES_NAME, Activity.MODE_PRIVATE);
        password = pref.getString(MyTheme.PASSWORD, "1111");
        init();
    }

    private void init() {
        subTitleTextView = findViewById(R.id.sub_title);
        passwordImage1 = findViewById(R.id.password_image_1);
        passwordImage2 = findViewById(R.id.password_image_2);
        passwordImage3 = findViewById(R.id.password_image_3);
        passwordImage4 = findViewById(R.id.password_image_4);

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
        if (input.equals(password)) {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            finish();
        } else {
            subTitleTextView.setText("비밀번호가 일치하지 않습니다");
            new Handler().postDelayed(new Runnable() {              // 0.1초 delay
                @Override
                public void run() {
                    initPasswordImages();
                }
            }, 100);
            input = "";
        }
    }

    private void setPasswordImages(String curInput) {
        switch (curInput.length()) {
            case 1:
                passwordImage1.setImageResource(R.drawable.circle_purple);
                break;
            case 2:
                passwordImage2.setImageResource(R.drawable.circle_purple);
                break;
            case 3:
                passwordImage3.setImageResource(R.drawable.circle_purple);
                break;
            case 4:
                passwordImage4.setImageResource(R.drawable.circle_purple);
                break;
        }
    }

    private void erasePasswordImage(int position) {
        switch (position) {
            case 0:
                passwordImage1.setImageResource(R.drawable.circle_light_purple);
                break;
            case 1:
                passwordImage2.setImageResource(R.drawable.circle_light_purple);
                break;
            case 2:
                passwordImage3.setImageResource(R.drawable.circle_light_purple);
                break;
            case 3:
                passwordImage4.setImageResource(R.drawable.circle_light_purple);
                break;
        }
    }

    private void initPasswordImages() {
        passwordImage1.setImageResource(R.drawable.circle_light_purple);
        passwordImage2.setImageResource(R.drawable.circle_light_purple);
        passwordImage3.setImageResource(R.drawable.circle_light_purple);
        passwordImage4.setImageResource(R.drawable.circle_light_purple);
    }

    private class NumberKeypadClickListener implements View.OnClickListener {
        private int number;

        public NumberKeypadClickListener(int number) {
            this.number = number;
        }

        @Override
        public void onClick(View v) {
            if(input.length() <= 3) {
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