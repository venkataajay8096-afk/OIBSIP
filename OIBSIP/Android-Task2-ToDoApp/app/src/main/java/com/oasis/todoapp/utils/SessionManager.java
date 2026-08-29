package com.oasis.todoapp.utils;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import com.oasis.todoapp.activity.LoginActivity;

import java.util.HashMap;

/**
 * SessionManager handles saving and clearing session details in SharedPreferences.
 */
public class SessionManager {

    private final SharedPreferences pref;
    private final SharedPreferences.Editor editor;
    private final Context context;

    private static final String PREF_NAME = "UserSessionPref";
    private static final String IS_LOGIN = "IsLoggedIn";
    
    public static final String KEY_USER_ID = "user_id";
    public static final String KEY_NAME = "name";
    public static final String KEY_EMAIL = "email";

    public SessionManager(Context context) {
        this.context = context;
        pref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = pref.edit();
    }

    /**
     * Create login session
     */
    public void createLoginSession(int userId, String name, String email) {
        editor.putBoolean(IS_LOGIN, true);
        editor.putInt(KEY_USER_ID, userId);
        editor.putString(KEY_NAME, name);
        editor.putString(KEY_EMAIL, email);
        editor.apply();
    }

    /**
     * Check user login status. If false, redirect user to LoginActivity.
     */
    public boolean checkLogin() {
        if (!this.isLoggedIn()) {
            Intent i = new Intent(context, LoginActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            context.startActivity(i);
            return false;
        }
        return true;
    }

    /**
     * Get stored session data
     */
    public HashMap<String, String> getUserDetails() {
        HashMap<String, String> user = new HashMap<>();
        user.put(KEY_NAME, pref.getString(KEY_NAME, null));
        user.put(KEY_EMAIL, pref.getString(KEY_EMAIL, null));
        return user;
    }

    /**
     * Get active logged in User ID
     */
    public int getUserId() {
        return pref.getInt(KEY_USER_ID, -1);
    }

    /**
     * Get active logged in User Name
     */
    public String getUserName() {
        return pref.getString(KEY_NAME, "User");
    }

    /**
     * Get active logged in User Email
     */
    public String getUserEmail() {
        return pref.getString(KEY_EMAIL, "");
    }

    /**
     * Clear session details and redirect user to Login screen.
     */
    public void logoutUser() {
        editor.clear();
        editor.apply();

        Intent i = new Intent(context, LoginActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        context.startActivity(i);
    }

    /**
     * Quick check for login state
     */
    public boolean isLoggedIn() {
        return pref.getBoolean(IS_LOGIN, false);
    }
}
