package com.example.baitap01_nhom6_ui_login_register_forgetpass.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.baitap01_nhom6_ui_login_register_forgetpass.R;


public class ForgetPasswordActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_forget_password);


        // 👉 Bắt sự kiện “Back to Login”
        TextView tvBackLogin = findViewById(R.id.tvBackLogin);
        tvBackLogin.setOnClickListener(v -> {
            Intent intent = new Intent(ForgetPasswordActivity.this, LoginActivity.class);
            startActivity(intent);
            finish(); // đóng trang quên mật khẩu
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out); // hiệu ứng mượt
        });
    }
}
