package com.balaabirami.abacusandroid.ui.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.balaabirami.abacusandroid.R;
import com.balaabirami.abacusandroid.databinding.ActivityHomeBinding;
import com.balaabirami.abacusandroid.local.preferences.PreferenceHelper;
import com.balaabirami.abacusandroid.model.Resource;
import com.balaabirami.abacusandroid.model.Status;
import com.balaabirami.abacusandroid.model.Stock;
import com.balaabirami.abacusandroid.model.User;
import com.balaabirami.abacusandroid.ui.TrackingActivity;
import com.balaabirami.abacusandroid.ui.fragments.StockListViewModel;
import com.balaabirami.abacusandroid.utils.UIUtils;
import com.balaabirami.abacusandroid.viewmodel.LoginViewModel;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.FirebaseApp;
import com.google.firebase.analytics.FirebaseAnalytics;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import java.util.List;


public class HomeActivity extends AppCompatActivity {

    private ActivityHomeBinding binding;
    private LoginViewModel loginViewModel;
    User currentUser;
    private AlertDialog.Builder logoutDialog;
    StockListViewModel stockListViewModel;
    private BottomNavigationView navigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityHomeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        navigationView = findViewById(R.id.nav_view);
        AppBarConfiguration appBarConfiguration;
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_home);
        currentUser = PreferenceHelper.getInstance(this).getCurrentUser();
        loginViewModel = new ViewModelProvider(this).get(LoginViewModel.class);
        if (currentUser.isIsAdmin()) {
            navController.setGraph(R.navigation.admin_navigation);
            navigationView.getMenu().findItem(R.id.franchiseListFragment).setVisible(true);
            appBarConfiguration = new AppBarConfiguration.Builder(R.id.franchiseListFragment, R.id.studentListFragment, R.id.stockListFragment, R.id.cartFragment).build();
        } else {
            navController.setGraph(R.navigation.franchise_navigation);
            navigationView.getMenu().findItem(R.id.franchiseListFragment).setVisible(false);
            navigationView.getMenu().findItem(R.id.stockListFragment).setVisible(false);
            appBarConfiguration = new AppBarConfiguration.Builder(R.id.studentListFragment, R.id.stockListFragment, R.id.cartFragment).build();
        }
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(binding.navView, navController);
        if (currentUser.isIsAdmin()) {
            stockListViewModel = new ViewModelProvider(this).get(StockListViewModel.class);
            stockListViewModel.getAllStocks();
            stockListViewModel.getStockListLiveData().observe(this, listResource -> {
                if (listResource.data != null && listResource.status == Status.SUCCESS) {
                    List<Stock> stocks = listResource.data;
                    StringBuilder items = new StringBuilder();
                    for (Stock stock : stocks) {
                        if (stock.getQuantity() <= 5) {
                            items.append(stock.getName()).append(", ");
                        }
                    }
                    if (items.length() > 0 && !UIUtils.IS_ALERT_SHOWN) {
                        Snackbar snackBar = Snackbar.make(this.findViewById(android.R.id.content),
                                "The following items are low in stock. " + items + ". Please refill it",
                                BaseTransientBottomBar.LENGTH_LONG);
                        snackBar.setDuration(Snackbar.LENGTH_INDEFINITE);
                        snackBar.setBackgroundTint(getResources().getColor(R.color.color_primary));
                        snackBar.setTextColor(Color.WHITE);
                        snackBar.setActionTextColor(Color.WHITE);
                        snackBar.setAction("View", view -> {
                            navigationView.setSelectedItemId(R.id.stockListFragment);
                            snackBar.dismiss();
                        });
                        snackBar.show();
                        UIUtils.IS_ALERT_SHOWN = true;
                        stockListViewModel.getStockListLiveData().removeObservers(this);
                    }
                }
            });
        }
        Bundle bundle = new Bundle();
        bundle.putString("user_id", currentUser.getId());
        FirebaseAnalytics.getInstance(this)
                .logEvent("home_opened", bundle);
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
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

    private void openTracks() {
        Intent intent = new Intent(this, TrackingActivity.class);
        startActivity(intent);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.menu_logout) {
            showLogoutConfirmDialog();
            return true;
        } else if (item.getItemId() == R.id.menu_track) {
            openTracks();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showLogoutConfirmDialog() {
        if (logoutDialog == null) {
            logoutDialog = new AlertDialog.Builder(this);
            logoutDialog.setMessage("Are you sure want to logout?");
            logoutDialog.setPositiveButton("Logout", (dialogInterface, i) -> {
                loginViewModel.logout();
                openAuthScreen();
            });
            logoutDialog.setNegativeButton("Cancel", (dialogInterface, i) -> {
                dialogInterface.dismiss();
            });
        }
        logoutDialog.show();
    }

    public void showProgress(boolean show) {
        if (show) {
            binding.pb.getRoot().setVisibility(View.VISIBLE);
        } else {
            binding.pb.getRoot().setVisibility(View.GONE);
        }
    }

    public void showProgress(boolean show, String message) {
        if (show) {
            binding.pb.getRoot().setVisibility(View.VISIBLE);
            if (message != null && message.length() > 0) {
                ((AppCompatTextView) binding.pb.getRoot().findViewById(R.id.message)).setVisibility(View.VISIBLE);
                ((AppCompatTextView) binding.pb.getRoot().findViewById(R.id.message)).setText(message);
            } else {
                ((AppCompatTextView) binding.pb.getRoot().findViewById(R.id.message)).setVisibility(View.GONE);
            }
        } else {
            ((AppCompatTextView) binding.pb.getRoot().findViewById(R.id.message)).setVisibility(View.GONE);
            binding.pb.getRoot().setVisibility(View.GONE);
        }
    }

    @Override
    public void onBackPressed() {
        if (!UIUtils.API_IN_PROGRESS) {
            super.onBackPressed();
        }
    }

    public boolean isProgressShown() {
        return binding.pb.getRoot().getVisibility() == View.VISIBLE;
    }
}