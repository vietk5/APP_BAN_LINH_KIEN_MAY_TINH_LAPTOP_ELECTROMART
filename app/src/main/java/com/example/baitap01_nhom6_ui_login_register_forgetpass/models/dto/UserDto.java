package com.example.baitap01_nhom6_ui_login_register_forgetpass.models.dto;

public class UserDto {
    public String id;
    public String email;
    public String hoTen;
    public String soDienThoai;

    public UserDto() {
    }

    public UserDto(String id, String email, String hoTen, String soDienThoai) {
        this.id = id;
        this.email = email;
        this.hoTen = hoTen;
        this.soDienThoai = soDienThoai;
    }

    public int getId() {
        try {
            return Integer.parseInt(id);
        } catch (Exception e) {
            return -1;
        }
    }

    public long getIdLong() {
        try {
            return Long.parseLong(id);
        } catch (Exception e) {
            return -1L;
        }
    }

    public String getEmail() {
        return email;
    }

    public String getHoTen() {
        return hoTen;
    }

    public String getSoDienThoai() {
        return soDienThoai;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setHoTen(String hoTen) {
        this.hoTen = hoTen;
    }

    public void setSoDienThoai(String soDienThoai) {
        this.soDienThoai = soDienThoai;
    }
}