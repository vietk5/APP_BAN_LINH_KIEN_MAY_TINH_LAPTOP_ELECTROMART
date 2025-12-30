package com.example.baitap01_nhom6_ui_login_register_forgetpass.activity;

import android.animation.ObjectAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.view.animation.DecelerateInterpolator;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.baitap01_nhom6_ui_login_register_forgetpass.R;
import com.example.baitap01_nhom6_ui_login_register_forgetpass.models.dto.SpinRequest;
import com.example.baitap01_nhom6_ui_login_register_forgetpass.models.dto.SpinResultResponse;
import com.example.baitap01_nhom6_ui_login_register_forgetpass.models.dto.SpinStatusResponse;
import com.example.baitap01_nhom6_ui_login_register_forgetpass.remote.ApiClient;
import com.example.baitap01_nhom6_ui_login_register_forgetpass.remote.ApiService;
import com.example.baitap01_nhom6_ui_login_register_forgetpass.util.SharedPrefManager;
import com.example.baitap01_nhom6_ui_login_register_forgetpass.widget.LuckyWheelView;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.button.MaterialButton;

import java.util.Arrays;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LuckySpinActivity extends AppCompatActivity {

    private LuckyWheelView wheelView;
    private MaterialButton btnSpin;

    private TextView tvSpinStatus;
    private TextView tvSpinRemain; // ✅ để field, loadStatus() dùng được

    private ApiService api;
    private SharedPrefManager pref;

    // ⚠️ Labels phải TRÙNG với backend label
    private final List<String> segments = Arrays.asList(
            "Chúc bạn may mắn",
            "Voucher 5%",
            "Voucher 10%",
            "Freeship",
            "Voucher 15%",
            "Quà Sticker"
    );

    private float currentRotation = 0f;
    private boolean spinning = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lucky_spin);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.rootMain), (v, insets) -> {
            Insets bars = insets.getInsets(WindowInsetsCompat.Type.statusBars());
            v.setPadding(0, bars.top, 0, 0);
            return insets;
        });

        api = ApiClient.get();
        pref = new SharedPrefManager(this);

        // ====== Toolbar ======
        MaterialToolbar toolbar = findViewById(R.id.toolbarSpin);
        toolbar.setNavigationOnClickListener(v -> finish());

        // ====== Views ======
        wheelView = findViewById(R.id.wheelView);
        btnSpin = findViewById(R.id.btnSpin);
        tvSpinStatus = findViewById(R.id.tvSpinStatus);
        tvSpinRemain = findViewById(R.id.tvSpinRemain);

        wheelView.setItems(segments);

        // ====== Bottom Nav ======
        BottomNavigationView bottomNav = findViewById(R.id.bottomNavSpin);

        bottomNav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();

            if (id == R.id.nav_home) {
                // Nếu app bạn có MainActivity chứa tab Home
                Intent i = new Intent(this, MainActivity.class);
                i.putExtra("open_tab", "home"); // bạn dùng nếu có
                startActivity(i);
                finish();
                return true;
            }

            return false;
        });

        // ====== Load status ======
        loadStatus();

        // ====== Spin click ======
        btnSpin.setOnClickListener(v -> {
            if (spinning) return;

            long userId = pref.getUserId();
            if (userId <= 0) {
                Toast.makeText(this, "Vui lòng đăng nhập", Toast.LENGTH_SHORT).show();
                return;
            }

            doSpin(userId);
        });
    }

    private void loadStatus() {
        long userId = pref.getUserId();
        if (userId <= 0) {
            tvSpinStatus.setText("Vui lòng đăng nhập để quay.");
            btnSpin.setEnabled(false);
            tvSpinRemain.setText("0 lượt quay");
            return;
        }

        api.getSpinStatus(userId).enqueue(new Callback<SpinStatusResponse>() {
            @Override
            public void onResponse(Call<SpinStatusResponse> call, Response<SpinStatusResponse> res) {
                if (!res.isSuccessful() || res.body() == null) {
                    tvSpinStatus.setText("Không thể tải trạng thái quay.");
                    btnSpin.setEnabled(false);
                    tvSpinRemain.setText("0 lượt quay");
                    return;
                }

                SpinStatusResponse s = res.body();

                // ✅ cập nhật UI ngay trong callback
                tvSpinStatus.setText(s.message != null ? s.message : "Sẵn sàng quay!");
                btnSpin.setEnabled(s.canSpin);

                // nếu backend có remainingSpinsToday
                // nếu bạn chưa có field này thì bỏ dòng dưới
                tvSpinRemain.setText(s.remainingSpinsToday + " lượt quay");
            }

            @Override
            public void onFailure(Call<SpinStatusResponse> call, Throwable t) {
                tvSpinStatus.setText("Không thể tải trạng thái quay.");
                btnSpin.setEnabled(false);
                tvSpinRemain.setText("0 lượt quay");
            }
        });
    }

    private void doSpin(long userId) {
        spinning = true;
        btnSpin.setEnabled(false);
        tvSpinStatus.setText("Đang quay...");

        api.spin(new SpinRequest(userId)).enqueue(new Callback<SpinResultResponse>() {
            @Override
            public void onResponse(Call<SpinResultResponse> call, Response<SpinResultResponse> res) {
                if (!res.isSuccessful() || res.body() == null) {
                    spinning = false;
                    loadStatus();
                    return;
                }

                SpinResultResponse r = res.body();
                int idx = segments.indexOf(r.label);
                if (idx < 0) idx = 0;

                animateToIndex(idx, () -> {
                    spinning = false;
                    showResultDialog(r);
                    loadStatus();
                });
            }

            @Override
            public void onFailure(Call<SpinResultResponse> call, Throwable t) {
                spinning = false;
                Toast.makeText(LuckySpinActivity.this, "Lỗi kết nối", Toast.LENGTH_SHORT).show();
                loadStatus();
            }
        });
    }

    private void animateToIndex(int index, Runnable onEnd) {
        int n = segments.size();
        float sweep = 360f / n;

        float targetCenter = index * sweep + sweep / 2f;
        float desired = 270f - targetCenter;

        while (desired < 0) desired += 360f;
        desired = desired % 360f;

        float endRotation = currentRotation + 360f * 6 + desired;

        ObjectAnimator anim = ObjectAnimator.ofFloat(wheelView, "rotation", currentRotation, endRotation);
        anim.setDuration(3500);
        anim.setInterpolator(new DecelerateInterpolator());
        anim.addListener(new android.animation.AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(android.animation.Animator animation) {
                currentRotation = endRotation % 360f;
                onEnd.run();
            }
        });
        anim.start();
    }

    private void showResultDialog(SpinResultResponse r) {
        String title = "Kết quả";
        String body = (r.message != null && !r.message.isEmpty())
                ? r.message
                : (r.label + " - " + (r.value != null ? r.value : ""));

        AlertDialog.Builder b = new AlertDialog.Builder(this)
                .setTitle(title)
                .setMessage(body)
                .setCancelable(false)
                .setPositiveButton("OK", (d, w) -> d.dismiss());

        // Nếu backend trả type = VOUCHER và value = CODE
        if ("VOUCHER".equalsIgnoreCase(r.type) && r.value != null && !r.value.isEmpty()) {
            b.setNeutralButton("Dùng ngay", (d, w) -> {
                // bạn cần có hàm saveLastVoucherCode trong SharedPrefManager
                pref.saveLastVoucherCode(r.value);
                Toast.makeText(this, "Đã lưu mã: " + r.value, Toast.LENGTH_SHORT).show();
                d.dismiss();
                finish();
            });
        }

        b.show();
    }
}
