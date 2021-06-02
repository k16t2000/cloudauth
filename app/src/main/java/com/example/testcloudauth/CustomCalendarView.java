package com.example.testcloudauth;

import android.app.AlertDialog;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class CustomCalendarView extends LinearLayout {
    private ImageButton NextButton,PreviousButton;
    private Button btnSaveWorkHours;
    private TextView CurrentDate;
    private GridView gridView;


    private FirebaseAuth mAuth;
    private FirebaseUser user;
    private String userId;
    private DatabaseReference workHoursDBRef;


    private static final int MAX_CALENDAR_DAYS = 42;
    Calendar calendar = Calendar.getInstance(Locale.ENGLISH);
    Context context;
    SimpleDateFormat dateFormat = new SimpleDateFormat("MMMM yyyy", Locale.ENGLISH);
    SimpleDateFormat workDateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);

    CalendarAdapter calendarAdapter;
    AlertDialog alertDialogAddWorkTime;
    List<Date> dates = new ArrayList<>();
    List<WorkingHoursList> workingHoursList = new ArrayList<>();

    private final int MIN_WORK_HOURS = 1;
    private final int MAX_WORK_HOURS = 24;

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
            public void onItemClick(final AdapterView<?> parent, View view, int position, long id) {
                final AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setCancelable(true);
                final View addView = LayoutInflater.from(parent.getContext()).inflate(R.layout.numberpicker_layout, null, false);
                btnSaveWorkHours = addView.findViewById(R.id.btn_save_numberpicker);
                final NumberPicker numberPicker = (NumberPicker) addView.findViewById(R.id.numberpicker);
                numberPicker.setMinValue(MIN_WORK_HOURS);
                numberPicker.setMaxValue(MAX_WORK_HOURS);

                final String workDate = workDateFormat.format(dates.get(position));

                // save the running time and update the calendar after clicking the save button
                btnSaveWorkHours.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int workingDuration = numberPicker.getValue();
                        if (workingDuration > 0) {
                            SaveWorkHours(userId, workDate, workingDuration);
                            SetUpCalendar();
                            alertDialogAddWorkTime.dismiss();
                        } else {
                            Toast.makeText(context, getResources().getString(R.string.work_duration_empty), Toast.LENGTH_SHORT).show();
                        }
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
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        userId = user != null ? user.getUid() : null;
        workHoursDBRef = FirebaseDatabase.getInstance().getReference().child("WorkingHoursList");
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

    private void SaveWorkHours(String userId, String workDate, int workDuration) {
        WorkingHoursList workingHoursList = new WorkingHoursList(userId, workDate, workDuration);
//        workHoursDBRef.child(userId + "_" + workDate).setValue(workingHoursList);
        System.out.println(workingHoursList.getUserId() + " \t " + workingHoursList.getDate() + " \t " + workingHoursList.getDuration());
        Toast.makeText(context, getResources().getString(R.string.work_hours_saved), Toast.LENGTH_SHORT).show();
    }
}
