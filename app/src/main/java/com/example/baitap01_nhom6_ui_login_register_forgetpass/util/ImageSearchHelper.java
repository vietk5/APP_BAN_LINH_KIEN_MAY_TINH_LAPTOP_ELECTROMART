package com.example.baitap01_nhom6_ui_login_register_forgetpass.util;

import android.graphics.Bitmap;
import android.util.Base64;
import android.util.Log;

import com.example.baitap01_nhom6_ui_login_register_forgetpass.models.Product;
import com.example.baitap01_nhom6_ui_login_register_forgetpass.models.dto.ApiResponse;
import com.example.baitap01_nhom6_ui_login_register_forgetpass.remote.ApiService;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Helper class để search sản phẩm bằng ảnh
 * Sử dụng Google Cloud Vision API
 */
public class ImageSearchHelper {

    private static final String TAG = "ImageSearchHelper";
    private ApiService apiService;

    public ImageSearchHelper(ApiService apiService) {
        this.apiService = apiService;
    }

    /**
     * Search với 1 ảnh (Bitmap)
     */
    public void searchByBitmap(Bitmap bitmap, ImageSearchCallback callback) {
        try {
            // Convert bitmap to base64
            String base64 = bitmapToBase64(bitmap);

            // Tạo request
            Map<String, String> request = new HashMap<>();
            request.put("imageBase64", base64);

            // Call API
            Call<ApiResponse<List<Product>>> call = apiService.searchByImage(request);
            call.enqueue(new Callback<ApiResponse<List<Product>>>() {
                @Override
                public void onResponse(Call<ApiResponse<List<Product>>> call,
                                       Response<ApiResponse<List<Product>>> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        ApiResponse<List<Product>> apiResponse = response.body();

                        if (apiResponse.isSuccess()) {
                            List<Product> products = apiResponse.getData();
                            Log.d(TAG, "Found " + products.size() + " products");
                            callback.onSuccess(products);
                        } else {
                            Log.e(TAG, "API error: " + apiResponse.getMessage());
                            callback.onError(apiResponse.getMessage());
                        }
                    } else {
                        Log.e(TAG, "Response not successful: " + response.code());
                        callback.onError("Lỗi kết nối: " + response.code());
                    }
                }

                @Override
                public void onFailure(Call<ApiResponse<List<Product>>> call, Throwable t) {
                    Log.e(TAG, "Network error: " + t.getMessage(), t);
                    callback.onError("Lỗi mạng: " + t.getMessage());
                }
            });

        } catch (Exception e) {
            Log.e(TAG, "Error in searchByBitmap: " + e.getMessage(), e);
            callback.onError("Lỗi xử lý ảnh: " + e.getMessage());
        }
    }

    /**
     * Search với nhiều ảnh (List of Bitmaps)
     */
    public void searchByMultipleBitmaps(List<Bitmap> bitmaps, ImageSearchCallback callback) {
        try {
            if (bitmaps == null || bitmaps.isEmpty()) {
                callback.onError("Không có ảnh");
                return;
            }

            if (bitmaps.size() > 5) {
                callback.onError("Tối đa 5 ảnh");
                return;
            }

            // Convert all bitmaps to base64
            List<String> base64List = new ArrayList<>();
            for (Bitmap bitmap : bitmaps) {
                String base64 = bitmapToBase64(bitmap);
                base64List.add(base64);
            }

            // Tạo request
            Map<String, List<String>> request = new HashMap<>();
            request.put("imageBase64List", base64List);

            // Call API
            Call<ApiResponse<List<Product>>> call = apiService.searchByMultipleImages(request);
            call.enqueue(new Callback<ApiResponse<List<Product>>>() {
                @Override
                public void onResponse(Call<ApiResponse<List<Product>>> call,
                                       Response<ApiResponse<List<Product>>> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        ApiResponse<List<Product>> apiResponse = response.body();

                        if (apiResponse.isSuccess()) {
                            List<Product> products = apiResponse.getData();
                            Log.d(TAG, "Found " + products.size() + " products from " +
                                    bitmaps.size() + " images");
                            callback.onSuccess(products);
                        } else {
                            callback.onError(apiResponse.getMessage());
                        }
                    } else {
                        callback.onError("Lỗi kết nối: " + response.code());
                    }
                }

                @Override
                public void onFailure(Call<ApiResponse<List<Product>>> call, Throwable t) {
                    Log.e(TAG, "Network error: " + t.getMessage(), t);
                    callback.onError("Lỗi mạng: " + t.getMessage());
                }
            });

        } catch (Exception e) {
            Log.e(TAG, "Error in searchByMultipleBitmaps: " + e.getMessage(), e);
            callback.onError("Lỗi xử lý ảnh: " + e.getMessage());
        }
    }

    /**
     * Search bằng upload file
     */
    public void searchByFile(File file, ImageSearchCallback callback) {
        try {
            // Tạo RequestBody
            RequestBody requestFile = RequestBody.create(
                    MediaType.parse("image/*"),
                    file
            );

            // Tạo MultipartBody.Part
            MultipartBody.Part body = MultipartBody.Part.createFormData(
                    "file",
                    file.getName(),
                    requestFile
            );

            // Call API
            Call<ApiResponse<List<Product>>> call = apiService.searchByUpload(body);
            call.enqueue(new Callback<ApiResponse<List<Product>>>() {
                @Override
                public void onResponse(Call<ApiResponse<List<Product>>> call,
                                       Response<ApiResponse<List<Product>>> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        ApiResponse<List<Product>> apiResponse = response.body();

                        if (apiResponse.isSuccess()) {
                            callback.onSuccess(apiResponse.getData());
                        } else {
                            callback.onError(apiResponse.getMessage());
                        }
                    } else {
                        callback.onError("Lỗi kết nối: " + response.code());
                    }
                }

                @Override
                public void onFailure(Call<ApiResponse<List<Product>>> call, Throwable t) {
                    callback.onError("Lỗi mạng: " + t.getMessage());
                }
            });

        } catch (Exception e) {
            callback.onError("Lỗi xử lý file: " + e.getMessage());
        }
    }

    /**
     * Convert Bitmap to Base64 string
     */
    private String bitmapToBase64(Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        // Compress ảnh (JPEG 80% quality để giảm size)
        bitmap.compress(Bitmap.CompressFormat.JPEG, 80, baos);

        byte[] imageBytes = baos.toByteArray();

        // Encode to Base64
        return Base64.encodeToString(imageBytes, Base64.NO_WRAP);
    }

    /**
     * Health check
     */
    public void healthCheck(HealthCheckCallback callback) {
        Call<ApiResponse<String>> call = apiService.searchHealthCheck();
        call.enqueue(new Callback<ApiResponse<String>>() {
            @Override
            public void onResponse(Call<ApiResponse<String>> call,
                                   Response<ApiResponse<String>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onHealthy(response.body().getData());
                } else {
                    callback.onUnhealthy("Service unavailable");
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<String>> call, Throwable t) {
                callback.onUnhealthy(t.getMessage());
            }
        });
    }

    /**
     * Callback interface for search results
     */
    public interface ImageSearchCallback {
        void onSuccess(List<Product> products);
        void onError(String error);
    }

    /**
     * Callback interface for health check
     */
    public interface HealthCheckCallback {
        void onHealthy(String message);
        void onUnhealthy(String error);
    }
}