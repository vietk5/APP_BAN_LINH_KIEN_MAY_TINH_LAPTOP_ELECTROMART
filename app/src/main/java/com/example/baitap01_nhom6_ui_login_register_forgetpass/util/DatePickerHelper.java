package com.example.baitap01_nhom6_ui_login_register_forgetpass.util;

import android.app.DatePickerDialog;
import android.content.Context;
import android.widget.EditText;

import java.util.Calendar;

public class DatePickerHelper {
    public static void pickDate(Context ctx, EditText target) {
        Calendar c = Calendar.getInstance();
        int y = c.get(Calendar.YEAR);
        int m = c.get(Calendar.MONTH);
        int d = c.get(Calendar.DAY_OF_MONTH);

        new DatePickerDialog(ctx, (view, year, month, dayOfMonth) -> {
            int mm = month + 1;
            String s = String.format("%04d-%02d-%02d", year, mm, dayOfMonth);
            target.setText(s);
        }, y, m, d).show();
    }
}
