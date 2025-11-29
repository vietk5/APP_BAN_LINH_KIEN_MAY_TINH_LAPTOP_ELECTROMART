package com.example.baitap01_nhom6_ui_login_register_forgetpass.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.baitap01_nhom6_ui_login_register_forgetpass.R;
import com.example.baitap01_nhom6_ui_login_register_forgetpass.adapters.CartAdapter;
import com.example.baitap01_nhom6_ui_login_register_forgetpass.models.CartItem;
import com.example.baitap01_nhom6_ui_login_register_forgetpass.models.Product;
import com.example.baitap01_nhom6_ui_login_register_forgetpass.singleton.CartManager;
import com.example.baitap01_nhom6_ui_login_register_forgetpass.remote.ApiClient;
import com.example.baitap01_nhom6_ui_login_register_forgetpass.remote.ApiService;
import com.example.baitap01_nhom6_ui_login_register_forgetpass.models.dto.CartItemDto;
import com.example.baitap01_nhom6_ui_login_register_forgetpass.models.dto.CheckoutItem;
import com.example.baitap01_nhom6_ui_login_register_forgetpass.util.SharedPrefManager;
import com.example.baitap01_nhom6_ui_login_register_forgetpass.util.BottomNavHelper;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

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

    // API + user
    private ApiService apiService;
    private SharedPrefManager sharedPrefManager;
    private int userId; // từ SharedPrefManager

    // Launcher để nhận result từ CheckoutActivity
    private ActivityResultLauncher<Intent> checkoutLauncher;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Đăng ký nhận kết quả từ màn Checkout
        checkoutLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        long[] purchasedIds = result.getData().getLongArrayExtra("purchased_ids");
                        if (purchasedIds != null && purchasedIds.length > 0) {
                            handlePurchasedItems(purchasedIds);
                        }
                    }
                }
        );

        setContentView(R.layout.activity_cart);

        initViews();
        initSession();
        loadCartDataLocal();    // dữ liệu local từ CartManager
        setupRecyclerView();
        setupEvents();

        updateSelectAllState();
        updateSummary();

        // sau khi UI sẵn sàng thì gọi server để đồng bộ giỏ hàng
        fetchCartFromServer();
        BottomNavHelper.setup(this, "CART");
    }

    private void initViews() {
        recyclerCart = findViewById(R.id.recycler_cart);
        checkboxSelectAll = findViewById(R.id.checkbox_select_all);
        tvSelectedCount = findViewById(R.id.tv_selected_count);
        tvTotalPrice = findViewById(R.id.tv_total_price);
        btnCheckout = findViewById(R.id.btn_checkout);
    }

    private void initSession() {
        apiService = ApiClient.get();
        sharedPrefManager = new SharedPrefManager(this);
        userId = sharedPrefManager.getUserId(); // -1 nếu chưa login
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
            if (!cartItems.isEmpty()) {
                cartAdapter.notifyItemRangeChanged(0, cartItems.size());
            }
            updateSummary();
            updateSelectAllState();
        });

        // Nút thanh toán
        btnCheckout.setOnClickListener(v -> {

            // bắt buộc đăng nhập
            if (!sharedPrefManager.isLoggedIn() || userId <= 0) {
                Toast.makeText(this,
                        "Vui lòng đăng nhập để thanh toán",
                        Toast.LENGTH_SHORT).show();
                startActivity(new Intent(this, LoginActivity.class));
                return;
            }

            // Lấy danh sách item đã chọn
            ArrayList<CheckoutItem> selectedItems = new ArrayList<>();

            for (CartItem item : cartItems) {
                if (!item.isSelected()) continue;

                try {
                    long price = Long.parseLong(item.getProduct().getPrice());
                    CheckoutItem ci = new CheckoutItem(
                            item.getProduct().getId(),
                            item.getProduct().getName(),
                            item.getProduct().getImageUrl(),
                            price,
                            item.getQuantity()
                    );
                    selectedItems.add(ci);
                } catch (NumberFormatException e) {
                    // bỏ qua item lỗi giá
                }
            }

            if (selectedItems.isEmpty()) {
                Toast.makeText(this,
                        "Vui lòng chọn ít nhất 1 sản phẩm để thanh toán",
                        Toast.LENGTH_SHORT).show();
                return;
            }

            Intent intent = new Intent(CartActivity.this, CheckoutActivity.class);
            intent.putExtra("items", selectedItems);
            checkoutLauncher.launch(intent);
        });
    }

    /**
     * Lấy dữ liệu từ CartManager (local) – fallback khi chưa login / lỗi mạng.
     */
    private void loadCartDataLocal() {
        cartItems = CartManager.getInstance().getCartItems();
        if (cartItems == null) {
            cartItems = new ArrayList<>();
        }
    }

    /**
     * Gọi API lấy giỏ hàng từ database nếu user đã đăng nhập.
     */
    private void fetchCartFromServer() {
        if (userId <= 0) {
            if (cartItems.isEmpty()) {
                goToEmptyCart();
            }
            return;
        }

        apiService.getCart(userId).enqueue(new Callback<List<CartItemDto>>() {
            @Override
            public void onResponse(Call<List<CartItemDto>> call,
                                   Response<List<CartItemDto>> response) {
                if (!response.isSuccessful() || response.body() == null) {
                    if (cartItems.isEmpty()) {
                        goToEmptyCart();
                    }
                    return;
                }

                List<CartItemDto> dtoList = response.body();

                cartItems.clear();
                for (CartItemDto dto : dtoList) {
                    CartItem item = mapDtoToCartItem(dto);
                    if (item != null) {
                        cartItems.add(item);
                    }
                }
                cartAdapter.notifyDataSetChanged();

                if (cartItems.isEmpty()) {
                    goToEmptyCart();
                } else {
                    updateSelectAllState();
                    updateSummary();
                }
            }

            @Override
            public void onFailure(Call<List<CartItemDto>> call, Throwable t) {
                if (cartItems.isEmpty()) {
                    goToEmptyCart();
                } else {
                    Toast.makeText(CartActivity.this,
                            "Không tải được giỏ hàng từ server, dùng dữ liệu offline.",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private CartItem mapDtoToCartItem(CartItemDto dto) {
        if (dto == null) return null;

        Long productId = dto.getProductId();
        String name = dto.getProductName();
        String imageUrl = dto.getImageUrl();

        BigDecimal unitPrice = dto.getUnitPrice();
        long priceLong = unitPrice != null ? unitPrice.longValue() : 0L;
        String priceStr = String.valueOf(priceLong);

        Product p = new Product(productId, name, priceStr, imageUrl);
        return new CartItem(p, dto.getQuantity(), true);
    }

    private void goToEmptyCart() {
        Intent intent = new Intent(this, EmptyCartActivity.class);
        startActivity(intent);
        finish();
    }

    // ========== Xử lý sau khi đặt hàng thành công: xóa sản phẩm khỏi giỏ ==========

    private void handlePurchasedItems(long[] purchasedIds) {
        Set<Long> purchasedSet = new HashSet<>();
        for (long id : purchasedIds) {
            purchasedSet.add(id);
        }

        // Xóa khỏi list cartItems (list này thường là list nội bộ của CartManager luôn)
        Iterator<CartItem> it = cartItems.iterator();
        while (it.hasNext()) {
            CartItem item = it.next();
            Long pid = item.getProduct().getId();
            if (pid != null && purchasedSet.contains(pid)) {
                it.remove();
            }
        }
        cartAdapter.notifyDataSetChanged();

        // Nếu dùng CartManager trả về đúng list tham chiếu thì không cần làm gì thêm.
        // Nếu CartManager của bạn dùng list riêng, có thể thêm hàm sync:

        // CartManager.getInstance().setCartItems(cartItems); // nếu bạn có hàm này

        if (cartItems.isEmpty()) {
            goToEmptyCart();
        } else {
            updateSelectAllState();
            updateSummary();
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
                    // giá không phải số thì bỏ qua
                }
            }
        }
        return total;
    }

    private void updateSummary() {
        int selectedCount = getSelectedCount();
        long total = getSelectedTotalPrice();

        tvSelectedCount.setText(
                String.format(new Locale("vi", "VN"),
                        "(%d sản phẩm được chọn)", selectedCount));
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

        checkboxSelectAll.setOnCheckedChangeListener(null);
        checkboxSelectAll.setChecked(allSelected);
        checkboxSelectAll.setOnCheckedChangeListener((buttonView, isChecked) -> {
            for (CartItem item : cartItems) {
                item.setSelected(isChecked);
            }
            if (!cartItems.isEmpty()) {
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
            goToEmptyCart();
        }
    }
}
