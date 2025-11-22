package com.example.baitap01_nhom6_ui_login_register_forgetpass.models.dto;

public class UserDto {
    public String id;
    public String email;
    public String hoTen;

    public int getId() {
        try {
            return Integer.parseInt(id);
        } catch (Exception e) { return -1; }
    }

    public String getEmail() {
        return email;
    }

    public String getHoTen() {
        return hoTen;
    }
}
