package com.example.baitap01_nhom6_ui_login_register_forgetpass.remote;

import com.example.baitap01_nhom6_ui_login_register_forgetpass.models.dto.ApiResponse;
import com.example.baitap01_nhom6_ui_login_register_forgetpass.models.dto.ProductDto;
import com.example.baitap01_nhom6_ui_login_register_forgetpass.models.dto.CategoryDto;
import com.example.baitap01_nhom6_ui_login_register_forgetpass.models.dto.UserLoginRequest;
import com.example.baitap01_nhom6_ui_login_register_forgetpass.models.dto.UserRegisterRequest;

import java.util.List;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface ApiService {
    @GET("api/products")
    Call<List<ProductDto>> getProducts();

    @GET("api/categories")
    Call<List<CategoryDto>> getCategories();

    @POST("api/auth/register")
    Call<ApiResponse> register(@Body UserRegisterRequest request);

    @POST("api/auth/login")
    Call<ApiResponse> login(@Body UserLoginRequest request);
}
