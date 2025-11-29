package com.example.baitap01_nhom6_ui_login_register_forgetpass.models.dto;

import java.io.Serializable;

public class CheckoutItem implements Serializable {
    private long productId;
    private String name;
    private String imageUrl;
    private long unitPrice;
    private int quantity;

    public CheckoutItem(long productId, String name, String imageUrl,
                        long unitPrice, int quantity) {
        this.productId = productId;
        this.name = name;
        this.imageUrl = imageUrl;
        this.unitPrice = unitPrice;
        this.quantity = quantity;
    }

    public long getProductId() { return productId; }
    public String getName()    { return name; }
    public String getImageUrl(){ return imageUrl; }
    public long getUnitPrice() { return unitPrice; }
    public int getQuantity()   { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
}
