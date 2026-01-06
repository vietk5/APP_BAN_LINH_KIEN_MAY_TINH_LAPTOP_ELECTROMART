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
import androidx.fragment.app.FragmentManager;

import com.example.baitap01_nhom6_ui_login_register_forgetpass.R;
import com.example.baitap01_nhom6_ui_login_register_forgetpass.fragment.CartFragment;
import com.example.baitap01_nhom6_ui_login_register_forgetpass.fragment.HomeFragment;
import com.example.baitap01_nhom6_ui_login_register_forgetpass.fragment.PcBuilderFragment;
import com.example.baitap01_nhom6_ui_login_register_forgetpass.fragment.ProfileFragment;

public class MainActivity extends AppCompatActivity {

    // Định danh các tab
    public static final String TAB_HOME = "HOME";
    public static final String TAB_CATEGORY = "CATEGORY";
    public static final String TAB_CART = "CART";
    public static final String TAB_PROFILE = "PROFILE";

    // Bottom nav
    private LinearLayout btnHome, btnPcBuilder, btnConsult, btnCart, btnUser;

    // (optional) header title
    private TextView tvHeaderTitle;

    // ===== Fragment cache (để chuyển tab mượt, không tạo lại) =====
    private Fragment homeFragment;
    private Fragment pcBuilderFragment;
    private Fragment cartFragment;
    private Fragment profileFragment;

    private Fragment activeFragment;

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
        setupFragmentsFirstTime();
    }

    private void setupFragmentsFirstTime() {
        FragmentManager fm = getSupportFragmentManager();

        // Nếu activity bị recreate (xoay màn hình) thì lấy lại fragment theo tag, tránh add trùng
        homeFragment = fm.findFragmentByTag(TAB_HOME);
        pcBuilderFragment = fm.findFragmentByTag(TAB_CATEGORY);
        cartFragment = fm.findFragmentByTag(TAB_CART);
        profileFragment = fm.findFragmentByTag(TAB_PROFILE);

        if (homeFragment == null) homeFragment = new HomeFragment();
        if (pcBuilderFragment == null) pcBuilderFragment = new PcBuilderFragment();
        if (cartFragment == null) cartFragment = new CartFragment();
        if (profileFragment == null) profileFragment = new ProfileFragment();

        // Lần đầu chưa có gì trong container -> add hết, show Home
        if (fm.findFragmentById(R.id.fragment_container) == null) {
            fm.beginTransaction()
                    .add(R.id.fragment_container, homeFragment, TAB_HOME)
                    .add(R.id.fragment_container, pcBuilderFragment, TAB_CATEGORY).hide(pcBuilderFragment)
                    .add(R.id.fragment_container, cartFragment, TAB_CART).hide(cartFragment)
                    .add(R.id.fragment_container, profileFragment, TAB_PROFILE).hide(profileFragment)
                    .commitNow(); // commitNow để UI lên ngay, không “đứng”

            activeFragment = homeFragment;
            updateBottomNavState(btnHome);
        } else {
            // Trường hợp đã có fragment (recreate), xác định fragment đang active
            Fragment current = fm.findFragmentById(R.id.fragment_container);
            // current có thể là 1 trong các fragment trên, nhưng vì ta dùng hide/show,
            // ta tìm cái đang visible làm active:
            if (homeFragment.isVisible()) activeFragment = homeFragment;
            else if (pcBuilderFragment.isVisible()) activeFragment = pcBuilderFragment;
            else if (cartFragment.isVisible()) activeFragment = cartFragment;
            else if (profileFragment.isVisible()) activeFragment = profileFragment;
            else activeFragment = homeFragment;

            // update state theo active
            if (activeFragment == homeFragment) updateBottomNavState(btnHome);
            else if (activeFragment == pcBuilderFragment) updateBottomNavState(btnPcBuilder);
            else if (activeFragment == cartFragment) updateBottomNavState(btnCart);
            else if (activeFragment == profileFragment) updateBottomNavState(btnUser);
        }
    }

    private void initBottomNav() {
        btnHome = findViewById(R.id.btnHome);
        btnPcBuilder = findViewById(R.id.btnPcBuilder);
        btnConsult = findViewById(R.id.btnConsult);
        btnCart = findViewById(R.id.btnCart);
        btnUser = findViewById(R.id.btnUser);

        btnHome.setOnClickListener(v -> {
            updateBottomNavState(btnHome);
            switchFragment(homeFragment, TAB_HOME);
        });

        btnPcBuilder.setOnClickListener(v -> {
            updateBottomNavState(btnPcBuilder);
            switchFragment(pcBuilderFragment, TAB_CATEGORY);
        });

        btnConsult.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, ChatBotActivity.class);
            startActivity(intent);
        });

        // ✅ FIX: bỏ postDelayed, chuyển ngay
        btnCart.setOnClickListener(v -> {
            updateBottomNavState(btnCart);
            switchFragment(cartFragment, TAB_CART);
        });

        // ✅ FIX: bỏ postDelayed, chuyển ngay
        btnUser.setOnClickListener(v -> {
            updateBottomNavState(btnUser);
            switchFragment(profileFragment, TAB_PROFILE);
        });
    }

    private void switchFragment(Fragment target, String tag) {
        if (target == null) return;
        if (activeFragment == target) return;

        getSupportFragmentManager()
                .beginTransaction()
                .hide(activeFragment)
                .show(target)
                .commit();

        activeFragment = target;
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

    private void updateBottomNavState(LinearLayout selectedBtn) {
        resetBtnState(btnHome);
        resetBtnState(btnPcBuilder);
        resetBtnState(btnCart);
        resetBtnState(btnUser);

        highlightBtnState(selectedBtn);
    }

    private void resetBtnState(LinearLayout btn) {
        if (btn == null) return;
        ImageView icon = (ImageView) btn.getChildAt(0);
        TextView text = (TextView) btn.getChildAt(1);
        icon.setColorFilter(ContextCompat.getColor(this, R.color.gray_600));
        text.setTextColor(ContextCompat.getColor(this, R.color.gray_600));
    }

    private void highlightBtnState(LinearLayout btn) {
        if (btn == null) return;
        ImageView icon = (ImageView) btn.getChildAt(0);
        TextView text = (TextView) btn.getChildAt(1);
        icon.setColorFilter(ContextCompat.getColor(this, R.color.primary_blue));
        text.setTextColor(ContextCompat.getColor(this, R.color.primary_blue));
    }
}
