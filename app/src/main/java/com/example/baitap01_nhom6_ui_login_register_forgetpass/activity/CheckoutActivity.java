package com.example.baitap01_nhom6_ui_login_register_forgetpass.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.baitap01_nhom6_ui_login_register_forgetpass.R;
import com.example.baitap01_nhom6_ui_login_register_forgetpass.adapters.CheckoutItemAdapter;
import com.example.baitap01_nhom6_ui_login_register_forgetpass.models.dto.CheckoutItem;
import com.example.baitap01_nhom6_ui_login_register_forgetpass.models.dto.CheckoutRequest;
import com.example.baitap01_nhom6_ui_login_register_forgetpass.models.voucher.ApplyVoucherRequest;
import com.example.baitap01_nhom6_ui_login_register_forgetpass.models.voucher.ApplyVoucherResponse;
import com.example.baitap01_nhom6_ui_login_register_forgetpass.remote.ApiClient;
import com.example.baitap01_nhom6_ui_login_register_forgetpass.remote.ApiService;
import com.example.baitap01_nhom6_ui_login_register_forgetpass.util.PriceFormatter;
import com.example.baitap01_nhom6_ui_login_register_forgetpass.util.SharedPrefManager;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CheckoutActivity extends AppCompatActivity
        implements CheckoutItemAdapter.OnQuantityChangeListener {

    private TextView tvReceiverName;
    private EditText edtReceiverPhone, edtReceiverAddress;

    private TextView tvItemsSummary;
    private RecyclerView rvCheckoutItems;

    private TextView tvSubtotal, tvDiscount, tvShippingFee, tvTotal, tvTotalBottom;
    private EditText edtVoucher;
    private TextView tvVoucherInfo;
    private Button btnApplyVoucher, btnPlaceOrder;
    private RadioGroup rgPayment;
    private CheckoutItemAdapter checkoutAdapter;

    private String appliedVoucherCode = ""; // code đã apply thành công (hoặc local)
    private int isBuyNow = 0;

    private final List<CheckoutItem> items = new ArrayList<>();

    private long shippingFee = 30000;  // demo
    private long discount = 0;

    private boolean isApplyingVoucher = false;

    private ApiService api;
    private SharedPrefManager pref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkout);

        pref = new SharedPrefManager(this);
        api  = ApiClient.get();

        initViews();
        applyWindowInsets();
        readIntentData();
        setupProductList();
        fillUserInfo();
        calculateTotals();
        setupEvents();
    }

    private void initViews() {
        tvReceiverName     = findViewById(R.id.tvReceiverName);
        edtReceiverPhone   = findViewById(R.id.edtReceiverPhone);
        edtReceiverAddress = findViewById(R.id.edtReceiverAddress);

        tvItemsSummary  = findViewById(R.id.tvItemsSummary);
        rvCheckoutItems = findViewById(R.id.rvCheckoutItems);

        tvSubtotal    = findViewById(R.id.tvSubtotal);
        tvDiscount    = findViewById(R.id.tvDiscount);
        tvShippingFee = findViewById(R.id.tvShippingFee);
        tvTotal       = findViewById(R.id.tvTotal);
        tvTotalBottom = findViewById(R.id.tvTotalBottom);

        edtVoucher      = findViewById(R.id.edtVoucher);
        btnApplyVoucher = findViewById(R.id.btnApplyVoucher);
        tvVoucherInfo   = findViewById(R.id.tvVoucherInfo);

        rgPayment    = findViewById(R.id.rgPayment);
        btnPlaceOrder= findViewById(R.id.btnPlaceOrder);
    }

    private void applyWindowInsets() {
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.checkoutRoot), (v, insets) -> {
            Insets bars = insets.getInsets(WindowInsetsCompat.Type.statusBars());
            v.setPadding(0, bars.top, 0, 0);
            return insets;
        });
    }

    @SuppressWarnings("unchecked")
    private void readIntentData() {
        ArrayList<CheckoutItem> fromIntent =
                (ArrayList<CheckoutItem>) getIntent().getSerializableExtra("items");
        if (fromIntent != null) {
            items.clear();
            items.addAll(fromIntent);
        }

        if (items.isEmpty()) {
            Toast.makeText(this, "Không có sản phẩm để thanh toán", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void setupProductList() {
        rvCheckoutItems.setLayoutManager(new LinearLayoutManager(this));
        checkoutAdapter = new CheckoutItemAdapter(items, this);
        rvCheckoutItems.setAdapter(checkoutAdapter);

        updateItemsSummary();
    }

    private void updateItemsSummary() {
        int count = 0;
        long subtotal = 0;
        for (CheckoutItem ci : items) {
            count += ci.getQuantity();
            subtotal += ci.getUnitPrice() * ci.getQuantity();
        }
        tvItemsSummary.setText(count + " sản phẩm đã chọn\n" + PriceFormatter.vnd(subtotal));
    }

    private void fillUserInfo() {
        String name = pref.getName();
        tvReceiverName.setText(!TextUtils.isEmpty(name) ? name : "Khách hàng");
    }

    private long getSubtotal() {
        long subtotal = 0;
        for (CheckoutItem ci : items) {
            subtotal += ci.getUnitPrice() * ci.getQuantity();
        }
        return subtotal;
    }

    private void calculateTotals() {
        long subtotal = getSubtotal();

        long total = subtotal + shippingFee - discount;
        if (total < 0) total = 0;

        tvSubtotal.setText(PriceFormatter.vnd(subtotal));
        tvDiscount.setText("- " + PriceFormatter.vnd(discount));
        tvShippingFee.setText(PriceFormatter.vnd(shippingFee));
        tvTotal.setText(PriceFormatter.vnd(total));
        tvTotalBottom.setText(PriceFormatter.vnd(total));
    }

    private void setupEvents() {

        // Áp dụng voucher
        btnApplyVoucher.setOnClickListener(v -> {
            String codeInput = edtVoucher.getText().toString().trim();
            appliedVoucherCode = codeInput; // tạm lưu user nhập
            applyVoucherAndRecompute(true);
        });

        // Xác nhận đặt hàng
        btnPlaceOrder.setOnClickListener(v -> {
            String phone   = edtReceiverPhone.getText().toString().trim();
            String address = edtReceiverAddress.getText().toString().trim();

            if (phone.isEmpty()) {
                edtReceiverPhone.setError("Vui lòng nhập số điện thoại");
                edtReceiverPhone.requestFocus();
                return;
            }
            if (address.isEmpty()) {
                edtReceiverAddress.setError("Vui lòng nhập địa chỉ nhận hàng");
                edtReceiverAddress.requestFocus();
                return;
            }

            int checkedId = rgPayment.getCheckedRadioButtonId();
            if (checkedId == -1) {
                Toast.makeText(this,
                        "Vui lòng chọn phương thức thanh toán",
                        Toast.LENGTH_SHORT).show();
                return;
            }

            String paymentMethod = (checkedId == R.id.rbCod) ? "COD" : "BANK";

            int userId = pref.getUserId();
            if (userId <= 0) {
                Toast.makeText(this,
                        "Lỗi tài khoản, vui lòng đăng nhập lại",
                        Toast.LENGTH_SHORT).show();
                return;
            }

            // ✅ gửi đúng voucher đã apply (nếu apply fail thì sẽ rỗng)
            String voucherCode  = (appliedVoucherCode == null) ? "" : appliedVoucherCode.trim();
            String receiverName = tvReceiverName.getText().toString().trim();

            // list productId
            List<Long> productIds = new ArrayList<>();
            for (CheckoutItem ci : items) {
                productIds.add(ci.getProductId());
            }

            isBuyNow = getIntent().getIntExtra("isBuyNow", 0);

            CheckoutRequest req = new CheckoutRequest(
                    isBuyNow,
                    userId,
                    receiverName,
                    phone,
                    address,
                    paymentMethod,
                    voucherCode,
                    productIds
            );

            long subtotal = getSubtotal();
            long tmpTotal = subtotal + shippingFee - discount;
            if (tmpTotal < 0) tmpTotal = 0;
            final long totalFinal = tmpTotal;

            if ("BANK".equals(paymentMethod)) {
                Intent bankIntent = new Intent(CheckoutActivity.this, BankPaymentActivity.class);
                bankIntent.putExtra("total_amount", totalFinal);
                bankIntent.putExtra("checkout_request", req);
                startActivity(bankIntent);
            } else {
                api.checkout(req).enqueue(new Callback<Void>() {
                    @Override
                    public void onResponse(Call<Void> call, Response<Void> response) {
                        if (response.isSuccessful()) {
                            Log.d("API_SUCCESS", "Order Created for User: " + response.body());
                            Intent successIntent =
                                    new Intent(CheckoutActivity.this, OrderSuccessActivity.class);
                            successIntent.putExtra("total_paid", totalFinal);
                            startActivity(successIntent);
                            finish();
                        } else {
                            Log.e("API_ERROR", "Mã lỗi: " + response.code());
                            Toast.makeText(CheckoutActivity.this,
                                    "Server từ chối: " + response.code(),
                                    Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<Void> call, Throwable t) {
                        Toast.makeText(CheckoutActivity.this,
                                "Lỗi kết nối", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    /**
     * Giữ logic cũ (GIAM10/FREESHIP) + gọi backend cho voucher khác
     */
    private void applyVoucherAndRecompute(boolean showMsg) {
        if (isApplyingVoucher) return;

        long subtotal = getSubtotal();
        String code = (appliedVoucherCode == null) ? "" : appliedVoucherCode.trim().toUpperCase();

        discount = 0;

        if (code.isEmpty()) {
            appliedVoucherCode = "";
            if (showMsg) tvVoucherInfo.setText("Chưa nhập mã giảm giá.");
            calculateTotals();
            return;
        }

        // ====== GIỮ CHỨC NĂNG CŨ (LOCAL) ======
        if ("GIAM10".equals(code)) {
            discount = Math.round(subtotal * 0.10);
            appliedVoucherCode = "GIAM10";
            if (showMsg) tvVoucherInfo.setText("Áp dụng mã GIAM10: giảm 10% giá trị đơn hàng.");
            calculateTotals();
            return;
        }

        if ("FREESHIP".equals(code)) {
            discount = shippingFee;
            appliedVoucherCode = "FREESHIP";
            if (showMsg) tvVoucherInfo.setText("Áp dụng mã FREESHIP: miễn phí vận chuyển.");
            calculateTotals();
            return;
        }

        // ====== VOUCHER THẬT (BACKEND) ======
        isApplyingVoucher = true;
        btnApplyVoucher.setEnabled(false);
        if (showMsg) tvVoucherInfo.setText("Đang áp dụng voucher...");

        // server của bạn đang test bằng tongTien = subtotal (theo ảnh)
        ApplyVoucherRequest req = new ApplyVoucherRequest(code, subtotal);

        api.applyVoucher(req).enqueue(new Callback<ApplyVoucherResponse>() {
            @Override
            public void onResponse(Call<ApplyVoucherResponse> call, Response<ApplyVoucherResponse> response) {
                isApplyingVoucher = false;
                btnApplyVoucher.setEnabled(true);

                if (!response.isSuccessful() || response.body() == null) {
                    appliedVoucherCode = "";
                    discount = 0;
                    if (showMsg) tvVoucherInfo.setText("Áp dụng thất bại (" + response.code() + ")");
                    calculateTotals();
                    return;
                }

                ApplyVoucherResponse r = response.body();
                if (!r.valid) {
                    appliedVoucherCode = "";
                    discount = 0;
                    if (showMsg) tvVoucherInfo.setText(r.message != null ? r.message : "Voucher không hợp lệ");
                    calculateTotals();
                    return;
                }

                // success
                appliedVoucherCode = code;
                discount = Math.max(0, r.soTienGiam);

                if (showMsg) {
                    String m = (r.message != null ? r.message : "Áp dụng voucher thành công");
                    // nếu discount = 0 thì cảnh báo nhẹ
                    if (discount == 0) m += " (Voucher hiện giảm 0đ)";
                    tvVoucherInfo.setText(m);
                }
                calculateTotals();
            }

            @Override
            public void onFailure(Call<ApplyVoucherResponse> call, Throwable t) {
                isApplyingVoucher = false;
                btnApplyVoucher.setEnabled(true);

                appliedVoucherCode = "";
                discount = 0;
                if (showMsg) tvVoucherInfo.setText("Lỗi kết nối khi áp voucher");
                calculateTotals();
            }
        });
    }

    // callback từ adapter khi quantity bất kỳ item thay đổi
    @Override
    public void onQuantityChanged() {
        updateItemsSummary();

        // Nếu đang có voucher -> tự cập nhật lại discount
        if (appliedVoucherCode != null && !appliedVoucherCode.trim().isEmpty()) {
            applyVoucherAndRecompute(false);
        } else {
            calculateTotals();
        }
    }
}
