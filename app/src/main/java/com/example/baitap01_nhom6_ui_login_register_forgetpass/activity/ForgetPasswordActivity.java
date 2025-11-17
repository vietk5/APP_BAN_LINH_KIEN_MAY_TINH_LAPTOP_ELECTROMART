package com.example.baitap01_nhom6_ui_login_register_forgetpass.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.baitap01_nhom6_ui_login_register_forgetpass.R;
import com.example.baitap01_nhom6_ui_login_register_forgetpass.models.dto.ApiResponse;
import com.example.baitap01_nhom6_ui_login_register_forgetpass.models.dto.ForgotPasswordRequest;
import com.example.baitap01_nhom6_ui_login_register_forgetpass.remote.ApiClient;
import com.example.baitap01_nhom6_ui_login_register_forgetpass.remote.ApiService;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ForgetPasswordActivity extends AppCompatActivity {

    private EditText edtEmail;
    private Button btnReset;
    private TextView tvBackLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_forget_password);

        edtEmail = findViewById(R.id.edtEmail);
        btnReset = findViewById(R.id.btnReset);
        tvBackLogin = findViewById(R.id.tvBackLogin);

        // Quay lại trang login
        tvBackLogin.setOnClickListener(v -> {
            Intent intent = new Intent(ForgetPasswordActivity.this, LoginActivity.class);
            startActivity(intent);
            finish(); // đóng trang quên mật khẩu
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        });

        // Gửi OTP
        btnReset.setOnClickListener(v -> {
            String email = edtEmail.getText().toString().trim();

            if (email.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập email", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                Toast.makeText(this, "Email không hợp lệ", Toast.LENGTH_SHORT).show();
                return;
            }

            sendOtp(email);
        });
    }

    private void sendOtp(String email) {
        ApiService api = ApiClient.get();

        // Tạo DTO ForgotPasswordRequest để gửi
        ForgotPasswordRequest request = new ForgotPasswordRequest(email);

        api.forgotPassword(request).enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse res = response.body();

                    // Hiển thị thông báo
                    Toast.makeText(ForgetPasswordActivity.this, res.getMessage(), Toast.LENGTH_SHORT).show();

                    // Nếu gửi OTP thành công, chuyển sang màn hình nhập OTP
                    if (res.isSuccess()) {
                        Intent intent = new Intent(ForgetPasswordActivity.this, OtpVerifyActivity.class);
                        intent.putExtra("email", email);
                        startActivity(intent);
                        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                    }

                } else {
                    Toast.makeText(ForgetPasswordActivity.this, "Lỗi server", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                Toast.makeText(ForgetPasswordActivity.this, "Không thể kết nối API", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
