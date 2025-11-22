package com.example.baitap01_nhom6_ui_login_register_forgetpass.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.baitap01_nhom6_ui_login_register_forgetpass.R;
import com.example.baitap01_nhom6_ui_login_register_forgetpass.adapters.AdminProductAdapter;
import com.example.baitap01_nhom6_ui_login_register_forgetpass.models.dto.AdminProductDto;
import com.example.baitap01_nhom6_ui_login_register_forgetpass.remote.ApiClient;
import com.example.baitap01_nhom6_ui_login_register_forgetpass.util.AdminNavHelper;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AdminProductActivity extends AppCompatActivity
        implements AdminProductAdapter.Listener {

    private EditText edtSearch;
    private ImageButton btnClearSearch;
    private MaterialButton btnAddProduct;
    private RecyclerView recyclerView;
    private Spinner spnSort;
    private String currentSort = null;


    private final List<AdminProductDto> data = new ArrayList<>();
    private AdminProductAdapter adapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_product);
        MaterialToolbar toolbar = findViewById(R.id.adminToolbar);
        AdminNavHelper.setupToolbar(this, toolbar, "Admin - Quản lí sản phẩm");
        initViews();
        setupRecyclerView();
        setupEvents();

        // Load dữ liệu lần đầu
        loadProducts(null);
    }

    private void initViews() {
        edtSearch      = findViewById(R.id.edtSearch);
        btnClearSearch = findViewById(R.id.btnClearSearch);
        btnAddProduct  = findViewById(R.id.btnAddProduct);
        recyclerView   = findViewById(R.id.recyclerProductsAdmin);
        spnSort        = findViewById(R.id.spnSort);
    }

    private void setupRecyclerView() {
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new AdminProductAdapter(data, this);
        recyclerView.setAdapter(adapter);
    }

    private void setupEvents() {
        // 1. Nút Thêm sản phẩm mới -> Chuyển sang màn hình ProductEntryActivity
        btnAddProduct.setOnClickListener(v -> {
            Intent i = new Intent(this, ProductEntryActivity.class);
            startActivity(i);
        });

        // 2. Nút Xóa tìm kiếm
        btnClearSearch.setOnClickListener(v -> {
            edtSearch.setText("");
            loadProducts(null);
        });

        // 3. Tìm kiếm realtime
        edtSearch.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override
            public void afterTextChanged(Editable s) {
                String keyword = s.toString().trim();
                loadProducts(keyword.isEmpty() ? null : keyword);
            }
        });
        // ================= SORT ==================
        ArrayAdapter<String> sortAdapter = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_item,
                new String[]{"Mặc định", "Tồn kho tăng dần", "Tồn kho giảm dần"});
        sortAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnSort.setAdapter(sortAdapter);

        spnSort.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 1:  // tăng dần
                        currentSort = "asc";
                        break;
                    case 2:  // giảm dần
                        currentSort = "desc";
                        break;
                    default:
                        currentSort = null;
                }

                // load lại danh sách khi chọn sort
                loadProducts(edtSearch.getText().toString().trim());
            }
            @Override public void onNothingSelected(AdapterView<?> parent) {}
        });
    }

    // ===== LOGIC GỌI API LẤY DANH SÁCH =====
    private void loadProducts(String keyword) {
        ApiClient.get().getAdminProducts(null, null, keyword)
                .enqueue(new Callback<List<AdminProductDto>>() {
                    @Override
                    public void onResponse(Call<List<AdminProductDto>> call,
                                           Response<List<AdminProductDto>> response) {
                        if (!response.isSuccessful() || response.body() == null) {
                            // Có thể thông báo lỗi nhẹ hoặc để trống
                            return;
                        }

//                        data.clear();
//                        data.addAll(response.body());
                        data.clear();
                        List<AdminProductDto> list = response.body();

                        if (currentSort != null && list != null) {
                            if (currentSort.equals("asc")) {
                                list.sort(Comparator.comparingInt(p -> p.tonKho));
                            } else if (currentSort.equals("desc")) {
                                list.sort((a, b) -> b.tonKho - a.tonKho);
                            }
                        }

                        data.addAll(list);
                        adapter.notifyDataSetChanged();
                        adapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onFailure(Call<List<AdminProductDto>> call, Throwable t) {
                        Toast.makeText(AdminProductActivity.this,
                                "Lỗi kết nối server", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    // ===== IMPLEMENT CALLBACK TỪ ADAPTER =====
    @Override
    public void onNhapHang(AdminProductDto p) {
        // Hiển thị Dialog nhập số lượng
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Nhập kho: " + p.ten);
        builder.setMessage("Vui lòng nhập số lượng muốn thêm:");

        // Tạo ô nhập liệu trong Dialog
        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_NUMBER); // Chỉ cho nhập số
        input.setHint("Số lượng (ví dụ: 10)");
        builder.setView(input);

        // Nút OK
        builder.setPositiveButton("Xác nhận", (dialog, which) -> {
            String qtyStr = input.getText().toString().trim();
            if (!qtyStr.isEmpty()) {
                try {
                    int delta = Integer.parseInt(qtyStr);
                    if (delta > 0) {
                        callApiChangeStock(p, delta);
                    } else {
                        Toast.makeText(this, "Số lượng phải lớn hơn 0", Toast.LENGTH_SHORT).show();
                    }
                } catch (NumberFormatException e) {
                    Toast.makeText(this, "Số lượng không hợp lệ", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Nút Hủy
        builder.setNegativeButton("Hủy", (dialog, which) -> dialog.cancel());
        builder.show();
    }

    @Override
    public void onXoa(AdminProductDto p) {
        // Hiển thị Dialog xác nhận xóa
        new AlertDialog.Builder(this)
                .setTitle("Cảnh báo xóa")
                .setMessage("Bạn có chắc chắn muốn xóa sản phẩm \"" + p.ten + "\" không? Hành động này không thể hoàn tác.")
                .setPositiveButton("Xóa luôn", (dialog, which) -> callApiDelete(p))
                .setNegativeButton("Hủy", null)
                .show();
    }

    // ===== CÁC HÀM API CON (HELPER) =====

    private void callApiChangeStock(AdminProductDto p, int delta) {
        ApiClient.get().changeProductStock(p.id, delta).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(AdminProductActivity.this,
                            "Đã thêm " + delta + " sản phẩm vào kho", Toast.LENGTH_SHORT).show();
                    // Load lại danh sách để thấy số tồn kho mới
                    loadProducts(edtSearch.getText().toString().trim());
                } else {
                    Toast.makeText(AdminProductActivity.this,
                            "Nhập hàng thất bại!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(AdminProductActivity.this,
                        "Lỗi kết nối!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void callApiDelete(AdminProductDto p) {
        ApiClient.get().deleteProduct(p.id).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(AdminProductActivity.this,
                            "Đã xóa thành công: " + p.ten, Toast.LENGTH_SHORT).show();
                    // Load lại danh sách để mất dòng đó đi
                    loadProducts(edtSearch.getText().toString().trim());
                } else {
                    Toast.makeText(AdminProductActivity.this,
                            "Không thể xóa (có thể sản phẩm đã có đơn hàng)", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(AdminProductActivity.this,
                        "Lỗi kết nối!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Lưu ý: Khi quay lại từ ProductEntryActivity (sau khi thêm mới thành công),
    // bạn có thể muốn load lại list. Sử dụng onResume để làm việc đó.
    @Override
    protected void onResume() {
        super.onResume();
        // Load lại danh sách mỗi khi màn hình này hiện lên (để cập nhật sp mới thêm)
        loadProducts(edtSearch.getText().toString().trim());
    }
}