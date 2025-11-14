package com.example.baitap01_nhom6_ui_login_register_forgetpass.util;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedPrefManager {
    private static final String PREF_NAME = "user_session";
    private static final String KEY_USER_ID = "user_id";
    private static final String KEY_EMAIL = "email";
    private static final String KEY_NAME = "name";

    private SharedPreferences pref;
    private SharedPreferences.Editor editor;

    public SharedPrefManager(Context context) {
        pref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = pref.edit();
    }

    public void saveUser(int id, String email, String name) {
        editor.putInt(KEY_USER_ID, id);
        editor.putString(KEY_EMAIL, email);
        editor.putString(KEY_NAME, name);
        editor.apply();
    }

    public boolean isLoggedIn() {
        return pref.contains(KEY_USER_ID);
    }

    public int getUserId() { return pref.getInt(KEY_USER_ID, -1); }
    public String getEmail() { return pref.getString(KEY_EMAIL, null); }
    public String getName() { return pref.getString(KEY_NAME, null); }

    public void logout() {
        editor.clear();
        editor.apply();
    }
}
