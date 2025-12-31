package com.example.baitap01_nhom6_ui_login_register_forgetpass.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.baitap01_nhom6_ui_login_register_forgetpass.R;
import com.example.baitap01_nhom6_ui_login_register_forgetpass.adapters.OrderDetailAdapter;
import com.example.baitap01_nhom6_ui_login_register_forgetpass.models.OrderProduct;
import com.example.baitap01_nhom6_ui_login_register_forgetpass.models.dto.OrderDetailItemDto;
import com.example.baitap01_nhom6_ui_login_register_forgetpass.remote.ApiClient;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import retrofit2.Call;

public class OrderDetailActivity extends AppCompatActivity {

    private RecyclerView rcvProducts;
    private TextView tvOrderId, tvStatus, tvDate, tvPayment, tvTotal;

    private OrderDetailAdapter adapter;
    private List<OrderProduct> productList = new ArrayList<>();

    private long orderId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_detail);

        initViews();
        setupRecyclerView();
        getIntentData();
        loadOrderItems();
    }

    private void initViews() {
        tvOrderId = findViewById(R.id.tvOrderId);
        tvStatus = findViewById(R.id.tvStatus);
        tvDate = findViewById(R.id.tvDate);
        tvPayment = findViewById(R.id.tvPayment);
        tvTotal = findViewById(R.id.tvTotal);
        rcvProducts = findViewById(R.id.rcvProducts);
        ImageView btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> {
            finish();
        });
    }

    private void setupRecyclerView() {
        adapter = new OrderDetailAdapter(this, productList);
        rcvProducts.setLayoutManager(new LinearLayoutManager(this));
        rcvProducts.setAdapter(adapter);
    }

    private void getIntentData() {
        Intent intent = getIntent();
        orderId = intent.getLongExtra("orderId", -1);

        tvOrderId.setText("Mã đơn hàng: #" + orderId);
//        tvStatus.setText("Trạng thái: " + intent.getStringExtra("status"));

        String status = intent.getStringExtra("status");
        setStatusText(status);

        long createdAt = intent.getLongExtra("createdAt", 0);
        tvDate.setText("Ngày đặt: " +
                new SimpleDateFormat("dd/MM/yyyy")
                        .format(new Date(createdAt)));

        tvPayment.setText("Phương thức thanh toán: " +
                intent.getStringExtra("paymentMethod"));

        tvTotal.setText(formatPrice(intent.getLongExtra("totalPrice", 0)));
    }

    private void loadOrderItems() {
        ApiClient.get().getUserOrderItems(orderId)
                .enqueue(new retrofit2.Callback<List<OrderDetailItemDto>>() {
                    @Override
                    public void onResponse(Call<List<OrderDetailItemDto>> call,
                                           retrofit2.Response<List<OrderDetailItemDto>> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            productList.clear();
                            for (OrderDetailItemDto dto : response.body()) {
                                OrderProduct p = new OrderProduct();
                                p.setName(dto.getProductName());
                                p.setQuantity(dto.getSoLuong());
                                p.setPrice(dto.getDonGia());
                                p.setImage(dto.getImageUrl());
                                productList.add(p);
                            }
                            adapter.notifyDataSetChanged();
                        }
                    }

                    @Override
                    public void onFailure(Call<List<OrderDetailItemDto>> call, Throwable t) {
                        Toast.makeText(OrderDetailActivity.this,
                                "Lỗi tải chi tiết đơn hàng", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private String formatPrice(long price) {
        return new DecimalFormat("#,###").format(price) + "đ";
    }
    private void setStatusText(String status) {
        String prefix = "Trạng thái: ";
        String statusText = mapStatus(status);
        String fullText = prefix + statusText;

        SpannableString spannable = new SpannableString(fullText);

        int color;

        switch (status) {
            case "DANG_XU_LY":
                color = ContextCompat.getColor(this, R.color.orange);
                break;
            case "HOAN_THANH":
                color = ContextCompat.getColor(this, R.color.green);
                break;
            case "DA_HUY":
                color = ContextCompat.getColor(this, R.color.red);
                break;
            default:
                color = ContextCompat.getColor(this, R.color.gray);
        }

        // chỉ đổi màu phần trạng thái
        spannable.setSpan(
                new ForegroundColorSpan(color),
                prefix.length(),
                fullText.length(),
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        );

        tvStatus.setText(spannable);
    }
    private String mapStatus(String status) {
        if (status == null) return "Không xác định";

        switch (status) {
            case "DANG_XU_LY":
                return "Đang xử lý";
            case "HOAN_THANH":
                return "Đã hoàn thành";
            case "DA_HUY":
                return "Đã huỷ";
            default:
                return "Không xác định";
        }
    }

}
