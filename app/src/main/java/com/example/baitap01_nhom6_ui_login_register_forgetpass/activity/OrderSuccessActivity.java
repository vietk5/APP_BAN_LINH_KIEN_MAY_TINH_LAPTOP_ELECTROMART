package com.example.baitap01_nhom6_ui_login_register_forgetpass.activity;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;

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

        //Hiện thông báo trên máy của người dùng
        showOrderSuccessNotification(totalPaid);
    }
    private void showOrderSuccessNotification(long totalPaid) {
        String channelId = "order_success_channel";

        // Khi bấm notification → mở MyOrdersActivity
        Intent intent = new Intent(this, MyOrdersActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);

        PendingIntent pendingIntent = PendingIntent.getActivity(
                this,
                0,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        // Android 8+ cần channel
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    channelId,
                    "Thông báo đơn hàng",
                    NotificationManager.IMPORTANCE_HIGH
            );
            channel.setDescription("Thông báo trạng thái đơn hàng");
            notificationManager.createNotificationChannel(channel);
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, channelId)
                .setSmallIcon(R.drawable.logo)
                .setContentTitle("🎉 Đặt hàng thành công")
                .setContentText("Tổng thanh toán: " + PriceFormatter.vnd(totalPaid))
                .setAutoCancel(true)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentIntent(pendingIntent);

        notificationManager.notify((int) System.currentTimeMillis(), builder.build());
    }

}
