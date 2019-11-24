package com.jeongwoochang.list_soohaeng.Util;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.jeongwoochang.list_soohaeng.Model.DBAdapter;
import com.jeongwoochang.list_soohaeng.Model.Schema.Alarm;
import com.jeongwoochang.list_soohaeng.Receiver.AlarmReceiver;

import org.joda.time.DateTime;

import java.util.concurrent.atomic.AtomicInteger;

import timber.log.Timber;

import static android.content.Intent.FLAG_INCLUDE_STOPPED_PACKAGES;

public class AlarmUtil {

    private static AtomicInteger mOpenCounter = new AtomicInteger();

    private final static String LOG_TAG = AlarmUtil.class.getSimpleName();

    private static AlarmUtil ourInstance;

    private Context context;
    private DBAdapter dbAdapter;
    private AlarmManager am;

    public synchronized static AlarmUtil getInstance() {
        if (ourInstance == null) {
            ourInstance = new AlarmUtil();
            ourInstance.dbAdapter = DBAdapter.getInstance();
        }
        return ourInstance;
    }

    public static void connect(Context context) {
        if (mOpenCounter.incrementAndGet() == 1) {
            ourInstance.context = context;
            ourInstance.am = (AlarmManager) ourInstance.context.getSystemService(Context.ALARM_SERVICE);
            DBAdapter.connect(ourInstance.context);
            Timber.plant(new Timber.DebugTree());
            Timber.tag(LOG_TAG);
        }
    }

    public void close() {
        try {
            if (mOpenCounter.decrementAndGet() == 0) {
                dbAdapter.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private AlarmUtil() {
    }

    public void addAlarm(Integer _id, DateTime alarmDate){
        Alarm alarmData;
        dbAdapter.addAlarm(alarmData = new Alarm(_id, alarmDate));

        Intent intent = new Intent(context, AlarmReceiver.class);
        Bundle args = new Bundle();
        args.putSerializable("DATA", alarmData);
        intent.putExtra("alarm_info", args);
        intent.addFlags(FLAG_INCLUDE_STOPPED_PACKAGES);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, alarmData.get_id(), intent, 0);

        if (pendingIntent != null) {
            am.cancel(pendingIntent);
        }

        pendingIntent = PendingIntent.getBroadcast(context, alarmData.get_id(), intent, PendingIntent.FLAG_UPDATE_CURRENT);

        am.set(AlarmManager.RTC_WAKEUP, alarmDate.getMillis(), pendingIntent);
    }

    public void removeAlarm(Integer _id){
        dbAdapter.removeAlarm(_id);
    }
}
