package com.example.baitap01_nhom6_ui_login_register_forgetpass.models.dto;

public class UserLoginRequest {
    public String email;
    public String matKhau;
    public UserLoginRequest(String email, String matKhau) {
        this.email = email;
        this.matKhau = matKhau;
    }
}
