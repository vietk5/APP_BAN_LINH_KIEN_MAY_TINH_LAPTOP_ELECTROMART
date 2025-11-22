package com.example.baitap01_nhom6_ui_login_register_forgetpass.models.dto;

public class BrandDto {
    public Long id;
    public String ten;

    // Hàm này giúp hiển thị tên trong Spinner thay vì hiện mã object
    @Override
    public String toString() {
        return ten;
    }

    // Getter (cần thiết nếu dùng Stream hoặc một số thư viện)
    public Long getId() { return id; }
    public String getTen() { return ten; }
}