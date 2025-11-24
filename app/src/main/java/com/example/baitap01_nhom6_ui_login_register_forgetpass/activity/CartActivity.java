package com.example.baitap01_nhom6_ui_login_register_forgetpass.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.baitap01_nhom6_ui_login_register_forgetpass.R;
import com.example.baitap01_nhom6_ui_login_register_forgetpass.adapters.CartAdapter;
import com.example.baitap01_nhom6_ui_login_register_forgetpass.models.CartItem;
import com.example.baitap01_nhom6_ui_login_register_forgetpass.singleton.CartManager;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class CartActivity extends AppCompatActivity implements CartAdapter.OnCartChangeListener {

    private RecyclerView recyclerCart;
    private CheckBox checkboxSelectAll;
    private TextView tvSelectedCount;
    private TextView tvTotalPrice;
    private Button btnCheckout;

    private CartAdapter cartAdapter;
    private List<CartItem> cartItems;

    private final NumberFormat priceFormat =
            NumberFormat.getInstance(new Locale("vi", "VN"));

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (CartManager.getInstance().getCartItems().isEmpty()) {
            startActivity(new Intent(this, EmptyCartActivity.class));
            finish();
            return;
        }

        setContentView(R.layout.activity_cart);

        initViews();
        loadCartData();
        setupRecyclerView();
        setupEvents();
        updateSelectAllState();
        updateSummary();
    }

    private void initViews() {
        recyclerCart = findViewById(R.id.recycler_cart);
        checkboxSelectAll = findViewById(R.id.checkbox_select_all);
        tvSelectedCount = findViewById(R.id.tv_selected_count);
        tvTotalPrice = findViewById(R.id.tv_total_price);
        btnCheckout = findViewById(R.id.btn_checkout);
    }

    private void setupRecyclerView() {
        recyclerCart.setLayoutManager(new LinearLayoutManager(this));
        cartAdapter = new CartAdapter(cartItems, this);
        recyclerCart.setAdapter(cartAdapter);
    }

    private void setupEvents() {
        // Chọn / bỏ chọn tất cả
        checkboxSelectAll.setOnCheckedChangeListener((buttonView, isChecked) -> {
            for (CartItem item : cartItems) {
                item.setSelected(isChecked);
            }
            if (cartItems.size() > 0) {
                cartAdapter.notifyItemRangeChanged(0, cartItems.size());
            }
            updateSummary();
            updateSelectAllState(); // để cập nhật lại text "(x sản phẩm...)"
        });

        // Nút thanh toán
        btnCheckout.setOnClickListener(v -> {
            int selectedCount = getSelectedCount();
            if (selectedCount == 0) {
                Toast.makeText(this,
                        "Vui lòng chọn ít nhất 1 sản phẩm để thanh toán",
                        Toast.LENGTH_SHORT).show();
                return;
            }

            long total = getSelectedTotalPrice();
            String message = "Thanh toán " + selectedCount + " sản phẩm, tổng "
                    + priceFormat.format(total) + " đ";
            Toast.makeText(this, message, Toast.LENGTH_LONG).show();

            // TODO: Sau này bạn chuyển sang màn hình thanh toán thật sự
        });
    }

    private void loadCartData() {
        cartItems = CartManager.getInstance().getCartItems();
        if (cartItems.isEmpty()) {
            startActivity(new Intent(this, EmptyCartActivity.class));
            finish();
        }
    }

    // ========== Các hàm xử lý tổng tiền & số lượng đã chọn ==========

    private int getSelectedCount() {
        int count = 0;
        for (CartItem item : cartItems) {
            if (item.isSelected()) count++;
        }
        return count;
    }

    private long getSelectedTotalPrice() {
        long total = 0;
        for (CartItem item : cartItems) {
            if (item.isSelected()) {
                try {
                    long price = Long.parseLong(item.getProduct().getPrice());
                    total += price * item.getQuantity();
                } catch (NumberFormatException e) {
                    // Xử lý trường hợp giá không phải là số
                }
            }
        }
        return total;
    }

    private void updateSummary() {
        int selectedCount = getSelectedCount();
        long total = getSelectedTotalPrice();

        tvSelectedCount.setText(String.format(new Locale("vi", "VN"), "(%d sản phẩm được chọn)", selectedCount));
        tvTotalPrice.setText(String.format("%s đ", priceFormat.format(total)));
    }

    private void updateSelectAllState() {
        boolean allSelected = !cartItems.isEmpty();
        for (CartItem item : cartItems) {
            if (!item.isSelected()) {
                allSelected = false;
                break;
            }
        }

        // Tạm tắt listener để tránh trigger đệ quy
        checkboxSelectAll.setOnCheckedChangeListener(null);
        checkboxSelectAll.setChecked(allSelected);
        checkboxSelectAll.setOnCheckedChangeListener((buttonView, isChecked) -> {
            for (CartItem item : cartItems) {
                item.setSelected(isChecked);
            }
            if(cartItems.size() > 0) {
                cartAdapter.notifyItemRangeChanged(0, cartItems.size());
            }
            updateSummary();
            updateSelectAllState();
        });
    }

    // ====== Callback từ Adapter ======

    @Override
    public void onCartChanged() {
        updateSelectAllState();
        updateSummary();
    }

    @Override
    public void onItemRemoved(int sizeAfterRemove) {
        updateSelectAllState();
        updateSummary();

        if (sizeAfterRemove == 0) {
            // Nếu giỏ hàng trống -> chuyển sang EmptyCartActivity
            Intent intent = new Intent(this, EmptyCartActivity.class);
            startActivity(intent);
            finish(); // Gọi finish() để người dùng không thể quay lại giỏ hàng trống bằng nút back
        }
    }

}

