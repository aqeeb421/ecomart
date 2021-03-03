package com.kisanBharath.ecomart.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.TaskExecutors;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.kisanBharath.ecomart.R;
import com.kisanBharath.ecomart.models.User;

import java.util.concurrent.TimeUnit;

public class VerifyPhoneActivity extends AppCompatActivity {

    private String verificationId;
    private FirebaseAuth mAuth;
    private ProgressBar progressBar;
    private EditText editText;
    private User mUser;
    private String phonenumber;
    SharedPreferences sharedPreferences;
    public static final String MyPREFERENCES = "MyPrefs";
    private static final String TAG = "RegisterActivity";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify_phone);

        mAuth = FirebaseAuth.getInstance();
        mUser = new User();

        progressBar = findViewById(R.id.progressbar);
        editText = findViewById(R.id.editTextCode);


        phonenumber = getIntent().getStringExtra("phonenumber");
        sendVerificationCode(phonenumber);


        findViewById(R.id.buttonSignIn).setOnClickListener(v -> {

            String code = editText.getText().toString().trim();

            if (code.isEmpty() || code.length() < 6) {

                editText.setError("Enter code...");
                editText.requestFocus();
                return;
            }

            verifyCode(code);
        });
    }

    public void addNewUser() {

        //add data to the "users" node
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        Log.d(TAG, "addNewUser: Adding new User: \n user_id:" + user.getUid());
        mUser.setPhoneNumber(phonenumber);
        mUser.setUser_id(user.getUid());

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Users").child(user.getUid());
        ref.child("phonenumber").setValue(phonenumber);
    }

    private void verifyCode(String code) {
        try {
            PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, code);
            signInWithCredential(credential);
        } catch (Exception e) {
            Toast toast = Toast.makeText(this, "Verification Code is wrong", Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
        }
    }

    private void signInWithCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {

                        SharedPreferences preferences = getApplicationContext().getSharedPreferences("User Details", Context.MODE_PRIVATE);
                        String userName = preferences.getString("userName", "No userName defined");

                        Intent intent;
                        if (!userName.equals("No userName defined")) {
                            intent = new Intent(VerifyPhoneActivity.this, MainActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        } else {
                            intent = new Intent(VerifyPhoneActivity.this, UserInfoActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            addNewUser();
                        }
                        startActivity(intent);


                    } else {
                        Toast.makeText(VerifyPhoneActivity.this, task.getException().getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
    }


    private void sendVerificationCode(String number) {
        progressBar.setVisibility(View.VISIBLE);
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                number,
                60,
                TimeUnit.SECONDS,
                TaskExecutors.MAIN_THREAD,
                mCallBack
        );

    }

    private PhoneAuthProvider.OnVerificationStateChangedCallbacks
            mCallBack = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

        @Override
        public void onCodeSent(String s, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
            super.onCodeSent(s, forceResendingToken);
            verificationId = s;
        }

        @Override
        public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
            String code = phoneAuthCredential.getSmsCode();
            if (code != null) {
                editText.setText(code);
                verifyCode(code);
            }
        }

        @Override
        public void onVerificationFailed(FirebaseException e) {
            Toast.makeText(VerifyPhoneActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
        }
    };
}