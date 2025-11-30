package com.example.baitap01_nhom6_ui_login_register_forgetpass.activity;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
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
    private ProgressBar progressLoading;

    private final List<AdminOrderDto> data = new ArrayList<>();
    private AdminOrderAdapter adapter;

    private final int size = 20;

    private TextView tvStatusCount;
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
        recycler        = findViewById(R.id.recyclerAdminOrders);
        spStatus        = findViewById(R.id.spStatus);
        spSort          = findViewById(R.id.spSort);
        progressLoading = findViewById(R.id.progressLoading);
        tvStatusCount   = findViewById(R.id.tvStatusCount);

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
                R.layout.item_admin_spinner,
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
        // Nhãn đang chọn trên spinner (Tất cả / MOI / DANG_XU_LY / ...)
        String statusLabel = spStatus.getSelectedItem().toString();
        // Tham số gửi lên API: null nếu chọn "Tất cả"
        String statusParam = "Tất cả".equals(statusLabel) ? null : statusLabel;

        if (progressLoading != null) {
            progressLoading.setVisibility(ProgressBar.VISIBLE);
        }

        ApiClient.get().getAdminOrders(statusParam, 0, size)
                .enqueue(new Callback<PageResponse<AdminOrderDto>>() {
                    @Override
                    public void onResponse(Call<PageResponse<AdminOrderDto>> call,
                                           Response<PageResponse<AdminOrderDto>> response) {

                        if (progressLoading != null) {
                            progressLoading.setVisibility(ProgressBar.GONE);
                        }

                        if (!response.isSuccessful() || response.body() == null) {
                            tvStatusCount.setText("Tổng: 0 đơn");
                            Toast.makeText(AdminOrdersActivity.this,
                                    "Không tải được đơn", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        // Lấy list từ server
                        List<AdminOrderDto> list = new ArrayList<>();
                        if (response.body().content != null) {
                            list.addAll(response.body().content);
                        }

                        // ===== LỌC LẠI THEO STATUS Ở CLIENT (phòng khi backend không lọc) =====
                        if (statusParam != null) {
                            List<AdminOrderDto> filtered = new ArrayList<>();
                            for (AdminOrderDto o : list) {
                                if (statusParam.equals(o.status)) {
                                    filtered.add(o);
                                }
                            }
                            list = filtered;
                        }

                        // ===== SẮP XẾP THEO SPINNER =====
                        boolean newestFirst =
                                "Mới nhất".equals(spSort.getSelectedItem().toString());

                        Collections.sort(list, (o1, o2) -> {
                            if (newestFirst) {
                                return Long.compare(o2.id, o1.id); // mới -> cũ
                            } else {
                                return Long.compare(o1.id, o2.id); // cũ -> mới
                            }
                        });

                        // Đổ vào data cho adapter
                        data.clear();
                        data.addAll(list);
                        adapter.notifyDataSetChanged();

                        // ===== CẬP NHẬT DÒNG "Trạng thái ...: X đơn" =====
                        int count = list.size();
                        String text;
                        if ("Tất cả".equals(statusLabel)) {
                            text = "Tổng: " + count + " đơn";
                        } else {
                            text = "Trạng thái " + statusLabel + ": " + count + " đơn";
                        }
                        tvStatusCount.setText(text);
                    }

                    @Override
                    public void onFailure(Call<PageResponse<AdminOrderDto>> call, Throwable t) {
                        if (progressLoading != null) {
                            progressLoading.setVisibility(ProgressBar.GONE);
                        }
                        tvStatusCount.setText("Tổng: 0 đơn");
                        Toast.makeText(AdminOrdersActivity.this,
                                t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }


    // ================== NGHIỆP VỤ ĐỔI TRẠNG THÁI ==================

    private void showChangeStatusDialog(AdminOrderDto order) {
        String current = order.status;

        // Đơn đã hoàn thành / đã hủy thì không cho sửa nữa
        if ("HOAN_THANH".equals(current) || "DA_HUY".equals(current)) {
            new AlertDialog.Builder(this)
                    .setTitle("Không thể cập nhật")
                    .setMessage("Đơn hàng đã ở trạng thái \"" + current + "\" và không thể thay đổi nữa.")
                    .setPositiveButton("Đóng", null)
                    .show();
            return;
        }

        // Xác định các trạng thái MỚI được phép chọn
        String[] statusList;

        switch (current) {
            case "MOI":
                statusList = new String[]{"DANG_XU_LY", "DA_HUY"};
                break;
            case "DANG_XU_LY":
                statusList = new String[]{"DANG_GIAO", "DA_HUY"};
                break;
            case "DANG_GIAO":
                statusList = new String[]{"HOAN_THANH", "DA_HUY"};
                break;
            default:
                // fallback: cho chọn tất cả (ít dùng)
                statusList = new String[]{"MOI", "DANG_XU_LY", "DANG_GIAO", "HOAN_THANH", "DA_HUY"};
        }

        new AlertDialog.Builder(this)
                .setTitle("Cập nhật trạng thái đơn #" + order.id)
                .setItems(statusList, (dialog, which) ->
                        updateStatus(order.id, statusList[which]))
                .show();
    }

    private void updateStatus(long id, String newStatus) {
        ApiClient.get().updateOrderStatus(id, newStatus)
                .enqueue(new Callback<Void>() {
                    @Override
                    public void onResponse(Call<Void> call, Response<Void> response) {
                        if (response.isSuccessful()) {
                            Toast.makeText(AdminOrdersActivity.this,
                                    "Cập nhật trạng thái thành công", Toast.LENGTH_SHORT).show();
                            loadOrders();
                        } else {
                            Toast.makeText(AdminOrdersActivity.this,
                                    "Cập nhật thất bại: " + response.code(), Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<Void> call, Throwable t) {
                        Toast.makeText(AdminOrdersActivity.this,
                                t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    // ================== XEM CHI TIẾT ĐƠN ==================
    private void openOrderDetail(AdminOrderDto order) {
        AdminOrderDetailBottomSheet sheet =
                AdminOrderDetailBottomSheet.newInstance(order);
        sheet.show(getSupportFragmentManager(), "admin_order_detail");
    }

}
