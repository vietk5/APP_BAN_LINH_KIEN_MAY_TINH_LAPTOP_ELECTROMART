package com.example.baitap01_nhom6_ui_login_register_forgetpass.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.baitap01_nhom6_ui_login_register_forgetpass.R;
import com.example.baitap01_nhom6_ui_login_register_forgetpass.fragment.HomeFragment;
import com.example.baitap01_nhom6_ui_login_register_forgetpass.util.PriceFormatter;

public class OrderSuccessActivity extends AppCompatActivity {

    private TextView tvThankYou, tvTotalPaid;
    private Button btnBackHome, btnViewOrders;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_success);

        tvThankYou  = findViewById(R.id.tvThankYou);
        tvTotalPaid = findViewById(R.id.tvTotalPaid);
        btnBackHome = findViewById(R.id.btnBackHome);
        btnViewOrders = findViewById(R.id.btnViewOrders);

        long totalPaid = getIntent().getLongExtra("total_paid", 0L);
        tvTotalPaid.setText(PriceFormatter.vnd(totalPaid));

        // Nút về trang chủ
        btnBackHome.setOnClickListener(v -> {
            Intent i = new Intent(this, HomeFragment.class);
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(i);
            finish();
        });

        // Nút xem đơn hàng
        // TODO: đổi OrderHistoryActivity thành màn "Đơn hàng" thật của bạn nếu đã có
        btnViewOrders.setOnClickListener(v -> {
            Intent i = new Intent(this, MyOrdersActivity.class);
            startActivity(i);
            finish();
        });
    }
}
