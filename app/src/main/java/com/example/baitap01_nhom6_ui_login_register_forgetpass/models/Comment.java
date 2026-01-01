package com.example.baitap01_nhom6_ui_login_register_forgetpass.models;


public class Comment {

    private Long id;
    private Long productId;
    private String username;
    private String content;
    private int rating;
    private String createdAt;
    private String imageUrl;

    public Comment() {}

    public Comment(Long productId, String username, String content, int rating) {
        this.productId = productId;
        this.username = username;
        this.content = content;
        this.rating = rating;
    }

    public Long getId() { return id; }
    public Long getProductId() { return productId; }
    public String getUsername() { return username; }
    public String getContent() { return content; }
    public int getRating() { return rating; }
    public String getCreatedAt() { return createdAt; }
    public String getImageUrl() { return imageUrl; }

    public void setId(Long id) { this.id = id; }
    public void setProductId(Long productId) { this.productId = productId; }
    public void setUsername(String username) { this.username = username; }
    public void setContent(String content) { this.content = content; }
    public void setRating(int rating) { this.rating = rating; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
}
