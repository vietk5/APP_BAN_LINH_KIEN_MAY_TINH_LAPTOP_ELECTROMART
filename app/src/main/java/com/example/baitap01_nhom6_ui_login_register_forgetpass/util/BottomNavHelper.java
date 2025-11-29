package com.example.baitap01_nhom6_ui_login_register_forgetpass.util;


import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.baitap01_nhom6_ui_login_register_forgetpass.R;
import com.example.baitap01_nhom6_ui_login_register_forgetpass.activity.CartActivity;
import com.example.baitap01_nhom6_ui_login_register_forgetpass.activity.CategoryActivity;
import com.example.baitap01_nhom6_ui_login_register_forgetpass.activity.ConsultActivity;
import com.example.baitap01_nhom6_ui_login_register_forgetpass.activity.HomeActivity;
import com.example.baitap01_nhom6_ui_login_register_forgetpass.activity.ProfileActivity;

public class BottomNavHelper {
    private static final int COLOR_SELECTED   = 0xFF3B82F6; // xanh
    private static final int COLOR_SELECTED1   = 0xFFF44336; // đỏ
    private static final int COLOR_UNSELECTED = 0xFFA1A1A1; // xám
    private static final int COLOR_UNSELECTED1 = 0xFF0A1EDC; // xanh
    public static void setup(final Activity activity, String currentScreen) {
        LinearLayout btnHome     = activity.findViewById(R.id.btnHome);
        LinearLayout btnCategory = activity.findViewById(R.id.btnCategory);
        LinearLayout btnConsult  = activity.findViewById(R.id.btnConsult);
        LinearLayout btnCart     = activity.findViewById(R.id.btnCart);
        LinearLayout btnUser     = activity.findViewById(R.id.btnUser);

        if (btnHome == null) return; // phòng TH layout này không có bottom_nav

        btnHome.setOnClickListener(v -> {
            if (!"HOME".equals(currentScreen)) {
                Intent intent = new Intent(activity, HomeActivity.class);
                activity.startActivity(intent);
            }
        });

        btnCategory.setOnClickListener(v -> {
            if (!"CATEGORY".equals(currentScreen)) {
                Intent intent = new Intent(activity, CategoryActivity.class);
                activity.startActivity(intent);
            }
        });

        btnConsult.setOnClickListener(v -> {
            if (!"CONSULT".equals(currentScreen)) {
                Intent intent = new Intent(activity, ConsultActivity.class);
                activity.startActivity(intent);
            }
        });

        btnCart.setOnClickListener(v -> {
            if (!"CART".equals(currentScreen)) {
                Intent intent = new Intent(activity, CartActivity.class);
                activity.startActivity(intent);
            }
        });

        btnUser.setOnClickListener(v -> {
            if (!"PROFILE".equals(currentScreen)) {
                Intent intent = new Intent(activity, ProfileActivity.class);
                activity.startActivity(intent);
            }
        });
        highlightCurrentTab(activity, currentScreen);
    }
    private static void highlightCurrentTab(Activity a, String screen) {
        ImageView iconHome      = a.findViewById(R.id.iconHome);
        ImageView iconCategory  = a.findViewById(R.id.iconCategory);
        ImageView iconConsult   = a.findViewById(R.id.iconConsult);
        ImageView iconCart      = a.findViewById(R.id.iconCart);
        ImageView iconUser      = a.findViewById(R.id.iconUser);

        TextView labelHome      = a.findViewById(R.id.labelHome);
        TextView labelCategory  = a.findViewById(R.id.labelCategory);
        TextView labelConsult   = a.findViewById(R.id.labelConsult);
        TextView labelCart      = a.findViewById(R.id.labelCart);
        TextView labelUser      = a.findViewById(R.id.labelUser);

        // reset về xám
        setTabColor(iconHome,     labelHome,     COLOR_UNSELECTED);
        setTabColor(iconCategory, labelCategory, COLOR_UNSELECTED);
        setTabColor(iconConsult,  labelConsult,  COLOR_UNSELECTED1);
        setTabColor(iconCart,     labelCart,     COLOR_UNSELECTED);
        setTabColor(iconUser,     labelUser,     COLOR_UNSELECTED);

        // chọn tab hiện tại
        switch (screen) {
            case "HOME":
                setTabColor(iconHome, labelHome, COLOR_SELECTED);
                break;
            case "CATEGORY":
                setTabColor(iconCategory, labelCategory, COLOR_SELECTED);
                break;
            case "CONSULT":
                setTabColor(iconConsult, labelConsult, COLOR_SELECTED1);
                break;
            case "CART":
                setTabColor(iconCart, labelCart, COLOR_SELECTED);
                break;
            case "PROFILE":
                setTabColor(iconUser, labelUser, COLOR_SELECTED);
                break;
        }
    }

    private static void setTabColor(ImageView icon, TextView label, int color) {
        if (icon != null)  icon.setColorFilter(color);
        if (label != null) label.setTextColor(color);
    }
}
