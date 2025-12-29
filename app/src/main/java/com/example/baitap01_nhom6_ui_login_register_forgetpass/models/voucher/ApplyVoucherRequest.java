// ApplyVoucherRequest.java
package com.example.baitap01_nhom6_ui_login_register_forgetpass.models.voucher;

public class ApplyVoucherRequest {
    public String code;
    public long tongTien;

    public ApplyVoucherRequest(String code, long tongTien) {
        this.code = code;
        this.tongTien = tongTien;
    }
}
