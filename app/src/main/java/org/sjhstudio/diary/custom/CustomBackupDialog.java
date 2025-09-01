package org.sjhstudio.diary.custom;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;

import org.sjhstudio.diary.R;

public class CustomBackupDialog extends Dialog {
    ImageButton cancelButton;

    Button cancelButton2;
    Button okButton;

    TextView titleTextView;
    TextView questionTextView;
    TextView subTextView1;
    TextView subTextView2;

    public CustomBackupDialog(@NonNull Context context) {
        super(context);
    }

    public CustomBackupDialog(@NonNull Context context, int themeResId) {
        super(context, themeResId);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_caution_backup);

        titleTextView = (TextView)findViewById(R.id.titleTextView);
        questionTextView = (TextView)findViewById(R.id.questionTextView);
        subTextView1 = (TextView)findViewById(R.id.subTextView1);
        subTextView2 = (TextView)findViewById(R.id.subTextView2);

        cancelButton = (ImageButton)findViewById(R.id.cancelButton);
        cancelButton2 = (Button)findViewById(R.id.cancelButton2);
        okButton = (Button)findViewById(R.id.okButton);
    }

    public void setCancelButtonOnClickListener(View.OnClickListener listener) {
        cancelButton.setOnClickListener(listener);
        cancelButton2.setOnClickListener(listener);
    }

    public void setOkButtonOnClickListener(View.OnClickListener listener) {
        okButton.setOnClickListener(listener);
    }

    public void setTitleTextView(String text) {
        titleTextView.setText(text);
    }

    public void setQuestionTextView(String text) {
        questionTextView.setText(text);
    }

    public void setSubTextView1(String text) {
        subTextView1.setText(text);
    }

    public void setSubTextView2(String text) {
        subTextView2.setText(text);
    }

    public void setOkButtonText(String text) {
        okButton.setText(text);
    }
}
