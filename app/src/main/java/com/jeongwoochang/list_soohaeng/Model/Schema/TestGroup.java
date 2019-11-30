package com.jeongwoochang.list_soohaeng.Model.Schema;

import com.jeongwoochang.list_soohaeng.Model.DBAdapter;
import com.jeongwoochang.list_soohaeng.Model.FirestoreRemoteSource;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.io.Serializable;
import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class TestGroup implements Serializable {

    private String _id;

    private String name;

    private DateTime pub_date;

    private User owner;

    private ArrayList<User> members;

    public TestGroup(String name, User owner, ArrayList<User> members, DateTime pub_date) {
        this.name = name;
        this.owner = owner;
        this.members = members;
        this.pub_date = pub_date;
    }

    public TestGroup(String _id, String name, String pub_date, User owner, ArrayList<User> members) {
        this._id = _id;
        this.name = name;
        this.owner = owner;
        this.members = members;
        this.pub_date = convertStringToDateTime(pub_date, DBAdapter.PUB_DATE_FORMAT);
    }

    private DateTime convertStringToDateTime(String s, String f) {
        DateTimeFormatter fmt = org.joda.time.format.DateTimeFormat.forPattern(f);
        return DateTime.parse(s, fmt);
    }

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
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

    public String getPubDateString(String f) {
        return DateTimeFormat.forPattern(f).print(pub_date);
    }

    public void setPub_date(DateTime pub_date) {
        this.pub_date = pub_date;
    }

    public User getOwner() {
        return owner;
    }

    public void setOwner(User owner) {
        this.owner = owner;
    }

    public ArrayList<User> getMembers() {
        return members;
    }

    public void setMembers(ArrayList<User> members) {
        this.members = members;
    }

    public void addMember(User member){
        if(member.equals(owner)) return;
        members.add(member);
    }

    @NonNull
    @Override
    public String toString() {
        return "{ _id:" + _id + ", name:" + name + ", owner:" + owner + ", members:" + members + ", pub_date:" + getPubDateString(FirestoreRemoteSource.PUB_DATE_FORMAT) + "}";
    }

    @Override
    public int hashCode() {
        return _id.hashCode();
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (obj instanceof TestGroup) {
            return ((TestGroup) obj).get_id().equals(_id);
        }
        return false;
    }
}
