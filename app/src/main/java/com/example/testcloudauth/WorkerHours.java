package com.example.testcloudauth;

import android.os.Bundle;
import android.view.Gravity;
import android.widget.ImageView;
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

public class WorkerHours extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private DatabaseReference dbRef;
    private String userID;
    private LinearLayout layout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.worker_hours);

        layout = findViewById(R.id.workersHoursList);

        mAuth = FirebaseAuth.getInstance();
        dbRef = FirebaseDatabase.getInstance().getReference(getResources().getString(R.string.tableWorkingHours));
        FirebaseUser user = mAuth.getCurrentUser();
        assert user != null;
        userID = user.getUid();

        dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                // get workers
                for (DataSnapshot childSnapshot : snapshot.getChildren()){
                    TextView ntext = new TextView(getApplicationContext());
                    ntext.setText("Worker: " + childSnapshot.getKey());
                    ntext.setPadding(10,10,10,10);

                    RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                            RelativeLayout.LayoutParams.WRAP_CONTENT,
                            RelativeLayout.LayoutParams.WRAP_CONTENT
                    );
                    params.setMargins(0, 15, 5, 0);
                    params.alignWithParent = true;

                    ntext.setLayoutParams(params);
                    ntext.setGravity(Gravity.CENTER);
                    layout.addView(ntext);

                    // get each worker hours
                    for (DataSnapshot childChildSnapshot : childSnapshot.getChildren()) {
                        TextView time = new TextView(getApplicationContext());
                        time.setText(
                            "Date -- Hours: " + childChildSnapshot.child("date").getValue() +
                            " -- " + childChildSnapshot.child("duration").getValue() + "h"
                        );
                        time.setPadding(50,10,10,10);

                        RelativeLayout.LayoutParams timeTable = new RelativeLayout.LayoutParams(
                                RelativeLayout.LayoutParams.WRAP_CONTENT,
                                RelativeLayout.LayoutParams.WRAP_CONTENT
                        );
                        timeTable.setMargins(0, 15, 5, 0);
                        timeTable.alignWithParent = true;

                        time.setLayoutParams(timeTable);
                        time.setGravity(Gravity.CENTER);
                        layout.addView(time);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {

            }
        });
    }
}
