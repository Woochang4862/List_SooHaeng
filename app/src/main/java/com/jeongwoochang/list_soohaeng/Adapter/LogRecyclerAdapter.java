package com.jeongwoochang.list_soohaeng.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.jeongwoochang.list_soohaeng.Adapter.Holder.LogHolder;
import com.jeongwoochang.list_soohaeng.Model.Schema.Log;
import com.jeongwoochang.list_soohaeng.R;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class LogRecyclerAdapter extends RecyclerView.Adapter<LogHolder> {

    private ArrayList<Log> items;

    @NonNull
    @Override
    public LogHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_log, parent, false);
        return new LogHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull LogHolder holder, int position) {
        Log item = items.get(position);
        holder.log.setText(item.getUser().getEmail() + "(이)가\n"+item.getStringOfDate()+"에 \n이 수행평가를 "+(item.getUpdate()?"수정":"추가")+"함");
    }

    @Override
    public int getItemCount() {
        if(items == null)return 0;
        return items.size();
    }

    public void setItems(ArrayList<Log> items) {
        this.items = items;
    }
}
