package com.jeongwoochang.list_soohaeng.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.jeongwoochang.list_soohaeng.Adapter.Holder.TestGroupHolder;
import com.jeongwoochang.list_soohaeng.Adapter.Listener.OnItemClickListener;
import com.jeongwoochang.list_soohaeng.Model.Schema.TestGroup;
import com.jeongwoochang.list_soohaeng.R;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class TestGroupRecyclerAdapter extends RecyclerView.Adapter<TestGroupHolder> {

    private ArrayList<TestGroup> items;
    private OnItemClickListener onItemClickListener;

    @NonNull
    @Override
    public TestGroupHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_test_group, parent, false);
        return new TestGroupHolder(v, onItemClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull TestGroupHolder holder, int position) {
        holder.name.setText(items.get(position).getName());
        holder.pub_date.setText(items.get(position).getPubDateString("yyyy-MM-dd"));
    }

    @Override
    public int getItemCount() {
        if(items == null)return 0;
        return items.size();
    }

    public void setItems(ArrayList<TestGroup> items) {
        this.items = items;
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public TestGroup getItem(int position) {
        return items.get(position);
    }
}
