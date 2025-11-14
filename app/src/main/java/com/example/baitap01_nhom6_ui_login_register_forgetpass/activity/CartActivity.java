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
import com.example.baitap01_nhom6_ui_login_register_forgetpass.models.Product;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class CartActivity extends AppCompatActivity implements CartAdapter.OnCartChangeListener {

    private RecyclerView recyclerCart;
    private CheckBox checkboxSelectAll;
    private TextView tvSelectedCount;
    private TextView tvTotalPrice;
    private Button btnCheckout;

    private CartAdapter cartAdapter;
    private final List<CartAdapter.CartItem> cartItems = new ArrayList<>();

    private final NumberFormat priceFormat =
            NumberFormat.getInstance(new Locale("vi", "VN"));

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        initViews();
        setupRecyclerView();
        setupEvents();
        loadCartData();     // TODO: sau này bạn thay phần fake data bằng dữ liệu thực
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
            for (CartAdapter.CartItem item : cartItems) {
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

    /**
     * Tạm thời fake dữ liệu mẫu để test.
     * Sau này bạn thay thế bằng dữ liệu thật (từ API / Singleton / DB / Intent,...)
     */
    private void loadCartData() {
        cartItems.clear();

        // Ví dụ: dùng Product có sẵn của bạn
        // Giả định Product có các hàm: getId(), getName(), getPrice(), getImageUrl()
        // Nếu khác, bạn sửa lại phần này cho khớp.
        Product p1 = new Product("Laptop Gaming XYZ", "15000000", "");
        Product p2 = new Product("Laptop Gaming XYZ", "15000000", "");
        Product p3 = new Product("Laptop Gaming XYZ", "15000000", "");

        cartItems.add(new CartAdapter.CartItem(p1, 1, true));
        cartItems.add(new CartAdapter.CartItem(p2, 1, true));
        cartItems.add(new CartAdapter.CartItem(p3, 1, true));

        cartAdapter.notifyDataSetChanged();
    }

    // ========== Các hàm xử lý tổng tiền & số lượng đã chọn ==========

    private int getSelectedCount() {
        int count = 0;
        for (CartAdapter.CartItem item : cartItems) {
            if (item.isSelected()) count++;
        }
        return count;
    }

    private long getSelectedTotalPrice() {
        long total = 0;
        for (CartAdapter.CartItem item : cartItems) {
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
        for (CartAdapter.CartItem item : cartItems) {
            if (!item.isSelected()) {
                allSelected = false;
                break;
            }
        }

        // Tạm tắt listener để tránh trigger đệ quy
        checkboxSelectAll.setOnCheckedChangeListener(null);
        checkboxSelectAll.setChecked(allSelected);
        checkboxSelectAll.setOnCheckedChangeListener((buttonView, isChecked) -> {
            for (CartAdapter.CartItem item : cartItems) {
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
            // Sửa dòng dưới đây
            Intent intent = new Intent(this, EmptyCartActivity.class); // <--- Sửa ở đây
            startActivity(intent);
            finish(); // Gọi finish() để người dùng không thể quay lại giỏ hàng trống bằng nút back
        }
    }

}
