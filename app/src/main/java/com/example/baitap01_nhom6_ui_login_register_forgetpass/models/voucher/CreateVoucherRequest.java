package com.example.baitap01_nhom6_ui_login_register_forgetpass.models.voucher;

import java.math.BigDecimal;

public class CreateVoucherRequest {
    public String code;
    public Integer phanTram;
    public BigDecimal giamToiDa;
    public String hieuLucTu;      // ISO Instant string
    public String hieuLucDen;     // ISO Instant string
    public Integer soLuongPhatHanh;
    public Boolean hoatDong;
}
