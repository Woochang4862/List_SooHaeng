package com.jeongwoochang.list_soohaeng.Fragment;


import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.tibolte.agendacalendarview.AgendaCalendarView;
import com.github.tibolte.agendacalendarview.CalendarPickerController;
import com.github.tibolte.agendacalendarview.models.BaseCalendarEvent;
import com.github.tibolte.agendacalendarview.models.CalendarEvent;
import com.github.tibolte.agendacalendarview.models.DayItem;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.jeongwoochang.list_soohaeng.Activity.EditTestActivity;
import com.jeongwoochang.list_soohaeng.Model.FirestoreRemoteSource;
import com.jeongwoochang.list_soohaeng.Model.Listener.OnCompleteListener;
import com.jeongwoochang.list_soohaeng.Model.Schema.Alarm;
import com.jeongwoochang.list_soohaeng.Model.Schema.Test;
import com.jeongwoochang.list_soohaeng.Model.Schema.TestGroup;
import com.jeongwoochang.list_soohaeng.R;
import com.jeongwoochang.list_soohaeng.Util.AlarmUtil;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import static android.app.Activity.RESULT_OK;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link DateFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DateFragment extends Fragment implements CalendarPickerController {

    private AgendaCalendarView calendarView;
    private FirestoreRemoteSource fsrs;
    private AlarmUtil au;
    private Map<Long, Test> map;
    private List<CalendarEvent> eventList;
    private Context context;
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

    public DateFragment() {
    }

    public static DateFragment newInstance() {
        DateFragment fragment = new DateFragment();
        return fragment;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        this.context = context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_date, container, false);

        fsrs = FirestoreRemoteSource.getInstance();
        au = AlarmUtil.getInstance();

        calendarView = view.findViewById(R.id.agenda_calendar_view);

        calendarViewInit();

        if(FirebaseAuth.getInstance().getCurrentUser() != null) {
            loadItems();
        } else {
            Snackbar.make(view, "로그인 해주세요.", Snackbar.LENGTH_SHORT).show();
        }
        return view;
    }

    private void calendarViewInit() {
        Calendar minDate = Calendar.getInstance();
        Calendar maxDate = Calendar.getInstance();

        minDate.add(Calendar.MONTH, -2);
        minDate.set(Calendar.DAY_OF_MONTH, 1);
        maxDate.add(Calendar.YEAR, 1);

        eventList = new ArrayList<>();
        calendarView.init(eventList, minDate, maxDate, Locale.getDefault(), DateFragment.this);
    }

    private void loadItems() {
        showProgressDialog();
        fsrs.getTestGroup(new OnCompleteListener<ArrayList<TestGroup>>() {
            @Override
            public void onComplete(ArrayList<TestGroup> result) {
                for (TestGroup testGroup : result) {
                    fsrs.getTestByGroup(testGroup.get_id(), new OnCompleteListener<ArrayList<Test>>() {
                        @Override
                        public void onComplete(ArrayList<Test> result) {
                            ArrayList<Test> tests = result;
                            map = new HashMap<>();
                            for (Test test : tests) {
                                Calendar startTime1 = test.getDate().minusDays((int) test.getExpectedTime()).toCalendar(Locale.getDefault());
                                Calendar endTime1 = test.getDate().plusDays(1).toCalendar(Locale.getDefault());
                                BaseCalendarEvent event1 = new BaseCalendarEvent(test.getName(), test.getSubject(), testGroup.getName(),
                                        ContextCompat.getColor(Objects.requireNonNull(context), R.color.colorAccent), startTime1, endTime1, true);
                                event1.setId(test.hashCode());
                                map.put(event1.getId(), test);
                                eventList.add(event1);

                                Calendar minDate = Calendar.getInstance();
                                Calendar maxDate = Calendar.getInstance();

                                minDate.add(Calendar.MONTH, -2);
                                minDate.set(Calendar.DAY_OF_MONTH, 1);
                                maxDate.add(Calendar.YEAR, 1);

                                calendarView.init(eventList, minDate, maxDate, Locale.getDefault(), DateFragment.this);
                            }
                            hideProgressDialog();
                        }

                        @Override
                        public void onException(Exception e) {
                            e.printStackTrace();
                            hideProgressDialog();
                        }
                    });
                }
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
    public void onDaySelected(DayItem dayItem) {

    }

    @Override
    public void onEventSelected(CalendarEvent event) {
        Test test = map.get(event.getId());
        Intent intent = new Intent(context, EditTestActivity.class);
        intent.putExtra("test", test);
        startActivityForResult(intent, 200);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
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

    private void updateTest(Test test) {
        fsrs.updateTest(test, new OnCompleteListener<Test>() {
            @Override
            public void onComplete(Test result) {
                updateAlarm(result);
                refreshFragment();
            }

            @Override
            public void onException(Exception e) {
                e.printStackTrace();
            }
        });
    }

    private void updateAlarm(Test test) {
        AlarmUtil.connect(context);
        au.removeAlarm(test.hashCode());
        au.addAlarm(new Alarm(test.hashCode(), test.getDate().minusDays((int) test.getExpectedTime())));
        au.close();
    }

    private void refreshFragment() {
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        if (Build.VERSION.SDK_INT >= 26) {
            ft.setReorderingAllowed(false);
        }
        ft.detach(this).attach(this).commit();
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
        AlarmUtil.connect(context);
        au.addAlarm(new Alarm(test.hashCode(), test.getDate().minusDays((int) test.getExpectedTime())));
        au.close();
    }

    @Override
    public void onScrollToDate(Calendar calendar) {

    }
}
