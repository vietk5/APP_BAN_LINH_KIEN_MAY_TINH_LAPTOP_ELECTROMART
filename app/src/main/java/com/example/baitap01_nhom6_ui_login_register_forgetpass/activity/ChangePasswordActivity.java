package com.example.baitap01_nhom6_ui_login_register_forgetpass.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.baitap01_nhom6_ui_login_register_forgetpass.R;
import com.example.baitap01_nhom6_ui_login_register_forgetpass.models.dto.ApiResponse;
import com.example.baitap01_nhom6_ui_login_register_forgetpass.models.dto.ChangePasswordRequest;
import com.example.baitap01_nhom6_ui_login_register_forgetpass.remote.ApiClient;
import com.example.baitap01_nhom6_ui_login_register_forgetpass.remote.ApiService;
import com.example.baitap01_nhom6_ui_login_register_forgetpass.util.SharedPrefManager;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChangePasswordActivity extends AppCompatActivity {

    private EditText etNewPassword, etReNewPassword;
    private TextView etEmail;
    private LinearLayout btnChangePassword;
    private ProgressBar progressBar;

    private SharedPrefManager sharedPref;
    private ApiService apiService;
    private Long userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        sharedPref = new SharedPrefManager(this);
        apiService = ApiClient.get();

        userId = (long) sharedPref.getUserId();
        if (userId == null || userId == -1) {
            Toast.makeText(this, "Vui lòng đăng nhập lại", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        initViews();
    }

    private void initViews() {
        etEmail = findViewById(R.id.etEmail);
        etNewPassword = findViewById(R.id.etNewPassword);
        etReNewPassword = findViewById(R.id.etReNewPassword);
        btnChangePassword = findViewById(R.id.btnChangePassword);
        progressBar = findViewById(R.id.progress_bar);

        // LẤY EMAIL TỪ SHAREDPREF
        String email = sharedPref.getEmail();
        if (email != null) {
            etEmail.setText(email);
            etEmail.setEnabled(false); // không cho user sửa email
        }


        btnChangePassword.setOnClickListener(v -> changePassword());
    }

    private void changePassword() {
        String newPass = etNewPassword.getText().toString().trim();
        String reNewPass = etReNewPassword.getText().toString().trim();

        // Validation
        if (newPass.isEmpty()) {
            etNewPassword.setError("Vui lòng nhập mật khẩu mới");
            etNewPassword.requestFocus();
            return;
        }

        if (newPass.length() < 6) {
            etNewPassword.setError("Mật khẩu phải có ít nhất 6 ký tự");
            etNewPassword.requestFocus();
            return;
        }

        if (!newPass.equals(reNewPass)) {
            etReNewPassword.setError("Mật khẩu xác nhận không khớp");
            etReNewPassword.requestFocus();
            return;
        }

        // Disable button + show progress
        btnChangePassword.setEnabled(false);
        progressBar.setVisibility(View.VISIBLE);

        ChangePasswordRequest request = new ChangePasswordRequest();
        request.setNewPassword(newPass);

        apiService.changePassword(userId, request)
                .enqueue(new Callback<ApiResponse<Void>>() {
                    @Override
                    public void onResponse(Call<ApiResponse<Void>> call, Response<ApiResponse<Void>> response) {
                        btnChangePassword.setEnabled(true);
                        progressBar.setVisibility(View.GONE);

                        if (response.isSuccessful() && response.body() != null) {
                            ApiResponse<Void> apiResponse = response.body();
                            if (apiResponse.isSuccess()) {
                                Toast.makeText(ChangePasswordActivity.this,
                                        "Đổi mật khẩu thành công", Toast.LENGTH_SHORT).show();
                                finish();
                            } else {
                                Toast.makeText(ChangePasswordActivity.this,
                                        apiResponse.getMessage() != null ? apiResponse.getMessage() : "Đổi mật khẩu thất bại",
                                        Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(ChangePasswordActivity.this,
                                    "Đổi mật khẩu thất bại", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<ApiResponse<Void>> call, Throwable t) {
                        btnChangePassword.setEnabled(true);
                        progressBar.setVisibility(View.GONE);
                        Toast.makeText(ChangePasswordActivity.this,
                                "Lỗi kết nối: " + t.getMessage(),
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
