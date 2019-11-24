package com.jeongwoochang.list_soohaeng.Model.Schema;

import com.jeongwoochang.list_soohaeng.Model.DBAdapter;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.io.Serializable;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class TestGroup implements Serializable {

    private Integer _id;

    private String name;

    private DateTime pub_date;

    public TestGroup(String name) {
        this.name = name;
    }

    public TestGroup(Integer _id, String name, String s) {
        this._id = _id;
        this.name = name;
        pub_date = convertStringToDateTime(s, DBAdapter.PUB_DATE_FORMAT);
    }

    private DateTime convertStringToDateTime(String s, String f) {
        DateTimeFormatter fmt = org.joda.time.format.DateTimeFormat.forPattern(f);
        return DateTime.parse(s, fmt);
    }

    public Integer get_id() {
        return _id;
    }

    public void set_id(Integer _id) {
        this._id = _id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public DateTime getPub_date() {
        return pub_date;
    }

    public String getPubDateString(String f){
        return DateTimeFormat.forPattern(f).print(pub_date);
    }

    public void setPub_date(DateTime pub_date) {
        this.pub_date = pub_date;
    }

    @NonNull
    @Override
    public String toString() {
        return "{ _id:" + _id + ", name:" + name + ", pub_date:" + getPubDateString(DBAdapter.PUB_DATE_FORMAT) + "}";
    }

    @Override
    public int hashCode() {
        return _id;
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if(obj instanceof TestGroup){
            return ((TestGroup) obj).get_id().equals(_id);
        }
        return false;
    }
}
