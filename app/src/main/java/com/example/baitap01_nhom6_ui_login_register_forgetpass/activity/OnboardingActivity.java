package com.example.baitap01_nhom6_ui_login_register_forgetpass.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.viewpager2.widget.ViewPager2;

import com.example.baitap01_nhom6_ui_login_register_forgetpass.R;
import com.example.baitap01_nhom6_ui_login_register_forgetpass.adapters.OnboardingAdapter;
import com.example.baitap01_nhom6_ui_login_register_forgetpass.models.dto.OnboardingItem;
import com.example.baitap01_nhom6_ui_login_register_forgetpass.util.OnboardingPref;
import com.google.android.material.button.MaterialButton;

import java.util.Arrays;
import java.util.List;

public class OnboardingActivity extends AppCompatActivity {

    private ViewPager2 pager;
    private MaterialButton btnSkip, btnNext;
    private LinearLayout dotsLayout;

    private List<OnboardingItem> slides;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Nếu đã xem onboarding rồi thì skip luôn (an toàn)
        if (OnboardingPref.isDone(this)) {
            startActivity(new Intent(this, MainActivity.class));
            finish();
            return;
        }

        setContentView(R.layout.activity_onboarding);

        // Fix tai thỏ / status bar / navigation bar
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.rootOnboarding), (v, insets) -> {
            Insets bars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(0, bars.top, 0, bars.bottom);
            return insets;
        });

        pager = findViewById(R.id.pager);
        btnSkip = findViewById(R.id.btnSkip);
        btnNext = findViewById(R.id.btnNext);
        dotsLayout = findViewById(R.id.dotsLayout);

        // ✅ ĐỔI NỘI DUNG SLIDE Ở ĐÂY
        slides = Arrays.asList(
                new OnboardingItem(
                        R.drawable.logo, // đổi ảnh ở đây
                        "Chào mừng đến ElectroMart",
                        "Mua linh kiện điện tử, laptop, PC chính hãng với giá tốt."
                ),
                new OnboardingItem(
                        R.drawable.logoo2,
                        "Đa dạng các mặt hàng điện tử",
                        "Laptop, linh kiện điện tử, linh kiện máy tính, linh kiện PC, đồ công nghệ mới nhất được cung cấp."
                ),
                new OnboardingItem(
                        R.drawable.logoo3,
                        "Dịch vụ tốt, ưu đãi tốt",
                        "Theo dõi đơn hàng dễ dàng, hỗ trợ COD và thanh toán ngân hàng, cùng với các voucher hấp dẫn, dịch vụ bảo hành tốt nhất."
                )
        );

        pager.setAdapter(new OnboardingAdapter(slides));

        // Setup dots custom
        setupDots(slides.size());
        updateDots(0);
        updateButtons(0);

        btnSkip.setOnClickListener(v -> finishOnboarding());

        btnNext.setOnClickListener(v -> {
            int pos = pager.getCurrentItem();
            if (pos < slides.size() - 1) {
                pager.setCurrentItem(pos + 1, true);
            } else {
                finishOnboarding();
            }
        });

        pager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                updateDots(position);
                updateButtons(position);
            }
        });
    }

    private void updateButtons(int position) {
        if (position == slides.size() - 1) {
            btnNext.setText("Bắt đầu");
            btnSkip.setVisibility(View.INVISIBLE); // hoặc GONE cho gọn
        } else {
            btnNext.setText("Tiếp");
            btnSkip.setVisibility(View.VISIBLE);
        }
    }

    private void setupDots(int count) {
        dotsLayout.removeAllViews();
        int margin = dp(6);

        for (int i = 0; i < count; i++) {
            View dot = new View(this);
            LinearLayout.LayoutParams lp =
                    new LinearLayout.LayoutParams(dp(10), dp(10));
            lp.setMargins(margin, 0, margin, 0);
            dot.setLayoutParams(lp);
            dot.setBackgroundResource(R.drawable.dot_inactive);
            dotsLayout.addView(dot);
        }
    }

    private void updateDots(int activePos) {
        for (int i = 0; i < dotsLayout.getChildCount(); i++) {
            View dot = dotsLayout.getChildAt(i);

            boolean active = (i == activePos);
            dot.setBackgroundResource(active ? R.drawable.dot_active : R.drawable.dot_inactive);

            // active dot hơi to hơn
            LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) dot.getLayoutParams();
            int size = active ? dp(10) : dp(8);
            lp.width = size;
            lp.height = size;
            dot.setLayoutParams(lp);
        }
    }

    private int dp(int v) {
        return Math.round(getResources().getDisplayMetrics().density * v);
    }

    private void finishOnboarding() {
        OnboardingPref.setDone(this, true);

        // Sau onboarding đi đâu: MainActivity hoặc LoginActivity tùy flow của bạn
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }
}
