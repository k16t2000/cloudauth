package com.example.testcloudauth;

import android.app.AlertDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.Nullable;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

public class CustomCalendarView extends LinearLayout {
    private final String TAG = "CustomCalendarView";

    ImageButton NextButton,PreviousButton, ibSetWorkHors;
    Button btnSaveWorkHours;
    TextView CurrentDate, tvWorkHours;
    GridView gridView;


    private static final int MAX_CALENDAR_DAYS = 42;
    Calendar calendar = Calendar.getInstance(Locale.ENGLISH);
    Context context;
    SimpleDateFormat dateFormat = new SimpleDateFormat("MMMM yyyy", Locale.ENGLISH);
    SimpleDateFormat workDateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);

    CalendarAdapter calendarAdapter;
    AlertDialog alertDialogAddWorkTime;
    List<Date> dates = new ArrayList<>();
    List<Events> workingHoursList = new ArrayList<>();

    public CustomCalendarView(Context context) { super(context); }

    public CustomCalendarView(final Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        InitializeLayout();
        SetUpCalendar();

        PreviousButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                calendar.add(Calendar.MONTH,-1);
                SetUpCalendar();
            }
        });
        NextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                calendar.add(Calendar.MONTH,1);
                SetUpCalendar();
            }
        });

        // action that will occur when you click on a date in the calendar
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                final AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setCancelable(true);
                final View addView = LayoutInflater.from(parent.getContext()).inflate(R.layout.add_new_work_time_layout, null, false);
                tvWorkHours = addView.findViewById(R.id.tv_work_hours);
                ibSetWorkHors = addView.findViewById(R.id.ib_set_work_time);
                btnSaveWorkHours = addView.findViewById(R.id.btn_save_work_hours);

                // calling TimePickerDialog when clicking on the clock image
                ibSetWorkHors.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Calendar calendar = Calendar.getInstance();
                        int defaultHours = calendar.get(Calendar.HOUR_OF_DAY);
                        int defaultMinutes = 0;
                        TimePickerDialog timePickerDialog = new TimePickerDialog(addView.getContext(), R.style.Theme_AppCompat_Dialog,
                                new TimePickerDialog.OnTimeSetListener() {
                                    @Override
                                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                                        Calendar c = Calendar.getInstance();
                                        c.set(Calendar.HOUR_OF_DAY, hourOfDay);
                                        c.set(Calendar.MINUTE, minute);
                                        c.setTimeZone(TimeZone.getDefault());
                                        SimpleDateFormat hformate = new SimpleDateFormat("HH:mm", Locale.ENGLISH);
                                        String workDuration  = hformate.format(c.getTime());
                                        tvWorkHours.setText(workDuration);
                                    }
                                }, defaultHours, defaultMinutes, true);
                        timePickerDialog.show();
                    }
                });

                final String workDate = workDateFormat.format(dates.get(position));

                // save the running time and update the calendar after clicking the save button
                btnSaveWorkHours.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        SaveWorkHours(workDate, tvWorkHours.getText().toString());
                        SetUpCalendar();
                        alertDialogAddWorkTime.dismiss();
                    }
                });

                builder.setView(addView);
                alertDialogAddWorkTime = builder.create();
                alertDialogAddWorkTime.show();
            }
        });
    }

    public CustomCalendarView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    private void InitializeLayout() {
        LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.calendar_layout, this);
        NextButton = view.findViewById(R.id.nextBtn);
        PreviousButton = view.findViewById(R.id.previousBtn);
        CurrentDate = view.findViewById(R.id.current_Date);
        gridView = view.findViewById(R.id.gridView);
    }

    private void SetUpCalendar() {
        String currentDate = dateFormat.format(calendar.getTime());
        CurrentDate.setText(currentDate);
        dates.clear();
        Calendar monthCalendar = (Calendar) calendar.clone();
        monthCalendar.set(Calendar.DAY_OF_MONTH, 1);
        int FirstDayOfMonth = monthCalendar.get(Calendar.DAY_OF_WEEK) - 2;

        if (FirstDayOfMonth < 0) FirstDayOfMonth = 7 + FirstDayOfMonth;

        monthCalendar.add(Calendar.DAY_OF_MONTH, - FirstDayOfMonth);

        while (dates.size() < MAX_CALENDAR_DAYS) {
            dates.add(monthCalendar.getTime());
            monthCalendar.add(Calendar.DAY_OF_MONTH, 1);
        }

        calendarAdapter = new CalendarAdapter(context, dates, calendar, workingHoursList);
        gridView.setAdapter(calendarAdapter);
    }

    private void SaveWorkHours(String workDate, String workDuration) {
        Log.d(TAG, "SaveWorkHours: date: " + workDate + "\t duration: " + workDuration);
        Toast.makeText(context, "Work hours saved", Toast.LENGTH_SHORT).show();
    }
}
