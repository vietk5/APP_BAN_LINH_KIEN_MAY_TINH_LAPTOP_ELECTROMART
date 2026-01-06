package com.example.baitap01_nhom6_ui_login_register_forgetpass.models;

import android.graphics.Bitmap;

public class CapturedImage {
    private String imagePath;
    private Bitmap bitmap;
    private String base64String;

    public CapturedImage() {
    }

    public CapturedImage(String imagePath, Bitmap bitmap) {
        this.imagePath = imagePath;
        this.bitmap = bitmap;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    public String getBase64String() {
        return base64String;
    }

    public void setBase64String(String base64String) {
        this.base64String = base64String;
    }
}