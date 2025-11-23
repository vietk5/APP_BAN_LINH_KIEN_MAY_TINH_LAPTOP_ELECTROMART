package com.example.baitap01_nhom6_ui_login_register_forgetpass.util;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedPrefManager {
    private static final String PREF_NAME = "user_session";
    private static final String KEY_USER_ID = "user_id";
    private static final String KEY_EMAIL = "email";
    private static final String KEY_NAME = "name";
    private static final String KEY_IS_ADMIN = "is_admin";

    private SharedPreferences pref;
    private SharedPreferences.Editor editor;

    public SharedPrefManager(Context context) {
        pref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = pref.edit();
    }

    // Lưu thông tin user
    public void saveUser(int id, String email, String name, boolean isAdmin) {
        editor.putInt(KEY_USER_ID, id);
        editor.putString(KEY_EMAIL, email);
        editor.putString(KEY_NAME, name);
        editor.putBoolean(KEY_IS_ADMIN, isAdmin);
        editor.apply();
    }

    public boolean isAdmin() {
        return pref.getBoolean(KEY_IS_ADMIN, false);
    }

    // Kiểm tra user đã login chưa
    public boolean isLoggedIn() {
        return pref.contains(KEY_USER_ID);
    }

    public int getUserId() { return pref.getInt(KEY_USER_ID, -1); }
    public String getEmail() { return pref.getString(KEY_EMAIL, null); }
    public String getName() { return pref.getString(KEY_NAME, null); }

    // Xóa thông tin khi logout
    public void logout() {
        editor.clear();
        editor.apply();
    }
}
