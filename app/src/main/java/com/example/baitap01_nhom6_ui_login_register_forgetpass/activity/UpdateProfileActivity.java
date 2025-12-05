package com.example.baitap01_nhom6_ui_login_register_forgetpass.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.baitap01_nhom6_ui_login_register_forgetpass.R;
import com.example.baitap01_nhom6_ui_login_register_forgetpass.models.dto.ApiResponse;
import com.example.baitap01_nhom6_ui_login_register_forgetpass.models.dto.UpdateProfileRequest;
import com.example.baitap01_nhom6_ui_login_register_forgetpass.models.dto.UserDto;
import com.example.baitap01_nhom6_ui_login_register_forgetpass.remote.ApiClient;
import com.example.baitap01_nhom6_ui_login_register_forgetpass.remote.ApiService;
import com.example.baitap01_nhom6_ui_login_register_forgetpass.util.AdminNavHelper;
import com.example.baitap01_nhom6_ui_login_register_forgetpass.util.SharedPrefManager;
import com.google.android.material.appbar.MaterialToolbar;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UpdateProfileActivity extends AppCompatActivity {
    private TextView tvEmail;
    private EditText etHoTen, etSoDienThoai;
    private LinearLayout btnUpdate;
    private ProgressBar progressBar;

    private SharedPrefManager sharedPref;
    private ApiService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_profile);

        MaterialToolbar toolbar = findViewById(R.id.userToolbar);
        AdminNavHelper.setupToolbar(this, toolbar, "Thông tin cá nhân");
        toolbar.setNavigationOnClickListener(v -> finish());

        sharedPref = new SharedPrefManager(this);
        apiService = ApiClient.get();

        initViews();
        loadUserInfo();
    }

    private void initViews() {
        tvEmail = findViewById(R.id.tv_email);
        etHoTen = findViewById(R.id.et_ho_ten);
        etSoDienThoai = findViewById(R.id.et_so_dien_thoai);
        btnUpdate = findViewById(R.id.btn_update);
        progressBar = findViewById(R.id.progress_bar);
        btnUpdate.setOnClickListener(v -> updateProfile());
    }

    private void loadUserInfo() {
        Long userId = (long) sharedPref.getUserId();
        if (userId == null || userId == -1) {
            Toast.makeText(this, "Vui lòng đăng nhập lại", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        showLoading(true);
        apiService.getProfile(userId).enqueue(new Callback<ApiResponse<UserDto>>() {
            @Override
            public void onResponse(Call<ApiResponse<UserDto>> call, Response<ApiResponse<UserDto>> response) {
                showLoading(false);
                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse<UserDto> apiResponse = response.body();
                    if (apiResponse.isSuccess() && apiResponse.getData() != null) {
                        displayUserInfo(apiResponse.getData());
                    } else {
                        Toast.makeText(UpdateProfileActivity.this,
                                apiResponse.getMessage() != null ? apiResponse.getMessage() : "Không thể tải thông tin",
                                Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(UpdateProfileActivity.this,
                            "Lỗi khi tải thông tin",
                            Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<UserDto>> call, Throwable t) {
                showLoading(false);
                Toast.makeText(UpdateProfileActivity.this,
                        "Lỗi kết nối: " + t.getMessage(),
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void displayUserInfo(UserDto user) {
        tvEmail.setText(user.getEmail() != null ? user.getEmail() : "");
        etHoTen.setText(user.getHoTen() != null ? user.getHoTen() : "");
        etSoDienThoai.setText(user.getSoDienThoai() != null ? user.getSoDienThoai() : "");
    }

    private void updateProfile() {
        String hoTen = etHoTen.getText().toString().trim();
        String soDienThoai = etSoDienThoai.getText().toString().trim();

        // Validation
        if (hoTen.isEmpty()) {
            etHoTen.setError("Vui lòng nhập họ tên");
            etHoTen.requestFocus();
            return;
        }

        if (!soDienThoai.isEmpty() && !soDienThoai.matches("^[0-9]{10,11}$")) {
            etSoDienThoai.setError("Số điện thoại không hợp lệ");
            etSoDienThoai.requestFocus();
            return;
        }

        Long userId = (long) sharedPref.getUserId();
        if (userId == null || userId == -1) {
            Toast.makeText(this, "Vui lòng đăng nhập lại", Toast.LENGTH_SHORT).show();
            return;
        }

        UpdateProfileRequest request = new UpdateProfileRequest();
        request.setHoTen(hoTen);
        request.setSoDienThoai(soDienThoai.isEmpty() ? null : soDienThoai);

        showLoading(true);
        apiService.updateProfile(userId, request).enqueue(new Callback<ApiResponse<UserDto>>() {
            @Override
            public void onResponse(Call<ApiResponse<UserDto>> call, Response<ApiResponse<UserDto>> response) {
                showLoading(false);
                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse<UserDto> apiResponse = response.body();
                    if (apiResponse.isSuccess() && apiResponse.getData() != null) {
                        UserDto updatedUser = apiResponse.getData();

                        // Cập nhật SharedPreferences
                        sharedPref.saveUser(
                                updatedUser.getId(),
                                updatedUser.getEmail(),
                                updatedUser.getHoTen() != null ? updatedUser.getHoTen() : ""
                        );

                        Toast.makeText(UpdateProfileActivity.this,
                                "Cập nhật thông tin thành công!",
                                Toast.LENGTH_SHORT).show();

                        setResult(RESULT_OK);
                        finish();
                    } else {
                        Toast.makeText(UpdateProfileActivity.this,
                                apiResponse.getMessage() != null ? apiResponse.getMessage() : "Cập nhật thất bại",
                                Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(UpdateProfileActivity.this,
                            "Cập nhật thất bại",
                            Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<UserDto>> call, Throwable t) {
                showLoading(false);
                Toast.makeText(UpdateProfileActivity.this,
                        "Lỗi kết nối: " + t.getMessage(),
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showLoading(boolean isLoading) {
        progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        btnUpdate.setEnabled(!isLoading);
        etHoTen.setEnabled(!isLoading);
        etSoDienThoai.setEnabled(!isLoading);
    }
}