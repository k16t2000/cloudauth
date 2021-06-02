package com.example.testcloudauth;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    boolean isEmailValid;
    private LinearLayout linearLayout;
    private EditText mEmail, mPassword;
    private Button btnSignIn, btnForgot;
    FirebaseUser user;

    DatabaseReference userDBRef;

    AlertDialog.Builder builder;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mEmail = (EditText) findViewById(R.id.email);
        mPassword = (EditText) findViewById(R.id.password);
        btnSignIn = (Button) findViewById(R.id.emial_sign_in_btn);
        btnForgot = (Button) findViewById(R.id.forgot_btn);
        linearLayout = (LinearLayout) findViewById(R.id.main);

        builder = new AlertDialog.Builder(this);//for alert

        mAuth = FirebaseAuth.getInstance();
        userDBRef = FirebaseDatabase.getInstance().getReference().child("Users");
        user = mAuth.getCurrentUser(); //get user

        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String email = mEmail.getText().toString();
                final String pass = mPassword.getText().toString();

                if (email.isEmpty() || pass.isEmpty()) {
                    toastMessage(getResources().getString(R.string.login_or_password_empty));
                } else {
                    mAuth.signInWithEmailAndPassword(email,pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()){
                            // Check if email exists in database
                            DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
                            Query query = rootRef.child("Users").orderByChild("email").equalTo(email);
                            query.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    if (!snapshot.exists()){
                                        toastMessage(getResources().getString(R.string.login_successful));
                                        startActivity(new Intent(getApplicationContext(), secpage.class));
                                    } else {
                                        startActivity(new Intent(getApplicationContext(), ProfileActivity.class));
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });
                        } else {
                            toastMessage(getResources().getString(R.string.incorrect_username_or_password));
                            // New user creating alert window
                            builder.setMessage(getResources().getString(R.string.registration_offer))
                                    .setCancelable(false)
                                    .setPositiveButton(getResources().getString(R.string.yes), new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            startActivity(new Intent(getApplicationContext(), regpage.class));
//                                        Toast.makeText(getApplicationContext(),getResources().getString(R.string.chose_yes), Toast.LENGTH_SHORT).show();
                                        }
                                    })
                                    .setNegativeButton(getResources().getString(R.string.no), new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            dialog.cancel();
//                                        Toast.makeText(getApplicationContext(), getResources().getString(R.string.chose_no), Toast.LENGTH_SHORT).show();
                                        }
                                    });

                                AlertDialog alert = builder.create();
                                alert.setTitle(getResources().getString(R.string.attention));
                                alert.show();
                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
//                            Toast.makeText(MainActivity.this, getResources().getString(R.string.registration_error), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });

        btnForgot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = mEmail.getText().toString();
                if (TextUtils.isEmpty(email)) {
                    mEmail.setError(getResources().getString(R.string.filled_field));
                } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    mEmail.setError(getResources().getString(R.string.valid_email));
                    isEmailValid = false;
                } else {
                    isEmailValid = true;
                    mAuth.useAppLanguage();
                    mAuth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Snackbar.make(linearLayout, getResources().getString(R.string.email_sent), Snackbar.LENGTH_SHORT).show();
                            } else {
                                Snackbar.make(linearLayout, getResources().getString(R.string.email_sending_error), Snackbar.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });
    }

    private void toastMessage(String message){
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}