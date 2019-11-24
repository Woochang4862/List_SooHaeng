package com.jeongwoochang.list_soohaeng.Fragment;


import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.tibolte.agendacalendarview.AgendaCalendarView;
import com.github.tibolte.agendacalendarview.CalendarPickerController;
import com.github.tibolte.agendacalendarview.models.BaseCalendarEvent;
import com.github.tibolte.agendacalendarview.models.CalendarEvent;
import com.github.tibolte.agendacalendarview.models.DayItem;
import com.jeongwoochang.list_soohaeng.Activity.EditTestActivity;
import com.jeongwoochang.list_soohaeng.Util.AlarmUtil;
import com.jeongwoochang.list_soohaeng.Model.DBAdapter;
import com.jeongwoochang.list_soohaeng.Model.Schema.Test;
import com.jeongwoochang.list_soohaeng.R;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static android.app.Activity.RESULT_OK;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link DateFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DateFragment extends Fragment implements CalendarPickerController {

    private AgendaCalendarView calendarView;
    private DBAdapter dbAdapter;
    private AlarmUtil au;
    private Map<Long, Test> map;
    private List<CalendarEvent> eventList;

    public DateFragment() {
    }

    public static DateFragment newInstance() {
        DateFragment fragment = new DateFragment();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_date, container, false);

        dbAdapter = DBAdapter.getInstance();
        au = AlarmUtil.getInstance();

        calendarView = view.findViewById(R.id.agenda_calendar_view);

        loadItems();
        return view;
    }

    private void loadItems() {
        eventList = new ArrayList<>();
        DBAdapter.connect(getContext());
        ArrayList<Test> tests = dbAdapter.getTest();
        map = new HashMap<>();
        for (Test test : tests) {
            Calendar startTime1 = test.getDate().minus(test.getExpectedTime()).toCalendar(Locale.getDefault());
            Calendar endTime1 = test.getDate().plusDays(1).toCalendar(Locale.getDefault());
            BaseCalendarEvent event1 = new BaseCalendarEvent(test.getName(), test.getSubject(), dbAdapter.getTestGroup(test.getGroup()).getName(),
                    ContextCompat.getColor(getContext(), R.color.colorAccent), startTime1, endTime1, true);
            map.put(event1.getId(), test);
            eventList.add(event1);
        }

        dbAdapter.close();

        Calendar minDate = Calendar.getInstance();
        Calendar maxDate = Calendar.getInstance();

        minDate.add(Calendar.MONTH, -2);
        minDate.set(Calendar.DAY_OF_MONTH, 1);
        maxDate.add(Calendar.YEAR, 1);

        calendarView.init(eventList, minDate, maxDate, Locale.getDefault(), this);
    }

    @Override
    public void onDaySelected(DayItem dayItem) {

    }

    @Override
    public void onEventSelected(CalendarEvent event) {
        Test test = map.get(event.getId());
        Intent intent = new Intent(getContext(), EditTestActivity.class);
        intent.putExtra("test", test);
        startActivityForResult(intent, 200);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case 200:
                    Test test = (Test) data.getSerializableExtra("test");
                    if (data.getBooleanExtra("isUpdate", true)) {
                        test.set_id((int) updateTest(test));
                        updateAlarm(test);
                    } else {
                        test.set_id((int) addTest(test));
                        addTest(test);
                    }
                    loadItems();
                    break;
                case 201:
                    loadItems();
                    break;
            }
        }
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

    @Override
    public void onScrollToDate(Calendar calendar) {

    }
}
