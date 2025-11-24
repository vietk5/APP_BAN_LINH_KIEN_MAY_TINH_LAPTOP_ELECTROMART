package com.example.baitap01_nhom6_ui_login_register_forgetpass.activity;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.widget.Toolbar;


import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.baitap01_nhom6_ui_login_register_forgetpass.R;
import com.example.baitap01_nhom6_ui_login_register_forgetpass.adapters.OrderProductAdapter;
import com.example.baitap01_nhom6_ui_login_register_forgetpass.models.Order;
import com.example.baitap01_nhom6_ui_login_register_forgetpass.models.OrderProduct;
import com.example.baitap01_nhom6_ui_login_register_forgetpass.models.ShippingAddress;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class OrderConfirmationActivity extends AppCompatActivity {
    // Views
    private Toolbar toolbar;
    private TextInputEditText edtReceiverName, edtPhoneNumber, edtDetailAddress, edtNote;
    private TextInputEditText edtVoucherCode;
    private AutoCompleteTextView spinnerProvince, spinnerDistrict, spinnerWard;
    private RecyclerView rcvOrderProducts;
    private RadioGroup rgPaymentMethod;
    private TextView tvProductCount, tvSubtotal, tvShippingFee, tvDiscount;
    private TextView tvTotalPrice, tvBottomTotalPrice, tvVoucherMessage;
    private MaterialButton btnPlaceOrder, btnApplyVoucher;

    // Data
    private OrderProductAdapter adapter;
    private List<OrderProduct> productList = new ArrayList<>();
    private Order order = new Order();

    // Price values
    private long subtotal = 0;
    private long shippingFee = 0;
    private long discount = 0; // Giảm giá mẫu
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_order_confirmation);

        initViews();
        setupToolbar();
        setupSpinners();
        setupRecyclerView();
        loadSampleData();
        setupListeners();
        calculateTotal();
    }
    private void initViews() {
        toolbar = findViewById(R.id.toolbar);
        edtReceiverName = findViewById(R.id.edtReceiverName);
        edtPhoneNumber = findViewById(R.id.edtPhoneNumber);
        edtDetailAddress = findViewById(R.id.edtDetailAddress);
        edtNote = findViewById(R.id.edtNote);
        edtVoucherCode = findViewById(R.id.edtVoucherCode);
        spinnerProvince = findViewById(R.id.spinnerProvince);
        spinnerDistrict = findViewById(R.id.spinnerDistrict);
        spinnerWard = findViewById(R.id.spinnerWard);
        rcvOrderProducts = findViewById(R.id.rcvOrderProducts);
        rgPaymentMethod = findViewById(R.id.rgPaymentMethod);
        tvProductCount = findViewById(R.id.tvProductCount);
        tvSubtotal = findViewById(R.id.tvSubtotal);
        tvShippingFee = findViewById(R.id.tvShippingFee);
        tvDiscount = findViewById(R.id.tvDiscount);
        tvTotalPrice = findViewById(R.id.tvTotalPrice);
        tvBottomTotalPrice = findViewById(R.id.tvBottomTotalPrice);
        tvVoucherMessage = findViewById(R.id.tvVoucherMessage);
        btnPlaceOrder = findViewById(R.id.btnPlaceOrder);
        btnApplyVoucher = findViewById(R.id.btnApplyVoucher);
    }

    private void setupToolbar() {
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        toolbar.setNavigationOnClickListener(v -> onBackPressed());
    }

    private void setupSpinners() {
        // Dữ liệu mẫu - Thực tế nên lấy từ API
        String[] provinces = {"TP. Hồ Chí Minh", "Hà Nội", "Đà Nẵng", "Cần Thơ", "Hải Phòng"};
        String[] districts = {"Quận 1", "Quận 3", "Quận 7", "Quận Bình Thạnh", "Quận Tân Bình"};
        String[] wards = {"Phường 1", "Phường 2", "Phường 3", "Phường 4", "Phường 5"};

        ArrayAdapter<String> provinceAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_dropdown_item_1line, provinces);
        spinnerProvince.setAdapter(provinceAdapter);

        ArrayAdapter<String> districtAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_dropdown_item_1line, districts);
        spinnerDistrict.setAdapter(districtAdapter);

        ArrayAdapter<String> wardAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_dropdown_item_1line, wards);
        spinnerWard.setAdapter(wardAdapter);

        // Lắng nghe khi chọn tỉnh/thành để load quận/huyện tương ứng
        spinnerProvince.setOnItemClickListener((parent, view, position, id) -> {
            spinnerDistrict.setText("");
            spinnerWard.setText("");
            // TODO: Load districts theo province được chọn
        });

        spinnerDistrict.setOnItemClickListener((parent, view, position, id) -> {
            spinnerWard.setText("");
            // TODO: Load wards theo district được chọn
        });
    }

    private void setupRecyclerView() {
        adapter = new OrderProductAdapter(this, productList);
        rcvOrderProducts.setLayoutManager(new LinearLayoutManager(this));
        rcvOrderProducts.setAdapter(adapter);
        rcvOrderProducts.setNestedScrollingEnabled(false);
    }

    private void loadSampleData() {
        // Dữ liệu mẫu - Thực tế lấy từ Cart/Intent
        productList.add(new OrderProduct(1, "Laptop Gaming ASUS", "https://www.google.com/url?sa=i&url=https%3A%2F%2Fcellphones.com.vn%2Flaptop%2Fasus%2Fgaming.html&psig=AOvVaw1t8PqoslyguS1srSSO3LQf&ust=1763954135024000&source=images&cd=vfe&opi=89978449&ved=0CBUQjRxqFwoTCPCVirCnh5EDFQAAAAAdAAAAABAE", "Red", 25990000, 30000000, 1));

        adapter.updateData(productList);
        tvProductCount.setText(productList.size() + " sản phẩm");
    }

    private void setupListeners() {
        btnPlaceOrder.setOnClickListener(v -> placeOrder());

        btnApplyVoucher.setOnClickListener(v -> applyVoucher());

        rgPaymentMethod.setOnCheckedChangeListener((group, checkedId) -> {
            // Xử lý khi thay đổi phương thức thanh toán
            if (checkedId == R.id.rbCOD) {
                order.setPaymentMethod("COD");
            }
//            else if (checkedId == R.id.rbBankTransfer) {
//                order.setPaymentMethod("BANK_TRANSFER");
//            } else if (checkedId == R.id.rbMomo) {
//                order.setPaymentMethod("MOMO");
//            } else if (checkedId == R.id.rbVNPay) {
//                order.setPaymentMethod("VNPAY");
//            }
        });
    }

    private void calculateTotal() {
        subtotal = 0;
        for (OrderProduct product : productList) {
            subtotal += product.getTotalPrice();
        }

        // Miễn phí ship cho đơn > 500k
        shippingFee = subtotal >= 500000 ? 0 : 30000;

        long total = subtotal + shippingFee - discount;

        tvSubtotal.setText(formatPrice(subtotal));

        if (shippingFee == 0) {
            tvShippingFee.setText("Miễn phí");
            tvShippingFee.setTextColor(getResources().getColor(R.color.green_500));
        } else {
            tvShippingFee.setText(formatPrice(shippingFee));
            tvShippingFee.setTextColor(getResources().getColor(R.color.text_primary));
        }

        tvDiscount.setText("-" + formatPrice(discount));
        tvTotalPrice.setText(formatPrice(total));
        tvBottomTotalPrice.setText(formatPrice(total));
    }

    private void placeOrder() {
        if (!validateInput()) return;

        // Tạo địa chỉ giao hàng
        ShippingAddress address = new ShippingAddress(
                edtReceiverName.getText().toString().trim(),
                edtPhoneNumber.getText().toString().trim(),
                spinnerProvince.getText().toString(),
                spinnerDistrict.getText().toString(),
                spinnerWard.getText().toString(),
                edtDetailAddress.getText().toString().trim()
        );

        // Tạo đơn hàng
        order.setShippingAddress(address);
        order.setProducts(productList);
        order.setNote(edtNote.getText().toString().trim());
        order.setSubtotal(subtotal);
        order.setShippingFee(shippingFee);
        order.setDiscount(discount);
        order.setTotalPrice(subtotal + shippingFee - discount);
        order.setStatus("PENDING");
        order.setCreatedAt(System.currentTimeMillis());

        // TODO: Gửi đơn hàng lên server
        // orderRepository.createOrder(order, new Callback() {...});

        Toast.makeText(this, "Đặt hàng thành công!", Toast.LENGTH_SHORT).show();

        // Chuyển đến trang thành công
        // Intent intent = new Intent(this, OrderSuccessActivity.class);
        // intent.putExtra("order", order);
        // startActivity(intent);
        // finish();
    }

    private boolean validateInput() {
        if (TextUtils.isEmpty(edtReceiverName.getText())) {
            edtReceiverName.setError("Vui lòng nhập họ tên");
            edtReceiverName.requestFocus();
            return false;
        }

        String phone = edtPhoneNumber.getText().toString().trim();
        if (TextUtils.isEmpty(phone)) {
            edtPhoneNumber.setError("Vui lòng nhập số điện thoại");
            edtPhoneNumber.requestFocus();
            return false;
        }
        if (!phone.matches("^(0[3|5|7|8|9])[0-9]{8}$")) {
            edtPhoneNumber.setError("Số điện thoại không hợp lệ");
            edtPhoneNumber.requestFocus();
            return false;
        }

        if (TextUtils.isEmpty(spinnerProvince.getText())) {
            spinnerProvince.setError("Vui lòng chọn tỉnh/thành phố");
            spinnerProvince.requestFocus();
            return false;
        }

        if (TextUtils.isEmpty(spinnerDistrict.getText())) {
            spinnerDistrict.setError("Vui lòng chọn quận/huyện");
            spinnerDistrict.requestFocus();
            return false;
        }

        if (TextUtils.isEmpty(spinnerWard.getText())) {
            spinnerWard.setError("Vui lòng chọn phường/xã");
            spinnerWard.requestFocus();
            return false;
        }

        if (TextUtils.isEmpty(edtDetailAddress.getText())) {
            edtDetailAddress.setError("Vui lòng nhập địa chỉ chi tiết");
            edtDetailAddress.requestFocus();
            return false;
        }

        return true;
    }

    private String formatPrice(long price) {
        DecimalFormat formatter = new DecimalFormat("#,###");
        return formatter.format(price) + "đ";
    }

    private void applyVoucher() {
        String voucherCode = edtVoucherCode.getText().toString().trim().toUpperCase();

        if (TextUtils.isEmpty(voucherCode)) {
            tvVoucherMessage.setVisibility(View.VISIBLE);
            tvVoucherMessage.setText("Vui lòng nhập mã giảm giá");
            tvVoucherMessage.setTextColor(getResources().getColor(R.color.red_500));
            return;
        }

        // TODO: Gọi API kiểm tra mã giảm giá
        // Ví dụ mã giảm giá mẫu
        if (voucherCode.equals("TECHSTORE2024")) {
            discount = 3000000; // Giảm 3 triệu
            tvVoucherMessage.setVisibility(View.VISIBLE);
            tvVoucherMessage.setText("✓ Áp dụng mã giảm giá thành công! Giảm " + formatPrice(discount));
            tvVoucherMessage.setTextColor(getResources().getColor(R.color.green_500));

            // Vô hiệu hóa input và button
            edtVoucherCode.setEnabled(false);
            btnApplyVoucher.setText("Đã áp dụng");
            btnApplyVoucher.setEnabled(false);

        } else if (voucherCode.equals("WELCOME10")) {
            // Giảm 10%
            discount = subtotal / 10;
            tvVoucherMessage.setVisibility(View.VISIBLE);
            tvVoucherMessage.setText("✓ Áp dụng mã giảm giá thành công! Giảm " + formatPrice(discount));
            tvVoucherMessage.setTextColor(getResources().getColor(R.color.green_500));

            edtVoucherCode.setEnabled(false);
            btnApplyVoucher.setText("Đã áp dụng");
            btnApplyVoucher.setEnabled(false);

        } else {
            discount = 0;
            tvVoucherMessage.setVisibility(View.VISIBLE);
            tvVoucherMessage.setText("✗ Mã giảm giá không hợp lệ hoặc đã hết hạn");
            tvVoucherMessage.setTextColor(getResources().getColor(R.color.red_500));
        }

        // Tính lại tổng tiền
        calculateTotal();
    }
}