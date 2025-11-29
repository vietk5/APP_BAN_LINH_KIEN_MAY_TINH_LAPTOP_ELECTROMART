package com.example.baitap01_nhom6_ui_login_register_forgetpass.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.baitap01_nhom6_ui_login_register_forgetpass.R;
import com.example.baitap01_nhom6_ui_login_register_forgetpass.adapters.CommentAdapter;
import com.example.baitap01_nhom6_ui_login_register_forgetpass.adapters.ProductAdapter;
import com.example.baitap01_nhom6_ui_login_register_forgetpass.models.Comment;
import com.example.baitap01_nhom6_ui_login_register_forgetpass.models.Product;
import com.example.baitap01_nhom6_ui_login_register_forgetpass.models.dto.CartRequest;
import com.example.baitap01_nhom6_ui_login_register_forgetpass.models.dto.CheckoutItem;
import com.example.baitap01_nhom6_ui_login_register_forgetpass.models.dto.ProductDto;
import com.example.baitap01_nhom6_ui_login_register_forgetpass.models.dto.RatingSummary;
import com.example.baitap01_nhom6_ui_login_register_forgetpass.remote.ApiClient;
import com.example.baitap01_nhom6_ui_login_register_forgetpass.remote.ApiService;
import com.example.baitap01_nhom6_ui_login_register_forgetpass.singleton.CartManager;
import com.example.baitap01_nhom6_ui_login_register_forgetpass.util.PriceFormatter;
import com.example.baitap01_nhom6_ui_login_register_forgetpass.util.SharedPrefManager;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProductDetailActivity extends AppCompatActivity {

    private ApiService api;
    private ImageView img;
    private TextView txtName, txtPrice, txtDesc, txtSpecs;
    private RecyclerView rvRelated, rvComments;

    private long productId;
    private Product currentProduct;
    private SharedPrefManager pref;

    private List<Comment> commentList = new ArrayList<>();
    private EditText edtComment;
    private Button btnSendComment, btnBuyNow, btnAddToCart;
    private RatingBar ratingBar;
    private TextView txtRatingAvg;
    private LinearLayout ratingBars;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_detail);

        // đẩy layout xuống khỏi status bar
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.rootLayout), (v, insets) -> {
            Insets statusBars = insets.getInsets(WindowInsetsCompat.Type.statusBars());
            v.setPadding(0, statusBars.top, 0, 0);
            return insets;
        });

        api = ApiClient.get();
        pref = new SharedPrefManager(this);
        productId = getIntent().getLongExtra("product_id", -1);

        initViews();

        // ⭐ KHÔI PHỤC COMMENT VÀ RATING SAU KHI LOGIN QUAY LẠI
        String draft = getIntent().getStringExtra("commentDraft");
        int draftRating = getIntent().getIntExtra("ratingDraft", 0);

        if (draft != null) {
            edtComment.setText(draft);
        }
        if (draftRating > 0) {
            ratingBar.setRating(draftRating);
        }

        loadProductDetail();
        loadRelatedProducts();
        loadComments();
        loadRatingSummary();

        btnSendComment.setOnClickListener(v -> sendComment());
        btnBuyNow.setOnClickListener(v -> handleBuyNow());
        btnAddToCart.setOnClickListener(v -> handleAddToCart());
    }

    private void initViews() {
        img = findViewById(R.id.imgProduct);
        txtName = findViewById(R.id.txtName);
        txtPrice = findViewById(R.id.txtPrice);
        txtDesc = findViewById(R.id.txtShortDesc);
        txtSpecs = findViewById(R.id.txtSpecs);

        rvRelated = findViewById(R.id.rvRelated);
        rvRelated.setLayoutManager(new LinearLayoutManager(this, RecyclerView.HORIZONTAL, false));

        rvComments = findViewById(R.id.rvComments);
        rvComments.setLayoutManager(new LinearLayoutManager(this));

        edtComment = findViewById(R.id.edtComment);
        btnSendComment = findViewById(R.id.btnSendComment);
        btnBuyNow = findViewById(R.id.btnBuyNow);
        btnAddToCart = findViewById(R.id.btnAddToCart);
        ratingBar = findViewById(R.id.ratingBar);
        txtRatingAvg = findViewById(R.id.txtRatingAvg);
        ratingBars   = findViewById(R.id.ratingBars);
    }

    // ================== BUY NOW ==================
    private void handleBuyNow() {
        if (!pref.isLoggedIn()) {
            showLoginDialog();
            return;
        }

        if (currentProduct == null) {
            Toast.makeText(this, "Không thể mua sản phẩm này", Toast.LENGTH_SHORT).show();
            return;
        }

        int userId = pref.getUserId();
        if (userId <= 0) {
            Toast.makeText(this, "Lỗi tài khoản, vui lòng đăng nhập lại", Toast.LENGTH_SHORT).show();
            return;
        }

        // Chuẩn bị CheckoutItem cho sản phẩm hiện tại
        long priceLong = 0L;
        try {
            priceLong = Long.parseLong(currentProduct.getPrice());
        } catch (NumberFormatException e) {
            // nếu lỗi parse thì để 0
        }

        ArrayList<CheckoutItem> checkoutItems = new ArrayList<>();
        checkoutItems.add(
                new CheckoutItem(
                        currentProduct.getId(),
                        currentProduct.getName(),
                        currentProduct.getImageUrl(),
                        priceLong,
                        1   // số lượng mặc định
                )
        );

        // Vẫn add vào giỏ để lưu DB
        CartRequest req = new CartRequest(userId, currentProduct.getId(), 1);

        api.addToCart(req).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> res) {
                if (res.isSuccessful()) {

                    // cache local nếu muốn
                    CartManager.getInstance().addProduct(currentProduct);

                    // ➜ Mở màn hình checkout với list "items"
                    Intent intent = new Intent(ProductDetailActivity.this, CheckoutActivity.class);
                    intent.putExtra("items", checkoutItems);
                    startActivity(intent);

                } else {
                    Toast.makeText(ProductDetailActivity.this,
                            "Không thể thêm vào giỏ (mã lỗi " + res.code() + ")",
                            Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(ProductDetailActivity.this,
                        "Lỗi kết nối: " + t.getMessage(),
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    // ================== ADD TO CART ==================
    private void handleAddToCart() {
        if (!pref.isLoggedIn()) {
            showLoginDialog();
            return;
        }

        if (currentProduct == null) {
            Toast.makeText(this, "Không thể thêm sản phẩm vào giỏ hàng", Toast.LENGTH_SHORT).show();
            return;
        }

        int userId = pref.getUserId();
        if (userId <= 0) {
            Toast.makeText(this, "Lỗi tài khoản, vui lòng đăng nhập lại", Toast.LENGTH_SHORT).show();
            return;
        }

        CartRequest req = new CartRequest(userId, currentProduct.getId(), 1);

        api.addToCart(req).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> res) {
                if (res.isSuccessful()) {
                    CartManager.getInstance().addProduct(currentProduct);

                    Toast.makeText(ProductDetailActivity.this,
                            "Đã thêm vào giỏ hàng",
                            Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(ProductDetailActivity.this,
                            "Không thể thêm vào giỏ (mã lỗi " + res.code() + ")",
                            Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(ProductDetailActivity.this,
                        "Lỗi kết nối: " + t.getMessage(),
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    // ================== LOAD DATA ==================
    private void loadProductDetail() {
        api.getProductById(productId).enqueue(new Callback<ProductDto>() {
            @Override
            public void onResponse(Call<ProductDto> call, Response<ProductDto> res) {
                if (!res.isSuccessful() || res.body() == null) return;

                ProductDto p = res.body();

                currentProduct = new Product(
                        p.getId(),
                        p.getName(),
                        String.valueOf(p.getPrice()),
                        p.getImageUrl()
                );

                txtName.setText(p.getName());
                txtPrice.setText(PriceFormatter.vnd(p.getPrice()));
                txtDesc.setText("Hàng mới 100%, bảo hành 24 tháng");
                txtSpecs.setText(p.getMoTaNgan());

                Glide.with(ProductDetailActivity.this)
                        .load(p.getImageUrl())
                        .into(img);
            }

            @Override
            public void onFailure(Call<ProductDto> call, Throwable t) {}
        });
    }

    private void loadRelatedProducts() {
        api.getRelated(productId).enqueue(new Callback<List<ProductDto>>() {
            @Override
            public void onResponse(Call<List<ProductDto>> call, Response<List<ProductDto>> res) {
                if (!res.isSuccessful() || res.body() == null) return;

                List<Product> list = new ArrayList<>();
                for (ProductDto dto : res.body()) {
                    list.add(new Product(
                            dto.getId(),
                            dto.getName(),
                            PriceFormatter.vnd(dto.getPrice()),
                            dto.getImageUrl()
                    ));
                }

                rvRelated.setAdapter(new ProductAdapter(list));
            }

            @Override
            public void onFailure(Call<List<ProductDto>> call, Throwable t) {}
        });
    }

    private void loadComments() {
        api.getComments(productId).enqueue(new Callback<List<Comment>>() {
            @Override
            public void onResponse(Call<List<Comment>> call, Response<List<Comment>> res) {
                if (res.isSuccessful() && res.body() != null) {
                    commentList.clear();
                    commentList.addAll(res.body());
                    rvComments.setAdapter(new CommentAdapter(commentList));
                }
            }

            @Override
            public void onFailure(Call<List<Comment>> call, Throwable t) {}
        });
    }

    // ================== COMMENT & RATING ==================
    private void sendComment() {
        if (!pref.isLoggedIn()) {
            showLoginDialog();
            return;
        }

        String content = edtComment.getText().toString().trim();
        if (content.isEmpty()) {
            edtComment.setError("Bạn chưa nhập bình luận");
            return;
        }

        int rating = (int) ratingBar.getRating();
        if (rating == 0) {
            Toast.makeText(this, "Vui lòng đánh giá số sao", Toast.LENGTH_SHORT).show();
            return;
        }

        Comment c = new Comment(
                productId,
                pref.getName(),
                content,
                rating
        );

        api.postComment(c).enqueue(new Callback<Comment>() {
            @Override
            public void onResponse(Call<Comment> call, Response<Comment> res) {
                if (res.isSuccessful()) {
                    edtComment.setText("");
                    ratingBar.setRating(0);
                    loadComments();
                    Toast.makeText(ProductDetailActivity.this,
                            "Đã gửi bình luận", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Comment> call, Throwable t) {}
        });
    }

    private void showLoginDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Cần đăng nhập")
                .setMessage("Bạn phải đăng nhập để thực hiện chức năng này.")
                .setPositiveButton("Đăng nhập", (dialog, which) -> {
                    Intent i = new Intent(this, LoginActivity.class);
                    i.putExtra("returnTo", "product_detail");
                    i.putExtra("productId", productId);
                    i.putExtra("commentDraft", edtComment.getText().toString());
                    i.putExtra("ratingDraft", (int) ratingBar.getRating());
                    startActivity(i);
                })
                .setNegativeButton("Hủy", null)
                .show();
    }

    private void loadRatingSummary() {
        api.getRatingSummary(productId).enqueue(new Callback<RatingSummary>() {
            @Override
            public void onResponse(Call<RatingSummary> call, Response<RatingSummary> res) {
                if (!res.isSuccessful() || res.body() == null) return;

                RatingSummary s = res.body();

                txtRatingAvg.setText(String.format("%.1f ★", s.avg));

                ratingBars.removeAllViews();

                addRatingRow(5, s.count5, s);
                addRatingRow(4, s.count4, s);
                addRatingRow(3, s.count3, s);
                addRatingRow(2, s.count2, s);
                addRatingRow(1, s.count1, s);
            }

            @Override
            public void onFailure(Call<RatingSummary> call, Throwable t) {}
        });
    }

    private void addRatingRow(int star, int count, RatingSummary sum) {
        View row = getLayoutInflater().inflate(R.layout.item_rating_bar, ratingBars, false);

        TextView tvStar = row.findViewById(R.id.tvStar);
        ProgressBar bar = row.findViewById(R.id.progressBar);
        TextView tvCount = row.findViewById(R.id.tvCount);

        tvStar.setText(star + "★");
        tvCount.setText(String.valueOf(count));

        int total = sum.count1 + sum.count2 + sum.count3 + sum.count4 + sum.count5;
        int percent = total == 0 ? 0 : (int) ((count * 100f) / total);

        bar.setMax(100);
        bar.setProgress(percent);

        ratingBars.addView(row);
    }
}
