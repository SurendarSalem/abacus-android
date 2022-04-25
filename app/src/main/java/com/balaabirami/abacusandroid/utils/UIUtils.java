package com.balaabirami.abacusandroid.utils;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class UIUtils {

    public static boolean IS_DATA_IMPORT = false;
    public static void showToast(Context context, String message) {
        Toast.makeText(context, message, Toast.LENGTH_LONG).show();
    }

    public static String getDate() {
        DateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
        return formatter.format(Calendar.getInstance().getTime());
    }

    public static String getDateWithTime() {
        DateFormat formatter = new SimpleDateFormat("dd-MM-yyyy hh:mm");
        return formatter.format(Calendar.getInstance().getTime());
    }

    public static boolean isNetworkConnected(Context context) {
        final ConnectivityManager connMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connMgr.getActiveNetworkInfo();

        if (activeNetworkInfo != null) { // connected to the internet
            Toast.makeText(context, activeNetworkInfo.getTypeName(), Toast.LENGTH_SHORT).show();

            if (activeNetworkInfo.getType() == ConnectivityManager.TYPE_WIFI) {
                // connected to wifi
                return true;
            } else return activeNetworkInfo.getType() == ConnectivityManager.TYPE_MOBILE;
        }
        return false;
    }

    public static void showSnack(Activity activity, String message) {
        Snackbar snackBar = Snackbar.make(activity.findViewById(android.R.id.content),
                message, BaseTransientBottomBar.LENGTH_LONG);
        snackBar.show();
    }

    public static void hideKeyboardFrom(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = activity.getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public static <T> List<T> cloneList(List<T> list) {
        List<T> clonedList = new ArrayList<T>();
        for (T t : list) {
            clonedList.add(t);
        }
        return clonedList;
    }

    public static String capitalizeWord(String str) {
        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }
}
