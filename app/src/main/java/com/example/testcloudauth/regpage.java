package com.example.testcloudauth;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.testcloudauth.Utils.Utils;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;

public class regpage extends AppCompatActivity {
    private FirebaseAuth mAuth;

    private Utils utils;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_regpage);

        mAuth = FirebaseAuth.getInstance();
        Button closebtn = findViewById(R.id.close_btn);
        closebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                System.exit(0);
            }
        });

        Button registerButton = findViewById(R.id.register_btn);
        final EditText username = findViewById(R.id.UserName);
        final EditText password = findViewById(R.id.password);

        utils = new Utils();

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (username.getText().toString().isEmpty() || password.getText().toString().isEmpty()) {
                    utils.toastMessage(getApplicationContext(), getString(R.string.loginOrPasswordEmpty));
                } else {
                    createNew(username.getText().toString(), password.getText().toString());
                }
            }
        });
    }

    private void createNew(String email, String password){
        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    // Sign in success, update UI with the signed-in user's information
                    FirebaseUser user = mAuth.getCurrentUser();
                    utils.toastMessage(getApplicationContext(), getString(R.string.authenticationSuccess)+ user.getEmail());
                    finish();
                } else {
                    // If sign in fails, display a message to the user.
                    try {
                        throw task.getException();
                    } catch(FirebaseAuthWeakPasswordException e) {
                        utils.toastMessage(getApplicationContext(), getString(R.string.passwordNumberSymbols));
                    } catch(FirebaseAuthUserCollisionException e) {
                        utils.toastMessage(getApplicationContext(), getString(R.string.userAlreadyExists));
                    } catch(Exception e) {
                        utils.toastMessage(getApplicationContext(), getString(R.string.authenticationFailed));
                    }
                }
            }
        });
    }
}