// activity/HomeActivity.java
package com.example.baitap01_nhom6_ui_login_register_forgetpass.activity;

import  android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
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

public class HomeActivity extends AppCompatActivity {
    RecyclerView recyclerFlashSale, recyclerProducts, recyclerNewProducts, recyclerLaptops, recyclerHeadphones;

    private final List<Product> flashData = new ArrayList<>();
    private final List<Product> hotData   = new ArrayList<>();
    private final List<Product> newData   = new ArrayList<>();
    private final List<Product> lapData   = new ArrayList<>();
    private final List<Product> soundData = new ArrayList<>();

    private ProductAdapter flashAdp, hotAdp, newAdp, lapAdp, soundAdp;

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_home);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.header), (v,insets)->{
            Insets bars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(v.getPaddingLeft(), bars.top, v.getPaddingRight(), v.getPaddingBottom());
            return insets;
        });

        recyclerFlashSale  = findViewById(R.id.recyclerFlashSale);
        recyclerProducts   = findViewById(R.id.recyclerProducts);
        recyclerNewProducts= findViewById(R.id.recyclerNewProducts);
        recyclerLaptops    = findViewById(R.id.recyclerLaptops);
        recyclerHeadphones = findViewById(R.id.recyclerHeadphones);

        recyclerFlashSale.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        recyclerProducts.setLayoutManager(new GridLayoutManager(this, 2));
        recyclerNewProducts.setLayoutManager(new GridLayoutManager(this, 2));
        recyclerLaptops.setLayoutManager(new GridLayoutManager(this, 2));
        recyclerHeadphones.setLayoutManager(new GridLayoutManager(this, 2));

        flashAdp = new ProductAdapter(flashData);
        hotAdp   = new ProductAdapter(hotData);
        newAdp   = new ProductAdapter(newData);
        lapAdp   = new ProductAdapter(lapData);
        soundAdp = new ProductAdapter(soundData);

        recyclerFlashSale.setAdapter(flashAdp);
        recyclerProducts.setAdapter(hotAdp);
        recyclerNewProducts.setAdapter(newAdp);
        recyclerLaptops.setAdapter(lapAdp);
        recyclerHeadphones.setAdapter(soundAdp);

        loadProducts(); // <-- gọi API
        // chuyen sang trang ca nhan cua nguoi dung
        LinearLayout btnUser = findViewById(R.id.btnUser);
        btnUser.setOnClickListener(v -> {
            Intent intent = new Intent(HomeActivity.this, ProfileActivity.class);
            startActivity(intent);
        });
        // chuyen sang gio hang
        LinearLayout btnCart = findViewById(R.id.btnCart);
        btnCart.setOnClickListener(v -> {
            Intent intent = new Intent(HomeActivity.this, CartActivity.class);
            startActivity(intent);
        });
    }

    private void loadProducts() {
        ApiClient.get().getProducts().enqueue(new Callback<List<ProductDto>>() {
            @Override public void onResponse(Call<List<ProductDto>> call, Response<List<ProductDto>> res) {
                if (!res.isSuccessful() || res.body()==null) {
                    Toast.makeText(HomeActivity.this, "Lỗi API", Toast.LENGTH_SHORT).show();
                    return;
                }
                splitIntoSections(res.body());
            }
            @Override public void onFailure(Call<List<ProductDto>> call, Throwable t) {
                Toast.makeText(HomeActivity.this, "Không gọi được API: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    // Chia danh sách thành 5 nhóm (rule đơn giản theo tên/brand)
    private void splitIntoSections(List<ProductDto> list) {
        // clear cũ
        flashData.clear(); hotData.clear(); newData.clear(); lapData.clear(); soundData.clear();

        for (ProductDto d : list) {
            String name = d.name != null ? d.name : "";
            String brand = d.brand != null ? d.brand : "";
            String key = (name + " " + brand).toLowerCase();

            Product ui = new Product(name, vnd(d.price), d.imageUrl);

            // Laptop
            if (key.contains("laptop") || key.contains("notebook") || key.contains("macbook")) {
                lapData.add(ui);
                continue;
            }
            // Tai nghe - Loa
            if (key.contains("tai nghe") || key.contains("headset") || key.contains("loa") || key.contains("speaker")) {
                soundData.add(ui);
                continue;
            }
            // Flash sale (demo: cho các sản phẩm có giá < 1.5tr)
            if (d.price > 0 && d.price <= 1_500_000) {
                flashData.add(ui);
            }
            // Sản phẩm mới (demo: thêm vài món đầu)
            if (newData.size() < 8) newData.add(ui);

            // Nổi bật: cho tất cả, hoặc chọn theo brand lớn
            if (brand.equalsIgnoreCase("Intel") || brand.equalsIgnoreCase("AMD") ||
                    brand.equalsIgnoreCase("ASUS") || brand.equalsIgnoreCase("MSI") ||
                    brand.equalsIgnoreCase("Samsung") || brand.equalsIgnoreCase("Kingston")) {
                hotData.add(ui);
            }
        }

        // cập nhật UI
        flashAdp.notifyDataSetChanged();
        hotAdp.notifyDataSetChanged();
        newAdp.notifyDataSetChanged();
        lapAdp.notifyDataSetChanged();
        soundAdp.notifyDataSetChanged();
    }

    private String vnd(long price) {
        return NumberFormat.getInstance(new Locale("vi", "VN")).format(price) + " đ";
    }
}
