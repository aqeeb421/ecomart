package com.kisanBharath.ecomart.activity;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.widget.EditText;

import com.google.firebase.auth.FirebaseAuth;
import com.hbb20.CountryCodePicker;
import com.kisanBharath.ecomart.R;
import com.kisanBharath.ecomart.models.User;

import java.util.Locale;

public class RegisterActivity extends AppCompatActivity {

    private static final String TAG = "LogInActivity";
    private EditText editText;
    private CountryCodePicker countryCodePicker;
    //defining view objects
    private EditText editTextEmail;
    private EditText editTextPassword;

    //defining firebaseauth object
    private FirebaseAuth firebaseAuth;
    private ProgressDialog progressDialog;
    private User mUser;
    private String phoneNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loadLocale();
        setContentView(R.layout.activity_register);

        editText = findViewById(R.id.editTextPhone);
        countryCodePicker = findViewById(R.id.countryCodeHolder);

        findViewById(R.id.buttonContinue).setOnClickListener(v -> {

            String number = editText.getText().toString().trim();

            if (number.isEmpty() || number.length() < 10) {
                editText.setError("Valid number is required");
                editText.requestFocus();
                return;
            }

            phoneNumber = countryCodePicker.getSelectedCountryCodeWithPlus() + number;


            SharedPreferences sharedPreferences = getSharedPreferences("LogIn Details", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("Number", phoneNumber);
            editor.apply();


            Intent intent = new Intent(RegisterActivity.this, VerifyPhoneActivity.class);
            intent.putExtra("phonenumber", phoneNumber);
            startActivity(intent);


        });


        findViewById(R.id.buttonLanguage).setOnClickListener(v -> {
            showChangeLanguage();
        });


    }

    private void showChangeLanguage() {
        final String[] listItems = {"English", "ಕನ್ನಡ"};

        AlertDialog.Builder builder = new AlertDialog.Builder(RegisterActivity.this);
        builder.setTitle("Choose Language");
        builder.setSingleChoiceItems(listItems, -1, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (which == 0) {
                    setLocale("en");
                    recreate();
                }
                if (which == 1) {
                    setLocale("kn");
                    recreate();
                }

                dialog.dismiss();
            }
        });
        AlertDialog mDialog = builder.create();
        mDialog.show();

    }

    private void setLocale(String lang) {
        Locale locale = new Locale(lang);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        getBaseContext().getResources().updateConfiguration(config, getResources().getDisplayMetrics());

        SharedPreferences.Editor editor = getSharedPreferences("Settings", MODE_PRIVATE).edit();
        editor.putString("MY_Lang", lang);
        editor.apply();
    }

    public void loadLocale() {
        SharedPreferences preferences = getSharedPreferences("Settings", MODE_PRIVATE);
        String language = preferences.getString("MY_Lang", "en");
        setLocale(language);
    }


    @Override
    protected void onStart() {
        super.onStart();
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            Intent intent = new Intent(this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        }
    }


}