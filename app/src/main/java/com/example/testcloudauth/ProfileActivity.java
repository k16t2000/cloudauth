package com.example.testcloudauth;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.net.URL;
import java.util.ArrayList;

import static com.google.firebase.auth.FirebaseAuth.getInstance;

public class ProfileActivity extends AppCompatActivity {
    //add Firebase Database stuff
    private FirebaseDatabase mFirebaseDatabase;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private DatabaseReference myRef;
    private String userID;
    private ImageView userPic;
    private ListView mListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile2);

        Button btnSignOut = (Button)findViewById(R.id.email_sign_out_btn);
        Button btncalendar = (Button)findViewById(R.id.calendar);
        mListView = (ListView)findViewById(R.id.listview);
        userPic = (ImageView)findViewById(R.id.userImage);
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
                    toastMessage("Successfully signed in with: " + user.getEmail());
                } else {
                    // User is signed out
                    toastMessage("Successfully signed out.");
                }
            }
        };

        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                showData(dataSnapshot.child("Users"));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        btncalendar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), CalendarActivity.class));
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
        boolean bool = false;
        final Users uInfo = new Users();
        uInfo.setName(dataSnapshot.child(userID).getValue(Users.class).getName());
        uInfo.setEmail(dataSnapshot.child(userID).getValue(Users.class).getEmail());
        uInfo.setPosition(dataSnapshot.child(userID).getValue(Users.class).getPosition());
        if (!bool){
            bool = true;
            uInfo.setImageurl(dataSnapshot.child(userID).getValue(Users.class).getImageurl());
            String url = uInfo.getImageurl();

            // Create new thread to fetch photo url and then update UI
            new Thread(new Runnable() {
                public void run() {
                    try {
                        URL url = new URL(uInfo.getImageurl());
                        final Bitmap bmp = BitmapFactory.decodeStream(url.openConnection().getInputStream());
                        userPic.post(new Runnable() {
                            public void run() {
                                userPic.setImageBitmap(bmp);
                            }
                        });
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            }).start();
        }

        //display all the information
        ArrayList<String> array  = new ArrayList<>();
        array.add(uInfo.getName());
        array.add(uInfo.getEmail());
        array.add(uInfo.getPosition());
        ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, array);
        mListView.setAdapter(adapter);
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