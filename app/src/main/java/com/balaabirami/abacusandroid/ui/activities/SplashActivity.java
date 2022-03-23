package com.balaabirami.abacusandroid.ui.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import com.balaabirami.abacusandroid.R;
import com.balaabirami.abacusandroid.local.preferences.PreferenceHelper;

public class SplashActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        showSplash();
    }

    private void showSplash() {
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            finish();
            Intent nextScreen;
            if (PreferenceHelper.getInstance(this).isLogin()) {
                nextScreen = new Intent(this, HomeActivity .class);
            } else {
                nextScreen = new Intent(this, AuthenticationActivity.class);
            }
            startActivity(nextScreen);
        }, 3000);
    }
}