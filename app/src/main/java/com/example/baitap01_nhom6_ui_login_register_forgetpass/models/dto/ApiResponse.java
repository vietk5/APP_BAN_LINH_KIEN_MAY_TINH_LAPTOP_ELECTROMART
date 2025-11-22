package com.example.baitap01_nhom6_ui_login_register_forgetpass.models.dto;
public class ApiResponse<T> {
    private boolean success;
    private String message;
    private T data;

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
    public T getData() { return data; }
}