package com.example.testcloudauth;

import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.StyleSpan;
import android.view.Gravity;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class WorkerHours extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private DatabaseReference dbRef, dbUsersRef;
    private String userID;
    private LinearLayout layout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.worker_hours);

        layout = findViewById(R.id.workersHoursList);

        mAuth = FirebaseAuth.getInstance();
        dbRef = FirebaseDatabase.getInstance().getReference(getResources().getString(R.string.tableWorkingHours));
        dbUsersRef = FirebaseDatabase.getInstance().getReference(getResources().getString(R.string.tableUsers));
        FirebaseUser user = mAuth.getCurrentUser();
        assert user != null;
        userID = user.getUid();

        dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                // get workers
                for (final DataSnapshot childSnapshot : snapshot.getChildren()){
                    int totalHours = 0;
                    final TextView ntext = new TextView(getApplicationContext());
                    setUsername(childSnapshot, ntext);

                    RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                            RelativeLayout.LayoutParams.MATCH_PARENT,
                            RelativeLayout.LayoutParams.MATCH_PARENT
                    );
                    params.setMargins(20, 15, 20, 0);
                    params.alignWithParent = true;

                    ntext.setLayoutParams(params);
                    ntext.setGravity(Gravity.CENTER);
                    ntext.setBackgroundColor(Color.rgb(225, 225, 225));
                    ntext.setTypeface(Typeface.DEFAULT_BOLD);
                    layout.addView(ntext);

                    // get each worker hours
                    for (DataSnapshot childChildSnapshot : childSnapshot.getChildren()) {
                        if (childChildSnapshot.child("date").getValue() != null) {
                            TextView time = new TextView(getApplicationContext());
                            String string = "";

                            String tmpDate = (String) childChildSnapshot.child("date").getValue();

                            Date tmpCurrDate;
                            SimpleDateFormat workDateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
                            Calendar calCurrMonth = Calendar.getInstance(Locale.ENGLISH);
                            try {
                                tmpCurrDate = workDateFormat.parse(tmpDate);
                                calCurrMonth.setTime(tmpCurrDate);
                                int month = calCurrMonth.get(Calendar.MONTH) + 1;
                                int day = calCurrMonth.get(Calendar.DAY_OF_MONTH);
                                string += (day < 10 ? ("0" + day) : day) +
                                        "-" + (month < 10 ? ("0" + month) : month) +
                                        "-" + calCurrMonth.get(Calendar.YEAR) +
                                        " — " + childChildSnapshot.child("duration").getValue() + "h";
                                totalHours += Integer.parseInt(childChildSnapshot.child("duration").getValue().toString());
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }

                            SpannableString spannableString = new SpannableString(string);
                            StyleSpan boldSpan = new StyleSpan(Typeface.BOLD);
                            spannableString.setSpan(boldSpan, 0, 12, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                            time.setText(spannableString);
                            time.setPadding(10, 10, 10, 10);

                            RelativeLayout.LayoutParams timeTable = new RelativeLayout.LayoutParams(
                                    RelativeLayout.LayoutParams.WRAP_CONTENT,
                                    RelativeLayout.LayoutParams.MATCH_PARENT
                            );
                            timeTable.setMargins(75, 15, 75, 0);
                            timeTable.alignWithParent = true;

                            time.setLayoutParams(timeTable);
                            time.setGravity(Gravity.CENTER_HORIZONTAL);
                            layout.addView(time);
                        }
                    }

                    final TextView totalWorkingHours = new TextView(getApplicationContext());
                    String string = "Total working hours: " + totalHours + "hours";
                    totalWorkingHours.setText(string);
                    RelativeLayout.LayoutParams params2 = new RelativeLayout.LayoutParams(
                            RelativeLayout.LayoutParams.MATCH_PARENT,
                            RelativeLayout.LayoutParams.MATCH_PARENT
                    );
                    params2.setMargins(200, 15, 200, 20);
                    params2.alignWithParent = true;

                    totalWorkingHours.setLayoutParams(params2);
                    totalWorkingHours.setGravity(Gravity.CENTER);
                    totalWorkingHours.setBackgroundColor(Color.rgb(225, 200, 200));
                    totalWorkingHours.setTypeface(Typeface.DEFAULT_BOLD);
                    layout.addView(totalWorkingHours);
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {

            }
        });
    }

    private void setUsername(final DataSnapshot dataSnapshot, final TextView textView) {
        dbUsersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot userChildSnapshot : snapshot.getChildren()){
                    if (userChildSnapshot.getKey().equals(dataSnapshot.getKey())) {
                        Users user = userChildSnapshot.getValue(Users.class);
                        String username = user.getName();
                        if (username != null && !username.isEmpty()) {
                            textView.setText(getResources().getString(R.string.worker) + username);
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}
