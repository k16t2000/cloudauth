package com.example.testcloudauth;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import static com.google.firebase.auth.FirebaseAuth.getInstance;

public class ProfileActivity extends AppCompatActivity {
    //add Firebase Database stuff
    private static final String TAG = "ProfileActivity";
    private FirebaseDatabase mFirebaseDatabase;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private DatabaseReference myRef;
    private String userID;

    private ListView mListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile2);

        Button btnSignOut = (Button)findViewById(R.id.email_sign_out_btn);
        Button btncalendar = (Button)findViewById(R.id.calendar);
        mListView = (ListView)findViewById(R.id.listview);
        mAuth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        myRef = mFirebaseDatabase.getReference();
        FirebaseUser user = mAuth.getCurrentUser();
        userID = user.getUid();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
            FirebaseUser user = firebaseAuth.getCurrentUser();
            if (user != null) {
                // User is signed in
                Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
                toastMessage("Successfully signed in with: " + user.getEmail());
            } else {
                // User is signed out
                Log.d(TAG, "onAuthStateChanged:signed_out");
                toastMessage("Successfully signed out.");
            }
            }
        };
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                showData(dataSnapshot);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        //tvshow=(TextView)findViewById(R.id.tvshowemail);
        //tvuserid=(TextView)findViewById(R.id.tvuserid);
        //tvshow.setText(user.getEmail());

        //tvuserid.setText(user.getUid());
        //btn calendar
        btncalendar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent(getApplicationContext(),CalendarActivity.class);
                startActivity(i);
                Toast.makeText(ProfileActivity.this, "Calendar view", Toast.LENGTH_SHORT).show();
            }
        });

        //sign out
        btnSignOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //mAuth.signOut();
                getInstance().signOut();
                Toast.makeText(ProfileActivity.this, "Signing Out...", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                finish();
            }
        });

    }

    private void showData(DataSnapshot dataSnapshot) {
        for (DataSnapshot ds : dataSnapshot.getChildren()){
            Users uInfo=new Users();
            uInfo.setName(ds.child(userID).getValue(Users.class).getName());
            uInfo.setEmail(ds.child(userID).getValue(Users.class).getEmail());
            uInfo.setPosition(ds.child(userID).getValue(Users.class).getPosition());
            //display all the information
            Log.d(TAG, "showData: name: " + uInfo.getName());
            Log.d(TAG, "showData: email: " + uInfo.getEmail());
            Log.d(TAG, "showData: position: " + uInfo.getPosition());

            ArrayList<String> array  = new ArrayList<>();
            array.add(uInfo.getName());
            array.add(uInfo.getEmail());
            array.add(uInfo.getPosition());
            ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, array);
            mListView.setAdapter(adapter);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

    /**
     * customizable toast
     * @param message
     */
    private void toastMessage(String message){
        Toast.makeText(this,message, Toast.LENGTH_SHORT).show();
    }
}