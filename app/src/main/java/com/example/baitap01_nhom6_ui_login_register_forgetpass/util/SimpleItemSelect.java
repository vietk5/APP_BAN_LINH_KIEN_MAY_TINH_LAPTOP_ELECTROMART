package com.example.baitap01_nhom6_ui_login_register_forgetpass.util;

import android.view.View;
import android.widget.AdapterView;

/**
 * Dùng cho Spinner: mỗi lần chọn item mới thì chạy callback.run()
 */
public class SimpleItemSelect implements AdapterView.OnItemSelectedListener {

    private final Runnable callback;

    public SimpleItemSelect(Runnable callback) {
        this.callback = callback;
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        if (callback != null) callback.run();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        // không cần làm gì
    }
}
