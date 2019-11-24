package com.jeongwoochang.list_soohaeng.Activity;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.hugocastelani.waterfalltoolbar.WaterfallToolbar;
import com.jeongwoochang.list_soohaeng.Adapter.Listener.OnItemClickListener;
import com.jeongwoochang.list_soohaeng.Adapter.TestRecyclerAdapter;
import com.jeongwoochang.list_soohaeng.Util.AlarmUtil;
import com.jeongwoochang.list_soohaeng.Model.DBAdapter;
import com.jeongwoochang.list_soohaeng.Model.Schema.Test;
import com.jeongwoochang.list_soohaeng.Model.Schema.TestGroup;
import com.jeongwoochang.list_soohaeng.R;

import java.util.ArrayList;

public class TestListActivity extends AppCompatActivity implements OnItemClickListener {

    private RecyclerView testList;
    private TestRecyclerAdapter adapter;
    private DBAdapter dbAdapter;
    private AlarmUtil au;
    private TestGroup data;
    private FloatingActionButton addBtn;
    private WaterfallToolbar waterfallToolbar;
    private TextView title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_list);

        Intent intent = getIntent();
        data = (TestGroup) intent.getSerializableExtra("test_group");

        title = findViewById(R.id.title);
        testList = findViewById(R.id.test_list);
        waterfallToolbar = findViewById(R.id.waterfall_toolbar);
        waterfallToolbar.setRecyclerView(testList);
        testList.setLayoutManager(new LinearLayoutManager(this));
        testList.setAdapter(adapter = new TestRecyclerAdapter());
        adapter.setOnItemClickListener(this);
        addBtn = findViewById(R.id.add_button);
        addBtn.setOnClickListener(v -> {
            Intent i = new Intent(TestListActivity.this, EditTestActivity.class);
            startActivityForResult(i, 200);
        });

        if (data != null) {
            title.setText(data.getName());
            dbAdapter = DBAdapter.getInstance();
            au = AlarmUtil.getInstance();
            loadItems();
        }
    }

    private void loadItems() {
        adapter.setItems(getTestGroup());
        adapter.notifyDataSetChanged();
    }

    private ArrayList<Test> getTestGroup() {
        DBAdapter.connect(this);
        ArrayList<Test> result = dbAdapter.getTest(data.get_id());
        dbAdapter.close();
        return result;
    }

    @Override
    public void onItemClick(View v, int position) {
        Test test = adapter.getItem(position);
        Intent intent = new Intent(this, EditTestActivity.class);
        intent.putExtra("test", test);
        startActivityForResult(intent, 200);
    }

    @Override
    public void onItemLongClick(View v, int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder
                .setTitle("알림")
                .setMessage("정말 삭제하시겠습니까?\n삭제시 복구할 수 없습니다.")
                .setPositiveButton("예", (dialog, which) -> {
                    Test test = adapter.getItem(position);
                    removeTest(test);
                    loadItems();
                    dialog.dismiss();
                })
                .setNegativeButton("아니오", (dialog, which) -> dialog.dismiss()).show();
    }

    private void removeTest(Test test) {
        DBAdapter.connect(this);
        dbAdapter.removeTest(test);
        dbAdapter.close();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == 200) {
            Test test = (Test) data.getSerializableExtra("test");
            if (data.getBooleanExtra("isUpdate", true)) {
                test.set_id((int) updateTest(test));
                updateAlarm(test);
            } else {
                test.set_id((int) addTest(test));
                addAlarm(test);
            }
            loadItems();
        }
    }

    private long updateTest(Test test) {
        DBAdapter.connect(this);
        long id = dbAdapter.updateTest(test);
        dbAdapter.close();
        return id;
    }

    private void updateAlarm(Test test) {
        AlarmUtil.connect(this);
        au.removeAlarm(test.get_id());
        au.addAlarm(test.get_id(), test.getDate().minus(test.getExpectedTime()));
        au.close();
    }

    private long addTest(Test test) {
        DBAdapter.connect(this);
        long id = dbAdapter.addTest(test);
        dbAdapter.close();
        return id;
    }

    private void addAlarm(Test test) {
        AlarmUtil.connect(this);
        au.addAlarm(test.get_id(), test.getDate().minus(test.getExpectedTime()));
        au.close();
    }
}
