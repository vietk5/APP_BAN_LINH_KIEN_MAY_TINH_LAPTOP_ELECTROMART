package com.example.baitap01_nhom6_ui_login_register_forgetpass.activity;

import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.baitap01_nhom6_ui_login_register_forgetpass.R;
import com.example.baitap01_nhom6_ui_login_register_forgetpass.adapters.CategoryAdapter;
import com.example.baitap01_nhom6_ui_login_register_forgetpass.adapters.ProductAdapter;
import com.example.baitap01_nhom6_ui_login_register_forgetpass.models.Category;
import com.example.baitap01_nhom6_ui_login_register_forgetpass.models.Product;
import com.example.baitap01_nhom6_ui_login_register_forgetpass.models.dto.ProductDto;
import com.example.baitap01_nhom6_ui_login_register_forgetpass.remote.ApiClient;
import com.example.baitap01_nhom6_ui_login_register_forgetpass.util.BottomNavHelper;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CategoryActivity extends AppCompatActivity
        implements CategoryAdapter.OnCategoryClickListener {

    private RecyclerView recyclerCategories;
    private RecyclerView recyclerProducts;
    private TextView tvProductTitle;

    private List<Category> categoryList = new ArrayList<>();
    private CategoryAdapter categoryAdapter;

    private final List<Product> allProducts = new ArrayList<>();
    private final List<Product> visibleProducts = new ArrayList<>();
    private ProductAdapter productAdapter;

    private final NumberFormat vndFormat =
            NumberFormat.getInstance(new Locale("vi", "VN"));

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category);

        // fix tai thỏ cho header
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.header), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(v.getPaddingLeft(), systemBars.top, v.getPaddingRight(), v.getPaddingBottom());
            return insets;
        });

        initViews();
        setupCategoryGrid();
        setupProductGrid();

        loadCategories();
        loadProductsFromApi();

        BottomNavHelper.setup(this, "CATEGORY");
    }

    private void initViews() {
        recyclerCategories = findViewById(R.id.recycler_categories);
        recyclerProducts   = findViewById(R.id.recycler_products);
        tvProductTitle     = findViewById(R.id.tv_product_title);
    }

    private void setupCategoryGrid() {
        recyclerCategories.setLayoutManager(new GridLayoutManager(this, 3));
        categoryAdapter = new CategoryAdapter(this, categoryList, this);
        recyclerCategories.setAdapter(categoryAdapter);
    }

    private void setupProductGrid() {
        recyclerProducts.setLayoutManager(new GridLayoutManager(this, 2));
        productAdapter = new ProductAdapter(visibleProducts);
        recyclerProducts.setAdapter(productAdapter);
    }

    private void loadCategories() {
        categoryList.clear();
        categoryList.add(new Category("Tất cả",    R.drawable.ic_category, "ALL"));
        categoryList.add(new Category("Laptop",    R.drawable.ic_laptop,       "LAPTOP"));
        categoryList.add(new Category("PC",        R.drawable.ic_pc,           "PC"));
        categoryList.add(new Category("Bàn phím",  R.drawable.ic_keyboard,     "KEYBOARD"));
        categoryList.add(new Category("Loa",       R.drawable.ic_speaker,      "SPEAKER"));
        categoryList.add(new Category("Chuột",     R.drawable.ic_mouse,        "MOUSE"));
        categoryList.add(new Category("Tai nghe",  R.drawable.ic_headphones,   "HEADPHONE"));
        categoryList.add(new Category("Màn hình",  R.drawable.ic_monitor,      "MONITOR"));

        categoryAdapter.notifyDataSetChanged();
        categoryAdapter.setSelectedPosition(0);  // chọn "Tất cả"
    }

    private void loadProductsFromApi() {
        ApiClient.get().getProducts().enqueue(new Callback<List<ProductDto>>() {
            @Override
            public void onResponse(Call<List<ProductDto>> call,
                                   Response<List<ProductDto>> response) {
                if (!response.isSuccessful() || response.body() == null) {
                    Toast.makeText(CategoryActivity.this,
                            "Không tải được danh sách sản phẩm",
                            Toast.LENGTH_SHORT).show();
                    return;
                }

                allProducts.clear();
                for (ProductDto d : response.body()) {
                    String priceStr = vndFormat.format(d.price) + " đ";
                    Product p = new Product(d.id, d.name, priceStr, d.imageUrl);
                    allProducts.add(p);
                }

                // mặc định hiển thị tất cả
                filterProductsByCategoryKey("ALL");
            }

            @Override
            public void onFailure(Call<List<ProductDto>> call, Throwable t) {
                Toast.makeText(CategoryActivity.this,
                        "Lỗi tải sản phẩm: " + t.getMessage(),
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    // ===== CLICK CATEGORY =====
    @Override
    public void onCategoryClick(Category category, int position) {
        categoryAdapter.setSelectedPosition(position);
        tvProductTitle.setText(category.getName());

        filterProductsByCategoryKey(category.getKey());
    }

    private void filterProductsByCategoryKey(String key) {
        visibleProducts.clear();

        if (allProducts.isEmpty()) {
            productAdapter.notifyDataSetChanged();
            return;
        }

        if ("ALL".equalsIgnoreCase(key)) {
            visibleProducts.addAll(allProducts);
        } else {
            for (Product p : allProducts) {
                if (matchCategory(p, key)) {
                    visibleProducts.add(p);
                }
            }
        }

        productAdapter.notifyDataSetChanged();
    }

    /**
     * Logic phân loại mềm theo tên sản phẩm.
     * Có thể chỉnh lại cho khớp data thực tế (theo loại, theo brand...).
     */
    private boolean matchCategory(Product p, String key) {
        if (p.getName() == null) return false;

        String name = p.getName().toLowerCase(Locale.ROOT);

        switch (key) {
            case "LAPTOP":
                return name.contains("laptop")
                        || name.contains("notebook")
                        || name.contains("macbook");
            case "PC":
                return name.contains("pc")
                        || name.contains("case")
                        || name.contains("desktop");
            case "KEYBOARD":
                return name.contains("bàn phím")
                        || name.contains("keyboard");
            case "SPEAKER":
                return name.contains("loa")
                        || name.contains("speaker");
            case "MOUSE":
                return name.contains("chuột")
                        || name.contains("mouse");
            case "HEADPHONE":
                return name.contains("tai nghe")
                        || name.contains("headphone")
                        || name.contains("headset");
            case "MONITOR":
                return name.contains("màn hình")
                        || name.contains("monitor");
            default:
                return true;
        }
    }
}
