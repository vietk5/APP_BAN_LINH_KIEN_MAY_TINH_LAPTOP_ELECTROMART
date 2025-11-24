// models/Product.java
package com.example.baitap01_nhom6_ui_login_register_forgetpass.models;

public class Product {
    private long id;
    private String name;
    private String price;       // đã format, dùng cho UI
    private int imageResId;     // ảnh local (giữ để không hỏng code cũ)
    private String imageUrl;    // ẢNH TỪ BACKEND (mới, có thể null)

    // Dùng cho dữ liệu cũ (ảnh local)
    public Product(String name, String price, int imageResId) {
        this.name = name;
        this.price = price;
        this.imageResId = imageResId;
    }

    // Dùng cho dữ liệu API (ảnh URL)
    public Product(String name, String price, String imageUrl) {
        this.name = name;
        this.price = price;
        this.imageUrl = imageUrl;
    }
    public Product(long id, String name, String price, String imageUrl) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.imageUrl = imageUrl;
    }

    public String getName() { return name; }
    public String getPrice() { return price; }
    public int getImageResId() { return imageResId; }
    public String getImageUrl() { return imageUrl; }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }
}
