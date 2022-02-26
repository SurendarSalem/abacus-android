package com.balaabirami.abacusandroid.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.balaabirami.abacusandroid.R;
import com.balaabirami.abacusandroid.databinding.ActivityHomeBinding;
import com.balaabirami.abacusandroid.local.preferences.PreferenceHelper;
import com.balaabirami.abacusandroid.viewmodel.LoginViewModel;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;


public class HomeActivity extends AppCompatActivity {

    private ActivityHomeBinding binding;
    private LoginViewModel loginViewModel;
    boolean isAdmin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityHomeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        isAdmin = PreferenceHelper.getInstance(this).isAdmin();
        BottomNavigationView navigationView = findViewById(R.id.nav_view);
        AppBarConfiguration appBarConfiguration;
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_home);
        if (isAdmin) {
            navController.setGraph(R.navigation.admin_navigation);
            navigationView.getMenu().findItem(R.id.franchiseListFragment).setVisible(true);
            appBarConfiguration = new AppBarConfiguration.Builder(R.id.franchiseListFragment, R.id.studentListFragment, R.id.stockListFragment).build();
        } else {
            navController.setGraph(R.navigation.franchise_navigation);
            navigationView.getMenu().findItem(R.id.franchiseListFragment).setVisible(false);
            appBarConfiguration = new AppBarConfiguration.Builder(R.id.studentListFragment, R.id.stockListFragment).build();
        }
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(binding.navView, navController);
        loginViewModel = new ViewModelProvider(this).get(LoginViewModel.class);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_home, menu);
        return true;
    }

    private void openAuthScreen() {
        finish();
        Intent intent = new Intent(this, AuthenticationActivity.class);
        startActivity(intent);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.menu_logout) {
            loginViewModel.logout();
            openAuthScreen();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void showProgress(boolean show) {
        if (show) {
            binding.pb.getRoot().setVisibility(View.VISIBLE);
        } else {
            binding.pb.getRoot().setVisibility(View.GONE);
        }
    }

}