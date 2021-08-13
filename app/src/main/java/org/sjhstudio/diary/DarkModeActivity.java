package org.sjhstudio.diary;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import org.sjhstudio.diary.helper.MyTheme;


public class DarkModeActivity extends AppCompatActivity {
    // UI
    private RadioButton systemModeButton;
    private RadioButton lightModeButton;
    private RadioButton darkModeButton;

    // 데이터
    private int curModeIndex = 0;           // SharedPreferences 를 이용해 가져온 테마모드 index
    private int selectedModeIndex = -1;     // 라디오 버튼으로 선택된 테마모드 index

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MyTheme.applyTheme(this);
        setContentView(R.layout.activity_darkmode);

        // 현재 app 에 설정된 테마모드 인덱스를 SharedPreferences 를 이용해 가져옴
        SharedPreferences pref = getSharedPreferences(MyTheme.SHARED_PREFERENCES_NAME, Activity.MODE_PRIVATE);
        if(pref != null) {
            curModeIndex = pref.getInt(MyTheme.MODE_KEY, 0);
        }

        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        toolbar.setTitle("다크모드");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        RadioGroup modeGroup = (RadioGroup)findViewById(R.id.modeGroup);
        systemModeButton = (RadioButton)findViewById(R.id.systemModeButton);
        lightModeButton = (RadioButton)findViewById(R.id.lightModeButton);
        darkModeButton = (RadioButton)findViewById(R.id.darkModeButton);
        modeGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch(checkedId) {
                    case R.id.systemModeButton:
                        selectedModeIndex = 0;
                        MyTheme.applyDarkmode(getApplicationContext(), selectedModeIndex);
                        break;
                    case R.id.lightModeButton:
                        selectedModeIndex = 1;
                        MyTheme.applyDarkmode(getApplicationContext(), selectedModeIndex);
                        break;
                    case R.id.darkModeButton:
                        selectedModeIndex = 2;
                        MyTheme.applyDarkmode(getApplicationContext(), selectedModeIndex);
                        break;
                }
            }
        });

        setInitRadioChecked();      // 가져온 curModeIndex 를 이용해 선택되어야하는 라디오 버튼 결정 (초기 설정)
    }

    private void setInitRadioChecked() {
        if(curModeIndex == 0) {
            systemModeButton.setChecked(true);
        } else if(curModeIndex == 1) {
            lightModeButton.setChecked(true);
        } else if(curModeIndex == 2){
            darkModeButton.setChecked(true);
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if(id == android.R.id.home) {
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}