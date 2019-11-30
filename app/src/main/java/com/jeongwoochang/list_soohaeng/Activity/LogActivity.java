package com.jeongwoochang.list_soohaeng.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import timber.log.Timber;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;

import com.jeongwoochang.list_soohaeng.Adapter.LogRecyclerAdapter;
import com.jeongwoochang.list_soohaeng.Model.FirestoreRemoteSource;
import com.jeongwoochang.list_soohaeng.Model.Listener.OnCompleteListener;
import com.jeongwoochang.list_soohaeng.Model.Schema.Log;
import com.jeongwoochang.list_soohaeng.Model.Schema.Test;
import com.jeongwoochang.list_soohaeng.R;

import java.util.ArrayList;

public class LogActivity extends AppCompatActivity {

    private RecyclerView logList;
    private LogRecyclerAdapter adapter;
    private FirestoreRemoteSource fsrs;
    private Test data;
    public ProgressDialog mProgressDialog;

    public void showProgressDialog() {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(this);
            mProgressDialog.setMessage(getString(R.string.loading));
            mProgressDialog.setIndeterminate(true);
            mProgressDialog.setCancelable(false);
        }

        mProgressDialog.show();
    }

    public void hideProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log);

        fsrs = FirestoreRemoteSource.getInstance();

        Intent intent = getIntent();
        data = (Test) intent.getSerializableExtra("test");

        logList = findViewById(R.id.log_list);
        logList.setLayoutManager(new LinearLayoutManager(this));
        adapter = new LogRecyclerAdapter();
        logList.setAdapter(adapter);

        if (data != null)
            loadItems();
    }

    void loadItems() {
        showProgressDialog();
        fsrs.getLog(data.get_id(), new OnCompleteListener<ArrayList<Log>>() {
            @Override
            public void onComplete(ArrayList<Log> result) {
                Timber.d(result.toString());
                adapter.setItems(result);
                adapter.notifyDataSetChanged();
                hideProgressDialog();
            }

            @Override
            public void onException(Exception e) {
                e.printStackTrace();
            }
        });
    }
}
