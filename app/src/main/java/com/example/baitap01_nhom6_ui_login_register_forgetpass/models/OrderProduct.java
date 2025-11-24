package com.example.baitap01_nhom6_ui_login_register_forgetpass.models;

import java.io.Serializable;

public class OrderProduct implements Serializable {
    private int id;
    private String name;
    private String image;
    private String variant;
    private long price;
    private long originalPrice;
    private int quantity;

    public OrderProduct() {}

    public OrderProduct(int id, String name, String image, String variant,
                        long price, long originalPrice, int quantity) {
        this.id = id;
        this.name = name;
        this.image = image;
        this.variant = variant;
        this.price = price;
        this.originalPrice = originalPrice;
        this.quantity = quantity;
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getImage() { return image; }
    public void setImage(String image) { this.image = image; }

    public String getVariant() { return variant; }
    public void setVariant(String variant) { this.variant = variant; }

    public long getPrice() { return price; }
    public void setPrice(long price) { this.price = price; }

    public long getOriginalPrice() { return originalPrice; }
    public void setOriginalPrice(long originalPrice) { this.originalPrice = originalPrice; }

    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }

    public long getTotalPrice() { return price * quantity; }
}