package org.sjhstudio.diary.helper;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import org.sjhstudio.diary.utils.Pref;

import java.util.Calendar;

public class AlarmHelper {

    public static final String TAG = "AlarmHelper";

    public AlarmManager alarmManager;
    public PendingIntent pendingIntent;
    private Context mContext;

    public AlarmHelper(Context context) {
        mContext = context;
    }

    public void startAlarm(boolean isRepeat) {
        Boolean useAlarm = Pref.getPUseAlarm(mContext);

        if(useAlarm) {
            Calendar cal = Calendar.getInstance();
            int hour = Pref.getPAlarmHour(mContext);
            int minute = Pref.getPAlarmMinute(mContext);

            cal.set(Calendar.HOUR_OF_DAY, hour);
            cal.set(Calendar.MINUTE, minute);
            cal.set(Calendar.SECOND, 0);

            if(isRepeat || Calendar.getInstance().getTimeInMillis() > cal.getTimeInMillis()) {
                cal.add(Calendar.DATE, 1);
            }

            Log.d(TAG, "HOUR : " + hour + ",  MINUTE : " + minute);

            alarmManager = (AlarmManager)mContext.getSystemService(Context.ALARM_SERVICE);
            Intent rIntent = new Intent(mContext, AlarmReceiver.class);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                pendingIntent = PendingIntent.getBroadcast(
                        mContext,
                        0,
                        rIntent,
                        PendingIntent.FLAG_IMMUTABLE
                );
            } else {
                pendingIntent = PendingIntent.getBroadcast(
                        mContext,
                        0,
                        rIntent,
                        0
                );
            }

            //alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent);
            //alarmManager.set(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), pendingIntent);
            //alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent);
            alarmManager.setAlarmClock(new AlarmManager.AlarmClockInfo(cal.getTimeInMillis(), pendingIntent), pendingIntent);
        }
    }

    public void stopAlarm() {
        alarmManager = (AlarmManager)mContext.getSystemService(Context.ALARM_SERVICE);
        Intent rIntent = new Intent(mContext, AlarmReceiver.class);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            pendingIntent = PendingIntent.getBroadcast(
                    mContext,
                    0,
                    rIntent,
                    PendingIntent.FLAG_IMMUTABLE
            );
        } else {
            pendingIntent = PendingIntent.getBroadcast(
                    mContext,
                    0,
                    rIntent,
                    0
            );
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
