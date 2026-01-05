
package com.example.baitap01_nhom6_ui_login_register_forgetpass.models.dto;

import java.io.Serializable;

public class Address implements Serializable {
    private Long id;
    private Long userId;
    private String tenNguoiNhan;
    private String soDienThoai;
    private String tinhThanhPho;
    private String quanHuyen;
    private String phuongXa;
    private String diaChiChiTiet;
    private String loaiDiaChi; // "Nhà" hoặc "Văn phòng"
    private boolean isDefault;
    private String createdAt;
    private String updatedAt;

    // Constructor
    public Address() {
    }

    public Address(Long userId, String tenNguoiNhan, String soDienThoai,
                   String tinhThanhPho, String quanHuyen, String phuongXa,
                   String diaChiChiTiet, String loaiDiaChi, boolean isDefault) {
        this.userId = userId;
        this.tenNguoiNhan = tenNguoiNhan;
        this.soDienThoai = soDienThoai;
        this.tinhThanhPho = tinhThanhPho;
        this.quanHuyen = quanHuyen;
        this.phuongXa = phuongXa;
        this.diaChiChiTiet = diaChiChiTiet;
        this.loaiDiaChi = loaiDiaChi;
        this.isDefault = isDefault;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getTenNguoiNhan() {
        return tenNguoiNhan;
    }

    public void setTenNguoiNhan(String tenNguoiNhan) {
        this.tenNguoiNhan = tenNguoiNhan;
    }

    public String getSoDienThoai() {
        return soDienThoai;
    }

    public void setSoDienThoai(String soDienThoai) {
        this.soDienThoai = soDienThoai;
    }

    public String getTinhThanhPho() {
        return tinhThanhPho;
    }

    public void setTinhThanhPho(String tinhThanhPho) {
        this.tinhThanhPho = tinhThanhPho;
    }

    public String getQuanHuyen() {
        return quanHuyen;
    }

    public void setQuanHuyen(String quanHuyen) {
        this.quanHuyen = quanHuyen;
    }

    public String getPhuongXa() {
        return phuongXa;
    }

    public void setPhuongXa(String phuongXa) {
        this.phuongXa = phuongXa;
    }

    public String getDiaChiChiTiet() {
        return diaChiChiTiet;
    }

    public void setDiaChiChiTiet(String diaChiChiTiet) {
        this.diaChiChiTiet = diaChiChiTiet;
    }

    public String getLoaiDiaChi() {
        return loaiDiaChi;
    }

    public void setLoaiDiaChi(String loaiDiaChi) {
        this.loaiDiaChi = loaiDiaChi;
    }

    public boolean isDefault() {
        return isDefault;
    }

    public void setDefault(boolean aDefault) {
        isDefault = aDefault;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    // Phương thức trợ giúp để lấy địa chỉ đầy đủ
    public String getFullAddress() {
        StringBuilder sb = new StringBuilder();
        if (diaChiChiTiet != null && !diaChiChiTiet.isEmpty()) {
            sb.append(diaChiChiTiet).append(", ");
        }
        if (phuongXa != null && !phuongXa.isEmpty()) {
            sb.append(phuongXa).append(", ");
        }
        if (quanHuyen != null && !quanHuyen.isEmpty()) {
            sb.append(quanHuyen).append(", ");
        }
        if (tinhThanhPho != null && !tinhThanhPho.isEmpty()) {
            sb.append(tinhThanhPho);
        }
        return sb.toString();
    }
}