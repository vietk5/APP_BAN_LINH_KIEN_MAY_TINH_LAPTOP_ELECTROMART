package com.example.baitap01_nhom6_ui_login_register_forgetpass.models.dto;

import java.util.List;

public class CheckoutRequest {
    private int userId;
    private String receiverName;
    private String receiverPhone;
    private String receiverAddress;
    private String paymentMethod;
    private String voucherCode;
    private List<Long> productIds; // danh sách productId trong đơn

    public CheckoutRequest() {
    }

    public CheckoutRequest(int userId, String receiverName, String receiverPhone,
                           String receiverAddress, String paymentMethod,
                           String voucherCode, List<Long> productIds) {
        this.userId = userId;
        this.receiverName = receiverName;
        this.receiverPhone = receiverPhone;
        this.receiverAddress = receiverAddress;
        this.paymentMethod = paymentMethod;
        this.voucherCode = voucherCode;
        this.productIds = productIds;
    }

    // getter/setter
    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getReceiverName() {
        return receiverName;
    }

    public void setReceiverName(String receiverName) {
        this.receiverName = receiverName;
    }

    public String getReceiverPhone() {
        return receiverPhone;
    }

    public void setReceiverPhone(String receiverPhone) {
        this.receiverPhone = receiverPhone;
    }

    public String getReceiverAddress() {
        return receiverAddress;
    }

    public void setReceiverAddress(String receiverAddress) {
        this.receiverAddress = receiverAddress;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public String getVoucherCode() {
        return voucherCode;
    }

    public void setVoucherCode(String voucherCode) {
        this.voucherCode = voucherCode;
    }

    public List<Long> getProductIds() {
        return productIds;
    }

    public void setProductIds(List<Long> productIds) {
        this.productIds = productIds;
    }
}
