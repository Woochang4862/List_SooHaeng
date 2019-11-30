package com.jeongwoochang.list_soohaeng.Model.Schema;

import com.jeongwoochang.list_soohaeng.Model.FirestoreRemoteSource;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class Test implements Serializable {

    private String _id;

    private String group;

    private String name;

    private String subject;

    private DateTime date;

    private Content content;

    private long expectedTime;

    private DateTime pub_date;

    public Test(String group, String name, String subject, DateTime date, Content content, long expectedTime) {
        this.group = group;
        this.name = name;
        this.subject = subject;
        this.date = date;
        this.content = content;
        this.expectedTime = expectedTime;
    }

    public Test(String _id, String group, String name, String subject, String date, Content content, long expectedTime, String pub_date) {
        this._id = _id;
        this.group = group;
        this.name = name;
        this.content = content;
        this.subject = subject;
        this.date = convertStringToDateTime(date, FirestoreRemoteSource.TEST_DATE_FORMAT);
        this.expectedTime = expectedTime;
        this.pub_date = convertStringToDateTime(pub_date, FirestoreRemoteSource.PUB_DATE_FORMAT);
    }

    public Test(String _id, String group, String name, String subject, DateTime date, Content content, long expectedTime, DateTime pub_date) {
        this._id = _id;
        this.group = group;
        this.name = name;
        this.subject = subject;
        this.date = date;
        this.content = content;
        this.expectedTime = expectedTime;
        this.pub_date = pub_date;
    }

    private DateTime convertStringToDateTime(String s, String f) {
        DateTimeFormatter fmt = DateTimeFormat.forPattern(f);
        return DateTime.parse(s, fmt);
    }

    private String convertDateTimeToString(DateTime d, String f) {
        return DateTimeFormat.forPattern(f).print(d);
    }

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public DateTime getDate() {
        return date;
    }

    public String getDateString() {
        return convertDateTimeToString(date, FirestoreRemoteSource.TEST_DATE_FORMAT);
    }

    public void setDate(DateTime date) {
        this.date = date;
    }

    public DateTime getPub_date() {
        return pub_date;
    }

    public String getPubDateString() {
        return convertDateTimeToString(pub_date, FirestoreRemoteSource.TEST_DATE_FORMAT);
    }

    public String getPubDateStringForDB() {
        return convertDateTimeToString(pub_date, FirestoreRemoteSource.PUB_DATE_FORMAT);
    }

    public void setPub_date(DateTime pub_date) {
        this.pub_date = pub_date;
    }

    public long getExpectedTime() {
        return expectedTime;
    }

    public void setExpectedTime(long expectedTime) {
        this.expectedTime = expectedTime;
    }

    public void setExpectedTime(int day) {
        this.expectedTime = day;
    }

    public Content getContent() {
        return content;
    }

    public void setContent(Content content) {
        this.content = content;
    }

    @NonNull
    @Override
    public String toString() {
        return "{ _id:" + _id + ", group:" + group + ", name:" + name + ", subject:" + subject + ", date:" + getDateString() + ", content:" + content + ", pub_date:" + getPubDateString() + ", expected:" + expectedTime + " }";
    }

    @Override
    public int hashCode() {
        try {
            return ByteBuffer.wrap(_id.getBytes("US-ASCII")).getInt();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return super.hashCode();
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (obj instanceof Test) {
            return ((Test) obj).get_id().equals(_id);
        }
        return false;
    }
}
