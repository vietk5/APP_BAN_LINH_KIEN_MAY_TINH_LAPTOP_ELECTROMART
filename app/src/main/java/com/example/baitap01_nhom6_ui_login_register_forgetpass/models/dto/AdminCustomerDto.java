package com.example.baitap01_nhom6_ui_login_register_forgetpass.models.dto;

import com.google.gson.annotations.SerializedName;

public class AdminCustomerDto {
    public long id;

    @SerializedName("fullName")
    public String fullName;

    public String email;
    public String phone;

    @SerializedName("createdAt")
    public String createdAt;  // ISO string, hiển thị format lại

    public boolean blocked;
}
