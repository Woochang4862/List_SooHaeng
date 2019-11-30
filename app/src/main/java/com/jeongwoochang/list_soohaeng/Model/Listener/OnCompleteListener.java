package com.jeongwoochang.list_soohaeng.Model.Listener;

public interface OnCompleteListener<T> {
    void onComplete(T result);
    void onException(Exception e);
}
