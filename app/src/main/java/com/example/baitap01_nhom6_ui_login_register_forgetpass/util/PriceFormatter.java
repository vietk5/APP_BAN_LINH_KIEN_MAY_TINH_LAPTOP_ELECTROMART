package com.example.baitap01_nhom6_ui_login_register_forgetpass.util;

import java.text.NumberFormat;
import java.util.Locale;

public class PriceFormatter {
    public static String vnd(long price) {
        return NumberFormat.getInstance(new Locale("vi","VN")).format(price) + " đ";
    }
}
