package com.jeongwoochang.list_soohaeng.Adapter.Holder;

import android.view.View;
import android.widget.TextView;

import com.jeongwoochang.list_soohaeng.Adapter.Listener.OnItemClickListener;
import com.jeongwoochang.list_soohaeng.R;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class TestHolder extends RecyclerView.ViewHolder {

    public TextView testName, testSubject, testDate, testExpectedDate;

    public TestHolder(@NonNull View itemView, final OnItemClickListener onItemClickListener) {
        super(itemView);

        testName = itemView.findViewById(R.id.test_name);
        testSubject = itemView.findViewById(R.id.test_subject);
        testDate = itemView.findViewById(R.id.test_date);
        testExpectedDate = itemView.findViewById(R.id.test_expected_date);
        itemView.setOnClickListener(v -> {
            if(onItemClickListener != null) onItemClickListener.onItemClick(v, getAdapterPosition());
        });
        itemView.setOnLongClickListener(v -> {
            if(onItemClickListener != null) onItemClickListener.onItemLongClick(v, getAdapterPosition());
            return true;
        });
    }
}
