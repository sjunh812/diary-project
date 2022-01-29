package org.sjhstudio.diary.helper;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.util.Log;

import org.sjhstudio.diary.AlarmActivity;

import java.util.Calendar;

public class AlarmHelper {
    public static final String LOG = "AlarmHelper";

    public AlarmManager alarmManager;
    public PendingIntent pendingIntent;
    private Context context;
    private Calendar cal = Calendar.getInstance();

    public AlarmHelper(Context context) {
        this.context = context;
    }

    public void startAlarm(boolean isRepeat) {
        SharedPreferences pref = context.getSharedPreferences(AlarmActivity.SHARED_PREFERENCES_NAME2, Activity.MODE_PRIVATE);

        if(pref != null) {
            if (pref.getBoolean(AlarmActivity.IS_ALARM_KEY, false)) {
                int hour = pref.getInt(AlarmActivity.HOUR_KEY, 22);
                int minute = pref.getInt(AlarmActivity.MINUTE_KEY, 0);

                cal = Calendar.getInstance();
                cal.set(Calendar.HOUR_OF_DAY, hour);
                cal.set(Calendar.MINUTE, minute);
                cal.set(Calendar.SECOND, 0);

                if(isRepeat || Calendar.getInstance().getTimeInMillis() > cal.getTimeInMillis()) {
                    cal.add(Calendar.DATE, 1);;
                }

                Log.d(LOG, "HOUR : " + hour + ",  MINUTE : " + minute);

                alarmManager = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
                Intent rIntent = new Intent(context, AlarmReceiver.class);
//                pendingIntent = PendingIntent.getBroadcast(context, 0, rIntent, 0);

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    pendingIntent = PendingIntent.getBroadcast(context, 0, rIntent, PendingIntent.FLAG_IMMUTABLE);
                } else {
                    pendingIntent = PendingIntent.getBroadcast(context, 0, rIntent, 0);
                }

                //alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent);
                //alarmManager.set(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), pendingIntent);
                //alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent);
                alarmManager.setAlarmClock(new AlarmManager.AlarmClockInfo(cal.getTimeInMillis(), pendingIntent), pendingIntent);
            }
        }
    }

    public void stopAlarm() {
        alarmManager = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        Intent rIntent = new Intent(context, AlarmReceiver.class);
//        pendingIntent = PendingIntent.getBroadcast(context, 0, rIntent, 0);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            pendingIntent = PendingIntent.getBroadcast(context, 0, rIntent, PendingIntent.FLAG_IMMUTABLE);
        } else {
            pendingIntent = PendingIntent.getBroadcast(context, 0, rIntent, 0);
        }

        alarmManager.cancel(pendingIntent);

/*        if(alarmManager != null && pendingIntent != null) {
            alarmManager = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
            Intent rIntent = new Intent(context, AlarmReceiver.class);
            pendingIntent = PendingIntent.getBroadcast(context, 0, rIntent, 0);
            alarmManager.cancel(pendingIntent);

            pendingIntent = null;
            alarmManager = null;
        }*/
    }
}
