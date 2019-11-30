package com.jeongwoochang.list_soohaeng.Activity;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.jeongwoochang.list_soohaeng.Adapter.Listener.OnItemClickListener;
import com.jeongwoochang.list_soohaeng.Adapter.TestGroupRecyclerAdapter;
import com.jeongwoochang.list_soohaeng.Dialog.GroupNameInputDialog;
import com.jeongwoochang.list_soohaeng.Model.DBAdapter;
import com.jeongwoochang.list_soohaeng.Model.FirestoreRemoteSource;
import com.jeongwoochang.list_soohaeng.Model.Listener.OnCompleteListener;
import com.jeongwoochang.list_soohaeng.Model.Schema.Test;
import com.jeongwoochang.list_soohaeng.Model.Schema.TestGroup;
import com.jeongwoochang.list_soohaeng.Model.Schema.User;
import com.jeongwoochang.list_soohaeng.R;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;

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
    private FloatingActionButton addBtn;
    private FirestoreRemoteSource fsrs;
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
        setContentView(R.layout.activity_test_group_list);

        fsrs = FirestoreRemoteSource.getInstance();

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
        FirebaseUser currUser = FirebaseAuth.getInstance().getCurrentUser();
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        if ( adapter.getItem(position).getOwner().equals(new User(currUser.getUid(), currUser.getEmail()))) {
            builder
                    .setTitle("알림")
                    .setMessage("이 그룹에 속해있는 수행평가 또한 삭제됩니다.\n정말 삭제하시겠습니까?\n삭제시 복구할 수 없습니다.")
                    .setPositiveButton("예", (dialog, which) -> {
                        TestGroup testGroup = adapter.getItem(position);
                        removeTestGroup(testGroup);
                        dialog.dismiss();
                    })
                    .setNegativeButton("아니오", (dialog, which) -> dialog.dismiss()).show();
        } else {
            builder
                    .setTitle("알림")
                    .setMessage("멤버는 삭제할 권한이 없습니다.")
                    .setPositiveButton("확인", (dialog, which) -> {
                        dialog.dismiss();
                    }).show();
        }
    }

    private void removeTestGroup(TestGroup testGroup) {
        fsrs.removeTestGroup(testGroup, new OnCompleteListener<TestGroup>() {
            @Override
            public void onComplete(TestGroup result) {
                loadItems();
            }

            @Override
            public void onException(Exception e) {
                e.printStackTrace();
            }
        });
    }

    private void loadItems() {
        getTestGroup();
    }

    private void getTestGroup() {
        showProgressDialog();
        fsrs.getTestGroup(new OnCompleteListener<ArrayList<TestGroup>>() {
            @Override
            public void onComplete(ArrayList<TestGroup> result) {
                adapter.setItems(result);
                adapter.notifyDataSetChanged();
                hideProgressDialog();
            }

            @Override
            public void onException(Exception e) {
                e.printStackTrace();
                hideProgressDialog();
            }
        });
    }

    private void addTestGroup(String groupName) {
        FirebaseUser currUser = FirebaseAuth.getInstance().getCurrentUser();
        User owner = new User(currUser.getUid(), currUser.getEmail());
        TestGroup testGroup = new TestGroup(groupName, owner, new ArrayList<>(), DateTime.now());
        fsrs.addTestGroup(testGroup, new OnCompleteListener<TestGroup>() {
            @Override
            public void onComplete(TestGroup result) {
                loadItems();
            }

            @Override
            public void onException(Exception e) {
                e.printStackTrace();
            }
        });
    }

    @Override
    public void onAddButtonClick(Dialog dialog, String groupName) {
        addTestGroup(groupName);
        dialog.dismiss();
    }

    @Override
    public void onBackPressed() {
        setResult(RESULT_OK);
        super.onBackPressed();
    }
}
