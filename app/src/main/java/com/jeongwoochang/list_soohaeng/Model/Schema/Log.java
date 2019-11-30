package com.jeongwoochang.list_soohaeng.Model.Schema;

import com.jeongwoochang.list_soohaeng.Model.DBAdapter;
import com.jeongwoochang.list_soohaeng.Model.FirestoreRemoteSource;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;

import java.io.Serializable;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class Log implements Serializable {
    private String _id;
    private User user;
    private String test;
    private Boolean isUpdate;
    private DateTime date;

    public Log(User user, String test, Boolean isUpdate, DateTime date) {
        this.user = user;
        this.test = test;
        this.isUpdate = isUpdate;
        this.date = date;
    }

    public Log(String _id, User user, String test, Boolean isUpdate, String date) {
        this._id = _id;
        this.user = user;
        this.test = test;
        this.isUpdate = isUpdate;
        this.date = DateTimeFormat.forPattern(FirestoreRemoteSource.LOG_DATE_FORMAT).parseDateTime(date);
    }

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getTest() {
        return test;
    }

    public void setTest(String test) {
        this.test = test;
    }

    public Boolean getUpdate() {
        return isUpdate;
    }

    public void setUpdate(Boolean update) {
        isUpdate = update;
    }

    public DateTime getDate() {
        return date;
    }

    public String getStringOfDate() {
        return DateTimeFormat.forPattern(DBAdapter.LOG_DATE_FORMAT).print(date);
    }

    public void setDate(DateTime date) {
        this.date = date;
    }

    @NonNull
    @Override
    public String toString() {
        return "{_id:" + _id + ", user:" + user + ", test:" + test + ", isUpdate:" + isUpdate + ", date:" + date + "}";
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if(obj instanceof Log){
            return ((Log) obj)._id.equals(_id);
        }
        return false;
    }
}
