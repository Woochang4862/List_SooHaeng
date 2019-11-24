package com.jeongwoochang.list_soohaeng.Model.Schema;

import com.jeongwoochang.list_soohaeng.Model.DBAdapter;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;

import java.io.Serializable;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class Alarm implements Serializable {
    private Integer _id;
    private DateTime alarmDate;

    public Alarm(Integer _id, DateTime alarmDate) {
        this._id = _id;
        this.alarmDate = alarmDate;
    }

    public Alarm(Integer _id, String alarmDate) {
        this._id = _id;
        this.alarmDate = DateTimeFormat.forPattern(DBAdapter.ALARM_DATE_FORMAT).parseDateTime(alarmDate);
    }

    public Integer get_id() {
        return _id;
    }

    public void set_id(Integer _id) {
        this._id = _id;
    }

    public DateTime getAlarmDate() {
        return alarmDate;
    }

    public void setAlarmDate(DateTime alarmDate) {
        this.alarmDate = alarmDate;
    }

    public String getAlarmDateForString() {
        return DateTimeFormat.forPattern(DBAdapter.ALARM_DATE_FORMAT).print(alarmDate);
    }

    @NonNull
    @Override
    public String toString() {
        return "{ _id:" + _id + ", alarmDate:" + getAlarmDateForString() + "}";
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (obj instanceof Alarm) return ((Alarm) obj)._id.equals(_id);
        return false;
    }
}
