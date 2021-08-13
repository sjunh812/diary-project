package org.sjhstudio.diary.custom;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Spinner;

import androidx.annotation.NonNull;

import org.sjhstudio.diary.R;

public class CustomAlignDialog extends Dialog {
    ImageButton cancelButton;
    Button yesButton;
    Button allButton;

    Spinner yearSpinner;
    Spinner monthSpinner;

    public CustomAlignDialog(@NonNull Context context) {
        super(context);
    }

    public CustomAlignDialog(@NonNull Context context, int themeResId) {
        super(context, themeResId);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.align_dialog_custom);

        cancelButton = (ImageButton)findViewById(R.id.cancelButton);
        yesButton = (Button)findViewById(R.id.yesButton);
        allButton = (Button)findViewById(R.id.allButton);

        yearSpinner = (Spinner)findViewById(R.id.yearSpinner);
        monthSpinner = (Spinner)findViewById(R.id.monthSpinner);
    }

    public void setCancelButtonOnClickListener(View.OnClickListener listener) {
        cancelButton.setOnClickListener(listener);
    }

    public void setYesButtonOnClickListener(View.OnClickListener listener) {
        yesButton.setOnClickListener(listener);
    }

    public void setAllButtonOnClickListener(View.OnClickListener listener) {
        allButton.setOnClickListener(listener);
    }

    public void setYearSpinnerAdapter(ArrayAdapter<String> adapter) {
        yearSpinner.setAdapter(adapter);
    }

    public void setYearSpinnerItemSelectedListener(AdapterView.OnItemSelectedListener listener) {
        yearSpinner.setOnItemSelectedListener(listener);
    }

    public void setSelectedYearSpinner(int position) {
        yearSpinner.setSelection(position);
    }

    public void setMonthSpinnerAdapter(ArrayAdapter<String> adapter) {
        monthSpinner.setAdapter(adapter);
    }

    public void setMonthSpinnerItemSelectedListener(AdapterView.OnItemSelectedListener listener) {
        monthSpinner.setOnItemSelectedListener(listener);
    }

    public void setSelectMonthSpinner(int position) {
        monthSpinner.setSelection(position);
    }
}
