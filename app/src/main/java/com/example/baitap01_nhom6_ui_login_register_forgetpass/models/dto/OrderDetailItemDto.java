package com.example.baitap01_nhom6_ui_login_register_forgetpass.models.dto;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class OrderDetailItemDto implements Serializable {

    @SerializedName("productId")
    private int productId;

    @SerializedName("productName")
    private String productName;

    @SerializedName("imageUrl")
    private String imageUrl;

    @SerializedName("soLuong")
    private int soLuong;

    @SerializedName("donGia")
    private Long donGia;

    @SerializedName("thanhTien")
    private int thanhTien;

    public OrderDetailItemDto() {}

    public int getProductId() { return productId; }
    public void setProductId(int productId) { this.productId = productId; }

    public String getProductName() { return productName; }
    public void setProductName(String productName) { this.productName = productName; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    public Long getDonGia() { return donGia; }
    public void setDonGia(Long donGia) { this.donGia = donGia; }

    public int getSoLuong() { return soLuong; }
    public void setSoLuong(int soLuong) { this.soLuong = soLuong; }
    public int getThanhTien() {
        return thanhTien;
    }

    public void setThanhTien(int thanhTien) {
        this.thanhTien = thanhTien;
    }
}
