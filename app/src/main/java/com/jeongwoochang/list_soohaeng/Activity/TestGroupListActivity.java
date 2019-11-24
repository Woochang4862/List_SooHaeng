package com.jeongwoochang.list_soohaeng.Activity;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.jeongwoochang.list_soohaeng.Adapter.Listener.OnItemClickListener;
import com.jeongwoochang.list_soohaeng.Adapter.TestGroupRecyclerAdapter;
import com.jeongwoochang.list_soohaeng.Dialog.GroupNameInputDialog;
import com.jeongwoochang.list_soohaeng.Model.DBAdapter;
import com.jeongwoochang.list_soohaeng.Model.Schema.TestGroup;
import com.jeongwoochang.list_soohaeng.R;

import java.util.ArrayList;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import timber.log.Timber;

public class TestGroupListActivity extends AppCompatActivity implements OnItemClickListener, GroupNameInputDialog.OnAddButtonClickListener {

    private RecyclerView testGroupList;
    private TestGroupRecyclerAdapter adapter;
    private DBAdapter dbAdapter;
    private FloatingActionButton addBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_group_list);

        addBtn = findViewById(R.id.add_button);
        addBtn.setOnClickListener(v -> {
            GroupNameInputDialog dialog = new GroupNameInputDialog(TestGroupListActivity.this);
            dialog.setOnAddButtonClickListener(TestGroupListActivity.this);
            dialog.show();
        });
        testGroupList = findViewById(R.id.test_group_list);
        adapter = new TestGroupRecyclerAdapter();
        adapter.setOnItemClickListener(this);
        testGroupList.setAdapter(adapter);
        testGroupList.setLayoutManager(new LinearLayoutManager(this));
        testGroupList.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));

        dbAdapter = DBAdapter.getInstance();

        loadItems();
    }

    @Override
    public void onItemClick(View v, int position) {
        TestGroup testGroup = adapter.getItem(position);
        Intent intent = new Intent(this, TestListActivity.class);
        intent.putExtra("test_group", testGroup);
        startActivity(intent);
    }

    @Override
    public void onItemLongClick(View v, final int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder
                .setTitle("알림")
                .setMessage("정말 삭제하시겠습니까?\n삭제시 복구할 수 없습니다.")
                .setPositiveButton("예", (dialog, which) -> {
                    TestGroup testGroup = adapter.getItem(position);
                    removeTestGroup(testGroup);
                    loadItems();
                    dialog.dismiss();
                })
                .setNegativeButton("아니오", (dialog, which) -> dialog.dismiss()).show();
    }

    private void removeTestGroup(TestGroup testGroup) {
        DBAdapter.connect(this);
        dbAdapter.removeTestGroup(testGroup);
        dbAdapter.close();
    }

    private void loadItems() {
        adapter.setItems(getTestGroup());
        adapter.notifyDataSetChanged();
    }

    private ArrayList<TestGroup> getTestGroup() {
        DBAdapter.connect(this);
        ArrayList<TestGroup> result = dbAdapter.getTestGroup();
        dbAdapter.close();
        Timber.d(result.toString());
        return result;
    }

    private void addTestGroup(String groupName){
        DBAdapter.connect(this);
        dbAdapter.addTestGroup(new TestGroup(groupName));
        dbAdapter.close();
    }

    @Override
    public void onAddButtonClick(Dialog dialog, String groupName) {
        addTestGroup(groupName);
        dialog.dismiss();
        loadItems();
    }

    @Override
    public void onBackPressed() {
        setResult(RESULT_OK);
        super.onBackPressed();
    }
}
