package com.example.baitap01_nhom6_ui_login_register_forgetpass.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.baitap01_nhom6_ui_login_register_forgetpass.R;
import com.example.baitap01_nhom6_ui_login_register_forgetpass.adapters.AdminCustomerAdapter;
import com.example.baitap01_nhom6_ui_login_register_forgetpass.models.dto.AdminCustomerDto;
import com.example.baitap01_nhom6_ui_login_register_forgetpass.models.dto.PageResponse;
import com.example.baitap01_nhom6_ui_login_register_forgetpass.remote.ApiClient;
import com.example.baitap01_nhom6_ui_login_register_forgetpass.util.AdminNavHelper;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AdminCustomersActivity extends AppCompatActivity
        implements AdminCustomerAdapter.OnCustomerActionListener {

    private EditText etSearch;
    private MaterialButton btnSort;
    private RecyclerView recycler;
    private TextView tvCustomerTotal;

    private final List<AdminCustomerDto> data = new ArrayList<>();
    private AdminCustomerAdapter adapter;

    private boolean newestFirst = true; // sortDir desc
    private String currentSearch = null;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_customers);

        MaterialToolbar toolbar = findViewById(R.id.adminToolbar);
        AdminNavHelper.setupToolbar(this, toolbar, "Admin - Quản lí khách hàng");


        initViews();
        setupRecycler();
        setupSearchAndSort();

        loadCustomers();
    }


    private void initViews() {
        etSearch        = findViewById(R.id.etSearchCustomer);
        btnSort         = findViewById(R.id.btnSort);
        recycler        = findViewById(R.id.recyclerCustomersAdmin);
        tvCustomerTotal = findViewById(R.id.tvCustomerTotal);
    }

    private void setupRecycler() {
        recycler.setLayoutManager(new LinearLayoutManager(this));
        adapter = new AdminCustomerAdapter(data, this);
        recycler.setAdapter(adapter);
    }

    private void setupSearchAndSort() {
        // Nhấn Enter trên bàn phím để search
        etSearch.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                applySearch();
                return true;
            }
            return false;
        });

        // Button sort đổi giữa "Mới nhất" / "Cũ nhất"
        btnSort.setOnClickListener(v -> {
            newestFirst = !newestFirst;
            btnSort.setText(newestFirst ? "Mới nhất" : "Cũ nhất");
            loadCustomers(); // reload với sort mới
        });
    }

    private void applySearch() {
        String text = etSearch.getText().toString().trim();
        currentSearch = TextUtils.isEmpty(text) ? null : text;
        loadCustomers();
    }

    private void loadCustomers() {
        String sortDir = newestFirst ? "desc" : "asc";

        ApiClient.get().getAdminCustomers(currentSearch, 0, 50, sortDir)
                .enqueue(new Callback<PageResponse<AdminCustomerDto>>() {
                    @Override
                    public void onResponse(Call<PageResponse<AdminCustomerDto>> call,
                                           Response<PageResponse<AdminCustomerDto>> response) {
                        if (!response.isSuccessful() || response.body() == null) {
                            Toast.makeText(AdminCustomersActivity.this,
                                    "Không tải được danh sách khách hàng",
                                    Toast.LENGTH_SHORT).show();
                            return;
                        }

                        PageResponse<AdminCustomerDto> body = response.body();
                        data.clear();
                        if (body.content != null) {
                            data.addAll(body.content);
                        }
                        adapter.notifyDataSetChanged();

                        tvCustomerTotal.setText("Tổng: " + body.totalElements + " khách hàng");
                    }

                    @Override
                    public void onFailure(Call<PageResponse<AdminCustomerDto>> call, Throwable t) {
                        Toast.makeText(AdminCustomersActivity.this,
                                "Lỗi kết nối: " + t.getMessage(),
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }

    // ===== CALLBACK TỪ ADAPTER =====

    @Override
    public void onBlockClicked(AdminCustomerDto customer) {
        ApiClient.get().toggleBlockCustomer(customer.id)
                .enqueue(new Callback<Void>() {
                    @Override
                    public void onResponse(Call<Void> call, Response<Void> response) {
                        if (!response.isSuccessful()) {
                            Toast.makeText(AdminCustomersActivity.this,
                                    "Không block/unblock được", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        // reload list
                        loadCustomers();
                    }

                    @Override
                    public void onFailure(Call<Void> call, Throwable t) {
                        Toast.makeText(AdminCustomersActivity.this,
                                "Lỗi block: " + t.getMessage(),
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    public void onDeleteClicked(AdminCustomerDto customer) {
        // Xác nhận trước khi xóa
        new androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle("Xóa tài khoản")
                .setMessage("Bạn có chắc muốn xóa khách hàng \"" + customer.fullName + "\"?")
                .setPositiveButton("Xóa", (dialog, which) -> DeleteCustomer(customer.id))
                .setNegativeButton("Hủy", null)
                .show();
    }

    private void DeleteCustomer(long id) {
        ApiClient.get().deleteCustomer(id)
                .enqueue(new Callback<Void>() {
                    @Override
                    public void onResponse(Call<Void> call, Response<Void> response) {
                        if (!response.isSuccessful()) {
                            Toast.makeText(AdminCustomersActivity.this,
                                    "Không xóa được khách hàng",
                                    Toast.LENGTH_SHORT).show();
                            return;
                        }
                        Toast.makeText(AdminCustomersActivity.this,
                                "Đã xóa khách hàng", Toast.LENGTH_SHORT).show();
                        loadCustomers();
                    }

                    @Override
                    public void onFailure(Call<Void> call, Throwable t) {
                        Toast.makeText(AdminCustomersActivity.this,
                                "Lỗi xóa: " + t.getMessage(),
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
