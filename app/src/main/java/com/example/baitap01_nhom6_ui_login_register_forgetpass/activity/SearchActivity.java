package com.example.baitap01_nhom6_ui_login_register_forgetpass.activity;

import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.baitap01_nhom6_ui_login_register_forgetpass.R;
import com.example.baitap01_nhom6_ui_login_register_forgetpass.adapters.ProductAdapter;
import com.example.baitap01_nhom6_ui_login_register_forgetpass.models.Product;
import com.example.baitap01_nhom6_ui_login_register_forgetpass.models.dto.ProductDto;
import com.example.baitap01_nhom6_ui_login_register_forgetpass.remote.ApiClient;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SearchActivity extends AppCompatActivity {

    private RecyclerView recyclerSearch;
    private TextView tvTitle;
    private final List<Product> searchData = new ArrayList<>();
    private ProductAdapter searchAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_search);

        recyclerSearch = findViewById(R.id.recyclerSearch);
        tvTitle = findViewById(R.id.tvSearchTitle);

        recyclerSearch.setLayoutManager(new GridLayoutManager(this, 2));
        searchAdapter = new ProductAdapter(searchData);
        recyclerSearch.setAdapter(searchAdapter);

        String keyword = getIntent().getStringExtra("keyword");
        if (keyword == null) keyword = "";
        tvTitle.setText("Kết quả cho: \"" + keyword + "\"");

        searchProducts(keyword);
    }

    private void searchProducts(String keyword) {
        final String kwLower = keyword.toLowerCase(Locale.ROOT);

        ApiClient.get().getProducts().enqueue(new Callback<List<ProductDto>>() {
            @Override
            public void onResponse(Call<List<ProductDto>> call, Response<List<ProductDto>> res) {
                if (!res.isSuccessful() || res.body() == null) {
                    Toast.makeText(SearchActivity.this, "Lỗi API", Toast.LENGTH_SHORT).show();
                    return;
                }

                searchData.clear();
                for (ProductDto d : res.body()) {
                    String name = d.name != null ? d.name : "";
                    String brand = d.brand != null ? d.brand : "";
                    String text = (name + " " + brand).toLowerCase(Locale.ROOT);

                    if (text.contains(kwLower)) {
                        searchData.add(new Product(
                                d.id,
                                d.name,
                                vnd(d.price),
                                d.imageUrl
                        ));
                    }
                }

                if (searchData.isEmpty()) {
                    Toast.makeText(SearchActivity.this,
                            "Không tìm thấy sản phẩm phù hợp",
                            Toast.LENGTH_SHORT).show();
                }
                searchAdapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(Call<List<ProductDto>> call, Throwable t) {
                Toast.makeText(SearchActivity.this,
                        "Không gọi được API: " + t.getMessage(),
                        Toast.LENGTH_LONG).show();
            }
        });
    }

    private String vnd(long price) {
        return NumberFormat.getInstance(new Locale("vi", "VN"))
                .format(price) + " đ";
    }
}
