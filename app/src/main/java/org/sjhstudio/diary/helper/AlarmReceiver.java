package org.sjhstudio.diary.helper;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import org.sjhstudio.diary.AlarmActivity;
import org.sjhstudio.diary.MainPasswordActivity;
import org.sjhstudio.diary.R;
import org.sjhstudio.diary.SplashActivity;

import java.util.Calendar;

public class AlarmReceiver extends BroadcastReceiver {
    public static final String CHANNEL_ID = "alarm_id";
    public static final String CHANNEL_NAME = "alarm";

    private AlarmHelper alarmHelper;

    @Override
    public void onReceive(Context context, Intent intent) {
        alarmHelper = new AlarmHelper(context);

        if("android.intent.action.BOOT_COMPLETED".equals(intent.getAction())) {     // 부팅 : 알람매니저 재등록 (SharedPreference 에 저장된 데이터 활용)
            setBootData(context);
        } else if(Intent.ACTION_MY_PACKAGE_REPLACED.equals(intent.getAction()) ||
        Intent.ACTION_PACKAGE_REPLACED.equals(intent.getAction())) {                // 앱 업데이트 이후 : 알람매니저 재등록
            setBootData(context);
            Log.d("LOG", "앱 업데이트 실행됨");
        } else if(Intent.ACTION_PACKAGE_REMOVED.equals(intent.getAction())) {       // 앱 삭제 : 비밀번호 초기화
            initPassword(context);
        } else {                                                                    // 일반적인 경우 : Notification 등록 및 알람 반복을 위해 알람매니저 재등록
            startNotification(context);
            Log.d("LOG", "AlarmRecevier 실행됨");
        }
    }

    private void initPassword(Context context) {
        SharedPreferences pref = context.getSharedPreferences(MyTheme.SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE);
        if(pref != null) {
            SharedPreferences.Editor editor = pref.edit();
            editor.remove(MyTheme.PASSWORD);
            editor.commit();
            Log.d("LOG", "앱이 삭제됨 : 비밀번호 초기화");
        }
    }

    private void startNotification(Context context) {
        NotificationManager notificationManager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
        NotificationCompat.Builder builder = null;

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if(notificationManager.getNotificationChannel(CHANNEL_ID) == null) {
                NotificationChannel notificationChannel = new NotificationChannel(
                        CHANNEL_ID,
                        CHANNEL_NAME,
                        NotificationManager.IMPORTANCE_DEFAULT
                );
                //notificationChannel.setVibrationPattern(new long[] {0, 500, 1000, 500});
                notificationManager.createNotificationChannel(notificationChannel);
            }

            builder = new NotificationCompat.Builder(context, CHANNEL_ID);
        } else {
            builder = new NotificationCompat.Builder(context);
        }

        Intent clickIntent = new Intent(context, SplashActivity.class);
        //clickIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        clickIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 1, clickIntent, PendingIntent.FLAG_CANCEL_CURRENT);

        builder.setContentTitle("1일 1일기");
        builder.setContentText("오늘 하루 어떠셨나요? 1일 1일기 실천하러 가보세요!");
        builder.setSmallIcon(R.drawable.app_icon);
        builder.setContentIntent(pendingIntent);
        builder.setAutoCancel(true);
        //builder.setVibrate(new long[] {0, 500, 1000, 500});

        Notification notification = builder.build();
        notificationManager.notify(1, notification);

        alarmHelper.startAlarm(true);
    }

    private void setBootData(Context context){
        SharedPreferences pref = context.getSharedPreferences(AlarmActivity.SHARED_PREFERENCES_NAME2, Activity.MODE_PRIVATE);

        if(pref != null) {
            if (pref.getBoolean(AlarmActivity.IS_ALARM_KEY, false)) {
                int hour = pref.getInt(AlarmActivity.HOUR_KEY, 22);
                int minute = pref.getInt(AlarmActivity.MINUTE_KEY, 0);

                Calendar cal = Calendar.getInstance();

                cal = Calendar.getInstance();
                cal.set(Calendar.HOUR_OF_DAY, hour);
                cal.set(Calendar.MINUTE, minute);
                cal.set(Calendar.SECOND, 0);

                if(Calendar.getInstance().getTimeInMillis() > cal.getTimeInMillis()) {
                    cal.add(Calendar.DATE, 1);;
                }

                AlarmManager alarmManager = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
                Intent rIntent = new Intent(context, AlarmReceiver.class);
                PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, rIntent, 0);

                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    alarmManager.setAlarmClock(new AlarmManager.AlarmClockInfo(cal.getTimeInMillis(), pendingIntent), pendingIntent);
                } else {
                    alarmManager.set(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), pendingIntent);
                }

            }
        }
    }
}