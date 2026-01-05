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
    @SerializedName("tenNguoiNhan")
    private String tenNguoiNhan;
    @SerializedName("soDienThoaiNhan")
    private String soDienThoaiNhan;

    @SerializedName("diaChiNhan")
    private String diaChiNhan;

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

    public String getTenNguoiNhan() {
        return tenNguoiNhan;
    }

    public void setTenNguoiNhan(String tenNguoiNhan) {
        this.tenNguoiNhan = tenNguoiNhan;
    }

    public String getSoDienThoaiNhan() {
        return soDienThoaiNhan;
    }

    public void setSoDienThoaiNhan(String soDienThoaiNhan) {
        this.soDienThoaiNhan = soDienThoaiNhan;
    }

    public String getDiaChiNhan() {
        return diaChiNhan;
    }

    public void setDiaChiNhan(String diaChiNhan) {
        this.diaChiNhan = diaChiNhan;
    }
}