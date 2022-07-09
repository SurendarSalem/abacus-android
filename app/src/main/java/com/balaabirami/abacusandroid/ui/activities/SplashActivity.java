package com.balaabirami.abacusandroid.ui.activities;

import android.app.Activity;
import android.content.Intent;
import android.content.IntentSender;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import com.balaabirami.abacusandroid.R;
import com.balaabirami.abacusandroid.local.preferences.PreferenceHelper;
import com.balaabirami.abacusandroid.utils.UIUtils;
import com.google.android.gms.tasks.Task;
import com.google.android.play.core.appupdate.AppUpdateInfo;
import com.google.android.play.core.appupdate.AppUpdateManager;
import com.google.android.play.core.appupdate.AppUpdateManagerFactory;
import com.google.android.play.core.install.model.AppUpdateType;
import com.google.android.play.core.install.model.UpdateAvailability;

public class SplashActivity extends Activity {

    private static final int MY_REQUEST_CODE = 105;

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

    @Override
    protected void onResume() {
        super.onResume();
        //checkForAppUpdate();
    }

    private void checkForAppUpdate() {
        AppUpdateManager appUpdateManager = AppUpdateManagerFactory.create(this);

// Returns an intent object that you use to check for an update.
        Task<AppUpdateInfo> appUpdateInfoTask = appUpdateManager.getAppUpdateInfo();

// Checks that the platform will allow the specified type of update.
        appUpdateInfoTask.addOnSuccessListener(appUpdateInfo -> {
            if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE
                    && appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.IMMEDIATE)) {
                updateApp(appUpdateManager, appUpdateInfo);
            } else {
                showSplash();
            }
        });
    }

    private void updateApp(AppUpdateManager appUpdateManager, AppUpdateInfo appUpdateInfo) {
        try {
            appUpdateManager.startUpdateFlowForResult(
                    // Pass the intent that is returned by 'getAppUpdateInfo()'.
                    appUpdateInfo,
                    // Or 'AppUpdateType.FLEXIBLE' for flexible updates.
                    AppUpdateType.IMMEDIATE,
                    // The current activity making the update request.
                    this,
                    // Include a request code to later monitor this update request.
                    MY_REQUEST_CODE);
        } catch (IntentSender.SendIntentException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == MY_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                UIUtils.showToast(this, "App updated!");
            } else {
                UIUtils.showToast(this, "App update cancelled!");
            }
        }
    }
}