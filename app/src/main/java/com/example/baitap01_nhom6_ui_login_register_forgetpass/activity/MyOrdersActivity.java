package com.example.baitap01_nhom6_ui_login_register_forgetpass.activity;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.baitap01_nhom6_ui_login_register_forgetpass.R;
import com.example.baitap01_nhom6_ui_login_register_forgetpass.adapters.MyOrdersAdapter;
import com.example.baitap01_nhom6_ui_login_register_forgetpass.models.Order;
import com.example.baitap01_nhom6_ui_login_register_forgetpass.models.OrderProduct;
import com.example.baitap01_nhom6_ui_login_register_forgetpass.models.dto.OrderDetailDto;
import com.example.baitap01_nhom6_ui_login_register_forgetpass.models.dto.OrderDetailItemDto;
import com.example.baitap01_nhom6_ui_login_register_forgetpass.remote.ApiClient;
import com.example.baitap01_nhom6_ui_login_register_forgetpass.util.SharedPrefManager;
import com.google.android.material.tabs.TabLayout;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;

public class MyOrdersActivity extends AppCompatActivity implements MyOrdersAdapter.OnOrderClickListener {
    private ImageView btnBack;
    private EditText edtSearch;
    private TabLayout tabLayout;
    private RecyclerView rcvOrders;
    private TextView tvEmptyOrders;


    private MyOrdersAdapter adapter;
    private List<Order> orderList = new ArrayList<>();
    private List<Order> filteredList = new ArrayList<>();
    private SharedPrefManager sharedPrefManager;

    private String currentStatus = "ALL"; // ALL, PROCESSING, SHIPPING, COMPLETED, CANCELLED
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_my_orders);

        // Back press dispatcher (chuẩn mới)
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {

                finish(); // custom back behavior
            }
        });

        initViews();
        setupRecyclerView();
        setupTabLayout();
        setupSearchBar();
        setupListeners();
        sharedPrefManager = new SharedPrefManager(this);
        loadOrdersFromApi(sharedPrefManager.getUserId());
    }
    private void initViews() {
        btnBack = findViewById(R.id.btnBack);
        edtSearch = findViewById(R.id.edtSearch);
        tabLayout = findViewById(R.id.tabLayout);
        rcvOrders = findViewById(R.id.rcvOrders);
        tvEmptyOrders = findViewById(R.id.tvEmptyOrders);

    }

    private void setupRecyclerView() {
        adapter = new MyOrdersAdapter(this, filteredList);
        adapter.setOnOrderClickListener(this);
        rcvOrders.setLayoutManager(new LinearLayoutManager(this));
        rcvOrders.setAdapter(adapter);
    }

    private void setupTabLayout() {
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                int position = tab.getPosition();
                switch (position) {
                    case 0:
                        currentStatus = "ALL"; break;
                    case 1:
                        currentStatus = "DANG_XU_LY"; break;
                    case 2:
                        currentStatus = "DANG_GIAO"; break;
                    case 3:
                        currentStatus = "HOAN_THANH"; break;
                    case 4:
                        currentStatus = "DA_HUY"; break;
                }
                filterOrders();
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {}

            @Override
            public void onTabReselected(TabLayout.Tab tab) {}
        });
    }

    private void setupSearchBar() {
        edtSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterOrders();
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    private void setupListeners() {
        btnBack.setOnClickListener(v -> finish());
    }

    private void loadOrdersFromApi(long userId) {
        ApiClient.get().getOrdersByUserId(userId)
                .enqueue(new retrofit2.Callback<List<OrderDetailDto>>() {
                    @Override
                    public void onResponse(Call<List<OrderDetailDto>> call,
                                           retrofit2.Response<List<OrderDetailDto>> response) {

                        Log.d("DEBUG_ORDER", "code=" + response.code());
                        Log.d("DEBUG_ORDER", "isSuccessful=" + response.isSuccessful());
                        Log.d("DEBUG_ORDER", "body=" + response.body());

                        if (response.isSuccessful() && response.body() != null) {
                            orderList.clear();

                            List<OrderDetailDto> dtoList = response.body();
                            Log.d("DEBUG_ORDER", "dtoList size=" + dtoList.size());

                            for (OrderDetailDto dto : dtoList) {
                                Order order = mapDtoToOrder(dto);
                                if (order != null) {
                                    orderList.add(order);
                                    Log.d("DEBUG_ORDER", "Added order: " + order.getOrderId());
                                }
                            }

                            filterOrders();
                        } else {
                            Log.e("DEBUG_ORDER", "Response failed, code=" + response.code());
                            Toast.makeText(MyOrdersActivity.this,
                                    "Không lấy được đơn hàng",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<List<OrderDetailDto>> call, Throwable t) {
                        Log.e("DEBUG_ORDER", "API call failed: " + t.getMessage());
                        t.printStackTrace();
                        Toast.makeText(MyOrdersActivity.this,
                                "Lỗi kết nối: " + t.getMessage(),
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void filterOrders() {
        filteredList.clear();
//        filteredList.addAll(orderList);
//        adapter.updateData(filteredList);

        Log.d("DEBUG_FILTER", "Showing all orders: " + filteredList.size());
        String searchQuery = edtSearch.getText().toString().toLowerCase().trim();

        Log.d("DEBUG_FILTER", "Filtering with status=" + currentStatus + ", search=" + searchQuery);
        Log.d("DEBUG_FILTER", "orderList size=" + orderList.size());

        for (Order order : orderList) {
            boolean matchStatus = currentStatus.equals("ALL") || order.getStatus().equals(currentStatus);

            boolean matchSearch = searchQuery.isEmpty() ||
                    order.getOrderId().toLowerCase().contains(searchQuery) ||
                    (order.getProducts() != null && order.getProducts().size() > 0 &&
                            order.getProducts().get(0).getName().toLowerCase().contains(searchQuery));

            Log.d("DEBUG_FILTER", "Order #" + order.getOrderId() +
                    ", matchStatus=" + matchStatus + ", matchSearch=" + matchSearch);

            if (matchStatus && matchSearch) {
                filteredList.add(order);
            }
        }

        Log.d("DEBUG_FILTER", "filteredList final size=" + filteredList.size());
        adapter.updateData(filteredList);
        if (filteredList.isEmpty()) {
            tvEmptyOrders.setVisibility(View.VISIBLE);
            rcvOrders.setVisibility(View.GONE);
        } else {
            tvEmptyOrders.setVisibility(View.GONE);
            rcvOrders.setVisibility(View.VISIBLE);
        }

    }

    @Override
    public void onViewDetailClick(Order order) {
        // Chuyển đến trang chi tiết đơn hàng
        // Intent intent = new Intent(this, OrderDetailActivity.class);
        // intent.putExtra("orderId", order.getOrderId());
        // startActivity(intent);

        Toast.makeText(this, "Xem chi tiết đơn #" + order.getOrderId(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onReorderClick(Order order) {
        // Thêm lại sản phẩm vào giỏ hàng
        Toast.makeText(this, "Đã thêm sản phẩm vào giỏ hàng", Toast.LENGTH_SHORT).show();

        // TODO: Thêm logic thêm vào giỏ hàng
        // for (OrderProduct product : order.getProducts()) {
        //     cartManager.addToCart(product);
        // }
    }
    private Order mapDtoToOrder(OrderDetailDto dto) {
        if (dto == null) {
            Log.e("DEBUG_ORDER", "DTO is null");
            return null;
        }

        Order order = new Order();
        order.setOrderId(String.valueOf(dto.getId()));
        order.setStatus(dto.getTrangThai());

        // Parse ngày đặt hàng
        if (dto.getNgayDatHang() != null) {
            try {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
                order.setCreatedAt(sdf.parse(dto.getNgayDatHang()).getTime());
            } catch (Exception e) {
                Log.e("DEBUG_ORDER", "Error parsing date: " + e.getMessage());
                order.setCreatedAt(System.currentTimeMillis());
            }
        } else {
            order.setCreatedAt(System.currentTimeMillis());
        }

        order.setPaymentMethod(dto.getPhuongThucThanhToan());
        order.setTotalPrice(dto.getTongTien());

        // Map products
        List<OrderProduct> products = new ArrayList<>();
        if (dto.getItems() != null && !dto.getItems().isEmpty()) {
            Log.d("DEBUG_ORDER", "Mapping " + dto.getItems().size() + " items");

            for (OrderDetailItemDto itemDto : dto.getItems()) {
                OrderProduct product = new OrderProduct();
                product.setId(itemDto.getProductId());
                product.setName(itemDto.getProductName());
                product.setImage(itemDto.getImageUrl());
                product.setPrice(itemDto.getDonGia() != null ? itemDto.getDonGia() : 0);
                product.setQuantity(itemDto.getSoLuong());

                Log.d("DEBUG_ORDER", "Product: " + product.getName() + ", price=" + product.getPrice());
                products.add(product);
            }
        } else {
            Log.w("DEBUG_ORDER", "No items in DTO");
        }

        order.setProducts(products);
        order.calculateTotal();

        return order;
    }
    @Override
    public void onCancelOrderClick(Order order) {
        new androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle("Xác nhận huỷ đơn")
                .setMessage("Bạn có chắc chắn muốn huỷ đơn hàng này không?")
                .setPositiveButton("Huỷ đơn", (dialog, which) -> {
                    callCancelOrderApi(order);
                })
                .setNegativeButton("Không", null)
                .show();
    }
    private void callCancelOrderApi(Order order) {
        ApiClient.get().cancelOrder(Long.parseLong(order.getOrderId()))
                .enqueue(new retrofit2.Callback<Void>() {
                    @Override
                    public void onResponse(Call<Void> call, retrofit2.Response<Void> response) {
                        if (response.isSuccessful()) {
                            Toast.makeText(MyOrdersActivity.this,
                                    "Huỷ đơn thành công",
                                    Toast.LENGTH_SHORT).show();

                            order.setStatus("DA_HUY");
                            filterOrders();
                        } else {
                            Toast.makeText(MyOrdersActivity.this,
                                    "Không thể huỷ đơn",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<Void> call, Throwable t) {
                        Toast.makeText(MyOrdersActivity.this,
                                "Lỗi: " + t.getMessage(),
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }
}