package com.jeongwoochang.list_soohaeng.Adapter.Holder;

import android.view.View;
import android.widget.TextView;

import com.jeongwoochang.list_soohaeng.Adapter.Listener.OnItemClickListener;
import com.jeongwoochang.list_soohaeng.R;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class TestGroupHolder extends RecyclerView.ViewHolder {

    public TextView name, pub_date;

    public TestGroupHolder(@NonNull View itemView, final OnItemClickListener onItemClickListener) {
        super(itemView);

        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(onItemClickListener != null)
                    onItemClickListener.onItemClick(v, getLayoutPosition());
            }
        });
        itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if(onItemClickListener != null)
                    onItemClickListener.onItemLongClick(v, getLayoutPosition());
                return true;
            }
        });
        name = itemView.findViewById(R.id.group_name);
        pub_date = itemView.findViewById(R.id.pub_date);
    }
}
