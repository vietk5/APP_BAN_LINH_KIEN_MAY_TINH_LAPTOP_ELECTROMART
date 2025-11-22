package com.example.baitap01_nhom6_ui_login_register_forgetpass.models.dto;

public class AdminOrderDto {
    public long id;
    public String customerName;
    public long totalAmount;
    public String status;
    public String createdAt;
    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }
}
