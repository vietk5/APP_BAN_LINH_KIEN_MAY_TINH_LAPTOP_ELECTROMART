package com.example.baitap01_nhom6_ui_login_register_forgetpass.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.baitap01_nhom6_ui_login_register_forgetpass.R;
import com.example.baitap01_nhom6_ui_login_register_forgetpass.models.dto.ApiResponse;
import com.example.baitap01_nhom6_ui_login_register_forgetpass.models.dto.UserRegisterRequest;
import com.example.baitap01_nhom6_ui_login_register_forgetpass.remote.ApiClient;
import com.example.baitap01_nhom6_ui_login_register_forgetpass.remote.ApiService;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class RegisterActivity extends AppCompatActivity {
    private EditText edtName, edtEmail, edtPassword;
    private Button btnRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register);

        edtName = findViewById(R.id.signupName);
        edtEmail = findViewById(R.id.signupEmail);
        edtPassword = findViewById(R.id.signupPassword);
        btnRegister = findViewById(R.id.btnSignup);
        btnRegister.setOnClickListener(v -> handleRegister());

        // 👉 Tìm TextView bằng id
        TextView tvLogin = findViewById(R.id.tvLogin);
        // 👉 Khi người dùng bấm "Log in" → chuyển sang LoginActivity
        tvLogin.setOnClickListener(v -> {
            Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
            startActivity(intent);
            finish(); // Đóng trang đăng ký để không quay lại bằng nút Back
        });
    }
    private void handleRegister() {
        String name = edtName.getText().toString().trim();
        String email = edtEmail.getText().toString().trim();
        String password = edtPassword.getText().toString().trim();

        if (name.isEmpty() || email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show();
            return;
        }

        UserRegisterRequest request = new UserRegisterRequest(name, email, password);

        ApiService apiService = ApiClient.get();
        apiService.register(request).enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                if (response.isSuccessful() && response.body() != null) {

                    ApiResponse apiRes = response.body();
                    Toast.makeText(RegisterActivity.this, apiRes.message, Toast.LENGTH_SHORT).show();

                    if (apiRes.success) {
                        startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                        finish();
                    }
                } else {
                    Toast.makeText(RegisterActivity.this, "Lỗi server", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                Toast.makeText(RegisterActivity.this, "Không thể kết nối API", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
