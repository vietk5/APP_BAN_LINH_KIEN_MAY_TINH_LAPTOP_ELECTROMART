package com.example.baitap01_nhom6_ui_login_register_forgetpass.util;

import android.content.Context;
import android.content.SharedPreferences;

public class OnboardingPref {
    private static final String PREF = "electromart_prefs";
    private static final String KEY_DONE = "onboarding_done";

    public static boolean isDone(Context ctx) {
        SharedPreferences sp = ctx.getSharedPreferences(PREF, Context.MODE_PRIVATE);
        return sp.getBoolean(KEY_DONE, false);
    }

    public static void setDone(Context ctx, boolean done) {
        SharedPreferences sp = ctx.getSharedPreferences(PREF, Context.MODE_PRIVATE);
        sp.edit().putBoolean(KEY_DONE, done).apply();
    }
}
