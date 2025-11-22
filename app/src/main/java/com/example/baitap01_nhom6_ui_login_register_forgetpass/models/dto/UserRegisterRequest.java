package com.example.baitap01_nhom6_ui_login_register_forgetpass.models.dto;

public class UserRegisterRequest {
    public String email;
    public String matKhau;
    public String hoTen;

    public UserRegisterRequest(String email, String matKhau, String hoTen) {
        this.hoTen = hoTen;
        this.email = email;
        this.matKhau = matKhau;
    }
}