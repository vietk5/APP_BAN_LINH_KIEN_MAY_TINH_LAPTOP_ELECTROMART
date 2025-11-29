package com.example.baitap01_nhom6_ui_login_register_forgetpass.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.content.Intent;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.baitap01_nhom6_ui_login_register_forgetpass.R;
import com.example.baitap01_nhom6_ui_login_register_forgetpass.adapters.ProductAdapter;
import com.example.baitap01_nhom6_ui_login_register_forgetpass.models.Product;
import com.example.baitap01_nhom6_ui_login_register_forgetpass.models.dto.ProductDto;
import com.example.baitap01_nhom6_ui_login_register_forgetpass.remote.ApiClient;
import com.example.baitap01_nhom6_ui_login_register_forgetpass.util.BottomNavHelper;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SearchActivity extends AppCompatActivity {

    private EditText edtKeyword, edtMinPrice, edtMaxPrice;
    private Spinner spnCategory, spnBrand;
    private Button btnApplyFilter;
    private RecyclerView recyclerResults;
    private TextView tvResultCount;
    private ImageView btnBack, btnSearch;

    private final List<ProductDto> allProducts = new ArrayList<>();
    private final List<Product> displayList = new ArrayList<>();
    private ProductAdapter adapter;

    private final NumberFormat priceFormat =
            NumberFormat.getInstance(new Locale("vi", "VN"));

    // dữ liệu spinner
    private final String[] categories = new String[]{
            "Tất cả",
            "Laptop",
            "PC",
            "Tai nghe",
            "Màn hình",
            "Bàn phím",
            "Chuột",
            "Loa"
    };
    private List<String> brandOptions = new ArrayList<>(); // sẽ fill sau khi load products

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.header), (v, insets) -> {
            Insets bars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(v.getPaddingLeft(), bars.top, v.getPaddingRight(), v.getPaddingBottom());
            return insets;
        });
        initViews();
        setupSystemBarsPadding();
        setupRecycler();
        setupCategorySpinner();
        setupEvents();

        // lấy keyword từ HomeActivity (nếu có)
        String keyword = getIntent().getStringExtra("keyword");
        if (keyword != null) {
            edtKeyword.setText(keyword);
        }

        // load danh sách sản phẩm từ API rồi apply filter
        loadAllProducts();
        BottomNavHelper.setup(this, "CONSULT");
    }

    private void initViews() {
        edtKeyword   = findViewById(R.id.edtKeyword);
        edtMinPrice  = findViewById(R.id.edtMinPrice);
        edtMaxPrice  = findViewById(R.id.edtMaxPrice);
        spnCategory  = findViewById(R.id.spnCategory);
        spnBrand     = findViewById(R.id.spnBrand);
        btnApplyFilter = findViewById(R.id.btnApplyFilter);
        recyclerResults = findViewById(R.id.recyclerResults);
        tvResultCount = findViewById(R.id.tvResultCount);
        btnBack      = findViewById(R.id.btnBack);
        btnSearch    = findViewById(R.id.btnSearch);
    }

    private void setupSystemBarsPadding() {
        View toolbar = findViewById(R.id.toolbarSearch);
        if (toolbar != null) {
            ViewCompat.setOnApplyWindowInsetsListener(toolbar, (v, insets) -> {
                Insets bars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
                v.setPadding(
                        v.getPaddingLeft(),
                        bars.top,
                        v.getPaddingRight(),
                        v.getPaddingBottom()
                );
                return insets;
            });
        }
    }

    private void setupRecycler() {
        recyclerResults.setLayoutManager(new GridLayoutManager(this, 2));
        adapter = new ProductAdapter(displayList);
        recyclerResults.setAdapter(adapter);
    }

    private void setupCategorySpinner() {
        ArrayAdapter<String> catAdapter =
                new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, categories);
        catAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnCategory.setAdapter(catAdapter);
    }

    private void setupBrandSpinner() {
        // brandOptions đã được fill sau khi loadAllProducts()
        ArrayAdapter<String> brandAdapter =
                new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, brandOptions);
        brandAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnBrand.setAdapter(brandAdapter);
    }

    private void setupEvents() {
        btnBack.setOnClickListener(v -> finish());

        btnApplyFilter.setOnClickListener(v -> applyFilters());

        btnSearch.setOnClickListener(v -> applyFilters());

        // khi đổi category / brand cũng có thể tự apply luôn
        spnCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                applyFilters();
            }
            @Override public void onNothingSelected(AdapterView<?> parent) {}
        });

        spnBrand.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                applyFilters();
            }
            @Override public void onNothingSelected(AdapterView<?> parent) {}
        });
        Button btnClearFilter = findViewById(R.id.btnClearFilter);

        btnClearFilter.setOnClickListener(v -> {
            edtKeyword.setText("");
            edtMinPrice.setText("");
            edtMaxPrice.setText("");

            spnCategory.setSelection(0);
            spnBrand.setSelection(0);
            applyFilters();
        });
    }

    private void loadAllProducts() {
        ApiClient.get().getProducts().enqueue(new Callback<List<ProductDto>>() {
            @Override
            public void onResponse(Call<List<ProductDto>> call, Response<List<ProductDto>> response) {
                if (!response.isSuccessful() || response.body() == null) {
                    Toast.makeText(SearchActivity.this, "Không tải được danh sách sản phẩm", Toast.LENGTH_SHORT).show();
                    return;
                }
                allProducts.clear();
                allProducts.addAll(response.body());

                // build danh sách brand cho spinner
                buildBrandOptions();
                setupBrandSpinner();

                // apply filter lần đầu
                applyFilters();
            }

            @Override
            public void onFailure(Call<List<ProductDto>> call, Throwable t) {
                Toast.makeText(SearchActivity.this,
                        "Lỗi kết nối: " + t.getMessage(),
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void buildBrandOptions() {
        Set<String> brands = new HashSet<>();
        for (ProductDto d : allProducts) {
            if (d.brand != null && !d.brand.trim().isEmpty()) {
                brands.add(d.brand.trim());
            }
        }
        brandOptions.clear();
        brandOptions.add("Tất cả hãng");
        brandOptions.addAll(brands);
    }

    private void applyFilters() {
        String keyword = edtKeyword.getText().toString().trim().toLowerCase(Locale.ROOT);

        Long minPrice = parsePriceInput(edtMinPrice);
        Long maxPrice = parsePriceInput(edtMaxPrice);

        String selectedCategory = (String) spnCategory.getSelectedItem();
        if (selectedCategory == null) selectedCategory = "Tất cả";

        String selectedBrand = (String) spnBrand.getSelectedItem();
        if (selectedBrand == null) selectedBrand = "Tất cả hãng";

        displayList.clear();

        for (ProductDto d : allProducts) {
            String name  = d.name  != null ? d.name  : "";
            String brand = d.brand != null ? d.brand : "";
            long price   = d.price; // kiểu long theo backend

            String key = (name + " " + brand).toLowerCase(Locale.ROOT);

            // 1) Keyword
            boolean matchKeyword = keyword.isEmpty()
                    || name.toLowerCase(Locale.ROOT).contains(keyword)
                    || brand.toLowerCase(Locale.ROOT).contains(keyword);

            // 2) Category
            boolean matchCategory = matchCategory(selectedCategory, key);

            // 3) Brand
            boolean matchBrand = "Tất cả hãng".equals(selectedBrand)
                    || brand.equalsIgnoreCase(selectedBrand);

            // 4) Price
            boolean matchPrice = true;
            if (minPrice != null && price < minPrice) matchPrice = false;
            if (maxPrice != null && price > maxPrice) matchPrice = false;

            if (matchKeyword && matchCategory && matchBrand && matchPrice) {
                Product ui = new Product(
                        d.id,
                        d.name,
                        vnd(price),
                        d.imageUrl
                );
                displayList.add(ui);
            }
        }

        adapter.notifyDataSetChanged();
        tvResultCount.setText(
                String.format(Locale.getDefault(), "Tìm thấy %d sản phẩm", displayList.size())
        );
    }

    /**
     * Parse giá: cho phép nhập 10–15 => hiểu là 10–15 triệu
     * Nếu người dùng nhập 10000000 thì giữ nguyên.
     */
    @Nullable
    private Long parsePriceInput(EditText edt) {
        String s = edt.getText().toString().trim()
                .replace(".", "")
                .replace(",", "");
        if (s.isEmpty()) return null;
        try {
            long value = Long.parseLong(s);
            // nếu nhập < 1000 coi như đã nhập đơn vị "triệu"
            if (value < 1000) {
                value = value * 1_000_000L;
            }
            return value;
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private boolean matchCategory(String category, String keyLower) {
        keyLower = keyLower.toLowerCase(Locale.ROOT);

        if ("Tất cả".equals(category)) return true;

        switch (category) {
            case "Laptop":
                return keyLower.contains("laptop")
                        || keyLower.contains("notebook")
                        || keyLower.contains("macbook");
            case "PC":
                return keyLower.contains("pc")
                        || keyLower.contains("case");
            case "Tai nghe":
                return keyLower.contains("tai nghe")
                        || keyLower.contains("headset");
            case "Màn hình":
                return keyLower.contains("màn hình")
                        || keyLower.contains("monitor");
            case "Bàn phím":
                return keyLower.contains("bàn phím")
                        || keyLower.contains("keyboard");
            case "Chuột":
                return keyLower.contains("chuột")
                        || keyLower.contains("mouse");
            case "Loa":
                return keyLower.contains("loa")
                        || keyLower.contains("speaker");
            default:
                return true;
        }
    }

    private String vnd(long price) {
        return priceFormat.format(price) + " đ";
    }
}
