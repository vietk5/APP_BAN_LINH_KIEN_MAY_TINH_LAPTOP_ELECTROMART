package com.example.baitap01_nhom6_ui_login_register_forgetpass.models.dto;
public class ApiResponse {
    private boolean success;
    private String message;

    public ApiResponse(boolean success, String message) {
        this.success = success;
        this.message = message;
    }

    // Thêm getter
    public boolean isSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }
}