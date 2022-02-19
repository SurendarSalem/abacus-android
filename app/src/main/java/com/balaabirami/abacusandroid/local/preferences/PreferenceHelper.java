package com.balaabirami.abacusandroid.local.preferences;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class PreferenceHelper {

    private static PreferenceHelper preferencehelper;
    private static SharedPreferences sharedPreferences;

    private static final String IS_LOGINNED = "IS_LOGINNED";
    private static final String USER_ID = "USER_ID";

    public static PreferenceHelper getInstance(Context context) {
        sharedPreferences = getSharedPreferences(context);
        return preferencehelper == null ? new PreferenceHelper() : preferencehelper;
    }

    public static SharedPreferences getSharedPreferences(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context);
    }


    public void updateLogin(boolean isLogin) {
        sharedPreferences.edit().putBoolean(IS_LOGINNED, isLogin).apply();
    }

    public void setUserId(String userId) {
        sharedPreferences.edit().putString(USER_ID, userId).apply();
    }

    public String getUserId() {
        return sharedPreferences.getString(USER_ID, "");
    }

    public boolean isLogin() {
        return sharedPreferences.getBoolean(IS_LOGINNED, false);
    }
}
