package com.example.baitap01_nhom6_ui_login_register_forgetpass.activity;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.StyleSpan;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.example.baitap01_nhom6_ui_login_register_forgetpass.R;
import com.example.baitap01_nhom6_ui_login_register_forgetpass.adapters.BannerAdapter;
import com.example.baitap01_nhom6_ui_login_register_forgetpass.adapters.ProductAdapter;
import com.example.baitap01_nhom6_ui_login_register_forgetpass.models.Product;
import com.example.baitap01_nhom6_ui_login_register_forgetpass.models.dto.ProductDto;
import com.example.baitap01_nhom6_ui_login_register_forgetpass.remote.ApiClient;
import com.example.baitap01_nhom6_ui_login_register_forgetpass.util.SharedPrefManager;
import  com.example.baitap01_nhom6_ui_login_register_forgetpass.util.BottomNavHelper;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeActivity extends AppCompatActivity {

    RecyclerView recyclerFlashSale, recyclerProducts, recyclerNewProducts,
            recyclerLaptops, recyclerHeadphones, recyclerSound, recyclerPc;

    private final List<Product> flashData     = new ArrayList<>();
    private final List<Product> hotData       = new ArrayList<>();
    private final List<Product> newData       = new ArrayList<>();
    private final List<Product> lapData       = new ArrayList<>();
    private final List<Product> headPhoneData = new ArrayList<>();
    private final List<Product> pcData        = new ArrayList<>();
    private final List<Product> soundData     = new ArrayList<>();

    private ProductAdapter flashAdp, hotAdp, newAdp, lapAdp, soundAdp, pcAdp, headPhoneAdp;

    // header
    private TextView tvWelcomeMessage;
    private EditText edtSearch;
    private ImageView btnSearch, btnCartHeader, btnNotification;

    // tab nhanh (layout mới)
    private LinearLayout btnTabPc, btnTabLaptop, btnTabHeadphone,
            btnTabMonitor, btnTabKeyboard, btnTabMouse;

    // banner slider
    private ViewPager2 bannerViewPager;
    private BannerAdapter bannerAdapter;

    // shared pref
    private SharedPrefManager sharedPrefManager;

    private final List<Integer> bannerImages = Arrays.asList(
            R.drawable.hero_1,
            R.drawable.hero_2,
            R.drawable.hero_3
    );

    private final Handler bannerHandler = new Handler(Looper.getMainLooper());
    private final Runnable bannerRunnable = new Runnable() {
        @Override
        public void run() {
            if (bannerViewPager == null || bannerImages.isEmpty()) return;
            int current = bannerViewPager.getCurrentItem();
            int next = (current + 1) % bannerImages.size();
            bannerViewPager.setCurrentItem(next, true);
            bannerHandler.postDelayed(this, 4000); // 4s đổi 1 ảnh
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_home);

        sharedPrefManager = new SharedPrefManager(this);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.header), (v, insets) -> {
            Insets bars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(v.getPaddingLeft(), bars.top, v.getPaddingRight(), v.getPaddingBottom());
            return insets;
        });

        initViews();
        setupRecyclerViews();
        setupBannerSlider();
        updateWelcomeMessage();   // dòng chào mừng theo trạng thái login
        setupHeaderActions();
        setupTabQuickSearch();    // tab danh mục mới
        loadProducts();
        BottomNavHelper.setup(this, "HOME");
    }

    private void initViews() {
        recyclerFlashSale   = findViewById(R.id.recyclerFlashSale);
        recyclerProducts    = findViewById(R.id.recyclerProducts);
        recyclerNewProducts = findViewById(R.id.recyclerNewProducts);
        recyclerLaptops     = findViewById(R.id.recyclerLaptops);
        recyclerHeadphones  = findViewById(R.id.recyclerHeadphones);
        recyclerSound       = findViewById(R.id.recyclerSpeakers);
        recyclerPc          = findViewById(R.id.recyclerPCs);

        tvWelcomeMessage = findViewById(R.id.tvWelcomeMessage);
        edtSearch        = findViewById(R.id.edtSearch);
        btnSearch        = findViewById(R.id.btnSearch);
        btnCartHeader    = findViewById(R.id.btnCartHeader);
        btnNotification  = findViewById(R.id.btnNotification); // trong header_layout

        // tab nhanh: bây giờ là LinearLayout, không còn Button nữa
        btnTabPc        = findViewById(R.id.btnTabPc);
        btnTabLaptop    = findViewById(R.id.btnTabLaptop);
        btnTabHeadphone = findViewById(R.id.btnTabHeadphone);
        btnTabMonitor   = findViewById(R.id.btnTabMonitor);
        btnTabKeyboard  = findViewById(R.id.btnTabKeyboard);
        btnTabMouse     = findViewById(R.id.btnTabMouse);
        bannerViewPager = findViewById(R.id.bannerViewPager);
    }
    /** Dòng chào mừng: nếu đã login thì hiện tên khách hàng (tên in đậm) */
    private void updateWelcomeMessage() {
        if (sharedPrefManager != null && sharedPrefManager.isLoggedIn()) {
            String customerName = sharedPrefManager.getName();
            if (customerName != null && !customerName.trim().isEmpty()) {

                String prefix = "Chào mừng bạn ";
                String suffix = " đến với ElectroMart";
                String full    = prefix + customerName + suffix;

                // Dùng Spannable để in đậm phần tên
                SpannableStringBuilder ssb = new SpannableStringBuilder(full);
                int start = prefix.length();
                int end   = start + customerName.length();
                ssb.setSpan(
                        new StyleSpan(Typeface.BOLD),
                        start,
                        end,
                        Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                );

                tvWelcomeMessage.setText(ssb);
                return;
            }
        }

        // mặc định khi chưa login / không có tên
        tvWelcomeMessage.setText("Chào mừng bạn đến với ElectroMart");
    }

    private void setupRecyclerViews() {
        recyclerFlashSale.setLayoutManager(
                new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        recyclerProducts.setLayoutManager(new GridLayoutManager(this, 2));
        recyclerNewProducts.setLayoutManager(new GridLayoutManager(this, 2));
        recyclerLaptops.setLayoutManager(new GridLayoutManager(this, 2));
        recyclerHeadphones.setLayoutManager(new GridLayoutManager(this, 2));
        recyclerSound.setLayoutManager(new GridLayoutManager(this, 2));
        recyclerPc.setLayoutManager(new GridLayoutManager(this, 2));

        flashAdp     = new ProductAdapter(flashData);
        hotAdp       = new ProductAdapter(hotData);
        newAdp       = new ProductAdapter(newData);
        lapAdp       = new ProductAdapter(lapData);
        soundAdp     = new ProductAdapter(soundData);
        pcAdp        = new ProductAdapter(pcData);
        headPhoneAdp = new ProductAdapter(headPhoneData);

        recyclerFlashSale.setAdapter(flashAdp);
        recyclerProducts.setAdapter(hotAdp);
        recyclerNewProducts.setAdapter(newAdp);
        recyclerLaptops.setAdapter(lapAdp);
        recyclerHeadphones.setAdapter(headPhoneAdp);
        recyclerSound.setAdapter(soundAdp);
        recyclerPc.setAdapter(pcAdp);
    }

    /** Banner slider */
    private void setupBannerSlider() {
        bannerAdapter = new BannerAdapter(bannerImages);
        bannerViewPager.setAdapter(bannerAdapter);
    }

    /** Header: search + cart + notification */
    private void setupHeaderActions() {
        btnCartHeader.setOnClickListener(v -> {
            Intent intent = new Intent(HomeActivity.this, CartActivity.class);
            startActivity(intent);
        });

        if (btnNotification != null) {
            btnNotification.setOnClickListener(v -> {
                Intent intent = new Intent(HomeActivity.this, NotificationActivity.class);
                startActivity(intent);
            });
        }

        btnSearch.setOnClickListener(v -> doSearch());

        edtSearch.setOnEditorActionListener((TextView v, int actionId, KeyEvent event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH
                    || actionId == EditorInfo.IME_ACTION_DONE
                    || (event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER
                    && event.getAction() == KeyEvent.ACTION_DOWN)) {
                doSearch();
                return true;
            }
            return false;
        });
    }

    /** Tìm kiếm bằng từ khóa nhập */
    private void doSearch() {
        String keyword = edtSearch.getText().toString().trim();
        openSearchWithKeyword(keyword);
    }

    /** Helper: gắn sự kiện cho 1 tab danh mục (cả layout + icon) */
    private void setupCategoryTab(LinearLayout tab, int imageViewId, String keyword) {
        if (tab == null) return;

        View.OnClickListener listener = v -> openSearchWithKeyword(keyword);

        // click cả vào "card"
        tab.setOnClickListener(listener);

        // click riêng vào icon (đúng yêu cầu “bấm vào cái ảnh”)
        ImageView img = tab.findViewById(imageViewId);
        if (img != null) {
            img.setOnClickListener(listener);
        }
    }

    /** Tab nhanh: PC, Laptop, Tai nghe, ... (dùng layout mới) */
    private void setupTabQuickSearch() {
        setupCategoryTab(btnTabPc,        R.id.imgTabPc,        "PC");
        setupCategoryTab(btnTabLaptop,    R.id.imgTabLaptop,    "Laptop");
        setupCategoryTab(btnTabHeadphone, R.id.imgTabHeadphone, "Tai nghe");
        setupCategoryTab(btnTabMonitor,   R.id.imgTabMonitor,   "Màn hình");
        setupCategoryTab(btnTabKeyboard,  R.id.imgTabKeyboard,  "Bàn phím");
        setupCategoryTab(btnTabMouse,     R.id.imgTabMouse,     "Chuột");
    }

    private void openSearchWithKeyword(String keyword) {
        Intent intent = new Intent(HomeActivity.this, SearchActivity.class);
        intent.putExtra("keyword", keyword);
        startActivity(intent);
    }



    /** Gọi API chia section như cũ */
    private void loadProducts() {
        ApiClient.get().getProducts().enqueue(new Callback<List<ProductDto>>() {
            @Override
            public void onResponse(Call<List<ProductDto>> call,
                                   Response<List<ProductDto>> res) {
                if (!res.isSuccessful() || res.body() == null) {
                    Toast.makeText(HomeActivity.this, "Lỗi API", Toast.LENGTH_SHORT).show();
                    return;
                }
                splitIntoSections(res.body());
            }

            @Override
            public void onFailure(Call<List<ProductDto>> call, Throwable t) {
                Toast.makeText(HomeActivity.this,
                        "Không gọi được API: " + t.getMessage(),
                        Toast.LENGTH_LONG).show();
            }
        });
    }

    private void splitIntoSections(List<ProductDto> list) {
        flashData.clear();
        hotData.clear();
        newData.clear();
        lapData.clear();
        soundData.clear();
        pcData.clear();
        headPhoneData.clear();

        for (ProductDto d : list) {
            String name = d.name != null ? d.name : "";
            String brand = d.brand != null ? d.brand : "";
            String key = (name + " " + brand).toLowerCase(Locale.ROOT);

            Product ui = new Product(
                    d.id,
                    d.name,
                    vnd(d.price),
                    d.imageUrl
            );

            if (key.contains("laptop") || key.contains("notebook") || key.contains("macbook")) {
                lapData.add(ui);
                continue;
            }
            if (key.contains("tai nghe") || key.contains("headset")) {
                headPhoneData.add(ui);
                continue;
            }
            if (key.contains("loa") || key.contains("speaker")) {
                soundData.add(ui);
                continue;
            }
            if (key.contains("pc") || key.contains("case")) {
                pcData.add(ui);
                continue;
            }
            if (d.price > 0 && d.price <= 1_500_000) {
                flashData.add(ui);
            }
            if (newData.size() < 8) newData.add(ui);

            if (brand.equalsIgnoreCase("Intel") || brand.equalsIgnoreCase("AMD") ||
                    brand.equalsIgnoreCase("ASUS") || brand.equalsIgnoreCase("MSI") ||
                    brand.equalsIgnoreCase("Samsung") || brand.equalsIgnoreCase("Kingston")) {
                hotData.add(ui);
            }
        }

        flashAdp.notifyDataSetChanged();
        hotAdp.notifyDataSetChanged();
        newAdp.notifyDataSetChanged();
        lapAdp.notifyDataSetChanged();
        soundAdp.notifyDataSetChanged();
        pcAdp.notifyDataSetChanged();
        headPhoneAdp.notifyDataSetChanged();
    }

    private String vnd(long price) {
        return NumberFormat
                .getInstance(new Locale("vi", "VN"))
                .format(price) + " đ";
    }

    @Override
    protected void onResume() {
        super.onResume();
        // reload chào mừng phòng trường hợp user mới login xong quay lại
        updateWelcomeMessage();
        bannerHandler.postDelayed(bannerRunnable, 4000);
    }

    @Override
    protected void onPause() {
        super.onPause();
        bannerHandler.removeCallbacks(bannerRunnable);
    }
}
