package org.sjhstudio.diary.custom;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import androidx.annotation.NonNull;

import org.sjhstudio.diary.R;

public class CustomStopWriteDialog extends Dialog implements View.OnClickListener {

    public CustomStopWriteDialog(@NonNull Context context) {
        super(context);
    }

    public CustomStopWriteDialog(@NonNull Context context, int themeResId) {
        super(context, themeResId);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.stop_write_dialog_custom);

        findViewById(R.id.cancelButton).setOnClickListener(this);
        findViewById(R.id.backButton).setOnClickListener(this);
        findViewById(R.id.continueButton).setOnClickListener(this);
    }

    public void setCancelButtonOnClickListener(View.OnClickListener listener) {
        findViewById(R.id.cancelButton).setOnClickListener(listener);
    }

    public void setBackButtonOnClickListener(View.OnClickListener listener) {
        findViewById(R.id.backButton).setOnClickListener(listener);
    }

    public void setContinueButtonOnClickListener(View.OnClickListener listener) {
        findViewById(R.id.continueButton).setOnClickListener(listener);
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()) {
            case R.id.cancelButton:
            case R.id.backButton:
            case R.id.continueButton:
                dismiss();
                break;
        }
    }
}
