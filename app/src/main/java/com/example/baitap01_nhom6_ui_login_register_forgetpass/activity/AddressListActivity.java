package com.example.baitap01_nhom6_ui_login_register_forgetpass.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.baitap01_nhom6_ui_login_register_forgetpass.R;
import com.example.baitap01_nhom6_ui_login_register_forgetpass.adapters.AddressAdapter;
import com.example.baitap01_nhom6_ui_login_register_forgetpass.models.dto.Address;
import com.example.baitap01_nhom6_ui_login_register_forgetpass.models.dto.ApiResponse;
import com.example.baitap01_nhom6_ui_login_register_forgetpass.remote.ApiClient;
import com.example.baitap01_nhom6_ui_login_register_forgetpass.remote.ApiService;
import com.example.baitap01_nhom6_ui_login_register_forgetpass.util.AdminNavHelper;
import com.example.baitap01_nhom6_ui_login_register_forgetpass.util.SharedPrefManager;
import com.google.android.material.appbar.MaterialToolbar;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddressListActivity extends AppCompatActivity implements AddressAdapter.OnAddressClickListener {

    private static final int REQUEST_ADD_ADDRESS = 100;
    private static final int REQUEST_EDIT_ADDRESS = 101;

    private RecyclerView rvAddresses;
    private LinearLayout emptyState;
    private Button btnAddAddress;
    private AddressAdapter adapter;
    private ApiService apiService;
    private SharedPrefManager sharedPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_address_list);

        MaterialToolbar toolbar = findViewById(R.id.userToolbar);
        AdminNavHelper.setupToolbar(this, toolbar, "Quản lý sổ địa chỉ");
        toolbar.setNavigationOnClickListener(v -> finish());

        initViews();
        setupRecyclerView();
        loadAddresses();
    }

    private void initViews() {
        rvAddresses = findViewById(R.id.rv_addresses);
        emptyState = findViewById(R.id.empty_state);
        btnAddAddress = findViewById(R.id.btn_add_address);

        apiService = ApiClient.get();
        sharedPref = new SharedPrefManager(this);

        btnAddAddress.setOnClickListener(v -> {
            Intent intent = new Intent(this, AddEditAddressActivity.class);
            startActivityForResult(intent, REQUEST_ADD_ADDRESS);
        });
    }

    private void setupRecyclerView() {
        adapter = new AddressAdapter(this);
        rvAddresses.setLayoutManager(new LinearLayoutManager(this));
        rvAddresses.setAdapter(adapter);
    }

    private void loadAddresses() {
        Long userId = (long) sharedPref.getUserId();

        apiService.getAllAddresses(userId).enqueue(new Callback<ApiResponse<List<Address>>>() {
            @Override
            public void onResponse(Call<ApiResponse<List<Address>>> call, Response<ApiResponse<List<Address>>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse<List<Address>> apiResponse = response.body();
                    if (apiResponse.isSuccess() && apiResponse.getData() != null) {
                        List<Address> addresses = apiResponse.getData();
                        if (addresses.isEmpty()) {
                            showEmptyState();
                        } else {
                            showAddressList(addresses);
                        }
                    }
                } else {
                    Toast.makeText(AddressListActivity.this, "Không thể tải danh sách địa chỉ", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<List<Address>>> call, Throwable t) {
                Toast.makeText(AddressListActivity.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showEmptyState() {
        emptyState.setVisibility(View.VISIBLE);
        rvAddresses.setVisibility(View.GONE);
    }

    private void showAddressList(List<Address> addresses) {
        emptyState.setVisibility(View.GONE);
        rvAddresses.setVisibility(View.VISIBLE);
        adapter.setAddressList(addresses);
    }

    @Override
    public void onEditClick(Address address) {
        Intent intent = new Intent(this, AddEditAddressActivity.class);
        intent.putExtra("ADDRESS_ID", address.getId());
        startActivityForResult(intent, REQUEST_EDIT_ADDRESS);
    }

    @Override
    public void onSetDefaultClick(Address address) {
        Long userId = (long) sharedPref.getUserId();

        apiService.setDefaultAddress(address.getId(), userId).enqueue(new Callback<ApiResponse<Address>>() {
            @Override
            public void onResponse(Call<ApiResponse<Address>> call, Response<ApiResponse<Address>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Toast.makeText(AddressListActivity.this, "Đã đặt làm địa chỉ mặc định", Toast.LENGTH_SHORT).show();
                    loadAddresses(); // Reload list
                } else {
                    Toast.makeText(AddressListActivity.this, "Không thể đặt địa chỉ mặc định", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<Address>> call, Throwable t) {
                Toast.makeText(AddressListActivity.this, "Lỗi: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_ADD_ADDRESS || requestCode == REQUEST_EDIT_ADDRESS) {
                loadAddresses(); // Reload danh sách sau khi thêm/sửa
            }
        }
    }
}