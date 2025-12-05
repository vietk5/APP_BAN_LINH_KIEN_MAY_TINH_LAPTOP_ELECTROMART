package com.example.baitap01_nhom6_ui_login_register_forgetpass.activity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.baitap01_nhom6_ui_login_register_forgetpass.R;
import com.example.baitap01_nhom6_ui_login_register_forgetpass.models.dto.Address;
import com.example.baitap01_nhom6_ui_login_register_forgetpass.models.dto.ApiResponse;
import com.example.baitap01_nhom6_ui_login_register_forgetpass.remote.ApiClient;
import com.example.baitap01_nhom6_ui_login_register_forgetpass.remote.ApiService;
import com.example.baitap01_nhom6_ui_login_register_forgetpass.util.AdminNavHelper;
import com.example.baitap01_nhom6_ui_login_register_forgetpass.util.LocationPickerHelper;
import com.example.baitap01_nhom6_ui_login_register_forgetpass.util.SharedPrefManager;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddEditAddressActivity extends AppCompatActivity {

    private TextInputEditText etName, etPhone, etDetailAddress;
    private TextView tvSelectedLocation;
    private RelativeLayout layoutSelectLocation;
    private MaterialButton btnTypeHome, btnTypeOffice;
    private CheckBox cbDefault;
    private Button btnSave;

    private ApiService apiService;
    private SharedPrefManager sharedPref;

    private Long addressId = null;
    private String selectedProvince = "";
    private String selectedDistrict = "";
    private String selectedWard = "";
    private String selectedAddressType = "Nhà";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit_address);

        initViews();
        setupClickListeners();
        MaterialToolbar toolbar = findViewById(R.id.userToolbar);
        // Kiểm tra xem đang thêm mới hay sửa
        if (getIntent().hasExtra("ADDRESS_ID")) {
            addressId = getIntent().getLongExtra("ADDRESS_ID", -1);
            AdminNavHelper.setupToolbar(this, toolbar, "Sửa địa chỉ");
            loadAddressDetail();
        } else {
            AdminNavHelper.setupToolbar(this, toolbar, "Thêm địa chỉ");
        }
        toolbar.setNavigationOnClickListener(v -> finish());
    }

    private void initViews() {
        etName = findViewById(R.id.et_name);
        etPhone = findViewById(R.id.et_phone);
        etDetailAddress = findViewById(R.id.et_detail_address);
        tvSelectedLocation = findViewById(R.id.tv_selected_location);
        layoutSelectLocation = findViewById(R.id.layout_select_location);
        btnTypeHome = findViewById(R.id.btn_type_home);
        btnTypeOffice = findViewById(R.id.btn_type_office);
        cbDefault = findViewById(R.id.cb_default);
        btnSave = findViewById(R.id.btn_save);

        apiService = ApiClient.get();
        sharedPref = new SharedPrefManager(this);
    }

    private void setupClickListeners() {

        // Chọn loại địa chỉ
        btnTypeHome.setOnClickListener(v -> {
            selectAddressType("Nhà");
        });

        btnTypeOffice.setOnClickListener(v -> {
            selectAddressType("Văn phòng");
        });

        // Chọn tỉnh/thành phố, quận/huyện, phường/xã
        layoutSelectLocation.setOnClickListener(v -> {
            // TODO: Mở dialog hoặc activity chọn địa điểm
            // Tạm thời dùng hard-code
            showLocationPicker();
        });

        // Lưu địa chỉ
        btnSave.setOnClickListener(v -> saveAddress());
    }

    private void selectAddressType(String type) {
        selectedAddressType = type;

        if (type.equals("Nhà")) {
            btnTypeHome.setStrokeColorResource(R.color.blue_500);
            btnTypeHome.setTextColor(getColor(R.color.blue_500));
            btnTypeOffice.setStrokeColorResource(R.color.gray_300);
            btnTypeOffice.setTextColor(getColor(R.color.gray_600));
        } else {
            btnTypeOffice.setStrokeColorResource(R.color.blue_500);
            btnTypeOffice.setTextColor(getColor(R.color.blue_500));
            btnTypeHome.setStrokeColorResource(R.color.gray_300);
            btnTypeHome.setTextColor(getColor(R.color.gray_600));
        }
    }

    private void showLocationPicker() {
        LocationPickerHelper.showLocationPicker(this, (province, district, ward) -> {
            selectedProvince = province;
            selectedDistrict = district;
            selectedWard = ward;

            String location = ward + ", " + district + ", " + province;
            tvSelectedLocation.setText(location);
            tvSelectedLocation.setTextColor(getColor(R.color.gray_900));
        });
    }

    private void loadAddressDetail() {
        // TODO: Gọi API lấy chi tiết địa chỉ theo addressId
        // Sau đó fill vào các field
    }

    private void saveAddress() {
        // Validate
        String name = etName.getText().toString().trim();
        String phone = etPhone.getText().toString().trim();
        String detailAddress = etDetailAddress.getText().toString().trim();

        if (name.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập tên", Toast.LENGTH_SHORT).show();
            return;
        }

        if (phone.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập số điện thoại", Toast.LENGTH_SHORT).show();
            return;
        }

        if (selectedProvince.isEmpty()) {
            Toast.makeText(this, "Vui lòng chọn địa chỉ", Toast.LENGTH_SHORT).show();
            return;
        }

        if (detailAddress.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập địa chỉ chi tiết", Toast.LENGTH_SHORT).show();
            return;
        }

        // Tạo object Address
        Address address = new Address();
        address.setUserId((long) sharedPref.getUserId());
        address.setTenNguoiNhan(name);
        address.setSoDienThoai(phone);
        address.setTinhThanhPho(selectedProvince);
        address.setQuanHuyen(selectedDistrict);
        address.setPhuongXa(selectedWard);
        address.setDiaChiChiTiet(detailAddress);
        address.setLoaiDiaChi(selectedAddressType);
        address.setDefault(cbDefault.isChecked());

        if (addressId == null) {
            // Thêm mới
            createAddress(address);
        } else {
            // Cập nhật
            address.setId(addressId);
            updateAddress(address);
        }
    }

    private void createAddress(Address address) {
        apiService.createAddress(address).enqueue(new Callback<ApiResponse<Address>>() {
            @Override
            public void onResponse(Call<ApiResponse<Address>> call, Response<ApiResponse<Address>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    if (response.body().isSuccess()) {
                        Toast.makeText(AddEditAddressActivity.this, "Thêm địa chỉ thành công", Toast.LENGTH_SHORT).show();
                        setResult(RESULT_OK);
                        finish();
                    } else {
                        Toast.makeText(AddEditAddressActivity.this, "Lỗi: " + response.body().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(AddEditAddressActivity.this, "Không thể thêm địa chỉ", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<Address>> call, Throwable t) {
                Toast.makeText(AddEditAddressActivity.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateAddress(Address address) {
        apiService.updateAddress(addressId, address).enqueue(new Callback<ApiResponse<Address>>() {
            @Override
            public void onResponse(Call<ApiResponse<Address>> call, Response<ApiResponse<Address>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    if (response.body().isSuccess()) {
                        Toast.makeText(AddEditAddressActivity.this, "Cập nhật địa chỉ thành công", Toast.LENGTH_SHORT).show();
                        setResult(RESULT_OK);
                        finish();
                    } else {
                        Toast.makeText(AddEditAddressActivity.this, "Lỗi: " + response.body().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(AddEditAddressActivity.this, "Không thể cập nhật địa chỉ", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<Address>> call, Throwable t) {
                Toast.makeText(AddEditAddressActivity.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}