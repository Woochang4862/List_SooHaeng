package com.jeongwoochang.list_soohaeng.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.jeongwoochang.list_soohaeng.Adapter.Listener.OnItemClickListener;
import com.jeongwoochang.list_soohaeng.Adapter.Holder.TestHolder;
import com.jeongwoochang.list_soohaeng.Model.Schema.Test;
import com.jeongwoochang.list_soohaeng.R;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class TestRecyclerAdapter extends RecyclerView.Adapter<TestHolder> {

    private ArrayList<Test> items;
    private OnItemClickListener onItemClickListener;

    @NonNull
    @Override
    public TestHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_test, parent, false);
        return new TestHolder(v, onItemClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull TestHolder holder, int position) {
        holder.testName.setText(items.get(position).getName());
        holder.testSubject.setText(items.get(position).getSubject());
        holder.testDate.setText(items.get(position).getDateString());
        holder.testExpectedDate.setText(items.get(position).getExpectedTime()+"일");
    }

    @Override
    public int getItemCount() {
        if (items == null) return 0;
        return items.size();
    }

    public void setItems(ArrayList<Test> items) {
        this.items = items;
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public Test getItem(int position) {
        if (items == null) return null;
        return items.get(position);
    }
}
