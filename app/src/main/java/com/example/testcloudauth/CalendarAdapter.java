package com.example.testcloudauth;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class CalendarAdapter extends ArrayAdapter {
    List<Date> dates;
    Calendar currentDate;
    List<WorkingHoursList> workingHoursLists;
    LayoutInflater inflater;

    public CalendarAdapter(@NonNull Context context, List<Date> dates, Calendar currentDate, List<WorkingHoursList> workingHoursLists) {
        super(context, R.layout.single_cell_layout);
        this.dates = dates;
        this.currentDate = currentDate;
        this.workingHoursLists = workingHoursLists;
        inflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        Date monthDate = dates.get(position);
        Calendar dateCalendar = Calendar.getInstance();
        dateCalendar.setTime(monthDate);
        int DayNo = dateCalendar.get(Calendar.DAY_OF_MONTH);
        int displayMonth = dateCalendar.get(Calendar.MONTH) + 1;
        int displayYear = dateCalendar.get(Calendar.YEAR);
        int currentMonth = currentDate.get(Calendar.MONTH) + 1;
        int currentYear = currentDate.get(Calendar.YEAR);
        int workingHoursPerDay = 0;

        View view = convertView;
        if (view == null) {
            view = inflater.inflate(R.layout.single_cell_layout, parent, false);
        }

        if (displayMonth == currentMonth && displayYear == currentYear) {
            view.setBackgroundColor(getContext().getResources().getColor(R.color.green));
        } else {
            view.setBackgroundColor(getContext().getResources().getColor(R.color.white));
        }

        Calendar tmpCalendar = Calendar.getInstance(Locale.ENGLISH);
        SimpleDateFormat tmpDate = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
        for (WorkingHoursList tmpList : workingHoursLists) {
            try {
                    tmpCalendar.setTime(tmpDate.parse(tmpList.getDate()));
                    if (tmpCalendar.get(Calendar.DAY_OF_MONTH) == DayNo &&
                            (tmpCalendar.get(Calendar.MONTH) + 1) == currentMonth &&
                            tmpCalendar.get(Calendar.YEAR) == currentYear
                    ) {
                        workingHoursPerDay = tmpList.getDuration();
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }
        }

        TextView DayNumber = view.findViewById(R.id.calendar_day);
        TextView WorkingHoursPerDay = view.findViewById(R.id.tvWorkHourPerDate);
        DayNumber.setText(String.valueOf(DayNo));

        if (workingHoursPerDay > 0) {
            WorkingHoursPerDay.setText(String.valueOf(workingHoursPerDay));
        } else {
            WorkingHoursPerDay.setText("");
        }

        return view;
    }

    @Override
    public int getCount() { return dates.size(); }

    @Override
    public int getPosition(@Nullable Object item) { return dates.indexOf(item); }

    @Nullable
    @Override
    public Object getItem(int position) { return dates.get(position); }
}
