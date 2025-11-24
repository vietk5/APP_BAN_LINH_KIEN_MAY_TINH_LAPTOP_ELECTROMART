package com.example.baitap01_nhom6_ui_login_register_forgetpass.models.dto;

public class UpdateProfileRequest {
    private String hoTen;
    private String soDienThoai;

    public UpdateProfileRequest() {
    }

    public UpdateProfileRequest(String hoTen, String soDienThoai) {
        this.hoTen = hoTen;
        this.soDienThoai = soDienThoai;
    }

    public String getHoTen() {
        return hoTen;
    }

    public void setHoTen(String hoTen) {
        this.hoTen = hoTen;
    }

    public String getSoDienThoai() {
        return soDienThoai;
    }

    public void setSoDienThoai(String soDienThoai) {
        this.soDienThoai = soDienThoai;
    }
}