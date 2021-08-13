package org.sjhstudio.diary;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import org.sjhstudio.diary.helper.MyTheme;

public class FontActivity extends AppCompatActivity {
    /** 상수 **/
    private static final String LOG = "FontActivity";

    /** UI **/
    private TextView exampleTextView;
    private RadioButton systemFontButton;
    private RadioButton basicFontButton;
    private RadioButton fontButton;
    private RadioButton fontButton2;
    private RadioButton fontButton3;
    private RadioButton fontButton4;
    private RadioButton fontButton5;
    private RadioButton fontButton6;
    private RadioButton fontButton7;
    private RadioButton fontButton8;
    private RadioButton fontButton9;
    private RadioButton fontButton10;

    /** data **/
    private int curFontIndex = 0;
    private int selectedFontIndex = -1;
    private Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MyTheme.applyTheme(this);
        setContentView(R.layout.activity_font);

        // 현재 app 에 설정된 폰트 인덱스를 SharedPreferences 를 이용해 가져옴
        SharedPreferences pref = getSharedPreferences(MyTheme.SHARED_PREFERENCES_NAME, Activity.MODE_PRIVATE);
        if(pref != null) {
            curFontIndex = pref.getInt(MyTheme.FONT_KEY, 0);
        }

        initUI();
        initRadioButtonListener();
        initButtonListener();

        setExampleTextViewFont();       // SharedPreferences 를 통해 가져온 폰트 인덱스를 통해 라디어 버튼 세팅
    }

    private void initUI() {
        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        toolbar.setTitle("폰트설정");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);          // 툴바의 왼쪽 돌아가기버튼 사용을 위한 코드

        exampleTextView = (TextView)findViewById(R.id.exampleTextView); // 폰트 적용 예시를 보여주는 텍스트 (폰트 적용에 따라 폰트가 바뀌어야함)
        systemFontButton = (RadioButton)findViewById(R.id.systemFontButton);
        basicFontButton = (RadioButton)findViewById(R.id.basicFontButton);
        fontButton = (RadioButton)findViewById(R.id.fontButton);
        fontButton2 = (RadioButton)findViewById(R.id.fontButton2);
        fontButton3 = (RadioButton)findViewById(R.id.fontButton3);
        fontButton4 = (RadioButton)findViewById(R.id.fontButton4);
        fontButton5 = (RadioButton)findViewById(R.id.fontButton5);
        fontButton6 = (RadioButton)findViewById(R.id.fontButton6);
        fontButton7 = (RadioButton)findViewById(R.id.fontButton7);
        fontButton8 = (RadioButton)findViewById(R.id.fontButton8);
        fontButton9 = (RadioButton)findViewById(R.id.fontButton9);
        fontButton10 = (RadioButton)findViewById(R.id.fontButton10);
    }

    private void initRadioButtonListener() {

        RadioGroup fontGroup = (RadioGroup)findViewById(R.id.fontGroup);
        fontGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                Typeface font = null;

                switch(checkedId) {
                    case R.id.systemFontButton:
                        font = Typeface.SANS_SERIF;
                        selectedFontIndex = 100;
                        break;
                    case R.id.basicFontButton:
                        font = Typeface.createFromAsset(getAssets(), "font.ttf");
                        selectedFontIndex = -1;
                        break;
                    case R.id.fontButton:
                        font = Typeface.createFromAsset(getAssets(), "font1.ttf");
                        selectedFontIndex = 0;
                        break;
                    case R.id.fontButton2:
                        font = Typeface.createFromAsset(getAssets(), "font2.ttf");
                        selectedFontIndex = 1;
                        break;
                    case R.id.fontButton3:
                        font = Typeface.createFromAsset(getAssets(), "font3.ttf");
                        selectedFontIndex = 2;
                        break;
                    case R.id.fontButton4:
                        font = Typeface.createFromAsset(getAssets(), "font4.ttf");
                        selectedFontIndex = 3;
                        break;
                    case R.id.fontButton5:
                        font = Typeface.createFromAsset(getAssets(), "font5.ttf");
                        selectedFontIndex = 4;
                        break;
                    case R.id.fontButton6:
                        font = Typeface.createFromAsset(getAssets(), "font6.ttf");
                        selectedFontIndex = 5;
                        break;
                    case R.id.fontButton7:
                        font = Typeface.createFromAsset(getAssets(), "font7.ttf");
                        selectedFontIndex = 6;
                        break;
                    case R.id.fontButton8:
                        font = Typeface.createFromAsset(getAssets(), "font8.ttf");
                        selectedFontIndex = 7;
                        break;
                    case R.id.fontButton9:
                        font = Typeface.createFromAsset(getAssets(), "font9.ttf");
                        selectedFontIndex = 8;
                        break;
                    case R.id.fontButton10:
                        font = Typeface.createFromAsset(getAssets(), "font10.ttf");
                        selectedFontIndex = 9;
                        break;
                }

                if(font != null) {
                    exampleTextView.setTypeface(font);
                }
            }
        });
    }

    private void initButtonListener() {
        Button okButton = (Button)findViewById(R.id.okButton);
        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MyTheme.applyTheme(getApplicationContext(), selectedFontIndex);
                intent = getIntent();
                setResult(RESULT_OK, intent);

                finish();
            }
        });

        Button cancelButton = (Button)findViewById(R.id.cancelButton);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void setExampleTextViewFont() {
        switch(curFontIndex) {
            case 100:
                systemFontButton.setChecked(true);
                break;
            case -1:
                basicFontButton.setChecked(true);
                break;
            case 0:
                fontButton.setChecked(true);
                break;
            case 1:
                fontButton2.setChecked(true);
                break;
            case 2:
                fontButton3.setChecked(true);
                break;
            case 3:
                fontButton4.setChecked(true);
                break;
            case 4:
                fontButton5.setChecked(true);
                break;
            case 5:
                fontButton6.setChecked(true);
                break;
            case 6:
                fontButton7.setChecked(true);
                break;
            case 7:
                fontButton8.setChecked(true);
                break;
            case 8:
                fontButton9.setChecked(true);
                break;
            case 9:
                fontButton10.setChecked(true);
                break;
            default:
                Log.e(LOG, "SharedPreferences 로부터 잘못된 폰트 인덱스 가져옴");
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