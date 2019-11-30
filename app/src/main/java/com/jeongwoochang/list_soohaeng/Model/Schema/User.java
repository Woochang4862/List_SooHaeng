package com.jeongwoochang.list_soohaeng.Model.Schema;

import java.io.Serializable;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class User implements Serializable {
    private String uid;

    private String email;

    public User(String uid, String email) {
        this.uid = uid;
        this.email = email;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if(obj instanceof  User){
            return ((User) obj).uid.equals(uid);
        }
        return false;
    }

    @NonNull
    @Override
    public String toString() {
        return "{uid:" + uid + ", email:" + email + "}";
    }
}
