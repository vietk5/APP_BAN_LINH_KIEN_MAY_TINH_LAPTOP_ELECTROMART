package com.example.baitap01_nhom6_ui_login_register_forgetpass.activity;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.baitap01_nhom6_ui_login_register_forgetpass.R;
import com.example.baitap01_nhom6_ui_login_register_forgetpass.adapters.AdminOrderAdapter;
import com.example.baitap01_nhom6_ui_login_register_forgetpass.models.dto.AdminOrderDto;
import com.example.baitap01_nhom6_ui_login_register_forgetpass.models.dto.PageResponse;
import com.example.baitap01_nhom6_ui_login_register_forgetpass.remote.ApiClient;
import com.example.baitap01_nhom6_ui_login_register_forgetpass.util.AdminNavHelper;
import com.example.baitap01_nhom6_ui_login_register_forgetpass.util.PriceFormatter;
import com.example.baitap01_nhom6_ui_login_register_forgetpass.util.SimpleItemSelect;
import com.google.android.material.appbar.MaterialToolbar;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AdminOrdersActivity extends AppCompatActivity {

    private RecyclerView recycler;
    private Spinner spStatus, spSort;

    private final List<AdminOrderDto> data = new ArrayList<>();
    private AdminOrderAdapter adapter;

    private final int size = 20;

    @Override
    protected void onCreate(Bundle b) {
        super.onCreate(b);
        setContentView(R.layout.activity_admin_orders);
        MaterialToolbar toolbar = findViewById(R.id.adminToolbar);
        AdminNavHelper.setupToolbar(this, toolbar,"Admin - Quản lí đơn hàng");
        initViews();
        setupFilter();
        loadOrders();
    }

    private void initViews() {
        recycler = findViewById(R.id.recyclerAdminOrders);
        spStatus = findViewById(R.id.spStatus);
        spSort   = findViewById(R.id.spSort);

        recycler.setLayoutManager(new LinearLayoutManager(this));

        adapter = new AdminOrderAdapter(
                data,
                this::showChangeStatusDialog,
                this::openOrderDetail
        );
        recycler.setAdapter(adapter);
    }

    private void setupFilter() {
        // ===== STATUS SPINNER =====
        ArrayAdapter<String> a1 = new ArrayAdapter<>(
                this,
                R.layout.item_admin_spinner,   // layout chữ trắng
                new String[]{
                        "Tất cả", "MOI", "DANG_XU_LY", "DANG_GIAO", "HOAN_THANH", "DA_HUY"
                }
        );
        a1.setDropDownViewResource(R.layout.item_admin_spinner_dropdown);
        spStatus.setAdapter(a1);

        // ===== SORT SPINNER =====
        ArrayAdapter<String> a2 = new ArrayAdapter<>(
                this,
                R.layout.item_admin_spinner,
                new String[]{"Mới nhất", "Cũ nhất"}
        );
        a2.setDropDownViewResource(R.layout.item_admin_spinner_dropdown);
        spSort.setAdapter(a2);

        // Khi chọn gì là reload lại danh sách
        spStatus.setOnItemSelectedListener(new SimpleItemSelect(this::loadOrders));
        spSort.setOnItemSelectedListener(new SimpleItemSelect(this::loadOrders));
    }

    private void loadOrders() {
        String status = spStatus.getSelectedItem().toString();
        if ("Tất cả".equals(status)) status = null;

        ApiClient.get().getAdminOrders(status, 0, size)
                .enqueue(new Callback<PageResponse<AdminOrderDto>>() {
                    @Override
                    public void onResponse(Call<PageResponse<AdminOrderDto>> call,
                                           Response<PageResponse<AdminOrderDto>> response) {

                        if (!response.isSuccessful() || response.body() == null) {
                            Toast.makeText(AdminOrdersActivity.this,
                                    "Không tải được đơn", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        data.clear();
                        if (response.body().content != null) {
                            data.addAll(response.body().content);
                        }

                        // ===== SẮP XẾP THEO SPINNER =====
                        boolean newestFirst =
                                "Mới nhất".equals(spSort.getSelectedItem().toString());

                        // sort tạm theo id (id lớn hơn = đơn mới hơn)
                        Collections.sort(data, (o1, o2) -> {
                            if (newestFirst) {
                                return Long.compare(o2.id, o1.id); // mới -> cũ
                            } else {
                                return Long.compare(o1.id, o2.id); // cũ -> mới
                            }
                        });

                        adapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onFailure(Call<PageResponse<AdminOrderDto>> call, Throwable t) {
                        Toast.makeText(AdminOrdersActivity.this,
                                t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void showChangeStatusDialog(AdminOrderDto order) {
        String[] statusList = {"MOI", "DANG_XU_LY", "DANG_GIAO", "HOAN_THANH", "DA_HUY"};

        new AlertDialog.Builder(this)
                .setTitle("Cập nhật trạng thái")
                .setItems(statusList, (dialog, which) ->
                        updateStatus(order.id, statusList[which]))
                .show();
    }

    private void updateStatus(long id, String newStatus) {
        ApiClient.get().updateOrderStatus(id, newStatus)
                .enqueue(new Callback<Void>() {
                    @Override
                    public void onResponse(Call<Void> call, Response<Void> response) {
                        Toast.makeText(AdminOrdersActivity.this,
                                "Cập nhật thành công", Toast.LENGTH_SHORT).show();
                        loadOrders();
                    }

                    @Override
                    public void onFailure(Call<Void> call, Throwable t) {
                        Toast.makeText(AdminOrdersActivity.this,
                                t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void openOrderDetail(AdminOrderDto order) {
        // Tạm dùng dialog text, dễ nhìn hơn Toast 1 dòng
        String message =
                "Mã đơn: #" + order.id + "\n" +
                        "Khách hàng: " + order.customerName + "\n" +
                        "Tổng tiền: " + PriceFormatter.vnd(order.totalAmount) + "\n" +
                        "Trạng thái: " + order.status + "\n" +
                        "Ngày tạo: " + order.createdAt;

        new AlertDialog.Builder(this)
                .setTitle("Chi tiết đơn hàng")
                .setMessage(message)
                .setPositiveButton("Đóng", null)
                .show();
    }
}
