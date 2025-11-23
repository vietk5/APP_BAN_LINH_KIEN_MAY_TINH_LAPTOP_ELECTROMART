package com.example.baitap01_nhom6_ui_login_register_forgetpass.activity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
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
import com.example.baitap01_nhom6_ui_login_register_forgetpass.models.dto.ProductDto;
import com.example.baitap01_nhom6_ui_login_register_forgetpass.remote.ApiService;
import com.example.baitap01_nhom6_ui_login_register_forgetpass.remote.ApiClient;
import com.example.baitap01_nhom6_ui_login_register_forgetpass.util.PriceFormatter;

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
    List<Comment> commentList = new ArrayList<>();
    EditText edtComment;
    Button btnSendComment;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_detail);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.rootLayout), (v, insets) -> {
            Insets statusBars = insets.getInsets(WindowInsetsCompat.Type.statusBars());
            v.setPadding(0, statusBars.top, 0, 0);
            return insets;
        });

        api = ApiClient.get();

        productId = getIntent().getLongExtra("product_id", -1);

        initViews();
        loadProductDetail();
        loadRelatedProducts();
        loadComments();
        btnSendComment.setOnClickListener(v -> sendComment());

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


    }

    private void loadProductDetail() {
        api.getProductById(productId).enqueue(new Callback<ProductDto>() {
            @Override
            public void onResponse(Call<ProductDto> call, Response<ProductDto> res) {
                if (!res.isSuccessful() || res.body() == null) return;

                ProductDto p = res.body();
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
                    list.add(new Product(dto.getId(),
                            dto.getName(),
                            PriceFormatter.vnd(dto.getPrice()),
                            dto.getImageUrl()));
                }

                rvRelated.setAdapter(new ProductAdapter(list));
            }

            @Override public void onFailure(Call<List<ProductDto>> call, Throwable t) {}
        });
    }

    private void loadComments() {
        api.getComments(productId).enqueue(new Callback<List<Comment>>() {
            @Override
            public void onResponse(Call<List<Comment>> call, Response<List<Comment>> res) {
                if(res.isSuccessful() && res.body() != null){
                    commentList.clear();
                    commentList.addAll(res.body());
                    rvComments.setAdapter(new CommentAdapter(commentList));
                }
            }

            @Override
            public void onFailure(Call<List<Comment>> call, Throwable t) {
            }
        });
    }
    private void sendComment(){
        String content = edtComment.getText().toString().trim();
        if(content.isEmpty()) return;

        Comment c = new Comment(productId, "User123", content, 5);

        api.postComment(c).enqueue(new Callback<Comment>() {
            @Override
            public void onResponse(Call<Comment> call, Response<Comment> res) {
                if(res.isSuccessful()){
                    edtComment.setText("");
                    loadComments(); // reload lại danh sách
                }
            }

            @Override
            public void onFailure(Call<Comment> call, Throwable t) {}
        });
    }

}
