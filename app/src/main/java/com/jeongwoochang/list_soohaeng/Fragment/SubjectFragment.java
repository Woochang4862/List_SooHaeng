package com.jeongwoochang.list_soohaeng.Fragment;


import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.jeongwoochang.list_soohaeng.Activity.EditTestActivity;
import com.jeongwoochang.list_soohaeng.Model.FirestoreRemoteSource;
import com.jeongwoochang.list_soohaeng.Model.Listener.OnCompleteListener;
import com.jeongwoochang.list_soohaeng.Model.Schema.Alarm;
import com.jeongwoochang.list_soohaeng.Model.Schema.Test;
import com.jeongwoochang.list_soohaeng.R;
import com.jeongwoochang.list_soohaeng.Util.AlarmUtil;

import java.util.ArrayList;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import iammert.com.expandablelib.ExpandCollapseListener;
import iammert.com.expandablelib.ExpandableLayout;
import iammert.com.expandablelib.Section;
import timber.log.Timber;

import static android.app.Activity.RESULT_OK;

/**
 * A simple {@link Fragment} subclass.
 */
public class SubjectFragment extends Fragment {

    private ExpandableLayout expandableLayout;
    private FirestoreRemoteSource fsrs;
    private AlarmUtil au;
    public ProgressDialog mProgressDialog;

    public void showProgressDialog() {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(getContext());
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

    public SubjectFragment() {
        // Required empty public constructor
    }

    public static SubjectFragment newInstance() {
        SubjectFragment fragment = new SubjectFragment();
        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_subject, container, false);
        fsrs = FirestoreRemoteSource.getInstance();
        au = AlarmUtil.getInstance();
        expandableLayout = v.findViewById(R.id.el);
        expandableLayout.setRenderer(new ExpandableLayout.Renderer<String, Test>() {
            @Override
            public void renderParent(View view, String model, boolean isExpanded, int parentPosition) {
                ((TextView) view.findViewById(R.id.header_subject)).setText(model);
                if (isExpanded) {
                    view.findViewById(R.id.expand_image).setRotation(180);
                } else {
                    view.findViewById(R.id.expand_image).setRotation(0);
                }
            }

            @Override
            public void renderChild(View view, Test model, int parentPosition, int childPosition) {
                view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(getContext(), EditTestActivity.class);
                        intent.putExtra("test", model);
                        startActivityForResult(intent, 200);
                    }
                });
                ((TextView) view.findViewById(R.id.test_name)).setText(model.getName());
                ((TextView) view.findViewById(R.id.test_subject)).setText(model.getSubject());
                ((TextView) view.findViewById(R.id.test_date)).setText(model.getDateString());
                ((TextView) view.findViewById(R.id.test_expected_date)).setText(model.getExpectedTime() + "일");
            }
        });
        expandableLayout.setCollapseListener((ExpandCollapseListener.CollapseListener<String>) (parentIndex, parent, view) -> {
            view.findViewById(R.id.expand_image).setRotation(0);
        });
        expandableLayout.setExpandListener((ExpandCollapseListener.ExpandListener<String>) (parentIndex, parent, view) -> {
            view.findViewById(R.id.expand_image).setRotation(180);
        });

        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            loadItems();
        } else {
            Snackbar.make(v, "로그인 해주세요.", Snackbar.LENGTH_SHORT).show();
        }
        return v;
    }

    private void loadItems() {
        getSubjectOfTest();
    }

    private void getSubjectOfTest() {
        showProgressDialog();
        fsrs.getSubejctOfTest(new OnCompleteListener<ArrayList<String>>() {
            @Override
            public void onComplete(ArrayList<String> result) {
                Log.d("SubjectFragment", result.toString());
                if (result.isEmpty()) {
                    hideProgressDialog();
                    return;
                }
                for (String subject : result) {
                    getTest(subject);
                }
            }

            @Override
            public void onException(Exception e) {
                e.printStackTrace();
                hideProgressDialog();
            }
        });
    }

    private void getTest(String subject) {
        fsrs.getTestBySubject(subject, new OnCompleteListener<ArrayList<Test>>() {
            @Override
            public void onComplete(ArrayList<Test> result) {
                Section<String, Test> section = new Section<>();
                section.parent = subject;
                section.children.addAll(result);
                expandableLayout.addSection(section);
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
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Timber.d(requestCode + "");
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case 200:
                    if (data == null) {
                        refreshFragment();
                        break;
                    }
                    Test test = (Test) data.getSerializableExtra("test");
                    if (data.getBooleanExtra("isUpdate", true)) {
                        updateTest(test);
                    } else {
                        addTest(test);
                    }
                    break;
            }
        }
    }

    private void refreshFragment() {
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        if (Build.VERSION.SDK_INT >= 26) {
            ft.setReorderingAllowed(false);
        }
        ft.detach(this).attach(this).commit();
    }

    private void updateTest(Test test) {
        fsrs.updateTest(test, new OnCompleteListener<Test>() {
            @Override
            public void onComplete(Test result) {
                updateAlarm(test);
                refreshFragment();
            }

            @Override
            public void onException(Exception e) {
                e.printStackTrace();
            }
        });
    }

    private void updateAlarm(Test test) {
        AlarmUtil.connect(getContext());
        au.removeAlarm(test.hashCode());
        au.addAlarm(new Alarm(test.hashCode(), test.getDate().minusDays((int) test.getExpectedTime())));
        au.close();
    }

    private void addTest(Test test) {
        fsrs.addTest(test, new OnCompleteListener<Test>() {
            @Override
            public void onComplete(Test result) {
                addAlarm(result);
                refreshFragment();
            }

            @Override
            public void onException(Exception e) {
                e.printStackTrace();
            }
        });
    }

    private void addAlarm(Test test) {
        AlarmUtil.connect(getContext());
        au.addAlarm(new Alarm(test.hashCode(), test.getDate().minusDays((int) test.getExpectedTime())));
        au.close();
    }
}
