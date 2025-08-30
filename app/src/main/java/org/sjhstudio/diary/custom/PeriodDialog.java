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

/**
 * 일기 기간 설정 팝업
 */
public class PeriodDialog extends Dialog {

    ImageButton cancelButton;
    Button yesButton;
    Button allButton;

    Spinner yearSpinner;
    Spinner monthSpinner;

    public PeriodDialog(@NonNull Context context) {
        super(context);
    }

    public PeriodDialog(@NonNull Context context, int themeResId) {
        super(context, themeResId);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_period);
        setCancelable(true);

        cancelButton = findViewById(R.id.cancelButton);
        yesButton = findViewById(R.id.yesButton);
        allButton = findViewById(R.id.allButton);

        yearSpinner = findViewById(R.id.yearSpinner);
        monthSpinner = findViewById(R.id.monthSpinner);

        cancelButton.setOnClickListener(v -> dismiss());
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
