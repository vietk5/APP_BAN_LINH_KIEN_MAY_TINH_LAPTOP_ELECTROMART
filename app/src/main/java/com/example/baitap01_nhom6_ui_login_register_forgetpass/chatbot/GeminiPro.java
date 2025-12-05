package com.example.baitap01_nhom6_ui_login_register_forgetpass.chatbot;

import android.content.Context;
import android.util.Log;

import com.example.baitap01_nhom6_ui_login_register_forgetpass.models.dto.AdminProductDto;
import com.example.baitap01_nhom6_ui_login_register_forgetpass.remote.ApiClient;
import com.google.ai.client.generativeai.java.ChatFutures; // Thêm import Chat
import com.google.ai.client.generativeai.GenerativeModel;
import com.google.ai.client.generativeai.java.GenerativeModelFutures;
import com.google.ai.client.generativeai.type.BlockThreshold;
import com.google.ai.client.generativeai.type.Content;
import com.google.ai.client.generativeai.type.GenerateContentResponse;
import com.google.ai.client.generativeai.type.GenerationConfig;
import com.google.ai.client.generativeai.type.HarmCategory;
import com.google.ai.client.generativeai.type.SafetySetting;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Executor;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class GeminiPro {

    private final Context context;
    // Bỏ list allProducts khỏi đây (sẽ dùng trong lúc khởi tạo chat)
    private ChatFutures  chat; // <-- Đối tượng Chat để lưu lịch sử hội thoại
    private boolean isChatInitialized = false; // Cờ kiểm tra trạng thái khởi tạo Chat

    public interface InitCallback {
        void onReady();
        void onError(Throwable error);
    }
    // Constructor nhận Context
    public GeminiPro(Context context) {
        this.context = context.getApplicationContext();
        // Bắt đầu tải sản phẩm ngay khi khởi tạo
        loadProductsAndInitializeChat();
    }

    /**
     * Hàm chính để gửi câu hỏi và nhận câu trả lời
     */
    public void getResponse(String query, ResponseCallback callback) {
        if (!isChatInitialized || chat == null) {
            callback.onError(new IllegalStateException("Chat system is still initializing (loading product data). Please try again in a moment."));
            return;
        }
        Content userMessage = new Content.Builder()
                .addText(query)
                .build();
        // SỬ DỤNG chat.sendMessage() để tự động gửi kèm lịch sử hội thoại
        ListenableFuture<GenerateContentResponse> response = chat.sendMessage(userMessage);
        Executor executor = Runnable::run;

        Futures.addCallback(response, new FutureCallback<GenerateContentResponse>() {
            @Override
            public void onSuccess(GenerateContentResponse result) {
                callback.onResponse(result.getText());
            }

            @Override
            public void onFailure(Throwable throwable) {
                throwable.printStackTrace();
                callback.onError(throwable);
            }
        }, executor);
    }

    // --- KHỞI TẠO VÀ TẢI DỮ LIỆU ---

    /**
     * Tải dữ liệu sản phẩm và sau đó khởi tạo đối tượng Chat.
     */
    private void loadProductsAndInitializeChat() {
        ApiClient.get().getAllProducts().enqueue(new Callback<List<AdminProductDto>>() {
            @Override
            public void onResponse(Call<List<AdminProductDto>> call, Response<List<AdminProductDto>> response) {
                List<AdminProductDto> products = new ArrayList<>();
                if (response.isSuccessful() && response.body() != null) {
                    products = response.body();
                } else {
                    Log.e("GeminiPro", "Failed to load products: " + response.code());
                    // Vẫn khởi tạo chat để model có thể trả lời các câu hỏi chung
                }

                // Sau khi có (hoặc không có) dữ liệu, tiến hành khởi tạo Chat
                initializeChat(products);
            }

            @Override
            public void onFailure(Call<List<AdminProductDto>> call, Throwable t) {
                Log.e("GeminiPro", "Network error loading products", t);
                // Khởi tạo Chat ngay cả khi mạng lỗi, nhưng không có dữ liệu sản phẩm
                initializeChat(new ArrayList<>());
            }
        });
    }

    /**
     * Khởi tạo đối tượng Chat với System Prompt và dữ liệu sản phẩm.
     */
    private void initializeChat(List<AdminProductDto> products) {
        GenerativeModel gm = getGenerativeModel();
        GenerativeModelFutures model = GenerativeModelFutures.from(gm);

        String systemPrompt = loadAssetText("chatbot_system_prompt.txt");
        if (systemPrompt == null || systemPrompt.trim().isEmpty()) {
            systemPrompt = "Bạn là trợ lý AI của cửa hàng ElectroMart. Chỉ trả lời về laptop, PC, linh kiện, phụ kiện công nghệ.";
        }
        Log.d("SYSTEM PROMPT: ", systemPrompt);
        // Chuyển dữ liệu sản phẩm thành JSON
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String productJson = gson.toJson(products);
        Log.d("PRODUCT_DATA_INIT", "JSON Data for Chat Init: " + productJson);

        // Gộp System Prompt và Dữ liệu sản phẩm vào nội dung khởi tạo
        String fullSystemPrompt = systemPrompt +
                "\n\n===== DỮ LIỆU SẢN PHẨM CỦA CỬA HÀNG =====\n" +
                productJson +
                "\n===== HẾT DANH SÁCH SẢN PHẨM =====\n";

        Content initialContent = new Content.Builder().addText(fullSystemPrompt).build();

        // Bắt đầu Chat (sử dụng startChat để thiết lập lịch sử)
        chat = model.startChat(Collections.singletonList(initialContent));
        isChatInitialized = true;
        Log.d("GeminiPro", "Chat system initialized successfully.");
    }

    // --- CẤU HÌNH MODEL ---

    /**
     * Tạo và trả về GenerativeModel.
     * (Hàm getModel() cũ được đổi tên để tránh nhầm lẫn)
     */
    private GenerativeModel getGenerativeModel() {
        String apiKey = BuildConfig.apiKey;

        SafetySetting harassmentSafety = new SafetySetting(HarmCategory.HARASSMENT,
                BlockThreshold.ONLY_HIGH);
        GenerationConfig.Builder configBuilder = new GenerationConfig.Builder();
        configBuilder.temperature = 0.9f;
        configBuilder.topK = 16;
        configBuilder.topP = 0.1f;
        GenerationConfig generationConfig = configBuilder.build();

        return new GenerativeModel(
                "gemini-2.5-flash",
                apiKey,
                generationConfig,
                Collections.singletonList(harassmentSafety)
        );
    }

    // Hàm này giữ nguyên
    private String loadAssetText(String fileName) {
        try {
            InputStream is = context.getAssets().open(fileName);
            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line).append("\n");
            }
            br.close();
            return sb.toString();
        } catch (Exception e) { return ""; }
    }

    // Hàm loadProducts() cũ không còn cần thiết, đã được tích hợp vào loadProductsAndInitializeChat()

    // Lưu ý: Cần thêm import com.google.ai.client.generativeai.Chat;
}