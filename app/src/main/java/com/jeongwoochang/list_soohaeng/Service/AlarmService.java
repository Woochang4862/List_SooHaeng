package com.jeongwoochang.list_soohaeng.Service;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.util.Pair;

import com.jeongwoochang.list_soohaeng.Activity.MainActivity;
import com.jeongwoochang.list_soohaeng.Util.AlarmUtil;
import com.jeongwoochang.list_soohaeng.Model.Schema.Alarm;
import com.jeongwoochang.list_soohaeng.R;

import androidx.core.app.NotificationCompat;

public class AlarmService extends Service {
    private static final String CHANNEL_ID = "com.lusle.android.soon";

    private Intent intent;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        this.intent = intent;
        Pair<Integer, Notification> p = startForegroundService();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForeground(p.first, p.second);
            stopForeground(STOP_FOREGROUND_DETACH); // To remove notification
        } else {
            NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            if (mNotificationManager != null) {
                mNotificationManager.notify(p.first, p.second);
            }
        }
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    private Pair<Integer, Notification> startForegroundService() {
        if (intent != null) {
            Alarm data = (Alarm) intent.getBundleExtra("alarm_info").getSerializable("DATA");
            if (data != null) {

                AlarmManager am = (AlarmManager) getSystemService(ALARM_SERVICE);

                Intent __intent = new Intent("com.lusle.android.soon.ALARM_START");
                PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), data.get_id(), __intent, 0);
                if (am != null) {
                    am.cancel(pendingIntent);
                }

                AlarmUtil au = AlarmUtil.getInstance();
                AlarmUtil.connect(this);
                au.removeAlarm(data.get_id());
                au.close();

                Intent _intent = new Intent(this, MainActivity.class);
                _intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                PendingIntent pi = PendingIntent.getActivity(this, 111, _intent, PendingIntent.FLAG_UPDATE_CURRENT);
                long[] pattern = {0, 1000, 0};
                NotificationCompat.BigTextStyle bigText = new NotificationCompat.BigTextStyle();
                bigText.bigText("수형평가 준비하세요!");
                //bigText.setBigContentTitle(data.get().getTitle() + "개봉일까지 " + Util.calDDay(releaseDate) + "일 전입니다");
                bigText.setSummaryText("List<수행>");
                NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this, CHANNEL_ID)
                        .setSmallIcon(R.mipmap.ic_launcher_round)
                        //.setContentTitle(data.getMovie().getTitle())
                        //.setContentText("개봉일까지 " + Util.calDDay(releaseDate) + "일 전입니다")
                        .setPriority(Notification.PRIORITY_HIGH)
                        .setContentIntent(pi)
                        .setAutoCancel(true)
                        .setVibrate(pattern)
                        .setOngoing(false)
                        .setStyle(bigText)
                        .setDefaults(Notification.DEFAULT_SOUND|Notification.DEFAULT_VIBRATE);

                createNotificationChannel();
                return new Pair<>(data.hashCode(), mBuilder.build());
            }
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            return new Pair<>(404, new Notification.Builder(this, CHANNEL_ID).build());
        } else {
            return new Pair<>(404, new Notification.Builder(this).build());
        }
    }

    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "movie_alarm_channel";
            String description = "movie_alarm_channel";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(channel);
            }
        }
    }
}