package com.example.baitap01_nhom6_ui_login_register_forgetpass.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.baitap01_nhom6_ui_login_register_forgetpass.R;
import com.example.baitap01_nhom6_ui_login_register_forgetpass.models.dto.ApiResponse;
import com.example.baitap01_nhom6_ui_login_register_forgetpass.models.dto.UserDto;
import com.example.baitap01_nhom6_ui_login_register_forgetpass.remote.ApiClient;
import com.example.baitap01_nhom6_ui_login_register_forgetpass.remote.ApiService;
import com.example.baitap01_nhom6_ui_login_register_forgetpass.util.SharedPrefManager;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfileActivity extends AppCompatActivity {

    private SharedPrefManager sharedPref;
    private ApiService apiService;

    private TextView tvUserName, tvUserEmail;
    private CircleImageView ivAvatar;
    private LinearLayout menuProfile, menuOrders, menuAddress, btnLogout, menuChangePassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        sharedPref = new SharedPrefManager(this);
        apiService = ApiClient.get();

        // Kiểm tra đăng nhập
        if (!sharedPref.isLoggedIn()) {
            showLoginDialog();
            return;
        }

        initViews();
        loadUserInfoFromApi();
        setupClickListeners();
    }

    private void initViews() {
        tvUserName = findViewById(R.id.tv_user_name);
        tvUserEmail = findViewById(R.id.tv_user_email);
        ivAvatar = findViewById(R.id.iv_avatar);

        menuProfile = findViewById(R.id.menu_profile);
        menuOrders = findViewById(R.id.menu_orders);
        menuAddress = findViewById(R.id.menu_address);
        menuChangePassword = findViewById(R.id.menu_change_password);
        btnLogout = findViewById(R.id.btn_logout);
    }

    /**
     * Load thông tin user từ API để đảm bảo dữ liệu mới nhất
     */
    private void loadUserInfoFromApi() {
        Long userId = (long) sharedPref.getUserId();
        if (userId == null || userId == -1) {
            // Fallback: hiển thị từ SharedPreferences
            loadUserInfoFromSharedPref();
            return;
        }

        apiService.getProfile(userId).enqueue(new Callback<ApiResponse<UserDto>>() {
            @Override
            public void onResponse(Call<ApiResponse<UserDto>> call, Response<ApiResponse<UserDto>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse<UserDto> apiResponse = response.body();
                    if (apiResponse.isSuccess() && apiResponse.getData() != null) {
                        UserDto user = apiResponse.getData();
                        displayUserInfo(user);

                        // Cập nhật lại SharedPreferences với data mới nhất
                        sharedPref.saveUser(
                                user.getId(),
                                user.getEmail(),
                                user.getHoTen() != null ? user.getHoTen() : ""
                        );
                    } else {
                        // Fallback nếu API fail
                        loadUserInfoFromSharedPref();
                    }
                } else {
                    // Fallback nếu API fail
                    loadUserInfoFromSharedPref();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<UserDto>> call, Throwable t) {
                // Fallback nếu API fail
                loadUserInfoFromSharedPref();
            }
        });
    }

    /**
     * Fallback: Load từ SharedPreferences nếu API không khả dụng
     */
    private void loadUserInfoFromSharedPref() {
        String hoTen = sharedPref.getName();
        String email = sharedPref.getEmail();

        if (hoTen != null && !hoTen.isEmpty()) {
            tvUserName.setText(hoTen);
        } else {
            tvUserName.setText("Người dùng");
        }

        if (email != null) {
            tvUserEmail.setText(email);
        }
    }

    private void displayUserInfo(UserDto user) {
        if (user.getHoTen() != null && !user.getHoTen().isEmpty()) {
            tvUserName.setText(user.getHoTen());
        } else {
            tvUserName.setText("Người dùng");
        }

        if (user.getEmail() != null) {
            tvUserEmail.setText(user.getEmail());
        }
    }

    private void setupClickListeners() {
        // Đơn hàng của tôi
        menuOrders.setOnClickListener(v -> {
            Intent intent = new Intent(ProfileActivity.this, MyOrdersActivity.class);
//            startActivityForResult(intent, 100);
            startActivity(intent);
//            finish();
        });

        // Xem/Sửa thông tin cá nhân
        menuProfile.setOnClickListener(v -> {
            Intent intent = new Intent(ProfileActivity.this, UpdateProfileActivity.class);
            startActivityForResult(intent, 100);
        });

        // Địa chỉ đã lưu
        menuAddress.setOnClickListener(v -> {
            Toast.makeText(this, "Chức năng đang phát triển", Toast.LENGTH_SHORT).show();
            // Chuyển sang AddressActivity
        });

        // Thay đổi mật khẩu
        menuChangePassword.setOnClickListener(v -> {
            Intent intent = new Intent(ProfileActivity.this, ChangePasswordActivity.class);
            startActivityForResult(intent, 100);
        });

        // Đăng xuất
        btnLogout.setOnClickListener(v -> {
            new AlertDialog.Builder(this)
                    .setTitle("Đăng xuất")
                    .setMessage("Bạn có chắc chắn muốn đăng xuất?")
                    .setPositiveButton("Đăng xuất", (dialog, which) -> {
                        sharedPref.logout();
                        Intent intent = new Intent(ProfileActivity.this, HomeActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        Toast.makeText(this, "Đăng xuất thành công", Toast.LENGTH_SHORT).show();
                    })
                    .setNegativeButton("Hủy", null)
                    .show();
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100 && resultCode == RESULT_OK) {
            // Reload lại thông tin user sau khi update
            loadUserInfoFromApi();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Reload thông tin mỗi khi quay lại màn hình
        if (sharedPref.isLoggedIn()) {
            loadUserInfoFromApi();
        }
    }

    private void showLoginDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Yêu cầu đăng nhập");
        builder.setMessage("Bạn cần đăng nhập để xem trang cá nhân.");

        builder.setPositiveButton("Đăng nhập", (dialog, which) -> {
            startActivity(new Intent(ProfileActivity.this, LoginActivity.class));
            finish();
        });

        builder.setNegativeButton("Đăng ký", (dialog, which) -> {
            startActivity(new Intent(ProfileActivity.this, RegisterActivity.class));
            finish();
        });

        builder.setCancelable(false);
        builder.show();
    }
}