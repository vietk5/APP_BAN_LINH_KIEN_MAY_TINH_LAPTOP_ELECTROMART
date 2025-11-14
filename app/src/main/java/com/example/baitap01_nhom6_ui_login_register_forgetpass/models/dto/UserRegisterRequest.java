package com.example.baitap01_nhom6_ui_login_register_forgetpass.models.dto;

public class UserRegisterRequest {
    public String hoTen;
    public String email;
    public String matKhau;

    public UserRegisterRequest(String hoTen, String email, String matKhau) {
        this.hoTen = hoTen;
        this.email = email;
        this.matKhau = matKhau;
    }
}