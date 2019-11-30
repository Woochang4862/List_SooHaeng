package com.jeongwoochang.list_soohaeng.Dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import com.google.android.material.textfield.TextInputEditText;
import com.jeongwoochang.list_soohaeng.Adapter.Listener.OnItemClickListener;
import com.jeongwoochang.list_soohaeng.Adapter.UserRecyclerAdapter;
import com.jeongwoochang.list_soohaeng.Model.FirestoreRemoteSource;
import com.jeongwoochang.list_soohaeng.Model.Listener.OnCompleteListener;
import com.jeongwoochang.list_soohaeng.Model.Schema.Log;
import com.jeongwoochang.list_soohaeng.Model.Schema.User;
import com.jeongwoochang.list_soohaeng.R;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import timber.log.Timber;

public class MemberNameInputDialog extends Dialog implements OnItemClickListener {

    private TextInputEditText memberName;
    private TextView searchBtn;
    private OnEmailClickListener onEmailClickListener;
    private FirestoreRemoteSource fsrs;
    private RecyclerView userList;
    private UserRecyclerAdapter adapter;

    public MemberNameInputDialog(@NonNull Context context) {
        super(context);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //다이얼로그 밖의 화면은 흐리게 만들어줌
        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
        layoutParams.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        layoutParams.dimAmount = 0.8f;
        getWindow().setAttributes(layoutParams);

        setContentView(R.layout.dialog_member_name_input);

        fsrs = FirestoreRemoteSource.getInstance();

        memberName = findViewById(R.id.member_name);
        searchBtn = findViewById(R.id.search_button);
        userList = findViewById(R.id.user_recycler);
        userList.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new UserRecyclerAdapter();
        adapter.setOnItemClickListener(this);
        userList.setAdapter(adapter);
        searchBtn.setOnClickListener(v -> {
            if (!memberName.getText().toString().isEmpty()) {
                fsrs.searchUserByEmail(memberName.getText().toString(), new OnCompleteListener<ArrayList<User>>() {
                    @Override
                    public void onComplete(ArrayList<User> result) {
                        Timber.d(result.toString());
                        adapter.setItems(result);
                        adapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onException(Exception e) {
                        e.printStackTrace();
                    }
                });
            }
        });
    }

    @Override
    public void onItemClick(View v, int position) {
        if (onEmailClickListener != null)
            onEmailClickListener.onEmailClick(this, adapter.getItem(position));
    }

    @Override
    public void onItemLongClick(View v, int position) {
        if (onEmailClickListener != null)
            onEmailClickListener.onEmailLongClick(this, adapter.getItem(position));
    }

    public interface OnEmailClickListener {
        void onEmailClick(Dialog dialog, User email);

        void onEmailLongClick(Dialog dialog, User email);
    }

    public void setOnAddButtonClickListener(OnEmailClickListener onEmailClickListener) {
        this.onEmailClickListener = onEmailClickListener;
    }
}
