package com.kisanBharath.ecomart.activity;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;
import com.kisanBharath.ecomart.R;

import java.util.ArrayList;
import java.util.Locale;

public class ContactActivity extends AppCompatActivity {

    private String TAG = "ContactActivity";
    Button change_language;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loadLocale();
        setContentView(R.layout.activity_contact);

        Intent intent1 = getIntent();
        String privacy = intent1.getStringExtra("privacy");

        try {
            if (privacy.equals("privacy")) {
                findViewById(R.id.logout).setVisibility(View.GONE);
            } else findViewById(R.id.logout).setVisibility(View.VISIBLE);

        } catch (Exception e) {
            Log.e(TAG, "onCreate: " + e.getMessage());
            findViewById(R.id.logout).setVisibility(View.VISIBLE);
        }

        findViewById(R.id.logout).setOnClickListener(v -> {
            FirebaseAuth.getInstance().signOut();
            Intent intent = new Intent(ContactActivity.this, RegisterActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        });

        findViewById(R.id.change_language).setOnClickListener(v -> {
            showChangeLanguage();
        });

        findViewById(R.id.customerCare_phone1).setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + "+917259310492"));
            startActivity(intent);
        });
        findViewById(R.id.customerCare_phone2).setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + "+917019735452"));
            startActivity(intent);
        });
        findViewById(R.id.customerCare_email).setOnClickListener(v -> {
            Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:" + "kisanbharathmart12@gmail.com"));
            emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Regarding Kisan Bharath app issue");
            startActivity(Intent.createChooser(emailIntent, "Hello Developer"));
        });

        findViewById(R.id.back_img).setOnClickListener(v -> {
            try {
                if (!privacy.equals("privacy")) {
                    startActivity(new Intent(this, MainActivity.class));
                } else
                    startActivity(new Intent(this, UserInfoActivity.class));

            } catch (Exception e) {
                Log.e(TAG, "onCreate: " + e.getMessage());
                startActivity(new Intent(this, MainActivity.class));
            }
        });

    }

    private void showChangeLanguage() {
        final String[] listItems = {"English", "ಕನ್ನಡ"};

        AlertDialog.Builder builder = new AlertDialog.Builder(ContactActivity.this);
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

    public void loadLocale(){
        SharedPreferences preferences = getSharedPreferences("Settings",MODE_PRIVATE);
        String language = preferences.getString("MY_Lang","en");
        setLocale(language);
    }
}