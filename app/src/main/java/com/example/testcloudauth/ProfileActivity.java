package com.example.testcloudauth;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;

import com.example.testcloudauth.Utils.Utils;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import static com.google.firebase.auth.FirebaseAuth.getInstance;

public class ProfileActivity extends AppCompatActivity {
    private static final int PICK_IMAGE_REQUEST = 1;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private DatabaseReference myRef;
    private String userID;
    private ImageView userPic;
    private ListView mListView;

    private Utils utils;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile2);

        Button btnSignOut = (Button)findViewById(R.id.email_sign_out_btn);
        Button btncalendar = (Button)findViewById(R.id.calendar);
        mListView = (ListView)findViewById(R.id.listview);
        userPic = (ImageView)findViewById(R.id.userImage);
        mAuth = FirebaseAuth.getInstance();
        //add Firebase Database stuff
        FirebaseDatabase mFirebaseDatabase = FirebaseDatabase.getInstance();
        myRef = mFirebaseDatabase.getReference();
        FirebaseUser user = mAuth.getCurrentUser();
        assert user != null;
        userID = user.getUid();
        utils = new Utils();

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();

                if (user != null) {
                    // User is signed in
//                    utils.toastMessage(getApplicationContext(), getString(R.string.successfullySignedIn) + user.getEmail());
                } else {
                    // User is signed out
                    utils.toastMessage(getApplicationContext(), getString(R.string.successfullySignedOut));
                }
            }
        };

        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                showData(dataSnapshot.child(getString(R.string.tableUsers)));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        // open image browser
        userPic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openFileChooser();
            }
        });

        // open calendar view
        btncalendar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), CalendarActivity.class));
//                utils.toastMessage(getApplicationContext(), getString(R.string.calendarView));
            }
        });

        // sign out
        btnSignOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //mAuth.signOut();
                getInstance().signOut();
                utils.toastMessage(getApplicationContext(), getString(R.string.signingOut));
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

        // Run this only once to get image url
        if (!bool){
            bool = true;
            uInfo.setImageurl(dataSnapshot.child(userID).getValue(Users.class).getImageurl());

            // Create new thread to fetch photo BASE64 string and then update UI
            new Thread(new Runnable() {
                public void run() {
                    try {
                        // fetch user pic base64 string in the background, then update the UI
                        userPic.post(new Runnable() {
                            public void run() {
                                byte[] imageBytes = Base64.decode(uInfo.getImageurl(), Base64.DEFAULT);
                                final Bitmap decodedImage = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
                                userPic.setImageBitmap(decodedImage);
                            }
                        });
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            }).start();
        }

        ArrayList<String> array  = new ArrayList<>();
        array.add(uInfo.getName());
        array.add(uInfo.getEmail());
        array.add(uInfo.getPosition());
        ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, array);
        mListView.setAdapter(adapter);
    }

    private void openFileChooser() {
        Intent intent = new Intent();
        intent.setType(getString(R.string.imageFolder));
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    // overrides openFileChooser method
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // if ok, get image URI, then get bitmap from that image and then convert it to base64 inorder to load it to database
        if(requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri imageUri = data.getData();
            try {
                // get image bitmap
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);

                // get base64 string
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
                byte[] imageBytes = byteArrayOutputStream.toByteArray();
                String imageString = Base64.encodeToString(imageBytes, Base64.DEFAULT);

                // send base64 string to database
                myRef.child(getString(R.string.tableUsers)).child(userID).child(getString(R.string.imageUrl)).setValue(imageString);
            } catch (IOException e) {
                e.printStackTrace();
            }
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
}