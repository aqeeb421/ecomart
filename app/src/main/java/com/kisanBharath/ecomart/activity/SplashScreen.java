package com.kisanBharath.ecomart.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.WindowManager;

import com.kisanBharath.ecomart.R;

public class SplashScreen extends AppCompatActivity {

    private static final long SPLASH_DISPLAY_LENGTH = 3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        //hide status bar https://developer.android.com/training/system-ui/status
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.splash_screen);

        new Handler().postDelayed(new Runnable() {
            public void run() {
                startActivity(new Intent(getApplicationContext(), RegisterActivity.class));
                finish();

            }
        }, SPLASH_DISPLAY_LENGTH * 1000);

    }
}