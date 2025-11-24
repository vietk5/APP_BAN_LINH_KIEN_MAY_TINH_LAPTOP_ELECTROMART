package com.example.baitap01_nhom6_ui_login_register_forgetpass.models;

import java.io.Serializable;
import java.util.List;

public class Order implements Serializable {
    private String orderId;
    private ShippingAddress shippingAddress;
    private List<OrderProduct> products;
    private String paymentMethod;
    private String note;
    private long subtotal;
    private long shippingFee;
    private long discount;
    private long totalPrice;
    private String status;
    private long createdAt;

    public Order() {}

    // Getters and Setters
    public String getOrderId() { return orderId; }
    public void setOrderId(String orderId) { this.orderId = orderId; }

    public ShippingAddress getShippingAddress() { return shippingAddress; }
    public void setShippingAddress(ShippingAddress shippingAddress) { this.shippingAddress = shippingAddress; }

    public List<OrderProduct> getProducts() { return products; }
    public void setProducts(List<OrderProduct> products) { this.products = products; }

    public String getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }

    public String getNote() { return note; }
    public void setNote(String note) { this.note = note; }

    public long getSubtotal() { return subtotal; }
    public void setSubtotal(long subtotal) { this.subtotal = subtotal; }

    public long getShippingFee() { return shippingFee; }
    public void setShippingFee(long shippingFee) { this.shippingFee = shippingFee; }

    public long getDiscount() { return discount; }
    public void setDiscount(long discount) { this.discount = discount; }

    public long getTotalPrice() { return totalPrice; }
    public void setTotalPrice(long totalPrice) { this.totalPrice = totalPrice; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public long getCreatedAt() { return createdAt; }
    public void setCreatedAt(long createdAt) { this.createdAt = createdAt; }

    public void calculateTotal() {
        this.subtotal = 0;
        if (products != null) {
            for (OrderProduct p : products) {
                this.subtotal += p.getTotalPrice();
            }
        }
        this.totalPrice = this.subtotal + this.shippingFee - this.discount;
    }
}
