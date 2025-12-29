package com.example.baitap01_nhom6_ui_login_register_forgetpass.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.baitap01_nhom6_ui_login_register_forgetpass.R;
import com.example.baitap01_nhom6_ui_login_register_forgetpass.adapters.AdminVoucherAdapter;
import com.example.baitap01_nhom6_ui_login_register_forgetpass.models.voucher.VoucherDto;
import com.example.baitap01_nhom6_ui_login_register_forgetpass.remote.ApiClient;
import com.example.baitap01_nhom6_ui_login_register_forgetpass.util.AdminNavHelper;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.chip.Chip;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AdminVoucherActivity extends AppCompatActivity {

    private SwipeRefreshLayout swipe;
    private RecyclerView rv;
    private TextView tvEmpty;
    private TextInputEditText edtSearch;
    private Chip chipAll, chipActive, chipInactive;

    private AdminVoucherAdapter adapter;
    private final List<VoucherDto> all = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_voucher);

        MaterialToolbar toolbar = findViewById(R.id.adminToolbar);
        AdminNavHelper.setupToolbar(this, toolbar, "Quản lý voucher");

        swipe = findViewById(R.id.swipe);
        rv = findViewById(R.id.rv);
        tvEmpty = findViewById(R.id.tvEmpty);
        edtSearch = findViewById(R.id.edtSearch);

        chipAll = findViewById(R.id.chipAll);
        chipActive = findViewById(R.id.chipActive);
        chipInactive = findViewById(R.id.chipInactive);

        adapter = new AdminVoucherAdapter(this, new AdminVoucherAdapter.Listener() {
            @Override public void onEdit(VoucherDto v) {
                Intent i = new Intent(AdminVoucherActivity.this, AdminVoucherFormActivity.class);
                i.putExtra("mode", "edit");
                i.putExtra("voucher", v);
                startActivity(i);
            }

            @Override public void onToggle(VoucherDto v) {
                boolean next = !v.hoatDong;
                ApiClient.get().adminToggleVoucher(v.id, next).enqueue(new Callback<VoucherDto>() {
                    @Override public void onResponse(Call<VoucherDto> call, Response<VoucherDto> response) {
                        if (!response.isSuccessful() || response.body() == null) {
                            toast("Toggle thất bại");
                            return;
                        }
                        toast("Đã " + (next ? "bật" : "tắt") + " voucher");
                        load();
                    }
                    @Override public void onFailure(Call<VoucherDto> call, Throwable t) { toast(t.getMessage()); }
                });
            }

            @Override public void onDelete(VoucherDto v) {
                new AlertDialog.Builder(AdminVoucherActivity.this)
                        .setTitle("Xoá voucher")
                        .setMessage("Bạn chắc chắn muốn xoá voucher: " + v.code + " ?")
                        .setPositiveButton("Xoá", (d, w) -> doDelete(v))
                        .setNegativeButton("Huỷ", null)
                        .show();
            }
        });

        rv.setLayoutManager(new LinearLayoutManager(this));
        rv.setAdapter(adapter);

        swipe.setOnRefreshListener(this::load);

        ExtendedFloatingActionButton fab = findViewById(R.id.fabAdd);
        fab.setOnClickListener(v -> {
            Intent i = new Intent(this, AdminVoucherFormActivity.class);
            i.putExtra("mode", "create");
            startActivity(i);
        });

        edtSearch.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) { applyFilter(); }
            @Override public void afterTextChanged(Editable s) {}
        });

        View.OnClickListener chipListener = v -> applyFilter();
        chipAll.setOnClickListener(chipListener);
        chipActive.setOnClickListener(chipListener);
        chipInactive.setOnClickListener(chipListener);

        load();
    }

    @Override
    protected void onResume() {
        super.onResume();
        load();
    }

    private void doDelete(VoucherDto v) {
        ApiClient.get().adminDeleteVoucher(v.id).enqueue(new Callback<Void>() {
            @Override public void onResponse(Call<Void> call, Response<Void> response) {
                if (!response.isSuccessful()) { toast("Xoá thất bại"); return; }
                toast("Đã xoá");
                load();
            }
            @Override public void onFailure(Call<Void> call, Throwable t) { toast(t.getMessage()); }
        });
    }

    private void load() {
        swipe.setRefreshing(true);
        ApiClient.get().adminListVouchers().enqueue(new Callback<List<VoucherDto>>() {
            @Override
            public void onResponse(Call<List<VoucherDto>> call, Response<List<VoucherDto>> response) {
                swipe.setRefreshing(false);
                if (!response.isSuccessful() || response.body() == null) {
                    toast("Không tải được danh sách voucher");
                    return;
                }
                all.clear();
                all.addAll(response.body());
                applyFilter();
            }

            @Override
            public void onFailure(Call<List<VoucherDto>> call, Throwable t) {
                swipe.setRefreshing(false);
                toast("Lỗi: " + t.getMessage());
            }
        });
    }

    private void applyFilter() {
        String q = edtSearch.getText() == null ? "" : edtSearch.getText().toString().trim().toLowerCase();

        boolean onlyActive = chipActive.isChecked();
        boolean onlyInactive = chipInactive.isChecked();

        List<VoucherDto> out = new ArrayList<>();
        for (VoucherDto v : all) {
            if (q.length() > 0) {
                String code = v.code == null ? "" : v.code.toLowerCase();
                if (!code.contains(q)) continue;
            }
            if (onlyActive && !v.hoatDong) continue;
            if (onlyInactive && v.hoatDong) continue;
            out.add(v);
        }

        adapter.submit(out);
        tvEmpty.setVisibility(out.isEmpty() ? View.VISIBLE : View.GONE);
    }

    private void toast(String s) {
        Toast.makeText(this, s, Toast.LENGTH_SHORT).show();
    }
}
