// AdminActivity.java
package com.example.baitap01_nhom6_ui_login_register_forgetpass.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.baitap01_nhom6_ui_login_register_forgetpass.R;
import com.example.baitap01_nhom6_ui_login_register_forgetpass.models.admin.AdminDashboardSummary;
import com.example.baitap01_nhom6_ui_login_register_forgetpass.remote.ApiClient;
import com.example.baitap01_nhom6_ui_login_register_forgetpass.util.AdminNavHelper;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AdminActivity extends AppCompatActivity {

    private TextView tvCustomerCount, tvProductCount;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_dashboard);

        initViews();
        setupToolbar();
        setupQuickActions();
        loadSummary();
    }

    private void initViews() {
        tvCustomerCount = findViewById(R.id.tvCustomerCount);
        tvProductCount  = findViewById(R.id.tvProductCount);
    }

    private void setupToolbar() {
        MaterialToolbar toolbar = findViewById(R.id.adminToolbar);
        AdminNavHelper.setupToolbar(this, toolbar, "ElectroMart - Admin");
    }


    private void setupQuickActions() {
        MaterialButton btnManageProducts  = findViewById(R.id.btnManageProducts);
        MaterialButton btnManageOrders    = findViewById(R.id.btnManageOrders);
        MaterialButton btnManageCustomers = findViewById(R.id.btnManageCustomers);
        MaterialButton btnManageRevenue   = findViewById(R.id.btnManageRevenue);
        MaterialButton btnManageVouchers = findViewById(R.id.btnManageVouchers);

        btnManageProducts.setOnClickListener(v ->
                startActivity(new Intent(this, AdminProductActivity.class)));

        btnManageOrders.setOnClickListener(v ->
                startActivity(new Intent(this, AdminOrdersActivity.class)));

        btnManageCustomers.setOnClickListener(v ->
                startActivity(new Intent(this, AdminCustomersActivity.class)));

        btnManageRevenue.setOnClickListener(v ->
                startActivity(new Intent(this, AdminRevenueActivity.class)));
        btnManageVouchers.setOnClickListener(v ->
                startActivity(new Intent(this, AdminVoucherActivity.class)));
    }

    // ====== GỌI API DASHBOARD (giữ logic cũ) ======
    private void loadSummary() {
        ApiClient.get().getAdminSummary().enqueue(new Callback<AdminDashboardSummary>() {
            @Override
            public void onResponse(Call<AdminDashboardSummary> call,
                                   Response<AdminDashboardSummary> response) {
                if (!response.isSuccessful() || response.body() == null) return;

                AdminDashboardSummary s = response.body();
                tvCustomerCount.setText(String.valueOf(s.totalCustomers));
                tvProductCount.setText(String.valueOf(s.totalProducts));
            }

            @Override
            public void onFailure(Call<AdminDashboardSummary> call, Throwable t) {
                Toast.makeText(AdminActivity.this,
                        "Lỗi summary: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
