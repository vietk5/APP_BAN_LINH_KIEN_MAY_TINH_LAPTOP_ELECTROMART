package com.example.baitap01_nhom6_ui_login_register_forgetpass.models.dto;
public class ForgotPasswordRequest {
    private String email;
    private String otp;

    // Constructor cho gửi email (quên mật khẩu)
    public ForgotPasswordRequest(String email) {
        this.email = email;
    }

    // Constructor cho verify OTP
    public ForgotPasswordRequest(String email, String otp) {
        this.email = email;
        this.otp = otp;
    }
    public String getEmail() {
        return email;
    }

    public String getOtp() {
        return otp;
    }
}