package com.example.baitap01_nhom6_ui_login_register_forgetpass.remote;

import com.example.baitap01_nhom6_ui_login_register_forgetpass.models.dto.ProductDto;
import com.example.baitap01_nhom6_ui_login_register_forgetpass.models.dto.CategoryDto;
import java.util.List;
import retrofit2.Call;
import retrofit2.http.GET;

public interface ApiService {
    @GET("api/products")
    Call<List<ProductDto>> getProducts();

    @GET("api/categories")
    Call<List<CategoryDto>> getCategories();
}
