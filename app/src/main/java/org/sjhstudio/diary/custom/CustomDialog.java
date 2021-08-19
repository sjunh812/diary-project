package org.sjhstudio.diary.custom;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import androidx.annotation.NonNull;
import org.sjhstudio.diary.R;

public class CustomDialog extends Dialog {
    ImageButton cancelButton;
    Button cameraButton;
    Button albumButton;

    public CustomDialog(@NonNull Context context) {
        super(context);
    }

    public CustomDialog(@NonNull Context context, int themeResId) {
        super(context, themeResId);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_custom);

        cancelButton = (ImageButton)findViewById(R.id.cancelButton);
        cameraButton = (Button)findViewById(R.id.cameraButton);
        albumButton = (Button)findViewById(R.id.albumButton);

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
    }

    public void setCancelButtonOnClickListener(View.OnClickListener listener) {
        cancelButton.setOnClickListener(listener);
    }

    public void setCameraButtonOnClickListener(View.OnClickListener listener) {
        cameraButton.setOnClickListener(listener);
    }

    public void setAlbumButtonOnClickListener(View.OnClickListener listener) {
        albumButton.setOnClickListener(listener);
    }
}
