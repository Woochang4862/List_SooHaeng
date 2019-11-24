package com.jeongwoochang.list_soohaeng.Model;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.jeongwoochang.list_soohaeng.Model.Schema.Alarm;
import com.jeongwoochang.list_soohaeng.Model.Schema.Test;
import com.jeongwoochang.list_soohaeng.Model.Schema.TestGroup;

import org.jetbrains.annotations.NotNull;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;

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

    private final static String LOG_TAG = DBAdapter.class.getSimpleName();

    //Test Group Table
    public static final String TABLE_TEST_GROUP = "test_group";
    public static final String TEST_GROUP_ID = "_id";
    public static final String TEST_GROUP_NAME = "name";
    public static final String TEST_GROUP_PUB_DATE = "pub_date";
    public static final String[] TEST_GROUP_COLUMNS = {TEST_GROUP_ID, TEST_GROUP_NAME, TEST_GROUP_PUB_DATE};

    //Test Table
    public static final String TABLE_TEST = "test";
    public static final String TEST_ID = "_id";
    public static final String TEST_GROUP = "test_group";
    public static final String TEST_NAME = "name";
    public static final String TEST_SUBJECT = "subject";
    public static final String TEST_DATE = "date";
    public static final String TEST_CONTENT_FILE_NAME = "content_file_name";
    public static final String TEST_CONTENT_EXTENSION = "content_extension";
    public static final String TEST_CONTENT_CONTENT = "content_content";
    public static final String TEST_EXPECTED_TIME = "expected_time";
    public static final String TEST_PUB_DATE = "pub_date";
    public static final String[] TEST_COLUMNS = {TEST_ID, TEST_GROUP, TEST_NAME, TEST_SUBJECT, TEST_DATE, TEST_CONTENT_FILE_NAME, TEST_CONTENT_EXTENSION, TEST_CONTENT_CONTENT, TEST_EXPECTED_TIME, TEST_PUB_DATE};

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
            Timber.tag(LOG_TAG);
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
            db.execSQL("CREATE TABLE " + TABLE_TEST_GROUP + "(" + TEST_GROUP_ID + " INTEGER PRIMARY KEY, " + TEST_GROUP_NAME + " TEXT, " + TEST_GROUP_PUB_DATE + " TEXT)");
            db.execSQL("CREATE TABLE " + TABLE_TEST + "(" +
                    TEST_ID + " INTEGER PRIMARY KEY, " +
                    TEST_GROUP + " INTEGER, " +
                    TEST_NAME + " TEXT, " +
                    TEST_SUBJECT + " TEXT, " +
                    TEST_DATE + " TEXT, " +
                    TEST_CONTENT_FILE_NAME + " TEXT, " +
                    TEST_CONTENT_EXTENSION + " TEXT, " +
                    TEST_CONTENT_CONTENT + " BLOB, " +
                    TEST_EXPECTED_TIME + " INTEGER, " +
                    TEST_PUB_DATE + " TEXT" +
                    ")");
            db.execSQL("CREATE TABLE " + TABLE_ALARM + "(" + ALARM_ID + " INTEGER PRIMARY KEY, " + ALARM_DATE + " TEXT)");
        }

        @Override
        public void onUpgrade(@NotNull SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_TEST_GROUP);
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_TEST);
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
     * @param testGroup TestGroup
     * @brief 수행평가를 추가합니다.
     */
    public void addTestGroup(@Nullable TestGroup testGroup) {
        if (testGroup != null) {
            ContentValues values = new ContentValues();
            values.put(TEST_GROUP_NAME, testGroup.getName());
            values.put(TEST_GROUP_PUB_DATE, DateTimeFormat.forPattern(PUB_DATE_FORMAT).print(DateTime.now()));
            connection.insert(TABLE_TEST_GROUP, null, values);
            Timber.d("test group(" + testGroup + ") is added");
        }
    }

    /**
     * @param testGroup TestGroup
     * @brief 수행평가를 추가합니다.
     */
    public void updateTestGroup(@Nullable TestGroup testGroup) {
        if (testGroup != null) {
            ContentValues values = new ContentValues();
            values.put(TEST_GROUP_NAME, testGroup.getName());
            values.put(TEST_GROUP_PUB_DATE, DateTimeFormat.forPattern(PUB_DATE_FORMAT).print(DateTime.now()));
            connection.insert(TABLE_TEST_GROUP, null, values);
            Timber.d("test group(" + testGroup + ") is added");
        }
    }

    /**
     * @param test Test
     * @brief 수행평가를 추가합니다.
     */
    public long addTest(Test test) {
        ContentValues values = new ContentValues();
        values.put(TEST_GROUP, test.getGroup());
        values.put(TEST_NAME, test.getName());
        values.put(TEST_SUBJECT, test.getSubject());
        values.put(TEST_DATE, test.getDateString());
        values.put(TEST_CONTENT_FILE_NAME, test.getContent().getFileName());
        values.put(TEST_CONTENT_EXTENSION, test.getContent().getExtension());
        values.put(TEST_CONTENT_CONTENT, test.getContent().getContent());
        values.put(TEST_EXPECTED_TIME, test.getExpectedTime());
        values.put(TEST_PUB_DATE, DateTimeFormat.forPattern(PUB_DATE_FORMAT).print(DateTime.now()));
        Timber.d("test(" + test + ") is added");
        return connection.insert(TABLE_TEST, null, values);
    }

    /**
     * @param test Test
     * @brief 수행평가를 변경합니다.
     */
    public long updateTest(Test test) {
        ContentValues values = new ContentValues();
        values.put(TEST_ID, test.get_id());
        values.put(TEST_GROUP, test.getGroup());
        values.put(TEST_NAME, test.getName());
        values.put(TEST_SUBJECT, test.getSubject());
        values.put(TEST_DATE, test.getDateString());
        values.put(TEST_CONTENT_FILE_NAME, test.getContent().getFileName());
        values.put(TEST_CONTENT_EXTENSION, test.getContent().getExtension());
        values.put(TEST_CONTENT_CONTENT, test.getContent().getContent());
        values.put(TEST_EXPECTED_TIME, test.getExpectedTime());
        values.put(TEST_PUB_DATE, test.getPubDateStringForDB());
        Timber.d("test(" + test + ") is updated. ");
        long id = connection.update(TABLE_TEST, values, TEST_ID + "=" + test.get_id(), null);
        return id;
    }

    /**
     * @return ArrayList<TestGroup>
     * @brief 모든 수행평가 그룹을 반환합니다.
     */
    public ArrayList<TestGroup> getTestGroup() {
        ArrayList<TestGroup> result = new ArrayList<>();
        Cursor c = connection.query(TABLE_TEST_GROUP, TEST_GROUP_COLUMNS, null, null, null, null, null, null);
        while (c.moveToNext()) {
            result.add(new TestGroup(c.getInt(0), c.getString(1), c.getString(2)));
        }
        c.close();
        return result;
    }

    /**
     * @param _id int
     * @return TestGroup
     * @brief 특정 수행평가 그룹을 반환합니다.
     */
    public TestGroup getTestGroup(int _id) {
        TestGroup result = null;
        Cursor c = connection.query(TABLE_TEST_GROUP, TEST_GROUP_COLUMNS, TEST_GROUP_ID + "=?", new String[]{String.valueOf(_id)}, null, null, null, null);
        while (c.moveToNext()) {
            result = new TestGroup(c.getInt(0), c.getString(1), c.getString(2));
        }
        c.close();
        return result;
    }

    /**
     * @return ArrayList<Test>
     * @brief 모든 수행평가를 반환합니다.
     */
    public ArrayList<Test> getTest() {
        ArrayList<Test> result = new ArrayList<>();
        Cursor c = connection.query(TABLE_TEST, TEST_COLUMNS, null, null, null, null, null, null);
        while (c.moveToNext()) {
            result.add(new Test(
                            c.getInt(0),
                            c.getInt(1),
                            c.getString(2),
                            c.getString(3),
                            c.getString(4),
                            c.getString(5),
                            c.getString(6),
                            c.getBlob(7),
                            c.getInt(8),
                            c.getString(9)
                    )
            );
        }
        c.close();
        return result;
    }

    /**
     * @return ArrayList<Test>
     * @brief 모든 수행평가d 과목을 반환합니다.
     */
    public ArrayList<String> getSubejctOfTest() {
        ArrayList<String> result = new ArrayList<>();
        Cursor c = connection.query(TABLE_TEST, new String[]{TEST_SUBJECT}, null, null, null, null, null, null);
        while (c.moveToNext()) {
            result.add(c.getString(0));
        }
        c.close();
        return result;
    }

    /**
     * @param group int
     * @return ArrayList<Test>
     * @brief 해당 그룹의 모든 수행평가를 반환합니다.
     */
    public ArrayList<Test> getTest(int group) {
        ArrayList<Test> result = new ArrayList<>();
        Cursor c = connection.query(TABLE_TEST, TEST_COLUMNS, TEST_GROUP + "=" + group, null, null, null, null, null);
        while (c.moveToNext()) {
            result.add(new Test(
                            c.getInt(0),
                            c.getInt(1),
                            c.getString(2),
                            c.getString(3),
                            c.getString(4),
                            c.getString(5),
                            c.getString(6),
                            c.getBlob(7),
                            c.getInt(8),
                            c.getString(9)
                    )
            );
        }
        c.close();
        return result;
    }

    /**
     * @param subject String
     * @return ArrayList<Test>
     * @brief 해당 과목의 모든 수행평가를 반환합니다.
     */
    public ArrayList<Test> getTest(String subject) {
        ArrayList<Test> result = new ArrayList<>();
        Cursor c = connection.query(TABLE_TEST, TEST_COLUMNS, TEST_SUBJECT + "='" + subject + "'", null, null, null, null, null);
        while (c.moveToNext()) {
            result.add(new Test(
                            c.getInt(0),
                            c.getInt(1),
                            c.getString(2),
                            c.getString(3),
                            c.getString(4),
                            c.getString(5),
                            c.getString(6),
                            c.getBlob(7),
                            c.getInt(8),
                            c.getString(9)
                    )
            );
        }
        c.close();
        return result;
    }

    /**
     * @param testGroup TestGroup
     * @brief 수행평가 그룹 삭제하기
     */
    public void removeTestGroup(@NotNull TestGroup testGroup) {
        connection.delete(TABLE_TEST_GROUP, TEST_GROUP_ID + "=?", new String[]{String.valueOf(testGroup.get_id())});
    }

    /**
     * @param test Test
     * @brief 수행평가 삭제하기
     */
    public void removeTest(@NotNull Test test) {
        connection.delete(TABLE_TEST, TEST_ID + "=?", new String[]{String.valueOf(test.get_id())});
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
