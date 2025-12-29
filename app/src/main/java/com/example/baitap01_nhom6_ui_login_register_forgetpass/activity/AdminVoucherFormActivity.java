package com.example.baitap01_nhom6_ui_login_register_forgetpass.activity;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.baitap01_nhom6_ui_login_register_forgetpass.R;
import com.example.baitap01_nhom6_ui_login_register_forgetpass.models.voucher.CreateVoucherRequest;
import com.example.baitap01_nhom6_ui_login_register_forgetpass.models.voucher.VoucherDto;
import com.example.baitap01_nhom6_ui_login_register_forgetpass.remote.ApiClient;
import com.example.baitap01_nhom6_ui_login_register_forgetpass.util.AdminNavHelper;
import com.example.baitap01_nhom6_ui_login_register_forgetpass.util.DatePickerHelper;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.android.material.textfield.TextInputEditText;

import java.math.BigDecimal;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AdminVoucherFormActivity extends AppCompatActivity {

    private EditText edtCode, edtPercent, edtMax, edtStart, edtEnd, edtQty;
    private SwitchMaterial swActive;
    private MaterialButton btnSave;

    private String mode = "create";
    private VoucherDto editing;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_voucher_form);

        MaterialToolbar toolbar = findViewById(R.id.adminToolbar);
        AdminNavHelper.setupToolbar(this, toolbar, "Voucher");

        mode = getIntent().getStringExtra("mode");
        editing = (VoucherDto) getIntent().getSerializableExtra("voucher");

        edtCode = findViewById(R.id.edtCode);
        edtPercent = findViewById(R.id.edtPercent);
        edtMax = findViewById(R.id.edtMax);
        edtStart = findViewById(R.id.edtStart);
        edtEnd = findViewById(R.id.edtEnd);
        edtQty = findViewById(R.id.edtQty);
        swActive = findViewById(R.id.swActive);
        btnSave = findViewById(R.id.btnSave);

        setupDatePickers();

        if ("edit".equals(mode) && editing != null) {
            fillForm(editing);
            btnSave.setText("Cập nhật voucher");
        } else {
            btnSave.setText("Tạo voucher");
        }

        btnSave.setOnClickListener(v -> submit());
    }

    private void fillForm(VoucherDto v) {
        edtCode.setText(v.code);
        if (v.phanTram != null) edtPercent.setText(String.valueOf(v.phanTram));
        if (v.giamToiDa != null) edtMax.setText(v.giamToiDa.toPlainString());
        if (v.hieuLucTu != null && v.hieuLucTu.length() >= 10) edtStart.setText(v.hieuLucTu.substring(0, 10));
        if (v.hieuLucDen != null && v.hieuLucDen.length() >= 10) edtEnd.setText(v.hieuLucDen.substring(0, 10));
        if (v.soLuongPhatHanh != null) edtQty.setText(String.valueOf(v.soLuongPhatHanh));
        swActive.setChecked(v.hoatDong);

        // code thường không cho sửa để tránh trùng
        edtCode.setEnabled(false);
    }

    private void setupDatePickers() {
        // đơn giản: dùng DatePickerDialog native (nhẹ, dễ chạy).
        edtStart.setOnClickListener(v -> DatePickerHelper.pickDate(this, edtStart));
        edtEnd.setOnClickListener(v -> DatePickerHelper.pickDate(this, edtEnd));
    }

    private void submit() {
        CreateVoucherRequest req = new CreateVoucherRequest();

        String code = edtCode.getText().toString().trim();
        String pct = edtPercent.getText().toString().trim();
        String start = edtStart.getText().toString().trim();
        String end = edtEnd.getText().toString().trim();

        if (code.isEmpty()) { toast("Vui lòng nhập mã"); return; }
        if (pct.isEmpty()) { toast("Vui lòng nhập % giảm"); return; }

        int percent;
        try { percent = Integer.parseInt(pct); }
        catch (Exception e) { toast("Phần trăm không hợp lệ"); return; }

        if (percent < 1 || percent > 100) { toast("% giảm phải từ 1-100"); return; }
        if (start.isEmpty() || end.isEmpty()) { toast("Chọn ngày bắt đầu/kết thúc"); return; }

        req.code = code;
        req.phanTram = percent;

        String maxStr = edtMax.getText().toString().trim();
        if (!maxStr.isEmpty()) {
            try { req.giamToiDa = new BigDecimal(maxStr); }
            catch (Exception e) { toast("Giảm tối đa không hợp lệ"); return; }
        }

        // Backend dùng Instant => gửi ISO-8601: yyyy-MM-ddT00:00:00Z / 23:59:59Z
        req.hieuLucTu = start + "T00:00:00Z";
        req.hieuLucDen = end + "T23:59:59Z";

        String qtyStr = edtQty.getText().toString().trim();
        if (!qtyStr.isEmpty()) {
            try {
                int q = Integer.parseInt(qtyStr);
                if (q < 1) { toast("Số lượng phải >= 1"); return; }
                req.soLuongPhatHanh = q;
            } catch (Exception e) {
                toast("Số lượng không hợp lệ"); return;
            }
        }

        req.hoatDong = swActive.isChecked();

        btnSave.setEnabled(false);

        if ("edit".equals(mode) && editing != null) {
            ApiClient.get().adminUpdateVoucher(editing.id, req).enqueue(cbDone("Cập nhật"));
        } else {
            ApiClient.get().adminCreateVoucher(req).enqueue(cbDone("Tạo"));
        }
    }

    private Callback<VoucherDto> cbDone(String action) {
        return new Callback<VoucherDto>() {
            @Override public void onResponse(Call<VoucherDto> call, Response<VoucherDto> response) {
                btnSave.setEnabled(true);
                if (!response.isSuccessful()) {
                    toast(action + " thất bại (" + response.code() + ")");
                    return;
                }
                toast(action + " thành công");
                finish();
            }

            @Override public void onFailure(Call<VoucherDto> call, Throwable t) {
                btnSave.setEnabled(true);
                toast("Lỗi: " + t.getMessage());
            }
        };
    }

    private String text(TextInputEditText e) {
        return e.getText() == null ? "" : e.getText().toString();
    }

    private void toast(String s) { Toast.makeText(this, s, Toast.LENGTH_SHORT).show(); }
}
