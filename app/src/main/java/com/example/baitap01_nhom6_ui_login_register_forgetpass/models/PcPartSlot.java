package com.example.baitap01_nhom6_ui_login_register_forgetpass.models;

public class PcPartSlot {
    private String key;         // Định danh: "CPU", "MAIN", "RAM", "VGA"...
    private String title;       // Tên hiển thị: "Vi xử lý", "Bo mạch chủ"...
    private int iconRes;        // Icon hiển thị
    private Product product;    // Sản phẩm người dùng đã chọn (null nếu chưa chọn)
    private boolean isRequired; // Bắt buộc phải chọn hay không

    public PcPartSlot(String key, String title, int iconRes, boolean isRequired) {
        this.key = key;
        this.title = title;
        this.iconRes = iconRes;
        this.isRequired = isRequired;
    }

    // Getters & Setters
    public String getKey() { return key; }
    public String getTitle() { return title; }
    public int getIconRes() { return iconRes; }
    public Product getProduct() { return product; }
    public void setProduct(Product product) { this.product = product; }
    public boolean isRequired() { return isRequired; }
}