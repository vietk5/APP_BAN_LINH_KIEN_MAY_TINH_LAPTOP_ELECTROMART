package com.example.baitap01_nhom6_ui_login_register_forgetpass.models.voucher;

import java.io.Serializable;
import java.math.BigDecimal;

public class VoucherDto implements Serializable {
    public Long id;
    public String code;
    public Integer phanTram;
    public BigDecimal giamToiDa;
    public String hieuLucTu;   // ISO-8601 string (Instant)
    public String hieuLucDen;  // ISO-8601 string (Instant)
    public Integer soLuongPhatHanh;
    public Integer daSuDung;
    public boolean hoatDong;
}
