package com.example.baitap01_nhom6_ui_login_register_forgetpass.activity;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.ImageView;
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
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;

public class MyOrdersActivity extends AppCompatActivity implements MyOrdersAdapter.OnOrderClickListener {
    private ImageView btnBack;
    private EditText edtSearch;
    private TabLayout tabLayout;
    private RecyclerView rcvOrders;

    private MyOrdersAdapter adapter;
    private List<Order> orderList = new ArrayList<>();
    private List<Order> filteredList = new ArrayList<>();

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
        loadSampleData();
    }
    private void initViews() {
        btnBack = findViewById(R.id.btnBack);
        edtSearch = findViewById(R.id.edtSearch);
        tabLayout = findViewById(R.id.tabLayout);
        rcvOrders = findViewById(R.id.rcvOrders);
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
                        currentStatus = "ALL";
                        break;
                    case 1:
                        currentStatus = "PROCESSING";
                        break;
                    case 2:
                        currentStatus = "SHIPPING";
                        break;
                    case 3:
                        currentStatus = "COMPLETED";
                        break;
                    case 4:
                        currentStatus = "CANCELLED";
                        break;
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

    private void loadSampleData() {
        // Đơn hàng 1 - Đã hủy
        Order order1 = new Order();
        order1.setOrderId("2937150");
        order1.setStatus("CANCELLED");
        order1.setCreatedAt(System.currentTimeMillis());
        order1.setTotalPrice(0);

        List<OrderProduct> products1 = new ArrayList<>();
        OrderProduct product1 = new OrderProduct();
        product1.setName("NƯỚC CẮT TIÊM VINPHACO 10X5 ỐNG");
        product1.setImage("");
        product1.setPrice(0);
        product1.setQuantity(1);
        products1.add(product1);
        order1.setProducts(products1);

        orderList.add(order1);

        // Đơn hàng 2 - Đã giao
        Order order2 = new Order();
        order2.setOrderId("7346293");
        order2.setStatus("COMPLETED");
        order2.setCreatedAt(System.currentTimeMillis() - 86400000L); // 1 ngày trước
        order2.setTotalPrice(831000);

        List<OrderProduct> products2 = new ArrayList<>();
        OrderProduct product2_1 = new OrderProduct();
        product2_1.setName("SỮA PEPTAMEN NESTLE 400G");
        product2_1.setImage("");
        product2_1.setPrice(208000);
        product2_1.setQuantity(1);
        products2.add(product2_1);

        OrderProduct product2_2 = new OrderProduct();
        product2_2.setName("Sản phẩm khác 1");
        products2.add(product2_2);

        OrderProduct product2_3 = new OrderProduct();
        product2_3.setName("Sản phẩm khác 2");
        products2.add(product2_3);

        OrderProduct product2_4 = new OrderProduct();
        product2_4.setName("Sản phẩm khác 3");
        products2.add(product2_4);

        order2.setProducts(products2);
        orderList.add(order2);

        // Đơn hàng 3 - Đang giao
        Order order3 = new Order();
        order3.setOrderId("8521479");
        order3.setStatus("SHIPPING");
        order3.setCreatedAt(System.currentTimeMillis() - 172800000L); // 2 ngày trước
        order3.setTotalPrice(2450000);

        List<OrderProduct> products3 = new ArrayList<>();
        OrderProduct product3 = new OrderProduct();
        product3.setName("Chuột Logitech MX Master 3S");
        product3.setImage("");
        product3.setPrice(2450000);
        product3.setQuantity(1);
        products3.add(product3);
        order3.setProducts(products3);

        orderList.add(order3);

        // Đơn hàng 4 - Đang xử lý
        Order order4 = new Order();
        order4.setOrderId("9632587");
        order4.setStatus("PROCESSING");
        order4.setCreatedAt(System.currentTimeMillis() - 259200000L); // 3 ngày trước
        order4.setTotalPrice(15990000);

        List<OrderProduct> products4 = new ArrayList<>();
        OrderProduct product4 = new OrderProduct();
        product4.setName("MacBook Air M2 13 inch");
        product4.setImage("");
        product4.setPrice(15990000);
        product4.setQuantity(1);
        products4.add(product4);
        order4.setProducts(products4);

        orderList.add(order4);

        filterOrders();
    }

    private void filterOrders() {
        filteredList.clear();
        String searchQuery = edtSearch.getText().toString().toLowerCase().trim();

        for (Order order : orderList) {
            // Lọc theo status
            boolean matchStatus = currentStatus.equals("ALL") || order.getStatus().equals(currentStatus);

            // Lọc theo search query
            boolean matchSearch = searchQuery.isEmpty() ||
                    order.getOrderId().toLowerCase().contains(searchQuery) ||
                    (order.getProducts() != null && order.getProducts().size() > 0 &&
                            order.getProducts().get(0).getName().toLowerCase().contains(searchQuery));

            if (matchStatus && matchSearch) {
                filteredList.add(order);
            }
        }

        adapter.updateData(filteredList);

        // Hiển thị empty state nếu không có đơn hàng
        if (filteredList.isEmpty()) {
            // TODO: Show empty state view
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
}