package com.example.baitap01_nhom6_ui_login_register_forgetpass.models.dto;

import com.google.gson.annotations.SerializedName;

public class UploadImageResponse {
    @SerializedName("url")
    private String url;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
