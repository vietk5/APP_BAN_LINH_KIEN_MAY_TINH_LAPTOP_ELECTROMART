package com.example.baitap01_nhom6_ui_login_register_forgetpass.activity;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.*;

import com.example.baitap01_nhom6_ui_login_register_forgetpass.R;
import com.example.baitap01_nhom6_ui_login_register_forgetpass.models.dto.ApiResponse;
import com.example.baitap01_nhom6_ui_login_register_forgetpass.models.dto.UserDto;
import com.example.baitap01_nhom6_ui_login_register_forgetpass.models.dto.UserLoginRequest;
import com.example.baitap01_nhom6_ui_login_register_forgetpass.remote.ApiClient;
import com.example.baitap01_nhom6_ui_login_register_forgetpass.remote.ApiService;
import com.example.baitap01_nhom6_ui_login_register_forgetpass.util.SharedPrefManager;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    EditText edtEmail, edtPassword;
    Button btnLogin;
    TextView tvSignup, tvForgotPass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedPrefManager sharedPref = new SharedPrefManager(this);
        if (sharedPref.isLoggedIn()) {
            // Đã login → chuyển thẳng sang MainActivity
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
            return;
        }

        setContentView(R.layout.activity_login);

        edtEmail = findViewById(R.id.edtEmail);
        edtPassword = findViewById(R.id.edtPassword);
        btnLogin = findViewById(R.id.btnLogin);

        tvSignup = findViewById(R.id.tvSignUp);        // TextView “Don’t have an account? Sign up”
        tvForgotPass = findViewById(R.id.tvForgotPass); // TextView “Forgot password?”
        // Chuyển sang trang đăng kí
        tvSignup.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(intent);
        });

        // Chuyển sang trang quên mật khẩu
        tvForgotPass.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, ForgetPasswordActivity.class);
            startActivity(intent);
        });

        btnLogin.setOnClickListener(v -> {
            String email = edtEmail.getText().toString().trim();
            String password = edtPassword.getText().toString().trim();

            // Validate email
            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập email và mật khẩu", Toast.LENGTH_SHORT).show();
                return;
            }

            // ✅ ADMIN HARD-CODE (theo yêu cầu)
            if (email.equalsIgnoreCase("admin@electromart.com") && password.equals("admin123")) {
                // (tuỳ chọn) lưu trạng thái admin để lần sau vào thẳng admin
                SharedPrefManager sp = new SharedPrefManager(LoginActivity.this);
                sp.saveUser(0, "admin@electromart.com", "Admin"); // hoặc tạo method saveAdmin
                // nếu bạn có saveAdmin/isAdmin thì dùng sẽ chuẩn hơn

                Intent intent = new Intent(LoginActivity.this, AdminActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
                return;
            }

            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                Toast.makeText(this, "Email không hợp lệ", Toast.LENGTH_SHORT).show();
                return;
            }

            UserLoginRequest req = new UserLoginRequest(email, password);
            ApiService api = ApiClient.get();
            api.login(req).enqueue(new Callback<ApiResponse<UserDto>>() {
                @Override
                public void onResponse(Call<ApiResponse<UserDto>> call, Response<ApiResponse<UserDto>> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        ApiResponse<UserDto> res = response.body();
                        Toast.makeText(LoginActivity.this, res.getMessage(), Toast.LENGTH_SHORT).show();
                        if (res.isSuccess()) {
                            int userId = res.getData().getId();
                            String userEmail = res.getData().getEmail();
                            String userName = res.getData().getHoTen();

                            SharedPrefManager sharedPref = new SharedPrefManager(LoginActivity.this);
                            sharedPref.saveUser(userId, userEmail, userName);

                            startActivity(new Intent(LoginActivity.this, MainActivity.class));
                            finish();
                        }
                    } else {
                        Toast.makeText(LoginActivity.this, "Lỗi server", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<ApiResponse<UserDto>> call, Throwable t) {
                    Toast.makeText(LoginActivity.this, "Không thể kết nối API", Toast.LENGTH_SHORT).show();
                }
            });
        });
    }
}
