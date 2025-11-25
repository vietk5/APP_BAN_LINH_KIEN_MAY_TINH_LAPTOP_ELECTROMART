package com.example.baitap01_nhom6_ui_login_register_forgetpass.models.dto;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

public class OrderDetailDto implements Serializable {

    @SerializedName("id")
    private long id;

    @SerializedName("ngayDatHang")
    private String  ngayDatHang;

    @SerializedName("trangThai")
    private String trangThai;
    @SerializedName("customerName")
    private String customerName;

    @SerializedName("customerEmail")
    private String customerEmail;

    @SerializedName("customerPhone")
    private String customerPhone;

    @SerializedName("tongTien")
    private long tongTien;

    @SerializedName("phuongThucThanhToan")
    private String phuongThucThanhToan;

    @SerializedName("items")
    private List<OrderDetailItemDto> items;

    public OrderDetailDto() {}

    public long getId() { return id; }
    public void setId(long id) { this.id = id; }

    public String  getNgayDatHang() { return ngayDatHang; }
    public void setNgayDatHang(String  ngayDatHang) { this.ngayDatHang = ngayDatHang; }

    public String getTrangThai() { return trangThai; }
    public void setTrangThai(String trangThai) { this.trangThai = trangThai; }

    public long getTongTien() { return tongTien; }
    public void setTongTien(long tongTien) { this.tongTien = tongTien; }

    public String getPhuongThucThanhToan() { return phuongThucThanhToan; }
    public void setPhuongThucThanhToan(String phuongThucThanhToan) { this.phuongThucThanhToan = phuongThucThanhToan; }

    public List<OrderDetailItemDto> getItems() { return items; }
    public void setItems(List<OrderDetailItemDto> items) { this.items = items; }
    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getCustomerEmail() {
        return customerEmail;
    }

    public void setCustomerEmail(String customerEmail) {
        this.customerEmail = customerEmail;
    }

    public String getCustomerPhone() {
        return customerPhone;
    }

    public void setCustomerPhone(String customerPhone) {
        this.customerPhone = customerPhone;
    }
}