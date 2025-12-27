package com.example.baitap01_nhom6_ui_login_register_forgetpass.activity;

import android.content.Intent;
import android.os.Bundle;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.baitap01_nhom6_ui_login_register_forgetpass.R;
import com.example.baitap01_nhom6_ui_login_register_forgetpass.fragment.HomeFragment;
import com.example.baitap01_nhom6_ui_login_register_forgetpass.models.dto.CheckoutRequest;
import com.example.baitap01_nhom6_ui_login_register_forgetpass.remote.ApiClient;
import com.example.baitap01_nhom6_ui_login_register_forgetpass.remote.ApiService;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BankPaymentActivity extends AppCompatActivity {

    private WebView webView;
    private String paymentUrl;
    private long orderId, total;

    private CheckoutRequest checkoutRequest;
    private ApiService api;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bank_payment);

        webView = findViewById(R.id.webViewPayment);

        orderId = getIntent().getLongExtra("order_id", 1);
        total = getIntent().getLongExtra("total_amount", 100000);

        paymentUrl = "http://10.0.2.2:8080/api/payment/page"
                + "?orderId=" + orderId
                + "&total=" + total;

        api = ApiClient.get();
        checkoutRequest = (CheckoutRequest)
                getIntent().getSerializableExtra("checkout_request");

        setupWebView();
    }

    private void setupWebView() {
        WebSettings settings = webView.getSettings();
        settings.setJavaScriptEnabled(true); // Quan trọng: Phải bật JS để trang ngân hàng chạy
        settings.setDomStorageEnabled(true);

        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                if (url.startsWith("myapp://payment/success")) {
                    handlePaymentSuccess();
                    return true;
                }

                if (url.startsWith("myapp://payment/failure")) {
                    handlePaymentFailure();
                    return true;
                }

                if (url.startsWith("myapp://payment/exit")) {
                    handlePaymentExit();
                    return true;
                }
                return false;
            }
        });

        webView.loadUrl(paymentUrl);
    }

    private void handlePaymentSuccess() {
        api.checkout(checkoutRequest).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(BankPaymentActivity.this,
                            "Thanh toán & đặt hàng thành công!",
                            Toast.LENGTH_SHORT).show();

                    Intent intent =
                            new Intent(BankPaymentActivity.this, OrderSuccessActivity.class);
                    intent.putExtra("total_paid", total);
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(BankPaymentActivity.this,
                            "Thanh toán thành công nhưng tạo đơn thất bại",
                            Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(BankPaymentActivity.this,
                        "Lỗi tạo đơn: " + t.getMessage(),
                        Toast.LENGTH_LONG).show();
            }
        });
    }

    private void handlePaymentFailure() {
        // Gọi API xóa đơn hàng như bạn muốn ở bước trước
        Toast.makeText(this, "Thanh toán thất bại, vui lòng thử lại!", Toast.LENGTH_SHORT).show();

        // Xóa lịch sử để không quay lại trang lỗi
        webView.clearHistory();
        webView.clearCache(true);

        // Load lại trang thanh toán ban đầu
        webView.loadUrl(paymentUrl);
    }

    private void handlePaymentExit() {
        Intent intent = new Intent(BankPaymentActivity.this, HomeFragment.class);
        startActivity(intent);
        Toast.makeText(this, "Thanh toán thất bại, đơn hàng đã hủy.", Toast.LENGTH_SHORT).show();
        finish();
    }

}