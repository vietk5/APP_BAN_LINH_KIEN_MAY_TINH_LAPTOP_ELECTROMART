package com.example.baitap01_nhom6_ui_login_register_forgetpass.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.baitap01_nhom6_ui_login_register_forgetpass.R;
import com.example.baitap01_nhom6_ui_login_register_forgetpass.util.SharedPrefManager;

public class FirstPage extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_first_page);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
//        Admin login → chuyển thẳng AdminActivity.
//        User bình thường → chuyển HomeActivity.
//        Lần sau mở app → tự động điều hướng đúng màn hình dựa vào SharedPreferences.
        SharedPrefManager sharedPref = new SharedPrefManager(this);
        new Handler().postDelayed(() -> {
            if (sharedPref.isLoggedIn()) {
                // Nếu là admin thì vào thẳng trang admin, còn không thì vào trang home cho người dùng
                if (sharedPref.isAdmin()) {
                    startActivity(new Intent(this, AdminActivity.class));
                } else {
                    startActivity(new Intent(this, HomeActivity.class));
                }
                finish();
                // Nếu khi thoát app người dùng đã đăng xuất thì vào thẳng trang login để đăng nhập lại
            } else {
                startActivity(new Intent(this, LoginActivity.class));
                finish();
            }
        }, 3000);
    }
}
