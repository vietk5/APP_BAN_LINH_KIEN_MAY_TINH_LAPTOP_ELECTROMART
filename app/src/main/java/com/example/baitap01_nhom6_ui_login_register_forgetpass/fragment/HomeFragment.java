package com.example.baitap01_nhom6_ui_login_register_forgetpass.fragment;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Looper;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.StyleSpan;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.example.baitap01_nhom6_ui_login_register_forgetpass.R;
import com.example.baitap01_nhom6_ui_login_register_forgetpass.activity.LuckySpinActivity;
import com.example.baitap01_nhom6_ui_login_register_forgetpass.activity.MainActivity;
import com.example.baitap01_nhom6_ui_login_register_forgetpass.activity.NotificationActivity;
import com.example.baitap01_nhom6_ui_login_register_forgetpass.activity.ProductDetailActivity;
import com.example.baitap01_nhom6_ui_login_register_forgetpass.activity.SearchActivity;
import com.example.baitap01_nhom6_ui_login_register_forgetpass.adapters.BannerAdapter;
import com.example.baitap01_nhom6_ui_login_register_forgetpass.adapters.FlashSaleAdapter;
import com.example.baitap01_nhom6_ui_login_register_forgetpass.adapters.ProductAdapter;
import com.example.baitap01_nhom6_ui_login_register_forgetpass.models.Product;
import com.example.baitap01_nhom6_ui_login_register_forgetpass.models.dto.ProductDto;
import com.example.baitap01_nhom6_ui_login_register_forgetpass.remote.ApiClient;
import com.example.baitap01_nhom6_ui_login_register_forgetpass.util.SharedPrefManager;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeFragment extends Fragment {

    private RecyclerView recyclerFlashSale, recyclerProducts, recyclerNewProducts,
            recyclerLaptops, recyclerHeadphones, recyclerSound, recyclerPc;

    // Dữ liệu cho các grid cũ (ProductAdapter dùng Product model)
    private final List<Product> hotData       = new ArrayList<>();
    private final List<Product> newData       = new ArrayList<>();
    private final List<Product> lapData       = new ArrayList<>();
    private final List<Product> headPhoneData = new ArrayList<>();
    private final List<Product> pcData        = new ArrayList<>();
    private final List<Product> soundData     = new ArrayList<>();

    // Flash sale dùng ProductDto để tính -5% và hiển thị giá cũ gạch
    private final List<ProductDto> flashSaleDtos = new ArrayList<>();
    private FlashSaleAdapter flashSaleAdapter;

    private ProductAdapter hotAdp, newAdp, lapAdp, soundAdp, pcAdp, headPhoneAdp;

    // header
    private TextView tvWelcomeMessage;
    private EditText edtSearch;
    private ImageView btnSearch, btnCartHeader, btnNotification;
    private FloatingActionButton fabSpin;

    // tab nhanh
    private LinearLayout btnTabPc, btnTabLaptop, btnTabHeadphone,
            btnTabMonitor, btnTabKeyboard, btnTabMouse;

    // banner slider
    private ViewPager2 bannerViewPager;
    private BannerAdapter bannerAdapter;

    // shared pref
    private SharedPrefManager sharedPrefManager;
    // 10 phút (bạn muốn bao nhiêu thì đổi)
    private TextView tvCountdown;
    private CountDownTimer flashTimer;
    private static final long FLASH_DURATION_MS = 10 * 60 * 1000L;
    private long flashRemainMs = FLASH_DURATION_MS;

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
            bannerHandler.postDelayed(this, 4000);
        }
    };

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (getContext() != null) {
            sharedPrefManager = new SharedPrefManager(getContext());
        }

        initViews(view);
        startFlashCountdown(FLASH_DURATION_MS);

        setupRecyclerViews();
        setupBannerSlider();
        updateWelcomeMessage();
        setupHeaderActions();
        setupTabQuickSearch();
        loadProducts();
    }

    private void initViews(View view) {
        recyclerFlashSale   = view.findViewById(R.id.recyclerFlashSale);
        recyclerProducts    = view.findViewById(R.id.recyclerProducts);
        recyclerNewProducts = view.findViewById(R.id.recyclerNewProducts);
        recyclerLaptops     = view.findViewById(R.id.recyclerLaptops);
        recyclerHeadphones  = view.findViewById(R.id.recyclerHeadphones);
        recyclerSound       = view.findViewById(R.id.recyclerSpeakers);
        recyclerPc          = view.findViewById(R.id.recyclerPCs);

        tvWelcomeMessage = view.findViewById(R.id.tvWelcomeMessage);
        edtSearch        = view.findViewById(R.id.edtSearch);
        btnSearch        = view.findViewById(R.id.btnSearch);
        btnCartHeader    = view.findViewById(R.id.btnCartHeader);
        btnNotification  = view.findViewById(R.id.btnNotification);

        btnTabPc        = view.findViewById(R.id.btnTabPc);
        btnTabLaptop    = view.findViewById(R.id.btnTabLaptop);
        btnTabHeadphone = view.findViewById(R.id.btnTabHeadphone);
        btnTabMonitor   = view.findViewById(R.id.btnTabMonitor);
        btnTabKeyboard  = view.findViewById(R.id.btnTabKeyboard);
        btnTabMouse     = view.findViewById(R.id.btnTabMouse);

        bannerViewPager = view.findViewById(R.id.bannerViewPager);
        tvCountdown = view.findViewById(R.id.tvCountdown);
        fabSpin = view.findViewById(R.id.fabSpin);

    }

    private void updateWelcomeMessage() {
        if (sharedPrefManager != null && sharedPrefManager.isLoggedIn()) {
            String customerName = sharedPrefManager.getName();
            if (customerName != null && !customerName.trim().isEmpty()) {
                String prefix = "Chào mừng bạn ";
                String suffix = " đến với ElectroMart";
                String full = prefix + customerName + suffix;

                SpannableStringBuilder ssb = new SpannableStringBuilder(full);
                int start = prefix.length();
                int end = start + customerName.length();
                ssb.setSpan(new StyleSpan(Typeface.BOLD), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                tvWelcomeMessage.setText(ssb);
                return;
            }
        }
        tvWelcomeMessage.setText("Chào mừng bạn đến với ElectroMart");
    }

    private void setupRecyclerViews() {
        if (getContext() == null) return;

        // Flash sale: ngang
        recyclerFlashSale.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));

        // Grid sections
        recyclerProducts.setLayoutManager(new GridLayoutManager(getContext(), 2));
        recyclerNewProducts.setLayoutManager(new GridLayoutManager(getContext(), 2));
        recyclerLaptops.setLayoutManager(new GridLayoutManager(getContext(), 2));
        recyclerHeadphones.setLayoutManager(new GridLayoutManager(getContext(), 2));
        recyclerSound.setLayoutManager(new GridLayoutManager(getContext(), 2));
        recyclerPc.setLayoutManager(new GridLayoutManager(getContext(), 2));

        flashSaleAdapter = new FlashSaleAdapter(flashSaleDtos, p -> {
            if (getContext() == null) return;

            Intent i = new Intent(getContext(), ProductDetailActivity.class);

            // ✅ phải trùng key với ProductAdapter
            i.putExtra("product_id", p.id);

            // ✅ preview để vào nhanh (đỡ thấy trống ảnh/giá lúc chờ API)
            i.putExtra("preview_name", p.name);
            i.putExtra("preview_price", p.price);
            i.putExtra("preview_img", p.imageUrl);

            startActivity(i);
        });
        recyclerFlashSale.setAdapter(flashSaleAdapter);

        recyclerFlashSale.setAdapter(flashSaleAdapter);


        // Các adapter cũ giữ nguyên (Product model)
        hotAdp       = new ProductAdapter(hotData);
        newAdp       = new ProductAdapter(newData);
        lapAdp       = new ProductAdapter(lapData);
        soundAdp     = new ProductAdapter(soundData);
        pcAdp        = new ProductAdapter(pcData);
        headPhoneAdp = new ProductAdapter(headPhoneData);

        recyclerProducts.setAdapter(hotAdp);
        recyclerNewProducts.setAdapter(newAdp);
        recyclerLaptops.setAdapter(lapAdp);
        recyclerHeadphones.setAdapter(headPhoneAdp);
        recyclerSound.setAdapter(soundAdp);
        recyclerPc.setAdapter(pcAdp);
    }

    private void setupBannerSlider() {
        bannerAdapter = new BannerAdapter(bannerImages);
        bannerViewPager.setAdapter(bannerAdapter);
    }

    private void setupHeaderActions() {
        btnCartHeader.setOnClickListener(v -> {
            if (getActivity() instanceof MainActivity) {
                ((MainActivity) getActivity()).switchToTab(MainActivity.TAB_CART);
            }
        });

        if (btnNotification != null) {
            btnNotification.setOnClickListener(v -> {
                Intent intent = new Intent(getContext(), NotificationActivity.class);
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
        // ✅ Wheel spin chỉ hiện và xử lý ở Home
        if (fabSpin != null) {
            fabSpin.setOnClickListener(v -> {
                if (getContext() == null) return;
                startActivity(new Intent(getContext(), LuckySpinActivity.class));
            });
        }
    }

    private void doSearch() {
        String keyword = edtSearch.getText().toString().trim();
        openSearchWithKeyword(keyword);
    }

    private void setupCategoryTab(LinearLayout tab, int imageViewId, String keyword) {
        if (tab == null) return;
        View.OnClickListener listener = v -> openSearchWithKeyword(keyword);
        tab.setOnClickListener(listener);
        ImageView img = tab.findViewById(imageViewId);
        if (img != null) img.setOnClickListener(listener);
    }

    private void setupTabQuickSearch() {
        setupCategoryTab(btnTabPc,        R.id.imgTabPc,        "PC");
        setupCategoryTab(btnTabLaptop,    R.id.imgTabLaptop,    "Laptop");
        setupCategoryTab(btnTabHeadphone, R.id.imgTabHeadphone, "Tai nghe");
        setupCategoryTab(btnTabMonitor,   R.id.imgTabMonitor,   "Màn hình");
        setupCategoryTab(btnTabKeyboard,  R.id.imgTabKeyboard,  "Bàn phím");
        setupCategoryTab(btnTabMouse,     R.id.imgTabMouse,     "Chuột");
    }

    private void openSearchWithKeyword(String keyword) {
        Intent intent = new Intent(getContext(), SearchActivity.class);
        intent.putExtra("keyword", keyword);
        startActivity(intent);
    }

    private void loadProducts() {
        ApiClient.get().getProducts().enqueue(new Callback<List<ProductDto>>() {
            @Override
            public void onResponse(Call<List<ProductDto>> call, Response<List<ProductDto>> res) {
                if (!res.isSuccessful() || res.body() == null) {
                    if (getContext() != null) Toast.makeText(getContext(), "Lỗi API", Toast.LENGTH_SHORT).show();
                    return;
                }
                splitIntoSections(res.body());
            }

            @Override
            public void onFailure(Call<List<ProductDto>> call, Throwable t) {
                if (getContext() != null) Toast.makeText(getContext(), "Không gọi được API: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void splitIntoSections(List<ProductDto> list) {
        flashSaleDtos.clear();
        hotData.clear();
        newData.clear();
        lapData.clear();
        soundData.clear();
        pcData.clear();
        headPhoneData.clear();

        if (list == null || list.isEmpty()) {
            flashSaleAdapter.notifyDataSetChanged();
            hotAdp.notifyDataSetChanged();
            newAdp.notifyDataSetChanged();
            lapAdp.notifyDataSetChanged();
            soundAdp.notifyDataSetChanged();
            pcAdp.notifyDataSetChanged();
            headPhoneAdp.notifyDataSetChanged();
            return;
        }

        // 1) Sort newest theo id giảm dần (vì chưa có createdAt)
        List<ProductDto> sortedNew = new ArrayList<>(list);
        sortedNew.sort((a, b) -> Long.compare(b.id, a.id));

        // 2) FLASH SALE = 10 sản phẩm mới nhất
        int flashCount = Math.min(10, sortedNew.size());
        for (int i = 0; i < flashCount; i++) {
            flashSaleDtos.add(sortedNew.get(i));
        }

        // 3) NEW PRODUCTS = 8 sản phẩm tiếp theo sau flash sale
        int idxStart = flashCount;
        int idxEnd = Math.min(idxStart + 8, sortedNew.size());
        for (int i = idxStart; i < idxEnd; i++) {
            ProductDto d = sortedNew.get(i);
            newData.add(new Product(d.id, d.name, vnd(d.price), d.imageUrl));
        }

        // 4) HOT PRODUCTS = top 10 theo giá cao (tạm coi “bán chạy”)
        List<ProductDto> sortedByPrice = new ArrayList<>(list);
        sortedByPrice.sort((a, b) -> Long.compare(b.price, a.price));
        int hotCount = Math.min(10, sortedByPrice.size());
        for (int i = 0; i < hotCount; i++) {
            ProductDto d = sortedByPrice.get(i);
            hotData.add(new Product(d.id, d.name, vnd(d.price), d.imageUrl));
        }

        // 5) Phân loại category theo keyword (giới hạn 10/item để UI gọn)
        for (ProductDto d : list) {
            String name = d.name != null ? d.name : "";
            String brand = d.brand != null ? d.brand : "";
            String key = (name + " " + brand).toLowerCase(Locale.ROOT);

            Product ui = new Product(d.id, d.name, vnd(d.price), d.imageUrl);

            if (isLaptop(key)) {
                if (lapData.size() < 10) lapData.add(ui);
                continue;
            }
            if (isHeadphone(key)) {
                if (headPhoneData.size() < 10) headPhoneData.add(ui);
                continue;
            }
            if (isSpeaker(key)) {
                if (soundData.size() < 10) soundData.add(ui);
                continue;
            }
            if (isPC(key)) {
                if (pcData.size() < 10) pcData.add(ui);
            }
        }

        // Notify all
        flashSaleAdapter.notifyDataSetChanged();
        hotAdp.notifyDataSetChanged();
        newAdp.notifyDataSetChanged();
        lapAdp.notifyDataSetChanged();
        soundAdp.notifyDataSetChanged();
        pcAdp.notifyDataSetChanged();
        headPhoneAdp.notifyDataSetChanged();
    }

    private boolean isLaptop(String key) {
        return key.contains("laptop") || key.contains("notebook") || key.contains("macbook")
                || key.contains("thinkpad") || key.contains("vivobook") || key.contains("ideapad");
    }

    private boolean isHeadphone(String key) {
        return key.contains("tai nghe") || key.contains("headphone") || key.contains("headset")
                || key.contains("earbuds") || key.contains("airpods");
    }

    private boolean isSpeaker(String key) {
        return key.contains("loa") || key.contains("speaker") || key.contains("soundbar");
    }

    private boolean isPC(String key) {
        return key.contains("pc ") || key.contains(" pc") || key.contains("desktop")
                || key.contains("case") || key.contains("máy tính bàn");
    }

    private String vnd(long price) {
        return NumberFormat.getInstance(new Locale("vi", "VN")).format(price) + " đ";
    }
    private String formatCountdown(long ms) {
        long totalSec = ms / 1000;
        long min = totalSec / 60;
        long sec = totalSec % 60;
        return String.format(Locale.getDefault(), "%02d:%02d", min, sec);
    }

    private void startFlashCountdown(long startMs) {
        stopFlashCountdown();

        flashRemainMs = startMs;
        if (tvCountdown != null) tvCountdown.setText(formatCountdown(flashRemainMs));

        flashTimer = new CountDownTimer(flashRemainMs, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                flashRemainMs = millisUntilFinished;
                if (tvCountdown != null) tvCountdown.setText(formatCountdown(flashRemainMs));
            }

            @Override
            public void onFinish() {
                flashRemainMs = 0;
                if (tvCountdown != null) tvCountdown.setText("00:00");

                // ✅ tuỳ chọn: reset vòng mới
                startFlashCountdown(FLASH_DURATION_MS);

                // ✅ tuỳ chọn: reload flash sale khi hết giờ
                // loadProducts();
            }
        }.start();
    }

    private void stopFlashCountdown() {
        if (flashTimer != null) {
            flashTimer.cancel();
            flashTimer = null;
        }
    }


    @Override
    public void onResume() {
        super.onResume();
        updateWelcomeMessage();
        bannerHandler.postDelayed(bannerRunnable, 4000);
        startFlashCountdown(flashRemainMs <= 0 ? FLASH_DURATION_MS : flashRemainMs);

    }

    @Override
    public void onPause() {
        super.onPause();
        bannerHandler.removeCallbacks(bannerRunnable);
        stopFlashCountdown();
    }

}
