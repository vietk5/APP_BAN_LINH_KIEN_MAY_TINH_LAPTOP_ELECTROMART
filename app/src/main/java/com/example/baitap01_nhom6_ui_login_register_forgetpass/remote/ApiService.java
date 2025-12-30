package com.example.baitap01_nhom6_ui_login_register_forgetpass.remote;

import com.example.baitap01_nhom6_ui_login_register_forgetpass.models.Comment;
import com.example.baitap01_nhom6_ui_login_register_forgetpass.models.dto.ApiResponse;
import com.example.baitap01_nhom6_ui_login_register_forgetpass.models.dto.BrandDto;
import com.example.baitap01_nhom6_ui_login_register_forgetpass.models.dto.ChangePasswordRequest;
import com.example.baitap01_nhom6_ui_login_register_forgetpass.models.dto.ForgotPasswordRequest;
import com.example.baitap01_nhom6_ui_login_register_forgetpass.models.dto.OrderDetailDto;
import com.example.baitap01_nhom6_ui_login_register_forgetpass.models.dto.OrderDetailItemDto;
import com.example.baitap01_nhom6_ui_login_register_forgetpass.models.dto.PageResponse;
import com.example.baitap01_nhom6_ui_login_register_forgetpass.models.dto.ProductDto;
import com.example.baitap01_nhom6_ui_login_register_forgetpass.models.dto.CategoryDto;
import com.example.baitap01_nhom6_ui_login_register_forgetpass.models.dto.RatingSummary;
import com.example.baitap01_nhom6_ui_login_register_forgetpass.models.dto.ResetPasswordRequest;
import com.example.baitap01_nhom6_ui_login_register_forgetpass.models.dto.SpinRequest;
import com.example.baitap01_nhom6_ui_login_register_forgetpass.models.dto.SpinResultResponse;
import com.example.baitap01_nhom6_ui_login_register_forgetpass.models.dto.SpinStatusResponse;
import com.example.baitap01_nhom6_ui_login_register_forgetpass.models.dto.UpdateProfileRequest;
import com.example.baitap01_nhom6_ui_login_register_forgetpass.models.dto.UserDto;
import com.example.baitap01_nhom6_ui_login_register_forgetpass.models.dto.UserLoginRequest;
import com.example.baitap01_nhom6_ui_login_register_forgetpass.models.dto.UserRegisterRequest;
import com.example.baitap01_nhom6_ui_login_register_forgetpass.models.admin.AdminDashboardSummary;
import com.example.baitap01_nhom6_ui_login_register_forgetpass.models.admin.LowStockProduct;
import com.example.baitap01_nhom6_ui_login_register_forgetpass.models.admin.RecentOrder;
import com.example.baitap01_nhom6_ui_login_register_forgetpass.models.dto.AdminProductDto;
import com.example.baitap01_nhom6_ui_login_register_forgetpass.models.dto.AdminOrderDto;
import com.example.baitap01_nhom6_ui_login_register_forgetpass.models.dto.AdminCustomerDto;
import com.example.baitap01_nhom6_ui_login_register_forgetpass.models.dto.RevenuePointDto;
import com.example.baitap01_nhom6_ui_login_register_forgetpass.models.dto.CartItemDto;
import com.example.baitap01_nhom6_ui_login_register_forgetpass.models.dto.CartRequest;
import com.example.baitap01_nhom6_ui_login_register_forgetpass.models.dto.CheckoutRequest;
import com.example.baitap01_nhom6_ui_login_register_forgetpass.models.dto.Address;
import com.example.baitap01_nhom6_ui_login_register_forgetpass.models.voucher.ApplyVoucherRequest;
import com.example.baitap01_nhom6_ui_login_register_forgetpass.models.voucher.ApplyVoucherResponse;
import com.example.baitap01_nhom6_ui_login_register_forgetpass.models.voucher.CreateVoucherRequest;
import com.example.baitap01_nhom6_ui_login_register_forgetpass.models.voucher.VoucherDto;


import java.util.List;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ApiService {
    @GET("api/products")
    Call<List<ProductDto>> getProducts();

    @GET("api/categories")
    Call<List<CategoryDto>> getCategories();

    @POST("api/auth/register")
    Call<ApiResponse<Void>> register(@Body UserRegisterRequest request);

    @POST("api/auth/login")
    Call<ApiResponse<UserDto>> login(@Body UserLoginRequest request);
    @GET("/api/admin/dashboard/summary")
    Call<AdminDashboardSummary> getAdminSummary();

    @GET("/api/admin/dashboard/low-stock")
    Call<List<LowStockProduct>> getLowStockTop10();

    @GET("/api/admin/dashboard/recent-orders")
    Call<List<RecentOrder>> getRecentOrdersTop10();

    // ======== ADMIN PRODUCT ===========
    @GET("/api/admin/products")
    Call<List<AdminProductDto>> getAdminProducts(
            @Query("loaiId") Long loaiId,
            @Query("thuongHieuId") Long thuongHieuId,
            @Query("keyword") String keyword
    );

    @PATCH("/api/admin/products/{id}/stock")
    Call<Void> changeProductStock(@Path("id") long id,
                                  @Query("delta") int delta);

    @DELETE("/api/admin/products/{id}")
    Call<Void> deleteProduct(@Path("id") long id);

    // ======== ADMIN ORDERS ============
    @GET("/api/admin/orders")
    Call<PageResponse<AdminOrderDto>> getAdminOrders(
            @Query("status") String status,
            @Query("page")   int page,
            @Query("size")   int size
    );
    @PATCH("/api/admin/orders/{id}/status")
    Call<Void> updateOrderStatus(@Path("id") long id,
                                 @Query("status") String status);
    // ApiService.java
    @GET("/api/admin/orders/{id}/items")
    Call<List<OrderDetailItemDto>> getAdminOrderItems(@Path("id") long orderId);


    // ======== ADMIN CUSTOMERS =========
    @GET("/api/admin/customers")
    Call<List<AdminCustomerDto>> getAdminCustomers();

    @PATCH("/api/admin/customers/{id}/block")
    Call<Void> blockCustomer(@Path("id") long id,
                             @Query("block") boolean block);

    // ======== ADMIN REVENUE ===========
    @GET("/api/admin/revenue")
    Call<List<RevenuePointDto>> getRevenue(
            @Query("from") String fromDate,   // "2025-11-01"
            @Query("to") String toDate,       // "2025-11-30"
            @Query("groupBy") String groupBy  // "DAY" / "MONTH" / "YEAR"
    );

    // ======== FORGOT PASSWORD ===========
    @POST("api/auth/forgot-password")
    Call<ApiResponse<Void>> forgotPassword(@Body ForgotPasswordRequest request);

    @POST("api/auth/verify-otp")
    Call<ApiResponse<Void>> verifyOtp(@Body ForgotPasswordRequest request);

    @POST("api/auth/reset-password")
    Call<ApiResponse<Void>> resetPassword(@Body ResetPasswordRequest request);

    // ======== PROFILE (USER) ===========
    // Lấy thông tin profile theo userId
    @GET("api/profile/{userId}")
    Call<ApiResponse<UserDto>> getProfile(@Path("userId") Long userId);

    // Cập nhật thông tin cá nhân
    @PUT("api/profile/{userId}")
    Call<ApiResponse<UserDto>> updateProfile(
            @Path("userId") Long userId,
            @Body UpdateProfileRequest request
    );

    // Đổi mật khẩu
    @PUT("api/profile/{userId}/change-password")
    Call<ApiResponse<Void>> changePassword(
            @Path("userId") Long userId,
            @Body ChangePasswordRequest request
    );

    @POST("/api/admin/products")
    Call<AdminProductDto> createProduct(@Body AdminProductDto product);
    @GET("api/brands")
    Call<List<BrandDto>> getBrands();

    // Lấy danh sách khách hàng admin
    @GET("/api/admin/customers")
    Call<PageResponse<AdminCustomerDto>> getAdminCustomers(
            @Query("search") String search,
            @Query("page") int page,
            @Query("size") int size,
            @Query("sortDir") String sortDir  // "asc" / "desc"
    );

    // Block / Unblock
    @POST("/api/admin/customers/{id}/toggle-block")
    Call<Void> toggleBlockCustomer(@Path("id") long customerId);

    // Xóa tài khoản
    @DELETE("/api/admin/customers/{id}")
    Call<Void> deleteCustomer(@Path("id") long customerId);

    @GET("api/products/{id}/related")
    Call<List<ProductDto>> getRelated(@Path("id") long id);

    @GET("api/comments/{productId}")
    Call<List<Comment>> getComments(@Path("productId") long productId);

    @POST("api/comments")
    Call<Comment> postComment(@Body Comment comment);
    @GET("api/products/{id}")
    Call<ProductDto> getProductById(@Path("id") long id);
    // lâys dữ liệu tỷ lệ rate
    @GET("api/products/{id}/rating-summary")
    Call<RatingSummary> getRatingSummary(@Path("id") long id);

    @GET("api/user/orders/by-user/{userId}")
    Call<List<OrderDetailDto>> getOrdersByUserId(@Path("userId") long userId);

    // them, lay gio hang
    @POST("api/cart/add")
    Call<Void> addToCart(@Body CartRequest request);

    @GET("api/cart/{userId}")
    Call<List<CartItemDto>> getCart(@Path("userId") int userId);
    @POST("api/cart/checkout")
    Call<Void> checkout(@Body CheckoutRequest request);

    @DELETE("api/cart/{userId}/item/{productId}")
    Call<Void> deleteCartItem(@Path("userId") int userId,
                              @Path("productId") long productId);

    @GET("/api/admin/products")
    Call<List<AdminProductDto>> getAllProducts();

    // ==================== ADDRESS ENDPOINTS ====================

    /**
     * Lấy tất cả địa chỉ của user
     */
    @GET("api/addresses/user/{userId}")
    Call<ApiResponse<List<Address>>> getAllAddresses(@Path("userId") Long userId);

    /**
     * Lấy chi tiết một địa chỉ
     */
    @GET("api/addresses/{id}")
    Call<ApiResponse<Address>> getAddressById(
            @Path("id") Long id,
            @Query("userId") Long userId
    );

    /**
     * Lấy địa chỉ mặc định
     */
    @GET("api/addresses/user/{userId}/default")
    Call<ApiResponse<Address>> getDefaultAddress(@Path("userId") Long userId);

    /**
     * Tạo địa chỉ mới
     */
    @POST("api/addresses")
    Call<ApiResponse<Address>> createAddress(@Body Address address);

    /**
     * Cập nhật địa chỉ
     */
    @PUT("api/addresses/{id}")
    Call<ApiResponse<Address>> updateAddress(
            @Path("id") Long id,
            @Body Address address
    );

    /**
     * Xóa địa chỉ
     */
    @DELETE("api/addresses/{id}")
    Call<ApiResponse<Void>> deleteAddress(
            @Path("id") Long id,
            @Query("userId") Long userId
    );

    /**
     * Đặt địa chỉ làm mặc định
     */
    @PUT("api/addresses/{id}/set-default")
    Call<ApiResponse<Address>> setDefaultAddress(
            @Path("id") Long id,
            @Query("userId") Long userId
    );

    // ====== ADMIN VOUCHERS ======
    @GET("/api/admin/vouchers")
    Call<List<VoucherDto>> adminListVouchers();

    @POST("/api/admin/vouchers")
    Call<VoucherDto> adminCreateVoucher(
            @Body CreateVoucherRequest req
    );

    @PUT("/api/admin/vouchers/{id}")
    Call<VoucherDto> adminUpdateVoucher(
            @Path("id") long id,
            @Body CreateVoucherRequest req
    );

    @PATCH("/api/admin/vouchers/{id}/toggle")
    Call<VoucherDto> adminToggleVoucher(
            @Path("id") long id,
            @Query("hoatDong") boolean hoatDong
    );

    @DELETE("/api/admin/vouchers/{id}")
    Call<Void> adminDeleteVoucher(@Path("id") long id);


    // ====== USER APPLY ======
    @POST("/api/vouchers/apply")
    Call<ApplyVoucherResponse> applyVoucher(
            @Body ApplyVoucherRequest req
    );

    @GET("/api/spin/status")
    Call<SpinStatusResponse> getSpinStatus(@Query("userId") long userId);

    @POST("/api/spin")
    Call<SpinResultResponse> spin(@Body SpinRequest req);

    @PATCH("api/user/orders/{orderId}/cancel")
    Call<Void> cancelOrder(@Path("orderId") long orderId);
}
