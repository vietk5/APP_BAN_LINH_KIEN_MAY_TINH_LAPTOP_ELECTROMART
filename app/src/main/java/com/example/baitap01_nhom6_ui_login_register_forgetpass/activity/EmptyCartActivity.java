package com.example.baitap01_nhom6_ui_login_register_forgetpass.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.baitap01_nhom6_ui_login_register_forgetpass.R;

public class EmptyCartActivity extends AppCompatActivity {
    private Button btnShopNow;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_empty_cart);

        btnShopNow = findViewById(R.id.btn_shop_now);
        // 2. Thiết lập OnClickListener cho Button
        btnShopNow.setOnClickListener(v -> {
            Intent intent = new Intent(EmptyCartActivity.this, HomeActivity.class);
            startActivity(intent);
            finish();
        });
    }
}