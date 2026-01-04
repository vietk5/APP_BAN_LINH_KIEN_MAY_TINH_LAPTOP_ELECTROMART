package com.example.baitap01_nhom6_ui_login_register_forgetpass.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;

import com.example.baitap01_nhom6_ui_login_register_forgetpass.R;
import com.example.baitap01_nhom6_ui_login_register_forgetpass.fragment.CartFragment;
import com.example.baitap01_nhom6_ui_login_register_forgetpass.fragment.CategoryFragment;
import com.example.baitap01_nhom6_ui_login_register_forgetpass.fragment.HomeFragment;
import com.example.baitap01_nhom6_ui_login_register_forgetpass.fragment.PcBuilderFragment;
import com.example.baitap01_nhom6_ui_login_register_forgetpass.fragment.ProfileFragment;

public class MainActivity extends AppCompatActivity {

    // Định danh các tab
    public static final String TAB_HOME = "HOME";
    public static final String TAB_CATEGORY = "CATEGORY";
    public static final String TAB_CART = "CART";
    public static final String TAB_PROFILE = "PROFILE";

    // Các nút trong Bottom Nav (layout bottom_navigation.xml)
    private LinearLayout btnHome, btnPcBuilder, btnConsult, btnCart, btnUser;

    // Header elements (nếu muốn ẩn hiện hoặc thay đổi title theo tab)
    private TextView tvHeaderTitle;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.rootMain), (v, insets) -> {
            Insets bars = insets.getInsets(WindowInsetsCompat.Type.statusBars());
            v.setPadding(0, bars.top, 0, 0);
            return insets;
        });
        initBottomNav();

        // Mặc định load Home
        loadFragment(new HomeFragment());
        updateBottomNavState(btnHome);

        // Xử lý điều hướng từ EmptyCartActivity
//        boolean goHome = getIntent().getBooleanExtra("go_home", false);
//        if (goHome) {
//            loadFragment(new HomeFragment());
//            updateBottomNavState(btnHome);
//        }
    }

    private void initBottomNav() {
        // Ánh xạ từ file include bottom_navigation
        btnHome = findViewById(R.id.btnHome);
        btnPcBuilder = findViewById(R.id.btnPcBuilder);
        btnConsult = findViewById(R.id.btnConsult); // Nút Tư vấn (ChatBot)
        btnCart = findViewById(R.id.btnCart);
        btnUser = findViewById(R.id.btnUser);

        // Sự kiện click
        btnHome.setOnClickListener(v -> {
            loadFragment(new HomeFragment());
            updateBottomNavState(btnHome);
        });

        btnPcBuilder.setOnClickListener(v -> {
            loadFragment(new PcBuilderFragment());
            updateBottomNavState(btnPcBuilder);
        });

        btnConsult.setOnClickListener(v -> {
            // ChatBot là Activity riêng, không phải Fragment, nên start Activity
            Intent intent = new Intent(MainActivity.this, ChatBotActivity.class);
            startActivity(intent);
            // Không cần update state vì sẽ chuyển màn hình khác
        });

        btnCart.setOnClickListener(v -> {
            loadFragment(new CartFragment());
            updateBottomNavState(btnCart);
        });

        btnUser.setOnClickListener(v -> {
            loadFragment(new ProfileFragment());
            updateBottomNavState(btnUser);
        });
    }

    // Hàm load Fragment vào container
    private void loadFragment(Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commit();
    }

    // Hàm public để các Fragment con có thể gọi (ví dụ từ Home click icon giỏ hàng)
    public void switchToTab(String tabName) {
        switch (tabName) {
            case TAB_HOME:
                btnHome.performClick();
                break;
            case TAB_CATEGORY:
                btnPcBuilder.performClick();
                break;
            case TAB_CART:
                btnCart.performClick();
                break;
            case TAB_PROFILE:
                btnUser.performClick();
                break;
        }
    }

    // Hàm đổi màu icon/text để biết đang chọn tab nào
    private void updateBottomNavState(LinearLayout selectedBtn) {
        // Reset tất cả về màu xám
        resetBtnState(btnHome);
        resetBtnState(btnPcBuilder);
        resetBtnState(btnCart);
        resetBtnState(btnUser);

        // Highlight tab được chọn (Màu xanh #3B82F6)
        highlightBtnState(selectedBtn);
    }

    private void resetBtnState(LinearLayout btn) {
        if (btn == null) return;
        ImageView icon = (ImageView) btn.getChildAt(0);
        TextView text = (TextView) btn.getChildAt(1);
        icon.setColorFilter(ContextCompat.getColor(this, R.color.gray_600)); // Định nghĩa màu xám trong colors.xml
        text.setTextColor(ContextCompat.getColor(this, R.color.gray_600));
    }

    private void highlightBtnState(LinearLayout btn) {
        if (btn == null) return;
        ImageView icon = (ImageView) btn.getChildAt(0);
        TextView text = (TextView) btn.getChildAt(1);
        icon.setColorFilter(ContextCompat.getColor(this, R.color.primary_blue)); // Màu xanh chủ đạo
        text.setTextColor(ContextCompat.getColor(this, R.color.primary_blue));
    }
}