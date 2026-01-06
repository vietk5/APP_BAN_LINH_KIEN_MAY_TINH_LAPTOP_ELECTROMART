package com.example.baitap01_nhom6_ui_login_register_forgetpass.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.baitap01_nhom6_ui_login_register_forgetpass.R;
import com.example.baitap01_nhom6_ui_login_register_forgetpass.adapters.CapturedImageAdapter;
import com.example.baitap01_nhom6_ui_login_register_forgetpass.adapters.SearchResultProductAdapter;
import com.example.baitap01_nhom6_ui_login_register_forgetpass.fragment.CartFragment;
import com.example.baitap01_nhom6_ui_login_register_forgetpass.models.CapturedImage;
import com.example.baitap01_nhom6_ui_login_register_forgetpass.models.Product;
import com.example.baitap01_nhom6_ui_login_register_forgetpass.models.dto.ApiResponse;
import com.example.baitap01_nhom6_ui_login_register_forgetpass.remote.ApiClient;
import com.example.baitap01_nhom6_ui_login_register_forgetpass.remote.ApiService;
import com.example.baitap01_nhom6_ui_login_register_forgetpass.util.ImageUtils;
import com.example.baitap01_nhom6_ui_login_register_forgetpass.util.SharedPrefManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ImageSearchResultActivity extends AppCompatActivity implements
        SearchResultProductAdapter.OnProductSelectionChangedListener {

    private static final String TAG = "ImageSearchResult";

    private ImageButton btnBack, btnCart;
    private TextView tvCartBadge, tvResultCount, btnViewImage;
    private RecyclerView rvSearchImages, rvProducts;
    private LinearLayout emptyState;
    private Button btnSearchAgain, btnBuyNow;

    private CapturedImageAdapter imageAdapter;
    private SearchResultProductAdapter productAdapter;
    private ApiService apiService;
    private SharedPrefManager sharedPref;

    private ArrayList<String> base64Images;
    private List<Product> searchResults;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_search_result);

        initViews();
        setupRecyclerViews();
        setupClickListeners();
        loadCapturedImages();
        searchProducts();

    }

    private void initViews() {
        btnBack = findViewById(R.id.btn_back);
        btnCart = findViewById(R.id.btn_cart);
        tvCartBadge = findViewById(R.id.tv_cart_badge);
        tvResultCount = findViewById(R.id.tv_result_count);
        btnViewImage = findViewById(R.id.btn_view_image);
        rvSearchImages = findViewById(R.id.rv_search_images);
        rvProducts = findViewById(R.id.rv_products);
        emptyState = findViewById(R.id.empty_state);
        btnSearchAgain = findViewById(R.id.btn_search_again);
        btnBuyNow = findViewById(R.id.btn_buy_now);

        apiService = ApiClient.get();
        sharedPref = new SharedPrefManager(this);
    }

    private void setupRecyclerViews() {
        // Search images recycler view
        imageAdapter = new CapturedImageAdapter(position -> {
            Toast.makeText(this, "Xem ảnh " + (position + 1), Toast.LENGTH_SHORT).show();
        });
        rvSearchImages.setLayoutManager(
                new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        rvSearchImages.setAdapter(imageAdapter);

        // Products recycler view
        productAdapter = new SearchResultProductAdapter(this);
        rvProducts.setLayoutManager(new LinearLayoutManager(this));
        rvProducts.setAdapter(productAdapter);
    }

    private void setupClickListeners() {
        btnBack.setOnClickListener(v -> finish());

        btnCart.setOnClickListener(v -> {
            Intent intent = new Intent(this, CartFragment.class);
            startActivity(intent);
        });

        btnViewImage.setOnClickListener(v -> {
            Toast.makeText(this, "Xem tất cả ảnh", Toast.LENGTH_SHORT).show();
        });

        btnSearchAgain.setOnClickListener(v -> {
            Intent intent = new Intent(this, CameraActivity.class);
            startActivity(intent);
            finish();
        });

        btnBuyNow.setOnClickListener(v -> {
            addSelectedProductsToCart();
        });
    }

    private void loadCapturedImages() {
        base64Images = getIntent().getStringArrayListExtra("CAPTURED_IMAGES");
        if (base64Images != null && !base64Images.isEmpty()) {
            List<CapturedImage> images = new ArrayList<>();
            for (String base64 : base64Images) {
                CapturedImage image = new CapturedImage();
                image.setBitmap(ImageUtils.base64ToBitmap(base64));
                images.add(image);
            }
            imageAdapter.setImages(images);
        }
    }

    private void searchProducts() {
        if (base64Images == null || base64Images.isEmpty()) {
            showEmptyState();
            return;
        }

        // Show loading
        Toast.makeText(this, "Đang tìm kiếm sản phẩm...", Toast.LENGTH_SHORT).show();

        // Gọi API mới: /api/search/by-multiple-images
        Map<String, List<String>> request = new HashMap<>();
        request.put("imageBase64List", base64Images);

        apiService.searchByMultipleImages(request)
                .enqueue(new Callback<ApiResponse<List<Product>>>() {
                    @Override
                    public void onResponse(Call<ApiResponse<List<Product>>> call,
                                           Response<ApiResponse<List<Product>>> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            ApiResponse<List<Product>> apiResponse = response.body();

                            Log.d(TAG, "Response: success=" + apiResponse.isSuccess() +
                                    ", message=" + apiResponse.getMessage());

                            if (apiResponse.isSuccess() && apiResponse.getData() != null) {
                                searchResults = apiResponse.getData();
                                displayResults(searchResults);
                            } else {
                                String errorMsg = apiResponse.getMessage() != null
                                        ? apiResponse.getMessage()
                                        : "Không tìm thấy sản phẩm";
                                Toast.makeText(ImageSearchResultActivity.this,
                                        errorMsg, Toast.LENGTH_SHORT).show();
                                showEmptyState();
                            }
                        } else {
                            Log.e(TAG, "Search failed: " + response.code());
                            Toast.makeText(ImageSearchResultActivity.this,
                                    "Lỗi tìm kiếm: " + response.code(), Toast.LENGTH_SHORT).show();
                            showEmptyState();
                        }
                    }

                    @Override
                    public void onFailure(Call<ApiResponse<List<Product>>> call, Throwable t) {
                        Log.e(TAG, "Search error: " + t.getMessage(), t);
                        Toast.makeText(ImageSearchResultActivity.this,
                                "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                        showEmptyState();
                    }
                });
    }

    private void displayResults(List<Product> products) {
        if (products.isEmpty()) {
            showEmptyState();
        } else {
            emptyState.setVisibility(View.GONE);
            rvProducts.setVisibility(View.VISIBLE);

            productAdapter.setProducts(products);

            String message = "Tìm được " + products.size() + " sản phẩm";
            if (base64Images.size() > 1) {
                message += " từ " + base64Images.size() + " ảnh";
            }
            tvResultCount.setText(message);
        }
    }

    private void showEmptyState() {
        emptyState.setVisibility(View.VISIBLE);
        rvProducts.setVisibility(View.GONE);
        tvResultCount.setText("Không tìm thấy sản phẩm");
    }

    @Override
    public void onSelectionChanged(int selectedCount) {
        if (selectedCount > 0) {
            tvCartBadge.setText(String.valueOf(selectedCount));
            tvCartBadge.setVisibility(View.VISIBLE);
            btnBuyNow.setEnabled(true);
            btnBuyNow.setAlpha(1.0f);
        } else {
            tvCartBadge.setVisibility(View.GONE);
            btnBuyNow.setEnabled(false);
            btnBuyNow.setAlpha(0.5f);
        }
    }

    private void addSelectedProductsToCart() {
        Map<Long, Integer> selectedProducts = productAdapter.getSelectedProducts();

        if (selectedProducts.isEmpty()) {
            Toast.makeText(this, "Vui lòng chọn sản phẩm", Toast.LENGTH_SHORT).show();
            return;
        }

        Toast.makeText(this, "Đã thêm " + selectedProducts.size() + " sản phẩm vào giỏ hàng",
                Toast.LENGTH_SHORT).show();

        // TODO: Implement add to cart API call
        Intent intent = new Intent(this, CartFragment.class);
        startActivity(intent);
    }
}