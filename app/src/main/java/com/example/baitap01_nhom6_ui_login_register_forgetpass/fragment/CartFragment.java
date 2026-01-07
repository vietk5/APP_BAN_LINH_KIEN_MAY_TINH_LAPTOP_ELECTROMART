package com.example.baitap01_nhom6_ui_login_register_forgetpass.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.baitap01_nhom6_ui_login_register_forgetpass.R;
import com.example.baitap01_nhom6_ui_login_register_forgetpass.activity.CheckoutActivity;
import com.example.baitap01_nhom6_ui_login_register_forgetpass.activity.EmptyCartActivity;
import com.example.baitap01_nhom6_ui_login_register_forgetpass.activity.LoginActivity;
import com.example.baitap01_nhom6_ui_login_register_forgetpass.adapters.CartAdapter;
import com.example.baitap01_nhom6_ui_login_register_forgetpass.models.CartItem;
import com.example.baitap01_nhom6_ui_login_register_forgetpass.models.Product;
import com.example.baitap01_nhom6_ui_login_register_forgetpass.models.dto.CartItemDto;
import com.example.baitap01_nhom6_ui_login_register_forgetpass.models.dto.CheckoutItem;
import com.example.baitap01_nhom6_ui_login_register_forgetpass.remote.ApiClient;
import com.example.baitap01_nhom6_ui_login_register_forgetpass.remote.ApiService;
import com.example.baitap01_nhom6_ui_login_register_forgetpass.singleton.CartManager;
import com.example.baitap01_nhom6_ui_login_register_forgetpass.util.SharedPrefManager;

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

public class CartFragment extends Fragment implements CartAdapter.OnCartChangeListener {

    private RecyclerView recyclerCart;
    private CheckBox checkboxSelectAll;
    private TextView tvSelectedCount;
    private TextView tvTotalPrice;
    private Button btnCheckout;
    private ProgressBar progressBar; // Thêm biến ProgressBar

    private CartAdapter cartAdapter;
    private List<CartItem> cartItems;

    private final NumberFormat priceFormat = NumberFormat.getInstance(new Locale("vi", "VN"));

    private ApiService apiService;
    private SharedPrefManager sharedPrefManager;
    private int userId;
    private boolean isEmptyCartOpened = false;


    private ActivityResultLauncher<Intent> checkoutLauncher;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        checkoutLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                        long[] purchasedIds = result.getData().getLongArrayExtra("purchased_ids");
                        if (purchasedIds != null && purchasedIds.length > 0) {
                            handlePurchasedItems(purchasedIds);
                        }
                    }
                }
        );
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_cart, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initViews(view);
        initSession();
        loadCartDataLocal();
        setupRecyclerView();
        setupEvents();

        updateSelectAllState();
        updateSummary();
        fetchCartFromServer();
    }

    private void initViews(View view) {
        recyclerCart = view.findViewById(R.id.recycler_cart);
        checkboxSelectAll = view.findViewById(R.id.checkbox_select_all);
        tvSelectedCount = view.findViewById(R.id.tv_selected_count);
        tvTotalPrice = view.findViewById(R.id.tv_total_price);
        btnCheckout = view.findViewById(R.id.btn_checkout);
        progressBar = view.findViewById(R.id.progress_bar); // Ánh xạ ProgressBar
    }

    private void initSession() {
        if (getContext() == null) return;
        apiService = ApiClient.get();
        sharedPrefManager = new SharedPrefManager(getContext());
        userId = sharedPrefManager.getUserId();
    }

    private void setupRecyclerView() {
        if (getContext() == null) return;
        recyclerCart.setLayoutManager(new LinearLayoutManager(getContext()));
        cartAdapter = new CartAdapter(getContext(), cartItems, this, userId);
        recyclerCart.setAdapter(cartAdapter);
    }

    private void setupEvents() {
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

        btnCheckout.setOnClickListener(v -> {
            if (!sharedPrefManager.isLoggedIn() || userId <= 0) {
                Toast.makeText(getContext(), "Vui lòng đăng nhập để thanh toán", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(getContext(), LoginActivity.class));
                return;
            }

            ArrayList<CheckoutItem> selectedItems = new ArrayList<>();
            for (CartItem item : cartItems) {
                if (!item.isSelected()) continue;
                try {
                    long price = Long.parseLong(item.getProduct().getPrice().replaceAll("[^0-9]", "")); // Fix parse price string
                    CheckoutItem ci = new CheckoutItem(
                            item.getProduct().getId(),
                            item.getProduct().getName(),
                            item.getProduct().getImageUrl(),
                            price,
                            item.getQuantity()
                    );
                    selectedItems.add(ci);
                } catch (NumberFormatException e) { }
            }

            if (selectedItems.isEmpty()) {
                Toast.makeText(getContext(), "Vui lòng chọn ít nhất 1 sản phẩm để thanh toán", Toast.LENGTH_SHORT).show();
                return;
            }

            Intent intent = new Intent(getContext(), CheckoutActivity.class);
            intent.putExtra("items", selectedItems);
//            checkoutLauncher.launch(intent);
            startActivity(intent);
        });
    }

    private void loadCartDataLocal() {
        cartItems = CartManager.getInstance().getCartItems();
        if (cartItems == null) {
            cartItems = new ArrayList<>();
        }
    }

    private void fetchCartFromServer() {
        if (userId <= 0) {
            if (cartItems.isEmpty()) goToEmptyCart();
            return;
        }

        // Hiện ProgressBar khi bắt đầu gọi API
        if (progressBar != null) progressBar.setVisibility(View.VISIBLE);

        apiService.getCart(userId).enqueue(new Callback<List<CartItemDto>>() {
            @Override
            public void onResponse(Call<List<CartItemDto>> call, Response<List<CartItemDto>> response) {
                // Ẩn ProgressBar khi có phản hồi
                if (progressBar != null) progressBar.setVisibility(View.GONE);

                if (!response.isSuccessful() || response.body() == null) {
                    if (cartItems.isEmpty()) goToEmptyCart();
                    return;
                }

                List<CartItemDto> dtoList = response.body();

                // ✅ Nếu server trả rỗng mà local đang có -> GIỮ LOCAL, đừng clear
                if (dtoList.isEmpty()) {
                    cartAdapter.notifyDataSetChanged();
                    updateSelectAllState();
                    updateSummary();
                    // nếu cả local cũng rỗng thì mới đi empty
                    if (cartItems.isEmpty()) goToEmptyCart();
                    return;
                }

                // ✅ Có data server thì mới replace local
                List<CartItem> newList = new ArrayList<>();
                for (CartItemDto dto : dtoList) {
                    CartItem item = mapDtoToCartItem(dto);
                    if (item != null) newList.add(item);
                }

                cartItems.clear();
                cartItems.addAll(newList);

                cartAdapter.notifyDataSetChanged();
                updateSelectAllState();
                updateSummary();

                if (cartItems.isEmpty()) goToEmptyCart();
            }

            @Override
            public void onFailure(Call<List<CartItemDto>> call, Throwable t) {
                // Ẩn ProgressBar khi lỗi
                if (progressBar != null) progressBar.setVisibility(View.GONE);

                if (cartItems.isEmpty()) goToEmptyCart();
                else if (getContext() != null)
                    Toast.makeText(getContext(),
                            "Không tải được giỏ hàng từ server, dùng dữ liệu offline.",
                            Toast.LENGTH_SHORT).show();
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
        if (isEmptyCartOpened || getContext() == null) return;
        isEmptyCartOpened = true;
        Intent intent = new Intent(getContext(), EmptyCartActivity.class);
        startActivity(intent);
    }

    private void handlePurchasedItems(long[] purchasedIds) {
        Set<Long> purchasedSet = new HashSet<>();
        for (long id : purchasedIds) purchasedSet.add(id);

        Iterator<CartItem> it = cartItems.iterator();
        while (it.hasNext()) {
            CartItem item = it.next();
            Long pid = item.getProduct().getId();
            if (pid != null && purchasedSet.contains(pid)) it.remove();
        }
        cartAdapter.notifyDataSetChanged();

        if (cartItems.isEmpty()) goToEmptyCart();
        else {
            updateSelectAllState();
            updateSummary();
        }
    }

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
                    long price = Long.parseLong(item.getProduct().getPrice().replaceAll("[^0-9]", ""));
                    total += price * item.getQuantity();
                } catch (NumberFormatException e) { }
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
        checkboxSelectAll.setOnCheckedChangeListener(null);
        checkboxSelectAll.setChecked(allSelected);
        checkboxSelectAll.setOnCheckedChangeListener((buttonView, isChecked) -> {
            for (CartItem item : cartItems) item.setSelected(isChecked);
            if (!cartItems.isEmpty()) cartAdapter.notifyItemRangeChanged(0, cartItems.size());
            updateSummary();
            updateSelectAllState();
        });
    }

    @Override
    public void onCartChanged() {
        updateSelectAllState();
        updateSummary();
    }

    @Override
    public void onItemRemoved(int sizeAfterRemove) {
        updateSelectAllState();
        updateSummary();
        if (sizeAfterRemove == 0) goToEmptyCart();
    }
    @Override
    public void onResume() {
        super.onResume();
        loadCartDataLocal();
        if (cartAdapter != null) cartAdapter.notifyDataSetChanged();
        updateSelectAllState();
        updateSummary();
        fetchCartFromServer();
    }
}