package org.sjhstudio.diary;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import org.sjhstudio.diary.custom.CustomPasswordCautionDialog;
import org.sjhstudio.diary.helper.MyTheme;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;

public class PasswordActivity extends AppCompatActivity {
    private TextView subTitleTextView;

    /** password image **/
    private ImageView passwordImage1;
    private ImageView passwordImage2;
    private ImageView passwordImage3;
    private ImageView passwordImage4;

    /** data **/
    private ArrayList<Button> keypadArrayList;
    private String input = "";
    private String reInput = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MyTheme.applyTheme(this);
        setContentView(R.layout.activity_password);
        init();
    }

    private void init() {
        // toolbar
        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        toolbar.setTitle("잠금 설정");
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

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
        keypadArrayList = new ArrayList<>(Arrays.asList(keypad0, keypad1, keypad2, keypad3, keypad4, keypad5, keypad6, keypad7, keypad8, keypad9));
        ImageButton eraseKeypad = findViewById(R.id.keypad_erase);

        for(int i = 0; i < keypadArrayList.size(); i++) {
            keypadArrayList.get(i).setOnClickListener(new NumberKeypadClickListener(i));
        }

        eraseKeypad.setOnClickListener(v -> {
            if(input.length() == 4) {       // 비밀번호를 다시 입력하는 경우
                if(reInput.length() > 0) {
                    erasePasswordImage(reInput.length() - 1);
                    reInput = reInput.substring(0, reInput.length() - 1);
                }
            } else {                        // 비밀번호를 처음 입력하는 경우
                if(input.length() > 0) {
                    erasePasswordImage(input.length() - 1);
                    input = input.substring(0, input.length() - 1);
                }
            }
        });
    }

    private void comparePassword() {
        if (input.equals(reInput)) {
            SharedPreferences pref = getSharedPreferences(MyTheme.SHARED_PREFERENCES_NAME, Activity.MODE_PRIVATE);
            SharedPreferences.Editor editor = pref.edit();
            editor.putString(MyTheme.PASSWORD, input);
            editor.apply();

            startPasswordCautionDialog();
        } else {
            subTitleTextView.setText("비밀번호가 일치하지 않습니다\n처음부터 다시 시도해주세요");

            initPasswordImages();
            input = "";
            reInput = "";
        }
    }

    /** password 입력시 change password image (purple) **/
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

    /** position 에 위치한 password image 초기화 (light purple)**/
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

    /** 모든 password image 초기화 (light purple) **/
    private void initPasswordImages() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                passwordImage1.setImageResource(R.drawable.circle_light_purple);
                passwordImage2.setImageResource(R.drawable.circle_light_purple);
                passwordImage3.setImageResource(R.drawable.circle_light_purple);
                passwordImage4.setImageResource(R.drawable.circle_light_purple);
            }
        }, 100);
    }

    private void startPasswordCautionDialog() {
        CustomPasswordCautionDialog dialog = new CustomPasswordCautionDialog(this, this);
        dialog.show();
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
                setPasswordImages(input);

                if(input.length() == 4) {
                    subTitleTextView.setText("다시 한번 입력해주세요");
                    initPasswordImages();
                }
            } else {
                if(reInput.length() <= 3) {
                    reInput += number;
                    setPasswordImages(reInput);

                    if(reInput.length() == 4) {
                        comparePassword();
                    }
                }
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if(id == android.R.id.home) {       // back
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}