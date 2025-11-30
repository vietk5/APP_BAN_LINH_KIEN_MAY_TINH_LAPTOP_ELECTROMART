package com.example.baitap01_nhom6_ui_login_register_forgetpass.activity;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.baitap01_nhom6_ui_login_register_forgetpass.R;
import com.example.baitap01_nhom6_ui_login_register_forgetpass.adapters.AdminOrderItemAdapter;
import com.example.baitap01_nhom6_ui_login_register_forgetpass.models.dto.AdminOrderDto;
import com.example.baitap01_nhom6_ui_login_register_forgetpass.models.dto.OrderDetailItemDto;
import com.example.baitap01_nhom6_ui_login_register_forgetpass.remote.ApiClient;
import com.example.baitap01_nhom6_ui_login_register_forgetpass.remote.ApiService;
import com.example.baitap01_nhom6_ui_login_register_forgetpass.util.PriceFormatter;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.button.MaterialButton;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AdminOrderDetailBottomSheet extends BottomSheetDialogFragment {

    private static final String ARG_ID = "order_id";
    private static final String ARG_NAME = "customer_name";
    private static final String ARG_PHONE = "customer_phone";
    private static final String ARG_ADDRESS = "customer_address";
    private static final String ARG_STATUS = "status";
    private static final String ARG_CREATED = "created";
    private static final String ARG_TOTAL = "total";

    public static AdminOrderDetailBottomSheet newInstance(AdminOrderDto o) {
        Bundle args = new Bundle();
        args.putLong(ARG_ID, o.id);
        args.putString(ARG_NAME, o.customerName);
        args.putString(ARG_PHONE, o.customerPhone);
        args.putString(ARG_ADDRESS, o.customerAddress);
        args.putString(ARG_STATUS, o.status);
        args.putString(ARG_CREATED, o.createdAt);
        args.putLong(ARG_TOTAL, o.totalAmount);

        AdminOrderDetailBottomSheet f = new AdminOrderDetailBottomSheet();
        f.setArguments(args);
        return f;
    }

    private AdminOrderItemAdapter itemAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.bottom_sheet_admin_order_detail, container, false);

        TextView tvOrderCode   = v.findViewById(R.id.tvOrderCode);
        TextView tvCustomer    = v.findViewById(R.id.tvCustomerName);
        TextView tvPhone       = v.findViewById(R.id.tvCustomerPhone);
        TextView tvAddress     = v.findViewById(R.id.tvAddress);
        TextView tvStatus      = v.findViewById(R.id.tvStatus);
        TextView tvCreatedAt   = v.findViewById(R.id.tvCreatedAt);
        TextView tvTotal       = v.findViewById(R.id.tvTotalAmount);
        MaterialButton btnClose = v.findViewById(R.id.btnClose);
        RecyclerView rvItems   = v.findViewById(R.id.rvOrderItems);

        Bundle args = getArguments();
        long orderId = args.getLong(ARG_ID);

        tvOrderCode.setText("Đơn #" + orderId);
        tvCustomer.setText("Khách hàng: " + args.getString(ARG_NAME, ""));
        tvPhone.setText("SĐT: " + args.getString(ARG_PHONE, ""));
        tvAddress.setText("Địa chỉ: " + args.getString(ARG_ADDRESS, ""));
        tvStatus.setText("Trạng thái: " + args.getString(ARG_STATUS, ""));
        tvCreatedAt.setText("Ngày tạo: " + args.getString(ARG_CREATED, ""));
        tvTotal.setText("Tổng tiền: " + PriceFormatter.vnd(args.getLong(ARG_TOTAL)));

        btnClose.setOnClickListener(view -> dismiss());

        rvItems.setLayoutManager(new LinearLayoutManager(getContext()));
        itemAdapter = new AdminOrderItemAdapter();
        rvItems.setAdapter(itemAdapter);

        loadOrderItems(orderId);

        return v;
    }

    private void loadOrderItems(long orderId) {
        ApiClient.get().getAdminOrderItems(orderId)
                .enqueue(new Callback<List<OrderDetailItemDto>>() {
                    @Override
                    public void onResponse(Call<List<OrderDetailItemDto>> call,
                                           Response<List<OrderDetailItemDto>> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            itemAdapter.setItems(response.body());
                        } else {
                            Toast.makeText(getContext(),
                                    "Không lấy được danh sách sản phẩm", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<List<OrderDetailItemDto>> call, Throwable t) {
                        Toast.makeText(getContext(),
                                "Lỗi mạng: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

}
