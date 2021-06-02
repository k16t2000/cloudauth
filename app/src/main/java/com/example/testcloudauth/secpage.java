package com.example.testcloudauth;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.util.Base64;
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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Objects;

public class secpage extends AppCompatActivity {
    private FirebaseUser user;
    private FirebaseAuth mAuth;

    private static final String TAG = "SecPageActivity";
    private Button btnSignOut, btnInsertData;
    private TextView tvshow, tvuserid;
    private EditText etName;
    private Spinner spinnerPosition;
    private ImageView profileImage;
    private StorageReference storageReference;

    DatabaseReference userDBRef;

    private static final int PICK_IMAGE_REQUEST=8;
    private String imageString;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_secpage);

        etName = (EditText) findViewById(R.id.edName);
        spinnerPosition = (Spinner) findViewById(R.id.spinnerPosition);
        btnInsertData = (Button) findViewById(R.id.btnInsertData);
        profileImage = (ImageView) findViewById(R.id.imageUser);
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
        user = mAuth.getCurrentUser();//get user

        tvshow = (TextView)findViewById(R.id.tvshowemail);
        tvuserid = (TextView)findViewById(R.id.tvuserid);
        tvshow.setText(user.getEmail());
        tvuserid.setText(user.getUid());

        btnSignOut = (Button) findViewById(R.id.email_sign_out_btn);

        userDBRef = FirebaseDatabase.getInstance().getReference().child("Users");

        storageReference = FirebaseStorage.getInstance().getReference();//for pics

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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null){
            // get image uri from local storage
            Uri imageUri = data.getData();
            try {
                // get image bitmap
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
                profileImage.setImageBitmap(bitmap);

                // get base64 string
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
                byte[] imageBytes = byteArrayOutputStream.toByteArray();
                imageString = Base64.encodeToString(imageBytes, Base64.DEFAULT);

                //Log.d(TAG, "BASE64: " + imageString);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void insertUserData() {
        final String name = etName.getText().toString();
        final String position = spinnerPosition.getSelectedItem().toString();
        final String email = tvshow.getText().toString();

        if (name.isEmpty() || imageString == null) {
            Toast.makeText(secpage.this, "Image or name is empty", Toast.LENGTH_SHORT).show();
        } else {
            if (!position.equals("")) {
                Users users = new Users(name, position, email, imageString);
                userDBRef.child(Objects.requireNonNull(mAuth.getCurrentUser()).getUid()).setValue(users);
                Toast.makeText(secpage.this, "Data inserted!", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(getApplicationContext(), ProfileActivity.class));
                finish();
            } else {
                Toast.makeText(secpage.this, "You didn't fill in all the fields.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public String getFileName(Uri uri){
        String result = null;
        if (uri.getScheme().equals("content")){
            Cursor cursor=getContentResolver().query(uri,null,null,null,null);
            try {
                if (cursor != null && cursor.moveToFirst()){
                    result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                }
            } finally {
                cursor.close();
            }
        }

        if (result == null){
            result = uri.getPath();
            int cut = result.lastIndexOf('/');
            if (cut != -1){
                result = result.substring(cut+1);
            }
        }
        return result;
    }
}