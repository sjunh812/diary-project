package org.sjhstudio.diary.custom;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageButton;

import androidx.annotation.NonNull;

import org.sjhstudio.diary.R;
import org.sjhstudio.diary.utils.Pref;

public class CustomGPSDialog extends Dialog {
    ImageButton cancelButton;
    Button cancelButton2;
    Button yesButton;
    CheckBox doNotAskAgainCheckBox;

    SharedPreferences pref;
    private boolean isChecked = false;

    public CustomGPSDialog(@NonNull Context context) {
        super(context);
    }

    public CustomGPSDialog(@NonNull Context context, int themeResId) {
        super(context, themeResId);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gps_dialog_custom);
        setCancelable(false);

        init();
        initListener();
    }

    private void init() {
        cancelButton = (ImageButton)findViewById(R.id.cancelButton);
        cancelButton2 = (Button)findViewById(R.id.cancel_button_2);
        yesButton = (Button)findViewById(R.id.yes_button);
        doNotAskAgainCheckBox = (CheckBox)findViewById(R.id.do_not_ask_again_check_box);
    }

    private void initListener() {
        // 위치설정 알림 여부 체크박스
        doNotAskAgainCheckBox.setChecked(isChecked);
        doNotAskAgainCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                CustomGPSDialog.this.isChecked = isChecked;
            }
        });

        // 우상단 x버튼
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
                Pref.setPAskLocation(getContext(), !isChecked);
            }
        });
        // 하단 취소버튼
        cancelButton2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
                Pref.setPAskLocation(getContext(), !isChecked);
            }
        });
    }

    public boolean getIsChecked() {
        return isChecked;
    }

    public void setIsChecked(boolean isChecked) {
        this.isChecked = isChecked;
    }

    /** 하단 설정버튼 리스너설정 **/
    public void setYesButtonOnClickListener(View.OnClickListener listener) {
        yesButton.setOnClickListener(listener);
    }
}
