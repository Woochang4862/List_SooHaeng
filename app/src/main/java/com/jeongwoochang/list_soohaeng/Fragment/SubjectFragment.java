package com.jeongwoochang.list_soohaeng.Fragment;


import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.jeongwoochang.list_soohaeng.Activity.EditTestActivity;
import com.jeongwoochang.list_soohaeng.Util.AlarmUtil;
import com.jeongwoochang.list_soohaeng.Model.DBAdapter;
import com.jeongwoochang.list_soohaeng.Model.Schema.Test;
import com.jeongwoochang.list_soohaeng.R;

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
    private DBAdapter dbAdapter;
    private int currEditingParentPosition;
    private AlarmUtil au;

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
        dbAdapter = DBAdapter.getInstance();
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
                        currEditingParentPosition = parentPosition;

                        Intent intent = new Intent(getContext(), EditTestActivity.class);
                        intent.putExtra("test", model);
                        startActivityForResult(intent, 200);
                    }
                });
                ((TextView) view.findViewById(R.id.test_name)).setText(model.getName());
                ((TextView) view.findViewById(R.id.test_subject)).setText(model.getSubject());
                ((TextView) view.findViewById(R.id.test_date)).setText(model.getDateString());
                ((TextView) view.findViewById(R.id.test_expected_date)).setText(model.getExpectedTimeOfString() + "일");
            }
        });
        expandableLayout.setCollapseListener((ExpandCollapseListener.CollapseListener<String>) (parentIndex, parent, view) -> {
            //AnimationUtil.rotate(view.findViewById(R.id.expand_image), 180, 0, 4000);
            //AnimationUtil.collapse(view);
            view.findViewById(R.id.expand_image).setRotation(0);
        });
        expandableLayout.setExpandListener((ExpandCollapseListener.ExpandListener<String>) (parentIndex, parent, view) -> {
            //AnimationUtil.rotate(view.findViewById(R.id.expand_image), 0, 180, 4000);
            //AnimationUtil.expand(view);
            view.findViewById(R.id.expand_image).setRotation(180);
        });
        loadItems();
        return v;
    }

    private void loadItems() {
        Section<String, Test> section;
        ArrayList<String> subjects = getSubjectOfTest();
        ArrayList<Test> tests;

        for (String subject : subjects) {
            section = new Section<>();
            tests = getTest(subject);
            section.parent = subject;
            section.children.addAll(tests);
            expandableLayout.addSection(section);
        }
    }

    private ArrayList<Test> getTest(String subject) {
        DBAdapter.connect(getContext());
        ArrayList<Test> result = dbAdapter.getTest(subject);
        dbAdapter.close();
        return result;
    }

    private ArrayList<String> getSubjectOfTest() {
        DBAdapter.connect(getContext());
        ArrayList<String> result = dbAdapter.getSubejctOfTest();
        dbAdapter.close();
        return result;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Timber.d(requestCode + "");
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case 200:
                    Test test = (Test) data.getSerializableExtra("test");
                    if (data.getBooleanExtra("isUpdate", true)) {
                        test.set_id((int) updateTest(test));
                        updateAlarm(test);
                    } else {
                        test.set_id((int) addTest(test));
                        addAlarm(test);
                    }
                    refreshFragment();
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

    private long updateTest(Test test) {
        DBAdapter.connect(getContext());
        long id = dbAdapter.updateTest(test);
        dbAdapter.close();
        return id;
    }

    private void updateAlarm(Test test) {
        AlarmUtil.connect(getContext());
        au.removeAlarm(test.get_id());
        au.addAlarm(test.get_id(), test.getDate().minus(test.getExpectedTime()));
        au.close();
    }

    private long addTest(Test test) {
        DBAdapter.connect(getContext());
        long id = dbAdapter.addTest(test);
        dbAdapter.close();
        return id;
    }

    private void addAlarm(Test test) {
        AlarmUtil.connect(getContext());
        au.addAlarm(test.get_id(), test.getDate().minus(test.getExpectedTime()));
        au.close();
    }
}
