package org.sjhstudio.diary.custom;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import androidx.annotation.NonNull;

import org.sjhstudio.diary.R;

public class CustomStopWriteDialog extends Dialog {
    ImageButton cancelButton;
    Button backButton;
    Button continueButton;

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

        cancelButton = (ImageButton)findViewById(R.id.cancelButton);
        backButton = (Button)findViewById(R.id.backButton);
        continueButton = (Button)findViewById(R.id.continueButton);
    }

    public void setCancelButtonOnClickListener(View.OnClickListener listener) {
        cancelButton.setOnClickListener(listener);
    }

    public void setBackButtonOnClickListener(View.OnClickListener listener) {
        backButton.setOnClickListener(listener);
    }

    public void setContinueButtonOnClickListener(View.OnClickListener listener) {
        continueButton.setOnClickListener(listener);
    }
}
