package com.example.baitap01_nhom6_ui_login_register_forgetpass.models.dto;

import java.io.Serializable;
import java.util.List;

public class CheckoutRequest implements Serializable {
    private int isBuyNow;
    private int userId;
    private String receiverName;
    private String receiverPhone;
    private String receiverAddress;
    private String paymentMethod;
    private String voucherCode;
    private List<CheckoutItem> product; // danh sách productId trong đơn

    public CheckoutRequest() {
    }

    public CheckoutRequest(int isBuyNow, int userId, String receiverName, String receiverPhone,
                           String receiverAddress, String paymentMethod,
                           String voucherCode, List<CheckoutItem> product) {
        this.isBuyNow = isBuyNow;
        this.userId = userId;
        this.receiverName = receiverName;
        this.receiverPhone = receiverPhone;
        this.receiverAddress = receiverAddress;
        this.paymentMethod = paymentMethod;
        this.voucherCode = voucherCode;
        this.product = product;
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
    public int getIsBuyNow() {
        return isBuyNow;
    }

    public void setIsBuyNow(int isBuyNow) {
        this.isBuyNow = isBuyNow;
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

    public List<CheckoutItem> getProductIds() {
        return product;
    }

    public void setProductIds(List<CheckoutItem> productIds) {
        this.product = productIds;
    }
}
