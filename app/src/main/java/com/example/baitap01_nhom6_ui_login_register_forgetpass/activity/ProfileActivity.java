package com.example.baitap01_nhom6_ui_login_register_forgetpass.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.baitap01_nhom6_ui_login_register_forgetpass.R;
import com.example.baitap01_nhom6_ui_login_register_forgetpass.util.SharedPrefManager;

public class ProfileActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_profile);

        SharedPrefManager sharedPref = new SharedPrefManager(this);
//        sharedPref.logout();

        if (!sharedPref.isLoggedIn()) {
            showLoginDialog();
            return; // Ngừng chạy tiếp UI logout
        }

        LinearLayout btnLogout = findViewById(R.id.btn_logout);
        btnLogout.setOnClickListener(v -> {
            sharedPref.logout();
            // Chuyển về LoginActivity và xoá hết stack
            Intent intent = new Intent(ProfileActivity.this, HomeActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            Toast.makeText(this, "Đăng xuất thành công", Toast.LENGTH_SHORT).show();
        });
    }
    private void showLoginDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Yêu cầu đăng nhập");
        builder.setMessage("Bạn cần đăng nhập để xem trang cá nhân.");

        builder.setPositiveButton("Đăng nhập", (dialog, which) -> {
            startActivity(new Intent(ProfileActivity.this, LoginActivity.class));
            finish();
        });

        builder.setNegativeButton("Đăng ký", (dialog, which) -> {
            startActivity(new Intent(ProfileActivity.this, RegisterActivity.class));
            finish();
        });

        builder.setCancelable(false); // Không cho tắt dialog khi bấm ra ngoài
        builder.show();
    }
}