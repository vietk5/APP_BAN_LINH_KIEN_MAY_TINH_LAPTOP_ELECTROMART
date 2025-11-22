package com.example.baitap01_nhom6_ui_login_register_forgetpass.models.dto;

public class ResetPasswordRequest {
    private String email;
    private String newPass;

    public ResetPasswordRequest(String email, String newPass) {
        this.email = email;
        this.newPass = newPass;
    }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getNewPass() { return newPass; }
    public void setNewPass(String newPass) { this.newPass = newPass; }
}