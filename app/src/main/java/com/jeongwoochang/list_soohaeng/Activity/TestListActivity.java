package com.jeongwoochang.list_soohaeng.Activity;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.hugocastelani.waterfalltoolbar.WaterfallToolbar;
import com.jeongwoochang.list_soohaeng.Adapter.Listener.OnItemClickListener;
import com.jeongwoochang.list_soohaeng.Adapter.TestRecyclerAdapter;
import com.jeongwoochang.list_soohaeng.Dialog.MemberNameInputDialog;
import com.jeongwoochang.list_soohaeng.Model.FirestoreRemoteSource;
import com.jeongwoochang.list_soohaeng.Model.Listener.OnCompleteListener;
import com.jeongwoochang.list_soohaeng.Model.Schema.Alarm;
import com.jeongwoochang.list_soohaeng.Model.Schema.Test;
import com.jeongwoochang.list_soohaeng.Model.Schema.TestGroup;
import com.jeongwoochang.list_soohaeng.Model.Schema.User;
import com.jeongwoochang.list_soohaeng.R;
import com.jeongwoochang.list_soohaeng.Util.AlarmUtil;

import java.util.ArrayList;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class TestListActivity extends AppCompatActivity implements OnItemClickListener {

    private RecyclerView testList;
    private TestRecyclerAdapter adapter;
    private FirestoreRemoteSource fsrs;
    private AlarmUtil au;
    private TestGroup data;
    private FloatingActionButton addBtn;
    private WaterfallToolbar waterfallToolbar;
    private TextView title;
    private ImageView sharBtn;
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
        sharBtn = findViewById(R.id.add_member_button);
        FirebaseUser currUser = FirebaseAuth.getInstance().getCurrentUser();
        if (!data.getOwner().equals(new User(currUser.getUid(), currUser.getEmail())))
            sharBtn.setVisibility(View.GONE);
        sharBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MemberNameInputDialog dialog = new MemberNameInputDialog(TestListActivity.this);
                dialog.setOnAddButtonClickListener(new MemberNameInputDialog.OnEmailClickListener() {
                    @Override
                    public void onEmailClick(Dialog dialog, User member) {
                        data.addMember(member);
                        updateTestGroup();
                        dialog.dismiss();
                    }

                    @Override
                    public void onEmailLongClick(Dialog dialog, User member) {

                    }
                });
                dialog.show();
            }
        });

        if (data != null) {
            title.setText(data.getName());
            fsrs = FirestoreRemoteSource.getInstance();
            au = AlarmUtil.getInstance();
            loadItems();
        }
    }

    private void updateTestGroup() {
        fsrs.updateTestGroup(data, new OnCompleteListener<TestGroup>() {
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
        getTest();
    }

    private void getTest() {
        showProgressDialog();
        fsrs.getTestByGroup(data.get_id(), new OnCompleteListener<ArrayList<Test>>() {
            @Override
            public void onComplete(ArrayList<Test> result) {
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

    @Override
    public void onItemClick(View v, int position) {
        Test test = adapter.getItem(position);
        Intent intent = new Intent(this, EditTestActivity.class);
        intent.putExtra("test", test);
        startActivityForResult(intent, 200);
    }

    @Override
    public void onItemLongClick(View v, int position) {
        FirebaseUser currUser = FirebaseAuth.getInstance().getCurrentUser();
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        if ( data.getOwner().equals(new User(currUser.getUid(), currUser.getEmail()))) {
            builder
                    .setTitle("알림")
                    .setMessage("정말 삭제하시겠습니까?\n삭제시 복구할 수 없습니다.")
                    .setPositiveButton("예", (dialog, which) -> {
                        Test test = adapter.getItem(position);
                        removeTest(test);
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

    private void removeTest(Test test) {
        showProgressDialog();
        fsrs.removeTest(test, new OnCompleteListener<Test>() {
            @Override
            public void onComplete(Test result) {
                hideProgressDialog();
                loadItems();
            }

            @Override
            public void onException(Exception e) {
                e.printStackTrace();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == 200) {
            if (data == null) {
                loadItems();
                return;
            }
            Test test = (Test) data.getSerializableExtra("test");
            if (data.getBooleanExtra("isUpdate", true)) {
                updateTest(test);
            } else {
                addTest(test);
            }
        }
    }

    private void updateTest(Test test) {
        showProgressDialog();
        fsrs.updateTest(test, new OnCompleteListener<Test>() {
            @Override
            public void onComplete(Test result) {
                updateAlarm(result);
                hideProgressDialog();
                loadItems();
            }

            @Override
            public void onException(Exception e) {
                hideProgressDialog();
                e.printStackTrace();
            }
        });
    }

    private void updateAlarm(Test test) {
        AlarmUtil.connect(this);
        au.removeAlarm(test.hashCode());
        au.addAlarm(new Alarm(test.hashCode(), test.getDate().minusDays((int) test.getExpectedTime())));
        au.close();
    }

    private void addTest(Test test) {
        showProgressDialog();
        fsrs.addTest(test, new OnCompleteListener<Test>() {
            @Override
            public void onComplete(Test result) {
                addAlarm(test);
                hideProgressDialog();
                loadItems();
            }

            @Override
            public void onException(Exception e) {
                hideProgressDialog();
                e.printStackTrace();
            }
        });
    }

    private void addAlarm(Test test) {
        AlarmUtil.connect(this);
        au.addAlarm(new Alarm(test.hashCode(), test.getDate().minusDays((int) test.getExpectedTime())));
        au.close();
    }
}
