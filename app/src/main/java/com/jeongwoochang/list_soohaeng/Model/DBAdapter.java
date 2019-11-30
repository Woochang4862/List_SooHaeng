package com.jeongwoochang.list_soohaeng.Model;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.jeongwoochang.list_soohaeng.Model.Schema.Alarm;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;

import androidx.annotation.Nullable;
import timber.log.Timber;

public class DBAdapter {
    private static AtomicInteger mOpenCounter = new AtomicInteger();

    protected static final int DATABASE_VERSION = 1;
    protected static final String DB_NAME = "list_soohaeng.db";

    public static final String PUB_DATE_FORMAT = "yyyy-MM-dd hh:mm:ss";
    public static final String TEST_DATE_FORMAT = "yyyy-MM-dd";
    public static final String ALARM_DATE_FORMAT = "yyyy-MM-dd hh:mm:ss";
    public static final String LOG_DATE_FORMAT = "yyyy-MM-dd hh:mm:ss";

    private final static String LOG_TAG = DBAdapter.class.getSimpleName();

    public static final String TABLE_ALARM = "alarm";
    public static final String ALARM_ID = "_id";
    public static final String ALARM_DATE = "date";
    public static final String[] ALAEM_COLUMNS = {ALARM_ID, ALARM_DATE};

    private Context context;
    private SQLiteOpenHelper dbHelper;
    private SQLiteDatabase connection;

    private static DBAdapter instance;

    public static void connect(Context context) {
        if (mOpenCounter.incrementAndGet() == 1) {
            instance.context = context;
            instance.dbHelper = new DBHelper(instance.context);
            instance.connection = instance.dbHelper.getWritableDatabase();
            Timber.plant(new Timber.DebugTree());
        }
    }

    private DBAdapter() {
    }

    public synchronized static DBAdapter getInstance() {
        if (instance == null) {
            instance = new DBAdapter();
        }
        return instance;
    }

    private static class DBHelper extends SQLiteOpenHelper {

        public DBHelper(@Nullable Context context) {
            super(context, DB_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(@NotNull SQLiteDatabase db) {
            db.execSQL("CREATE TABLE " + TABLE_ALARM + "(" + ALARM_ID + " INTEGER PRIMARY KEY, " + ALARM_DATE + " TEXT)");
        }

        @Override
        public void onUpgrade(@NotNull SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_ALARM);
            onCreate(db);
        }
    }

    /**
     * @brief DB접속을 종료합니다.
     * 하지만 이 메소드를 호출하게 되면 dbHelper.getWritableDatabase() 값이 null이 되어버리므로 주의
     */
    public void close() {
        try {
            if (mOpenCounter.decrementAndGet() == 0) {
                connection.close();
                dbHelper.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * @param alarm Alarm
     * @brief 알림 정보를 추가합니다
     */
    public void addAlarm(Alarm alarm) {
        if (alarm != null) {
            ContentValues values = new ContentValues();
            values.put(ALARM_ID, alarm.get_id());
            values.put(ALARM_DATE, alarm.getAlarmDateForString());
            connection.insert(TABLE_ALARM, null, values);
            Timber.d("alarm(" + alarm + ") is added");
        }
    }

    /**
     * @return ArrayList<Alarm>
     * @brief 모든 알림 정보를 반화합니다.
     */
    public ArrayList<Alarm> getAlarm() {
        ArrayList<Alarm> alarms = new ArrayList<>();
        Cursor c = connection.query(TABLE_ALARM, null, null, null, null, null, null);
        while (c.moveToNext()) {
            alarms.add(new Alarm(c.getInt(0), c.getString(1)));
        }
        return alarms;
    }

    /**
     * @param _id Integer
     * @brief 해당 알림을 삭제합니다.
     */
    public void removeAlarm(int _id) {
        connection.delete(TABLE_ALARM, ALARM_ID + "=" + _id, null);
    }
}
