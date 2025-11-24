package com.example.baitap01_nhom6_ui_login_register_forgetpass.models;

public class CartItem {
    private final Product product;
    private int quantity;
    private boolean selected;

    public CartItem(Product product, int quantity, boolean selected) {
        this.product = product;
        this.quantity = quantity;
        this.selected = selected;
    }

    public Product getProduct() {
        return product;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        if (quantity < 1) quantity = 1;
        this.quantity = quantity;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }
}
