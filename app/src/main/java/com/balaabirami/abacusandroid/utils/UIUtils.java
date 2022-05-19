package com.balaabirami.abacusandroid.utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.fragment.app.FragmentActivity;

import com.balaabirami.abacusandroid.model.Order;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;

import java.io.File;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

public class UIUtils {

    public static boolean IS_ALERT_SHOWN = false;
    public static boolean IS_DATA_IMPORT = false;
    public static boolean IS_NO_PAYMENT = false;

    public static void showToast(Context context, String message) {
        Toast.makeText(context, message, Toast.LENGTH_LONG).show();
    }

    public static String getDate() {
        DateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
        return formatter.format(Calendar.getInstance().getTime());
    }

    public static Date getCurrentDate() {
        return Calendar.getInstance().getTime();
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

    public static int diffBetweenDates(String strStartDate, String strEndDate) {
        if (TextUtils.isEmpty(strStartDate) || TextUtils.isEmpty(strEndDate)) {
            return -1;
        }
        Date startDate = UIUtils.convertStringToDate(strStartDate);
        Date endDate = UIUtils.convertStringToDate(strEndDate);
        if (startDate == null || endDate == null) {
            return -1;
        }
        long msDiff = startDate.getTime() - endDate.getTime();
        return (int) TimeUnit.MILLISECONDS.toDays(msDiff);
    }

    public static Date convertStringToDate(String strDate) {
        @SuppressLint("SimpleDateFormat") SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy");
        Date date = null;
        try {
            date = format.parse(strDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }

    public static File createTempFileWithName(Context context, String tempFileName, boolean withDuplicate) {
        String folder = getTempFolder();
        return new File(folder, tempFileName);
    }

    public static String getTempFolder() {
        File tempDirectory = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS), "Alama_eOrder");
        if (!tempDirectory.exists()) {
            tempDirectory.mkdir();
        }
        return tempDirectory.getAbsolutePath();
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public static void mockDateForOrders(List<Order> data) {
        data.forEach(f -> {
            String[] intArray = {"18-05-2022", "19-05-2022", "20-05-2022", "21-05-2022", "22-05-2022", "23-05-2022",
                    "24-05-2022", "25-05-2022", "26-05-2022", "27-05-2022", "28-05-2022", "29-05-2022"};
            int idx = new Random().nextInt(intArray.length);
            String random = intArray[idx];
            f.setDate(random);
        });
    }

    public static void changeOrientation(FragmentActivity activity, int orientation) {
        activity.setRequestedOrientation(orientation);
    }

    public static boolean isDateNotValid(String date) {
        return date != null && (date.contains("D") || date.contains("M") || date.contains("Y"));
    }
}
