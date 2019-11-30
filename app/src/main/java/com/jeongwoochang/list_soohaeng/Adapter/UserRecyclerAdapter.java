package com.jeongwoochang.list_soohaeng.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.jeongwoochang.list_soohaeng.Adapter.Holder.UserHolder;
import com.jeongwoochang.list_soohaeng.Adapter.Listener.OnItemClickListener;
import com.jeongwoochang.list_soohaeng.Model.Schema.User;
import com.jeongwoochang.list_soohaeng.R;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class UserRecyclerAdapter extends RecyclerView.Adapter<UserHolder> {

    private ArrayList<User> items;
    private OnItemClickListener onItemClickListener;

    @NonNull
    @Override
    public UserHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_user, parent, false);
        return new UserHolder(v, onItemClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull UserHolder holder, int position) {
        holder.email.setText(items.get(position).getEmail());
    }

    @Override
    public int getItemCount() {
        if (items == null)return 0;
        return items.size();
    }

    public void setItems(ArrayList<User> items) {
        this.items = items;
    }

    public User getItem(int position){
        return items.get(position);
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }
}
