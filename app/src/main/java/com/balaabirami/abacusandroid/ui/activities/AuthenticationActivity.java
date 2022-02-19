package com.balaabirami.abacusandroid.ui.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.balaabirami.abacusandroid.R;
import com.balaabirami.abacusandroid.local.preferences.PreferenceHelper;

import java.util.Objects;

public class AuthenticationActivity extends AppCompatActivity {
    NavController navController;
    RelativeLayout progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_activity);
        progressBar = findViewById(R.id.pb);
        Objects.requireNonNull(getSupportActionBar()).setElevation(0);
        navController = Navigation.findNavController(this, R.id.my_nav_host_fragment);
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(navController.getGraph()).build();
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        if (PreferenceHelper.getInstance(getApplicationContext()).isLogin()) {
            Bundle bundle = new Bundle();
            bundle.putString("userId", PreferenceHelper.getInstance(getApplicationContext()).getUserId());
            navController.navigate(R.id.studentListFragment, bundle);
        }
    }

    public void showProgress(boolean show) {
        if (show) {
            progressBar.setVisibility(View.VISIBLE);
        } else {
            progressBar.setVisibility(View.GONE);
        }
    }
}