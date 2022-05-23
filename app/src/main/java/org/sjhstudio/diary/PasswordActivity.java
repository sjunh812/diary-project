package org.sjhstudio.diary;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import org.sjhstudio.diary.custom.CustomPasswordCautionDialog;
import org.sjhstudio.diary.helper.MyTheme;
import org.sjhstudio.diary.utils.BaseActivity;
import org.sjhstudio.diary.utils.Utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;

public class PasswordActivity extends BaseActivity {
    private TextView subTitleTextView;

    private ImageView passwordImage1;
    private ImageView passwordImage2;
    private ImageView passwordImage3;
    private ImageView passwordImage4;

    private String input = "";
    private String reInput = "";
    private Boolean canInput = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password);

        init();
    }

    private void init() {
        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        toolbar.setTitle("잠금 설정");
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        subTitleTextView = findViewById(R.id.sub_title);
        passwordImage1 = findViewById(R.id.password_image_1);
        passwordImage2 = findViewById(R.id.password_image_2);
        passwordImage3 = findViewById(R.id.password_image_3);
        passwordImage4 = findViewById(R.id.password_image_4);
        ArrayList<Button> keypadArrayList = new ArrayList<>(Arrays.asList(findViewById(R.id.keypad_0), findViewById(R.id.keypad_1),
                findViewById(R.id.keypad_2), findViewById(R.id.keypad_3), findViewById(R.id.keypad_4), findViewById(R.id.keypad_5),
                findViewById(R.id.keypad_6), findViewById(R.id.keypad_7), findViewById(R.id.keypad_8), findViewById(R.id.keypad_9)));

        for(int i = 0; i < keypadArrayList.size(); i++) {
            keypadArrayList.get(i).setOnClickListener(new NumberKeypadClickListener(i));
        }

        findViewById(R.id.keypad_erase).setOnClickListener(v -> {
            if(input.length() == 4) {   // 비밀번호를 다시 입력하는 경우
                if(reInput.length() > 0) {
                    erasePasswordImage(reInput.length() - 1);
                    reInput = reInput.substring(0, reInput.length() - 1);
                }
            } else {    // 비밀번호를 처음 입력하는 경우
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
            Utils.INSTANCE.startVibrator(this, 500, 50, true);
            canInput = false;

            initPasswordImages(500);

            input = "";
            reInput = "";
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

    private void initPasswordImages(long delay) {
        new Handler().postDelayed(() -> {
            canInput = true;
            passwordImage1.setImageResource(R.drawable.circle_light_purple);
            passwordImage2.setImageResource(R.drawable.circle_light_purple);
            passwordImage3.setImageResource(R.drawable.circle_light_purple);
            passwordImage4.setImageResource(R.drawable.circle_light_purple);
        }, delay);
    }

    private void startPasswordCautionDialog() {
        CustomPasswordCautionDialog dialog = new CustomPasswordCautionDialog(this, this);
        dialog.show();
    }

    private class NumberKeypadClickListener implements View.OnClickListener {

        private final int number;

        public NumberKeypadClickListener(int number) {
            this.number = number;
        }

        @Override
        public void onClick(View v) {
            if(!canInput) return;

            if(input.length() <= 3) {
                input += number;
                setPasswordImages(input);

                if(input.length() == 4) {
                    canInput = false;
                    initPasswordImages(100);
                    subTitleTextView.setText("다시 한번 입력해주세요");
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