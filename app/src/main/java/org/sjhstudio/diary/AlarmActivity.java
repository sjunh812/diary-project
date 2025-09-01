package org.sjhstudio.diary;

import androidx.appcompat.widget.Toolbar;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;

import org.sjhstudio.diary.custom.CustomTimePickerDialog;
import org.sjhstudio.diary.extensions.ViewExtensionKt;
import org.sjhstudio.diary.helper.AlarmHelper;
import org.sjhstudio.diary.utils.BaseActivity;
import org.sjhstudio.diary.utils.Pref;
import org.sjhstudio.diary.utils.Utils;

import java.util.Calendar;
import java.util.Date;
import java.util.Objects;

public class AlarmActivity extends BaseActivity {

    private static final String TAG = "AlarmActivity";

    private RelativeLayout timeLayout;
    private CustomTimePickerDialog timePickerDialog;
    private TextView timeTextView;
    private Switch alarmSwitch;
    private AlarmHelper alarmHelper;

    private boolean isAlarm = false;
    private int hour = 22;
    private int minute = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm);

        ViewExtensionKt.enableSystemBarPadding(findViewById(R.id.root));

        alarmHelper = new AlarmHelper(this);

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("알림");
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        timeTextView = findViewById(R.id.timeTextView);
        alarmSwitch = findViewById(R.id.alarmSwitch);
        alarmSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            Pref.setPUseAlarm(getApplicationContext(), isChecked);

            if(isChecked) {
                Pref.setPAlarmHour(getApplicationContext(), hour);
                Pref.setPAlarmMinute(getApplicationContext(), minute);
            }

            isAlarm = isChecked;
            setTimeLayout();
        });

        @SuppressLint("UseSwitchCompatOrMaterialCode")
        Switch locationSwitch = findViewById(R.id.locationSwitch);
        locationSwitch.setChecked(Pref.getPAskLocation(this));
        locationSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> Pref.setPAskLocation(getApplicationContext(), isChecked));

        timeLayout = findViewById(R.id.timeLayout);
        timeLayout.setOnClickListener(v -> setTimePickerDialog());

        isAlarm = Pref.getPUseAlarm(getApplicationContext());
        hour = Pref.getPAlarmHour(getApplicationContext());
        minute = Pref.getPAlarmMinute(getApplicationContext());
        timePickerDialog = new CustomTimePickerDialog(this, hour, minute);

        alarmSwitch.setChecked(isAlarm);
        setTimeTextView();
        setTimeLayout();
    }

    private void setTimeLayout() {
        if(isAlarm) {
            Log.d(TAG, "HOUR : " + hour + ",  MINUTE : " + minute);
            timeLayout.setVisibility(View.VISIBLE);
            alarmHelper.startAlarm(false);
        } else {
            timeLayout.setVisibility(View.GONE);
            alarmHelper.stopAlarm();
        }
    }

    private void setTimeTextView() {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, hour);
        cal.set(Calendar.MINUTE, minute);
        Date date = cal.getTime();

        String timeFormat = Utils.INSTANCE.getTimeFormat().format(date);
        timeTextView.setText(timeFormat);
    }

    private void setTimePickerDialog() {
        timePickerDialog.show();
        timePickerDialog.setCancelButtonOnClickListener(v -> timePickerDialog.dismiss());
        timePickerDialog.setOkButtonOnClickListener(v -> {
            hour = timePickerDialog.get_hour();
            minute = timePickerDialog.get_minute();

            Pref.setPUseAlarm(getApplicationContext(), true);
            Pref.setPAlarmHour(getApplicationContext(), hour);
            Pref.setPAlarmMinute(getApplicationContext(), minute);

            alarmHelper.startAlarm(false);
            setTimeTextView();
            timePickerDialog.dismiss();
        });
    }

}