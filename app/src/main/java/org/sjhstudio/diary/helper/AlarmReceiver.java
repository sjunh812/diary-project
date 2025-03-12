package org.sjhstudio.diary.helper;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import org.sjhstudio.diary.R;
import org.sjhstudio.diary.SplashActivity;
import org.sjhstudio.diary.utils.Constants;
import org.sjhstudio.diary.utils.Pref;

import java.util.Calendar;

public class AlarmReceiver extends BroadcastReceiver {

    private static final String TAG = "AlarmReceiver";
    private AlarmHelper alarmHelper;

    @Override
    public void onReceive(Context context, Intent intent) {
        alarmHelper = new AlarmHelper(context);

        if ("android.intent.action.BOOT_COMPLETED".equals(intent.getAction())) {
            // 부팅 : 알람매니저 재등록(SharedPreference 활용)
            Log.e(TAG, "기기 재부팅 : 알람매니저 재등록");
            setBootData(context);
        } else if (Intent.ACTION_MY_PACKAGE_REPLACED.equals(intent.getAction())
                || Intent.ACTION_PACKAGE_REPLACED.equals(intent.getAction())
        ) {
            // 앱 업데이트 이후 : 알람매니저 재등록
            Log.e(TAG, "앱 업데이트 실행 : 알람매니저 재등록");
            setBootData(context);
        } else if (Intent.ACTION_PACKAGE_REMOVED.equals(intent.getAction())) {
            // 앱 삭제 : 비밀번호 초기화
            Log.e(TAG, "앱 삭제 : 비밀번호 삭제");
            Pref.removePPassword(context);
        } else {
            // 일반적인 경우 : Notification 등록 및 알람 반복을 위해 알람매니저 재등록
            Log.e(TAG, "AlarmReceiver 실행");
            startNotification(context);
        }
    }

    private void startNotification(Context context) {
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        NotificationCompat.Builder builder;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if (notificationManager.getNotificationChannel(Constants.CHANNEL_ID) == null) {
                NotificationChannel notificationChannel = new NotificationChannel(
                        Constants.CHANNEL_ID,
                        Constants.CHANNEL_NAME,
                        NotificationManager.IMPORTANCE_DEFAULT
                );
                //notificationChannel.setVibrationPattern(new long[] {0, 500, 1000, 500});
                notificationManager.createNotificationChannel(notificationChannel);
            }
            builder = new NotificationCompat.Builder(context, Constants.CHANNEL_ID);
        } else {
            builder = new NotificationCompat.Builder(context);
        }

        Intent clickIntent = new Intent(context, SplashActivity.class);
        clickIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);

        PendingIntent pendingIntent;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            pendingIntent = PendingIntent.getActivity(
                    context,
                    1,
                    clickIntent,
                    PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_CANCEL_CURRENT
            );
        } else {
            pendingIntent = PendingIntent.getActivity(
                    context,
                    1,
                    clickIntent,
                    PendingIntent.FLAG_CANCEL_CURRENT
            );
        }

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

    private void setBootData(Context context) {
        Boolean useAlarm = Pref.getPUseAlarm(context);

        if (useAlarm) {
            Calendar cal = Calendar.getInstance();
            int hour = Pref.getPAlarmHour(context);
            int minute = Pref.getPAlarmMinute(context);

            cal.set(Calendar.HOUR_OF_DAY, hour);
            cal.set(Calendar.MINUTE, minute);
            cal.set(Calendar.SECOND, 0);

            if (Calendar.getInstance().getTimeInMillis() > cal.getTimeInMillis()) {
                cal.add(Calendar.DATE, 1);
            }

            AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            Intent rIntent = new Intent(context, AlarmReceiver.class);
            PendingIntent pendingIntent;

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                pendingIntent = PendingIntent.getBroadcast(
                        context,
                        0,
                        rIntent,
                        PendingIntent.FLAG_IMMUTABLE
                );
            } else {
                pendingIntent = PendingIntent.getBroadcast(
                        context,
                        0,
                        rIntent,
                        0
                );
            }

            alarmManager.setAlarmClock(new AlarmManager.AlarmClockInfo(cal.getTimeInMillis(), pendingIntent), pendingIntent);
        }
    }
}