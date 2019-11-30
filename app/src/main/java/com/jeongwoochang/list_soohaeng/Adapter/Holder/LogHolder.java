package com.jeongwoochang.list_soohaeng.Adapter.Holder;

import android.view.View;
import android.widget.TextView;

import com.jeongwoochang.list_soohaeng.R;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class LogHolder extends RecyclerView.ViewHolder {

    public TextView log;

    public LogHolder(@NonNull View itemView) {
        super(itemView);

        log = itemView.findViewById(R.id.log);
    }
}
