package com.example.baitap01_nhom6_ui_login_register_forgetpass.models;

import java.io.Serializable;

public class ShippingAddress implements Serializable {
    private String receiverName;
    private String phoneNumber;
    private String province;
    private String district;
    private String ward;
    private String detailAddress;

    public ShippingAddress() {}

    public ShippingAddress(String receiverName, String phoneNumber, String province,
                           String district, String ward, String detailAddress) {
        this.receiverName = receiverName;
        this.phoneNumber = phoneNumber;
        this.province = province;
        this.district = district;
        this.ward = ward;
        this.detailAddress = detailAddress;
    }

    // Getters and Setters
    public String getReceiverName() { return receiverName; }
    public void setReceiverName(String receiverName) { this.receiverName = receiverName; }

    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }

    public String getProvince() { return province; }
    public void setProvince(String province) { this.province = province; }

    public String getDistrict() { return district; }
    public void setDistrict(String district) { this.district = district; }

    public String getWard() { return ward; }
    public void setWard(String ward) { this.ward = ward; }

    public String getDetailAddress() { return detailAddress; }
    public void setDetailAddress(String detailAddress) { this.detailAddress = detailAddress; }

    public String getFullAddress() {
        return detailAddress + ", " + ward + ", " + district + ", " + province;
    }
}
