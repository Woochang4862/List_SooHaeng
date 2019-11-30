package com.jeongwoochang.list_soohaeng.Adapter.Holder;

import android.view.View;
import android.widget.TextView;

import com.jeongwoochang.list_soohaeng.Adapter.Listener.OnItemClickListener;
import com.jeongwoochang.list_soohaeng.R;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class UserHolder extends RecyclerView.ViewHolder {

    public TextView email;

    public UserHolder(@NonNull View itemView, OnItemClickListener onItemClickListener) {
        super(itemView);

        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(onItemClickListener != null)
                    onItemClickListener.onItemClick(v, getAdapterPosition());
            }
        });

        itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if(onItemClickListener != null)
                    onItemClickListener.onItemLongClick(v, getAdapterPosition());
                return true;
            }
        });

        email = itemView.findViewById(R.id.email);
    }
}
