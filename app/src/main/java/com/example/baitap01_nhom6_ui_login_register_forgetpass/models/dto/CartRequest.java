package com.example.baitap01_nhom6_ui_login_register_forgetpass.models.dto;


public class CartRequest {
    private int userId;
    private long productId;
    private int quantity;

    public CartRequest() {
    }

    public CartRequest(int userId, long productId, int quantity) {
        this.userId = userId;
        this.productId = productId;
        this.quantity = quantity;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public long getProductId() {
        return productId;
    }

    public void setProductId(long productId) {
        this.productId = productId;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}

