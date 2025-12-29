package com.example.baitap01_nhom6_ui_login_register_forgetpass.util;

import static androidx.core.content.ContextCompat.startActivity;

import android.content.Intent;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.baitap01_nhom6_ui_login_register_forgetpass.R;
import com.example.baitap01_nhom6_ui_login_register_forgetpass.activity.AdminActivity;
import com.example.baitap01_nhom6_ui_login_register_forgetpass.activity.AdminCustomersActivity;
import com.example.baitap01_nhom6_ui_login_register_forgetpass.activity.AdminOrdersActivity;
import com.example.baitap01_nhom6_ui_login_register_forgetpass.activity.AdminProductActivity;
import com.example.baitap01_nhom6_ui_login_register_forgetpass.activity.AdminRevenueActivity;
import com.example.baitap01_nhom6_ui_login_register_forgetpass.activity.LoginActivity;
import com.google.android.material.appbar.MaterialToolbar;

public class AdminNavHelper {

    private AdminNavHelper() {}

    // Thêm tham số String title vào đây
    public static void setupToolbar(AppCompatActivity act, MaterialToolbar toolbar, String title) {
        // 1. QUAN TRỌNG: BỎ dòng act.setSupportActionBar(toolbar);
        // Để Toolbar tự quản lý menu qua XML (app:menu)

        // 2. Set title trực tiếp từ tham số truyền vào
        toolbar.setTitle(title);

        // 3. Xử lý nút Navigation bên trái (Nút Back hoặc Menu)
        if (act instanceof AdminActivity) {
            // Màn hình chính Dashboard: Giữ icon Menu (từ XML) hoặc ẩn đi tùy bạn
            // toolbar.setNavigationIcon(null); // Bỏ comment nếu muốn ẩn icon ở trang chủ
            toolbar.setNavigationOnClickListener(null); // Trang chủ không có hành động back
        } else {
            // Các màn hình con: Đổi icon thành mũi tên quay lại
            toolbar.setNavigationIcon(androidx.appcompat.R.drawable.abc_ic_ab_back_material);
            toolbar.setNavigationOnClickListener(v -> act.finish());
        }

        // 4. Xử lý sự kiện bấm vào Menu Item
        toolbar.setOnMenuItemClickListener(item -> {
            int id = item.getItemId();
            Intent intent = null;

            if (id == R.id.nav_products) {
                if (act instanceof AdminProductActivity) return true;
                intent = new Intent(act, AdminProductActivity.class);
            } else if (id == R.id.nav_customers) {
                if (act instanceof AdminCustomersActivity) return true;
                intent = new Intent(act, AdminCustomersActivity.class);
            } else if (id == R.id.nav_orders) {
                if (act instanceof AdminOrdersActivity) return true;
                intent = new Intent(act, AdminOrdersActivity.class);
            } else if (id == R.id.nav_revenue) {
                if (act instanceof AdminRevenueActivity) return true;
                intent = new Intent(act, AdminRevenueActivity.class);
            } else if (id == R.id.nav_logout) {
                // ✅ Logout ở mọi màn admin
                SharedPrefManager sp = new SharedPrefManager(act);
                sp.logout(); // bạn cần có hàm này (mình hướng dẫn bên dưới)

                Toast.makeText(act, "Đăng xuất thành công", Toast.LENGTH_SHORT).show();

                intent = new Intent(act, LoginActivity.class);
                // ✅ clear sạch stack
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            } else if (id == R.id.nav_admin) {
                if (act instanceof AdminActivity) return true;
                // Nếu muốn quay về Dashboard và xóa hết các màn hình cũ trong stack
                intent = new Intent(act, AdminActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            }

            if (intent != null) {
                act.startActivity(intent);
                return true;
            }
            return false;
        });
    }
}