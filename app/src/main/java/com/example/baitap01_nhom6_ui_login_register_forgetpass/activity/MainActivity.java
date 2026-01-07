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
import com.example.baitap01_nhom6_ui_login_register_forgetpass.fragment.HomeFragment;
import com.example.baitap01_nhom6_ui_login_register_forgetpass.fragment.PcBuilderFragment;
import com.example.baitap01_nhom6_ui_login_register_forgetpass.fragment.ProfileFragment;

public class MainActivity extends AppCompatActivity {

    public static final String TAB_HOME = "HOME";
    public static final String TAB_CATEGORY = "CATEGORY";
    public static final String TAB_CART = "CART";
    public static final String TAB_PROFILE = "PROFILE";

    private LinearLayout btnHome, btnPcBuilder, btnConsult, btnCart, btnUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Setup Window Insets
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.rootMain), (v, insets) -> {
            Insets bars = insets.getInsets(WindowInsetsCompat.Type.statusBars());
            v.setPadding(0, bars.top, 0, 0);
            return insets;
        });

        initBottomNav();

        // Mặc định load Home
        loadFragment(new HomeFragment());
        updateBottomNavState(btnHome);
    }

    private void initBottomNav() {
        btnHome = findViewById(R.id.btnHome);
        btnPcBuilder = findViewById(R.id.btnPcBuilder);
        btnConsult = findViewById(R.id.btnConsult);
        btnCart = findViewById(R.id.btnCart);
        btnUser = findViewById(R.id.btnUser);

        // --- HOME ---
        btnHome.setOnClickListener(v -> {
            loadFragment(new HomeFragment());
            updateBottomNavState(btnHome);
        });

        // --- PC BUILDER ---
        btnPcBuilder.setOnClickListener(v -> {
            loadFragment(new PcBuilderFragment());
            updateBottomNavState(btnPcBuilder);
        });

        // --- CONSULT (CHATBOT) ---
        btnConsult.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, ChatBotActivity.class);
            startActivity(intent);
        });

        // --- CART (Đã xóa delay 5s) ---
        btnCart.setOnClickListener(v -> {
            // Load trực tiếp CartFragment thay vì LoadingFragment + delay
            loadFragment(new CartFragment());
            updateBottomNavState(btnCart);
        });

        // --- USER PROFILE (Đã xóa delay 5s) ---
        btnUser.setOnClickListener(v -> {
            // Load trực tiếp ProfileFragment thay vì LoadingFragment + delay
            loadFragment(new ProfileFragment());
            updateBottomNavState(btnUser);
        });
    }

    private void loadFragment(Fragment fragment) {
        // Sử dụng replace để thay thế fragment cũ bằng fragment mới
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commit();
    }

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