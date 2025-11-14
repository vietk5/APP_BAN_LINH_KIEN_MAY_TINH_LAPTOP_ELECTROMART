package com.example.baitap01_nhom6_ui_login_register_forgetpass.activity;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.*;

import com.example.baitap01_nhom6_ui_login_register_forgetpass.R;
import com.example.baitap01_nhom6_ui_login_register_forgetpass.models.dto.ApiResponse;
import com.example.baitap01_nhom6_ui_login_register_forgetpass.models.dto.UserDto;
import com.example.baitap01_nhom6_ui_login_register_forgetpass.models.dto.UserLoginRequest;
import com.example.baitap01_nhom6_ui_login_register_forgetpass.remote.ApiClient;
import com.example.baitap01_nhom6_ui_login_register_forgetpass.remote.ApiService;
import com.example.baitap01_nhom6_ui_login_register_forgetpass.util.SharedPrefManager;
import com.google.gson.Gson;

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
        setContentView(R.layout.activity_login);

        edtEmail = findViewById(R.id.edtEmail);
        edtPassword = findViewById(R.id.edtPassword);
        btnLogin = findViewById(R.id.btnLogin);

        tvSignup = findViewById(R.id.tvSignUp);        // TextView “Don’t have an account? Sign up”
        tvForgotPass = findViewById(R.id.tvForgotPass); // TextView “Forgot password?”

        btnLogin.setOnClickListener(v -> {
            String email = edtEmail.getText().toString().trim();
            String password = edtPassword.getText().toString().trim();

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập email và mật khẩu", Toast.LENGTH_SHORT).show();
                return;
            }

            UserLoginRequest req = new UserLoginRequest(email, password);
            ApiService api = ApiClient.get();
            api.login(req).enqueue(new Callback<ApiResponse<UserDto>>() {
                @Override
                public void onResponse(Call<ApiResponse<UserDto>> call, Response<ApiResponse<UserDto>> response) {
                    if (response.isSuccessful() && response.body() != null) {

                        ApiResponse<UserDto> res = response.body();

                        if (res.success) {
                            UserDto user = res.data;

                            SharedPrefManager sp = new SharedPrefManager(LoginActivity.this);
                            sp.saveUser(user.id, user.email, user.hoTen);

                            startActivity(new Intent(LoginActivity.this, HomeActivity.class));
                            finish();
                        }

                        Toast.makeText(LoginActivity.this, res.message, Toast.LENGTH_SHORT).show();
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
