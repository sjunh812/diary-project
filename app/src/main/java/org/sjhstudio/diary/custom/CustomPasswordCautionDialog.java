package org.sjhstudio.diary.custom;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import androidx.annotation.NonNull;

import org.sjhstudio.diary.R;

public class CustomPasswordCautionDialog extends Dialog {
    Activity activity;
    ImageButton cancelButton;
    Button okButton;

    public CustomPasswordCautionDialog(@NonNull Context context) {
        super(context);
    }

    public CustomPasswordCautionDialog(@NonNull Context context, Activity activity) {
        super(context);
        this.activity = activity;
    }

    public CustomPasswordCautionDialog(@NonNull Context context, int themeResId) {
        super(context, themeResId);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_caution_password);

        setCancelable(false);
        cancelButton = (ImageButton)findViewById(R.id.cancelButton);
        okButton = (Button)findViewById(R.id.okButton);

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
                activity.finish();
            }
        });
        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
                activity.finish();
            }
        });
    }
}
