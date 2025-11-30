package com.example.baitap01_nhom6_ui_login_register_forgetpass.activity;

import androidx.appcompat.app.AlertDialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.example.baitap01_nhom6_ui_login_register_forgetpass.R;
import com.example.baitap01_nhom6_ui_login_register_forgetpass.models.dto.AdminProductDto;
import com.example.baitap01_nhom6_ui_login_register_forgetpass.models.dto.BrandDto;
import com.example.baitap01_nhom6_ui_login_register_forgetpass.models.dto.CategoryDto;
import com.example.baitap01_nhom6_ui_login_register_forgetpass.remote.ApiClient;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProductEntryActivity extends AppCompatActivity {

    private EditText etName, etPrice, etQuantity, etImageUrl, etDescription;
    private AutoCompleteTextView autoBrand, autoCategory;
    private ImageView imgPreview;
    private MaterialButton btnClear, btnSubmit;

    // id đã chọn trong dropdown (nếu chọn từ list có sẵn)
    private Long selectedBrandId = null;
    private Long selectedCategoryId = null;

    // list hiển thị dropdown
    private final List<OptionItem> brandOptions = new ArrayList<>();
    private final List<OptionItem> categoryOptions = new ArrayList<>();

    // id đặc biệt cho "Khác (tự nhập)"
    private static final long ID_KHAC = -1L;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_entry);
        androidx.core.view.ViewCompat.setOnApplyWindowInsetsListener(
                findViewById(R.id.main),
                (v, insets) -> {
                    androidx.core.graphics.Insets systemBars =
                            insets.getInsets(androidx.core.view.WindowInsetsCompat.Type.systemBars());
                    v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
                    return insets;
                });
        initViews();
        setupToolbar();
        setupEvents();
        loadCategoriesFromApi();
        loadBrandsFromApi();
    }

    private void initViews() {
        etName        = findViewById(R.id.et_product_name);
        etPrice       = findViewById(R.id.et_price);
        etQuantity    = findViewById(R.id.et_quantity);
        etImageUrl    = findViewById(R.id.et_image_url);
        etDescription = findViewById(R.id.et_short_description);

        autoBrand    = findViewById(R.id.auto_brand);
        autoCategory = findViewById(R.id.auto_category);

        imgPreview = findViewById(R.id.img_preview);
        btnClear   = findViewById(R.id.btn_clear);
        btnSubmit  = findViewById(R.id.btn_submit);
    }

    private void setupToolbar() {
        MaterialToolbar toolbar = findViewById(R.id.topAppBar);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back); // dùng icon back của anh
        toolbar.setNavigationOnClickListener(v -> finish());
    }

    private void setupEvents() {

        // Preview ảnh mỗi khi URL thay đổi
        etImageUrl.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                String url = s.toString().trim();

                if (TextUtils.isEmpty(url)) {
                    // Nếu xóa URL thì về ảnh mặc định
                    imgPreview.setImageResource(R.drawable.ic_launcher_background);
                    return;
                }

                Glide.with(ProductEntryActivity.this)
                        .load(url)
                        .placeholder(R.drawable.ic_launcher_background)
                        .error(R.drawable.ic_launcher_background)
                        .into(imgPreview);
            }
        });

        btnClear.setOnClickListener(v -> clearForm());

        btnSubmit.setOnClickListener(v -> submitProduct());
    }

    // =========================================================
    // 4. LOAD CATEGORY / BRAND TỪ API + THÊM OPTION "KHÁC"
    // =========================================================

    private void loadCategoriesFromApi() {
        ApiClient.get().getCategories().enqueue(new Callback<List<CategoryDto>>() {
            @Override
            public void onResponse(Call<List<CategoryDto>> call, Response<List<CategoryDto>> response) {
                if (!response.isSuccessful() || response.body() == null) return;

                categoryOptions.clear();
                for (CategoryDto c : response.body()) {
                    categoryOptions.add(new OptionItem(c.id, c.name));
                }
                // thêm dòng Khác
                categoryOptions.add(new OptionItem(ID_KHAC, "Khác"));

                ArrayAdapter<String> catAdapter = new ArrayAdapter<>(
                        ProductEntryActivity.this,
                        android.R.layout.simple_dropdown_item_1line,
                        toNames(categoryOptions)
                );
                autoCategory.setAdapter(catAdapter);

                autoCategory.setOnItemClickListener((parent, view, position, id) -> {
                    OptionItem opt = categoryOptions.get(position);
                    if (opt.id == ID_KHAC) {
                        showNewCategoryDialog();
                    } else {
                        selectedCategoryId = opt.id;
                        autoCategory.setText(opt.name, false);
                    }
                });
            }

            @Override
            public void onFailure(Call<List<CategoryDto>> call, Throwable t) {
                // có thể Toast nhẹ nếu muốn
            }
        });
    }

    private void loadBrandsFromApi() {
        ApiClient.get().getBrands().enqueue(new Callback<List<BrandDto>>() {
            @Override
            public void onResponse(Call<List<BrandDto>> call, Response<List<BrandDto>> response) {
                if (!response.isSuccessful() || response.body() == null) return;

                brandOptions.clear();
                for (BrandDto b : response.body()) {
                    brandOptions.add(new OptionItem(b.id, b.ten));
                }
                // thêm dòng Khác
                brandOptions.add(new OptionItem(ID_KHAC, "Khác (tự nhập)"));

                ArrayAdapter<String> brandAdapter = new ArrayAdapter<>(
                        ProductEntryActivity.this,
                        android.R.layout.simple_dropdown_item_1line,
                        toNames(brandOptions)
                );
                autoBrand.setAdapter(brandAdapter);

                autoBrand.setOnItemClickListener((parent, view, position, id) -> {
                    OptionItem opt = brandOptions.get(position);
                    if (opt.id == ID_KHAC) {
                        showNewBrandDialog();
                    } else {
                        selectedBrandId = opt.id;
                        autoBrand.setText(opt.name, false);
                    }
                });

            }

            @Override
            public void onFailure(Call<List<BrandDto>> call, Throwable t) {
                // có thể Toast nhẹ nếu muốn
            }
        });
    }

    private List<String> toNames(List<OptionItem> list) {
        List<String> names = new ArrayList<>();
        for (OptionItem o : list) {
            names.add(o.name);
        }
        return names;
    }

    // =========================================================
    // 5. CLEAR + SUBMIT
    // =========================================================

    private void clearForm() {
        etName.setText("");
        etPrice.setText("");
        etQuantity.setText("10");
        etImageUrl.setText("");
        etDescription.setText("");
        imgPreview.setImageResource(R.drawable.ic_launcher_background);

        autoBrand.setText("");
        autoBrand.setHint("Thương hiệu");
        autoCategory.setText("");
        autoCategory.setHint("Loại SP");

        selectedBrandId = null;
        selectedCategoryId = null;
    }

    private void submitProduct() {
        String name  = etName.getText().toString().trim();
        String priceStr = etPrice.getText().toString().trim();
        String qtyStr   = etQuantity.getText().toString().trim();
        String imageUrl = etImageUrl.getText().toString().trim();
        String desc     = etDescription.getText().toString().trim();

        String brandText    = autoBrand.getText().toString().trim();
        String categoryText = autoCategory.getText().toString().trim();

        // ===== Validate đơn giản =====
        if (name.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập tên sản phẩm", Toast.LENGTH_SHORT).show();
            return;
        }
        if (priceStr.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập giá", Toast.LENGTH_SHORT).show();
            return;
        }
        if (qtyStr.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập số lượng", Toast.LENGTH_SHORT).show();
            return;
        }
        if (categoryText.isEmpty()) {
            Toast.makeText(this, "Vui lòng chọn / nhập Loại sản phẩm", Toast.LENGTH_SHORT).show();
            return;
        }
        if (brandText.isEmpty()) {
            Toast.makeText(this, "Vui lòng chọn / nhập Thương hiệu", Toast.LENGTH_SHORT).show();
            return;
        }

        long price;
        int quantity;
        try {
            price = Long.parseLong(priceStr);
            quantity = Integer.parseInt(qtyStr);
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Giá hoặc số lượng không hợp lệ", Toast.LENGTH_SHORT).show();
            return;
        }

        // ===== Build DTO gửi backend =====
        AdminProductDto dto = new AdminProductDto();
        dto.ten      = name;
        dto.gia      = price;
        dto.tonKho   = quantity;
        dto.imageUrl = imageUrl;
        dto.moTaNgan = desc;

        // Loại sản phẩm:
        if (selectedCategoryId != null) {
            dto.loaiId   = selectedCategoryId;
            dto.loaiTen  = null;            // backend dùng loaiId
        } else {
            dto.loaiId   = null;
            dto.loaiTen  = categoryText;    // backend sẽ findByTen hoặc tạo mới
        }

        // Thương hiệu:
        if (selectedBrandId != null) {
            dto.thuongHieuId  = selectedBrandId;
            dto.thuongHieuTen = null;
        } else {
            dto.thuongHieuId  = null;
            dto.thuongHieuTen = brandText;
        }

        // ===== Gọi API =====
        setLoading(true);

        ApiClient.get().createProduct(dto).enqueue(new Callback<AdminProductDto>() {
            @Override
            public void onResponse(Call<AdminProductDto> call, Response<AdminProductDto> response) {
                setLoading(false);
                if (response.isSuccessful() && response.body() != null) {
                    Toast.makeText(ProductEntryActivity.this,
                            "Thêm sản phẩm thành công!", Toast.LENGTH_SHORT).show();
                    finish();  // quay lại màn list, onResume bên AdminProductActivity sẽ load lại
                } else {
                    Toast.makeText(ProductEntryActivity.this,
                            "Thêm sản phẩm thất bại: " + response.code(),
                            Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<AdminProductDto> call, Throwable t) {
                setLoading(false);
                Toast.makeText(ProductEntryActivity.this,
                        "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void showNewBrandDialog() {
        final EditText input = new EditText(this);
        input.setHint("Ví dụ: Xtreme Gaming");
        input.setPadding(32, 16, 32, 16);

        new AlertDialog.Builder(this)
                .setTitle("Thêm thương hiệu mới")
                .setMessage("Nhập tên thương hiệu bạn muốn tạo:")
                .setView(input)
                .setPositiveButton("Lưu", (dialog, which) -> {
                    String text = input.getText().toString().trim();
                    if (text.isEmpty()) {
                        Toast.makeText(this, "Tên thương hiệu không được để trống", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    selectedBrandId = null; // dùng Ten, không dùng Id
                    autoBrand.setText(text, false); // hiển thị lại cho rõ
                })
                .setNegativeButton("Hủy", null)
                .show();
    }

    private void showNewCategoryDialog() {
        final EditText input = new EditText(this);
        input.setHint("Ví dụ: Bàn phím cơ");
        input.setPadding(32, 16, 32, 16);

        new AlertDialog.Builder(this)
                .setTitle("Thêm loại sản phẩm mới")
                .setMessage("Nhập tên loại sản phẩm bạn muốn tạo:")
                .setView(input)
                .setPositiveButton("Lưu", (dialog, which) -> {
                    String text = input.getText().toString().trim();
                    if (text.isEmpty()) {
                        Toast.makeText(this, "Tên loại sản phẩm không được để trống", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    selectedCategoryId = null; // dùng Ten, không dùng Id
                    autoCategory.setText(text, false);
                })
                .setNegativeButton("Hủy", null)
                .show();
    }

    private void setLoading(boolean loading) {
        if (loading) {
            btnSubmit.setEnabled(false);
            btnSubmit.setText("Đang lưu...");
        } else {
            btnSubmit.setEnabled(true);
            btnSubmit.setText("Lưu sản phẩm");
        }
    }

    // class nhỏ để map id + name
    private static class OptionItem {
        long id;
        String name;

        OptionItem(long id, String name) {
            this.id = id;
            this.name = name;
        }
    }
}
