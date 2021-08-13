package org.sjhstudio.diary.custom;

import android.app.Dialog;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TimePicker;

import androidx.annotation.NonNull;

import org.sjhstudio.diary.R;

public class CustomTimePickerDialog extends Dialog {
    ImageButton cancelButton;
    TimePicker timePicker;
    Button okButton;

    int _hour;
    int _minute;

    public CustomTimePickerDialog(@NonNull Context context) {
        super(context);

        _hour = 22;
        _minute = 0;
    }

    public CustomTimePickerDialog(@NonNull Context context, int hour, int minute) {
        super(context);

        _hour = hour;
        _minute = minute;
    }

    public CustomTimePickerDialog(@NonNull Context context, int themeResId) {
        super(context, themeResId);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.time_picker_dialog);

        cancelButton = (ImageButton)findViewById(R.id.cancelButton);
        timePicker = (TimePicker) findViewById(R.id.timePicker);
        okButton = (Button)findViewById(R.id.okButton);

        initTimePicker();
    }

    private void initTimePicker() {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            timePicker.setHour(_hour);
            timePicker.setMinute(_minute);
        } else {
            timePicker.setCurrentHour(_hour);
            timePicker.setCurrentMinute(_minute);
        }

        timePicker.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
            @Override
            public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
                _hour = hourOfDay;
                _minute = minute;
            }
        });
    }

    public void setCancelButtonOnClickListener(View.OnClickListener listener) {
        cancelButton.setOnClickListener(listener);
    }

    public void setOkButtonOnClickListener(View.OnClickListener listener) {
        okButton.setOnClickListener(listener);
    }

    public int get_hour() {
        return _hour;
    }

    public int get_minute() {
        return _minute;
    }
}
