package com.example.baitap01_nhom6_ui_login_register_forgetpass.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.baitap01_nhom6_ui_login_register_forgetpass.R;
import com.example.baitap01_nhom6_ui_login_register_forgetpass.models.dto.ApiResponse;
import com.example.baitap01_nhom6_ui_login_register_forgetpass.models.dto.ResetPasswordRequest;
import com.example.baitap01_nhom6_ui_login_register_forgetpass.remote.ApiClient;
import com.example.baitap01_nhom6_ui_login_register_forgetpass.remote.ApiService;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ResetPasswordActivity extends AppCompatActivity {

    EditText edtPass, edtConfirm;
    Button btnSave;
    String email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);

        edtPass = findViewById(R.id.edtPassword);
        edtConfirm = findViewById(R.id.edtConfirmPassword);
        btnSave = findViewById(R.id.btnSavePassword);

        email = getIntent().getStringExtra("email");

        btnSave.setOnClickListener(v -> {
            String pass = edtPass.getText().toString().trim();
            String confirm = edtConfirm.getText().toString().trim();

            if (pass.isEmpty() || confirm.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập mật khẩu", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!pass.equals(confirm)) {
                Toast.makeText(this, "Mật khẩu không khớp", Toast.LENGTH_SHORT).show();
                return;
            }

            updatePassword(email, pass);
        });
    }

    private void updatePassword(String email, String newPass) {

        ApiService api = ApiClient.get();

        // 👉 Tạo DTO đúng kiểu mà API backend yêu cầu
        ResetPasswordRequest request = new ResetPasswordRequest(email, newPass);

        api.resetPassword(request).enqueue(new Callback<ApiResponse<Void>>() {
            @Override
            public void onResponse(Call<ApiResponse<Void>> call, Response<ApiResponse<Void>> response) {

                if (response.isSuccessful() && response.body() != null) {

                    ApiResponse<Void> res = response.body();
                    Toast.makeText(ResetPasswordActivity.this, res.getMessage(), Toast.LENGTH_SHORT).show();

                    if (res.isSuccess()) {
                        startActivity(new Intent(ResetPasswordActivity.this, LoginActivity.class));
                        finish();
                    }

                } else {
                    Toast.makeText(ResetPasswordActivity.this, "Lỗi server", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<Void>> call, Throwable t) {
                Toast.makeText(ResetPasswordActivity.this, "Không thể kết nối API", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
