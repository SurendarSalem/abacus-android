package com.balaabirami.abacusandroid.local.preferences;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.balaabirami.abacusandroid.model.User;

public class PreferenceHelper {

    private static PreferenceHelper preferencehelper;
    private static SharedPreferences sharedPreferences;

    private static final String IS_LOGINNED = "IS_LOGINNED";
    private static final String USER_ID = "USER_ID";
    private static final String IS_ADMIN = "IS_ADMIN";
    private static final String USER_EMAIL = "USER_EMAIL";

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

    public void setIsAdmin(boolean isAdmin) {
        sharedPreferences.edit().putBoolean(IS_ADMIN, isAdmin).apply();
    }

    public boolean isAdmin() {
        return sharedPreferences.getBoolean(IS_ADMIN, false);
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

    public void setUserEmail(String email) {
        sharedPreferences.edit().putString(USER_EMAIL, email).apply();
    }

    public String getUserEmail() {
        return sharedPreferences.getString(USER_EMAIL, "");
    }

    public void setUser(User user) {
        updateLogin(true);
        setUserId(user.getId());
        setUserEmail(user.getEmail());
    }

    public void clearUser(User user) {
        updateLogin(false);
        setUserId(null);
        setUserEmail(null);
    }
}
