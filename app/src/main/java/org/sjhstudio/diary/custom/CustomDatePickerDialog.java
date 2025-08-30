package org.sjhstudio.diary.custom;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageButton;

import androidx.annotation.NonNull;

import org.sjhstudio.diary.R;

public class CustomDatePickerDialog extends Dialog {
    ImageButton cancelButton;
    DatePicker datePicker;
    Button okButton;

    int curYear;
    int curMonth;
    int curDay;

    public CustomDatePickerDialog(@NonNull Context context, int year, int month, int day) {
        super(context);
        
        curYear = year;
        curMonth = month;
        curDay = day;
    }

    public CustomDatePickerDialog(@NonNull Context context, int themeResId) {
        super(context, themeResId);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_date_picker);

        cancelButton = (ImageButton)findViewById(R.id.cancelButton);
        datePicker = (DatePicker)findViewById(R.id.datePicker);
        okButton = (Button)findViewById(R.id.okButton);

        initDatePicker();
    }

    private void initDatePicker() {
        datePicker.init(curYear, curMonth - 1, curDay, new DatePicker.OnDateChangedListener() {
            @Override
            public void onDateChanged(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                curYear = year;
                curMonth = monthOfYear;
                curDay = dayOfMonth;
            }
        });
    }

    public void setCancelButtonOnClickListener(View.OnClickListener listener) {
        cancelButton.setOnClickListener(listener);
    }

    public void setOkButtonOnClickListener(View.OnClickListener listener) {
        okButton.setOnClickListener(listener);
    }

    public int getCurYear() {
        return curYear;
    }

    public int getCurMonth() {
        return curMonth;
    }

    public int getCurDay() {
        return curDay;
    }
}
