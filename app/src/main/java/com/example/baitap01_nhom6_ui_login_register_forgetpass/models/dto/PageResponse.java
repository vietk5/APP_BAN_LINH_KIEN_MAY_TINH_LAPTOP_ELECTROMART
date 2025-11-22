// models/dto/PageResponse.java
package com.example.baitap01_nhom6_ui_login_register_forgetpass.models.dto;

import java.util.List;

public class PageResponse<T> {
    public List<T> content;

    // các field còn lại có cũng được, không bắt buộc
    public int totalPages;
    public long totalElements;
    public int number;      // page index
    public int size;
}
