package com.example.testcloudauth;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.List;

public class secpage extends AppCompatActivity {
    private FirebaseAuth mAuth;

    private static final String TAG = "MainActivity";
    private Button btnSignOut;

    private EditText etName;
    private Spinner spinnerPosition;
    private Button btnInsertData;

    private ImageView profileImage;
    private StorageReference storageReference;



    DatabaseReference userDBRef;


    private TextView tvshow, tvuserid;
    private FirebaseUser user;

    private static final int PICK_IMAGE_REQUEST=8;
    private Uri imageuri;
    private String imageurl, imagename;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_secpage);

        etName=(EditText) findViewById(R.id.edName);
        spinnerPosition=(Spinner) findViewById(R.id.spinnerPosition);
        btnInsertData=(Button) findViewById(R.id.btnInsertData);

        profileImage=(ImageView) findViewById(R.id.imageUser);
        //loadPicture=(Button) findViewById(R.id.btn_load_pic);
        profileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i=new Intent();
                i.setType("image/*"); //"video/*"   //"file/*"
                i.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(i, PICK_IMAGE_REQUEST);

            }
        });



        mAuth = FirebaseAuth.getInstance();
        user=mAuth.getCurrentUser();//get user

        tvshow=(TextView)findViewById(R.id.tvshowemail);
        tvuserid=(TextView)findViewById(R.id.tvuserid);
        tvshow.setText(user.getEmail());
        tvuserid.setText(user.getUid());


        btnSignOut = (Button) findViewById(R.id.email_sign_out_btn);


        userDBRef= FirebaseDatabase.getInstance().getReference().child("Users");

        storageReference= FirebaseStorage.getInstance().getReference();//for pics


        btnInsertData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                insertUserData();
            }
        });

        //sign out
        btnSignOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //mAuth.signOut();
                FirebaseAuth.getInstance().signOut();
                Toast.makeText(secpage.this, "Signing Out...", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                finish();
            }
        });



    }
//new now pic
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode==PICK_IMAGE_REQUEST && resultCode==RESULT_OK && data!=null){
            imageuri = data.getData();
            imagename=getFileName(imageuri);
            profileImage.setImageURI(imageuri);


        }
        super.onActivityResult(requestCode, resultCode, data);
    }




    private void insertUserData() {
        final String name=etName.getText().toString();
        final String position=spinnerPosition.getSelectedItem().toString();
        final String email=tvshow.getText().toString();
        //pic
        StorageReference abc = storageReference.child("images/img1.jpg");

        abc.putFile(imageuri)
                .addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                        if (task.isSuccessful()) {
                            //Toast.makeText(secpage.this, "Image uploaded", Toast.LENGTH_SHORT).show();
                            //new now
                            final StorageReference ref = storageReference.child("images").child("users").child(imagename);
                            ref.putFile(imageuri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                                    if (task.isSuccessful()) {
                                        ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                            @Override
                                            public void onSuccess(Uri uri) {
                                                imageurl = uri.toString();

                                                if (!name.equals("") && !position.equals("")) {
                                                    Users users = new Users(name, position, email, imageurl);
                                                    //userDBRef.push().setValue(users);
                                                    userDBRef.child(mAuth.getCurrentUser().getUid()).setValue(users);
                                                    Toast.makeText(secpage.this, "Data inserted!", Toast.LENGTH_SHORT).show();
                                                    startActivity(new Intent(getApplicationContext(), ProfileActivity.class));
                                                    finish();
                                                } else {
                                                    Toast.makeText(secpage.this, "You didn't fill in all the fields.", Toast.LENGTH_SHORT).show();
                                                }


                                            }
                                        });
                                    } else {
                                        Toast.makeText(secpage.this, "Not uploaded", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception exception) {
                                            Toast.makeText(secpage.this, exception.toString(), Toast.LENGTH_SHORT).show();
                                        }
                                    });
                            //end pic


                        }
                    }
                });
    }


    public String getFileName(Uri uri){
        String result=null;
        if (uri.getScheme().equals("content")){
            Cursor cursor=getContentResolver().query(uri,null,null,null,null);
            try {
                if (cursor != null && cursor.moveToFirst()){
                    result=cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                }
            }finally {
                cursor.close();
            }
        }
        if (result == null){
            result=uri.getPath();
            int cut=result.lastIndexOf('/');
            if (cut != -1){
                result=result.substring(cut+1);
                }
            }
        return result;
        }




}