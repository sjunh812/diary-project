package org.sjhstudio.diary.custom;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import androidx.annotation.NonNull;

import org.sjhstudio.diary.R;

public class CustomUpdateDialog extends Dialog {
    ImageButton cancelButton;
    Button deleteButton;
    Button updateButton;

    public CustomUpdateDialog(@NonNull Context context) {
        super(context);
    }

    public CustomUpdateDialog(@NonNull Context context, int themeResId) {
        super(context, themeResId);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.update_dialog_custom);
        setCancelable(true);

        cancelButton = (ImageButton)findViewById(R.id.cancelButton);
        deleteButton = (Button)findViewById(R.id.deleteButton);
        updateButton = (Button)findViewById(R.id.updateButton);

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
    }

    public void setDeleteButtonOnClickListener(View.OnClickListener listener) {
        deleteButton.setOnClickListener(listener);
    }

    public void setUpdateButtonOnClickListener(View.OnClickListener listener) {
        updateButton.setOnClickListener(listener);
    }
}
