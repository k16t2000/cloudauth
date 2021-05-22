package com.example.testcloudauth;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
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

    private static final String TAG = "MainActivity";
    private FirebaseAuth mAuth;
    //private FirebaseAuth.AuthStateListener mAuthListener;
    boolean isEmailValid;
    private LinearLayout linearLayout;
    private EditText mEmail, mPassword;
    private Button btnSignIn,btnForgot;
    private FirebaseUser user;

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

        builder= new AlertDialog.Builder(this);//for alert

        mAuth = FirebaseAuth.getInstance();
        userDBRef= FirebaseDatabase.getInstance().getReference().child("Users");
        user=mAuth.getCurrentUser();//get user
        /*if (user != null){
            startActivity(new Intent(getApplicationContext(), secpage.class));
        }*/
        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String email = mEmail.getText().toString();
                String pass = mPassword.getText().toString();

                if (email.isEmpty() || pass.isEmpty()) {
                    Toast.makeText(MainActivity.this, "Login or password is empty", Toast.LENGTH_SHORT).show();
                } else {
                    mAuth.signInWithEmailAndPassword(email,pass)
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()){
                                        //check if email exists in database
                                        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
                                        Query query=rootRef.child("Users").orderByChild("email").equalTo(email);
                                        query.addValueEventListener(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                if (!snapshot.exists()){
                                                    //Toast.makeText(MainActivity.this, "no in db", Toast.LENGTH_SHORT).show();
                                                    Intent i=new Intent(getApplicationContext(),secpage.class);
                                                    startActivity(i);
                                                    Toast.makeText(MainActivity.this, "Login successful, but please fill all fields", Toast.LENGTH_SHORT).show();
                                                }
                                                else{
                                                    Intent i=new Intent(getApplicationContext(),ProfileActivity.class);
                                                    startActivity(i);
                                                    Toast.makeText(MainActivity.this, "Login successful", Toast.LENGTH_SHORT).show();

                                                }
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError error) {}
                                        });


                                        ///
                                        /*Intent i=new Intent(getApplicationContext(),secpage.class);
                                        startActivity(i);
                                        Toast.makeText(MainActivity.this, "Login successful", Toast.LENGTH_SHORT).show();*/
                                    }
                                    else {
                                        Toast.makeText(MainActivity.this, "Wrong login", Toast.LENGTH_SHORT).show();
                                        //alert
                                        builder.setMessage("Don't have account yet. Would you like register ?")
                                                .setCancelable(false)
                                                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                                    public void onClick(DialogInterface dialog, int id) {
                                                        //finish();
                                                        startActivity(new Intent(MainActivity.this, regpage.class));
                                                        Toast.makeText(getApplicationContext(),"you choose yes action for alertbox",
                                                                Toast.LENGTH_SHORT).show();
                                                    }
                                                })
                                                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                                    public void onClick(DialogInterface dialog, int id) {
                                                        //  Action for 'NO' Button
                                                        dialog.cancel();
                                                        Toast.makeText(getApplicationContext(),"you choose no action for alertbox",
                                                                Toast.LENGTH_SHORT).show();
                                                    }
                                                });
                                        //Creating dialog box
                                        AlertDialog alert = builder.create();
                                        //Setting the title manually
                                        alert.setTitle("Notice");
                                        alert.show();



                                        //end alert
                                    }
                                }
                            })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(MainActivity.this, e.toString(), Toast.LENGTH_SHORT).show();

                        }
                    });
                }
            }
        });

        btnForgot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = mEmail.getText().toString();
                if(TextUtils.isEmpty(email)) {
                    mEmail.setError("Field needs to be filled!");
                }else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    mEmail.setError("You need to enter a valid email.");
                    isEmailValid = false;
                }else{
                    isEmailValid = true;
                    mAuth.useAppLanguage();
                    mAuth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Log.w(TAG, "Email successfully sent.");
                                Snackbar.make(linearLayout, "Email was sent to the address you provided.", Snackbar.LENGTH_SHORT).show();
                            }else{
                                Log.w(TAG, "Error occurred when sending email", task.getException());
                                Snackbar.make(linearLayout, "Error sending email", Snackbar.LENGTH_SHORT).show();
                            }
                        }
                    });
                }

            }

        });
        //end





    }




}