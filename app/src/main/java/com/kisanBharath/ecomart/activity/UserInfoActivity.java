package com.kisanBharath.ecomart.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatCheckBox;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.kisanBharath.ecomart.R;
import com.kisanBharath.ecomart.models.User;

import java.io.IOException;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserInfoActivity extends AppCompatActivity {

    private static final String TAG = "UserInfoActivity";
    private static final int PICK_PHOTO_FOR_AVATAR = 1234;
    CircleImageView profile_image;
    EditText editTextName, editTextEmail;
    String phonenumber;
    TextView tc_txt;

    //Firebase
    FirebaseStorage storage;
    FirebaseUser mUser;
    DatabaseReference ref;
    StorageReference storageReference;

    public static final String MyPREFERENCES = "MyPrefs";
    User userInfo;
    String userName, email, password, confirmPassword, userId;
    private Uri imageUri;
    private StorageTask uploadTask;
    private Uri filePath;
    AppCompatCheckBox tc_chbox;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_info);


        init();
        userInfo = new User();
        mUser = FirebaseAuth.getInstance().getCurrentUser();
        userId = mUser.getUid();
        Log.d(TAG, "onCreate: " + userId);
        ref = FirebaseDatabase.getInstance().getReference().child("Users").child(mUser.getUid());

        //Retrieve phoneNumber wherever necessary
        SharedPreferences preferences = getApplicationContext().getSharedPreferences("LogIn Details", Context.MODE_PRIVATE);
        phonenumber = preferences.getString("Number", "No number defined");

        Log.d(TAG, "onCreate: " + phonenumber);

    }

    private void init() {
        profile_image = findViewById(R.id.profile_image);
        editTextName = findViewById(R.id.editTextName);
        editTextEmail = findViewById(R.id.editTextEmail);
        tc_chbox = findViewById(R.id.tc_chbox);
        tc_txt = findViewById(R.id.tc_txt);


        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();


        findViewById(R.id.profile_image).setOnClickListener(v -> {

            chooseImage();

        });

        findViewById(R.id.tc_txt).setOnClickListener(v -> {
            Intent intent = new Intent(this, ContactActivity.class);
            intent.putExtra("privacy", "privacy");
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();

        });

        findViewById(R.id.buttonContinue).setOnClickListener(v -> {
            userName = editTextName.getText().toString();
            email = editTextEmail.getText().toString();


            if (!userName.isEmpty()) {


                SharedPreferences sharedPreferences = getSharedPreferences("User Details", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("userName", userName);
                editor.putString("password", password);
                editor.putString("phonenumber", phonenumber);
                editor.apply();
                if (tc_chbox.isChecked()) {
                    uploadImage();
                } else
                    Toast.makeText(this, R.string.tandc1, Toast.LENGTH_SHORT).show();
            } else {
                editTextName.setError("Enter User Name");
            }


        });
    }

    private void chooseImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_PHOTO_FOR_AVATAR);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_PHOTO_FOR_AVATAR && resultCode == RESULT_OK
                && data != null && data.getData() != null) {
            filePath = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                profile_image.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void uploadImage() {

        if (filePath != null) {
            final ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setTitle("Uploading...");
            progressDialog.show();

            StorageReference ref = storageReference.child("ProfileImages/").child(userId).child("profilePic");
            ref.putFile(filePath)
                    .addOnSuccessListener(taskSnapshot -> {
                        progressDialog.dismiss();
                        Toast.makeText(UserInfoActivity.this, "Uploaded", Toast.LENGTH_SHORT).show();

                        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
                        //insert the download url into the firebase database
                        Task<Uri> firebaseUri = taskSnapshot.getMetadata().getReference().getDownloadUrl();
                        firebaseUri.addOnSuccessListener(uri -> {
                            String imageUrl = uri.toString();
                            User user = new User();
                            user.setProfilePic(imageUrl);
                            user.setUserName(userName);
                            user.setEmail(email);
                            user.setPassword(password);
                            user.setPhoneNumber(phonenumber);
                            user.setUser_id(userId);
                            user.setSearch(userName.toLowerCase());
                            user.setStatus("offline");

                            reference.child("Users")
                                    .child(userId)
                                    .setValue(user);

                            resetFields();

                        });
                    }).addOnFailureListener(e -> {
                progressDialog.dismiss();
                Toast.makeText(UserInfoActivity.this, "Failed " + e.getMessage(), Toast.LENGTH_SHORT).show();
            })
                    .addOnProgressListener(taskSnapshot -> {
                        double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot
                                .getTotalByteCount());
                        progressDialog.setMessage("Uploaded " + (int) progress + "%");
                    });
        } else {
            DatabaseReference reference = FirebaseDatabase.getInstance().getReference();

            User user = new User();
            // user.setProfilePic(imageUrl);
            user.setUserName(userName);
            user.setEmail(email);
            user.setPassword(password);
            user.setPhoneNumber(phonenumber);
            user.setUser_id(userId);
            user.setSearch(userName.toLowerCase());
            user.setStatus("offline");

            reference.child("Users")
                    .child(userId)
                    .setValue(user);

            resetFields();
        }
    }

    private void resetFields() {
        startActivity(new Intent(getApplicationContext(), MainActivity.class));
        finish();
    }
}