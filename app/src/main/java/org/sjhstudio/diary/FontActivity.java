package org.sjhstudio.diary;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import org.sjhstudio.diary.helper.MyTheme;
import org.sjhstudio.diary.utils.BaseActivity;
import org.sjhstudio.diary.utils.Pref;

import java.util.Objects;

public class FontActivity extends BaseActivity {

    private static final String TAG = "FontActivity";

    private int curFontIndex = 0;
    private int curFontSizeIndex = 1;
    private int selectedFontIndex = -1;
    private int selectedFontSizeIndex = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_font);

        curFontIndex = Pref.getPFontKey(this);
        curFontSizeIndex = Pref.getPFontSize(this);

        init();
        setPrefs(); // 폰트종류 및 크기에 대한 prefs
    }

    private void init() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("폰트설정");
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        initFontRadioBtn();
        initFontSizeRadioBtn();
        initBottom();
    }

    private void initFontRadioBtn() {
        ((RadioGroup)findViewById(R.id.font_radio_group)).setOnCheckedChangeListener((group, checkedId) -> {
            switch(checkedId) {
                case R.id.systemFontButton:
                    selectedFontIndex = 100;
                    break;
                case R.id.basicFontButton:
                    selectedFontIndex = -1;
                    break;
                case R.id.fontButton:
                    selectedFontIndex = 0;
                    break;
                case R.id.fontButton2:
                    selectedFontIndex = 1;
                    break;
                case R.id.fontButton3:
                    selectedFontIndex = 2;
                    break;
                case R.id.fontButton4:
                    selectedFontIndex = 3;
                    break;
                case R.id.fontButton5:
                    selectedFontIndex = 4;
                    break;
                case R.id.fontButton6:
                    selectedFontIndex = 5;
                    break;
                case R.id.fontButton7:
                    selectedFontIndex = 6;
                    break;
                case R.id.fontButton8:
                    selectedFontIndex = 7;
                    break;
                case R.id.fontButton9:
                    selectedFontIndex = 8;
                    break;
                case R.id.fontButton10:
                    selectedFontIndex = 9;
                    break;
                case R.id.fontButton11:
                    selectedFontIndex = 10;
                    break;
                case R.id.fontButton12:
                    selectedFontIndex = 11;
                    break;
            }
        });
    }

    private void initFontSizeRadioBtn() {
        ((RadioGroup)findViewById(R.id.font_size_radio_group)).setOnCheckedChangeListener((group, checkedId) -> {
            switch (checkedId) {
                case R.id.ss_size_radio_btn:
                    selectedFontSizeIndex = 0;
                    break;
                case R.id.s_size_radio_btn:
                    selectedFontSizeIndex = 1;
                    break;
                case R.id.m_size_radio_btn:
                    selectedFontSizeIndex = 2;
                    break;
                case R.id.l_size_radio_btn:
                    selectedFontSizeIndex = 3;
                    break;
                case R.id.ll_size_radio_btn:
                    selectedFontSizeIndex = 4;
                    break;
            }
        });
    }

    private void initBottom() {
        ((Button)findViewById(R.id.okButton)).setOnClickListener( v -> {
            MyTheme.applyTheme(getApplicationContext(), selectedFontIndex, selectedFontSizeIndex);
            setResult(RESULT_OK, getIntent());
            finish();
        });

        ((Button)findViewById(R.id.cancelButton)).setOnClickListener( v -> {
            finish();
        });
    }

    private void setPrefs() {
        switch(curFontIndex) {
            case 100:
                ((RadioButton)findViewById(R.id.systemFontButton)).setChecked(true);
                break;
            case -1:
                ((RadioButton)findViewById(R.id.basicFontButton)).setChecked(true);
                break;
            case 0:
                ((RadioButton)findViewById(R.id.fontButton)).setChecked(true);
                break;
            case 1:
                ((RadioButton)findViewById(R.id.fontButton2)).setChecked(true);
                break;
            case 2:
                ((RadioButton)findViewById(R.id.fontButton3)).setChecked(true);
                break;
            case 3:
                ((RadioButton)findViewById(R.id.fontButton4)).setChecked(true);
                break;
            case 4:
                ((RadioButton)findViewById(R.id.fontButton5)).setChecked(true);
                break;
            case 5:
                ((RadioButton)findViewById(R.id.fontButton6)).setChecked(true);
                break;
            case 6:
                ((RadioButton)findViewById(R.id.fontButton7)).setChecked(true);
                break;
            case 7:
                ((RadioButton)findViewById(R.id.fontButton8)).setChecked(true);
                break;
            case 8:
                ((RadioButton)findViewById(R.id.fontButton9)).setChecked(true);
                break;
            case 9:
                ((RadioButton)findViewById(R.id.fontButton10)).setChecked(true);
                break;
            case 10:
                ((RadioButton)findViewById(R.id.fontButton11)).setChecked(true);
                break;
            case 11:
                ((RadioButton)findViewById(R.id.fontButton12)).setChecked(true);
                break;
        }

        switch(curFontSizeIndex) {
            case 0:
                ((RadioButton)findViewById(R.id.ss_size_radio_btn)).setChecked(true);
                break;
            case 1:
                ((RadioButton)findViewById(R.id.s_size_radio_btn)).setChecked(true);
                break;
            case 2:
                ((RadioButton)findViewById(R.id.m_size_radio_btn)).setChecked(true);
                break;
            case 3:
                ((RadioButton)findViewById(R.id.l_size_radio_btn)).setChecked(true);
                break;
            case 4:
                ((RadioButton)findViewById(R.id.ll_size_radio_btn)).setChecked(true);
                break;
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if(id == android.R.id.home) {   // 툴바 왼쪽 돌아가기 버튼 선택 시
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}