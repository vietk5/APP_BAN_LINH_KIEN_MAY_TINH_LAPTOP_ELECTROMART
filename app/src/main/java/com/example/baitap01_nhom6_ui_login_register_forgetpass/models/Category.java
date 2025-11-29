package com.example.baitap01_nhom6_ui_login_register_forgetpass.models;

public class Category {
    private String name;
    private int imageRes;
    // key dùng nội bộ để filter sản phẩm (LAPTOP, PC, ...)
    private String key;

    // ✅ Constructor cũ – KHÔNG LÀM HỎNG CODE CŨ
    // Chỗ nào trong app đang dùng new Category("Laptop", R.drawable.ic_laptop) vẫn chạy bình thường
    public Category(String name, int imageRes) {
        this(name, imageRes, null);
    }

    // ✅ Constructor mới – dùng cho màn Category mới (có key)
    public Category(String name, int imageRes, String key) {
        this.name = name;
        this.imageRes = imageRes;
        this.key = key;
    }

    public String getName() {
        return name;
    }

    public int getImageRes() {
        return imageRes;
    }

    public String getKey() {
        return key;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setImageRes(int imageRes) {
        this.imageRes = imageRes;
    }

    public void setKey(String key) {
        this.key = key;
    }
}
