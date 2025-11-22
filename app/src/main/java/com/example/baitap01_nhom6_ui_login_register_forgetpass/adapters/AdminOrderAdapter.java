package com.example.baitap01_nhom6_ui_login_register_forgetpass.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.util.Consumer;
import androidx.recyclerview.widget.RecyclerView;

import com.example.baitap01_nhom6_ui_login_register_forgetpass.R;
import com.example.baitap01_nhom6_ui_login_register_forgetpass.models.dto.AdminOrderDto;
import com.example.baitap01_nhom6_ui_login_register_forgetpass.util.PriceFormatter;
import com.google.android.material.button.MaterialButton;

import java.util.List;

public class AdminOrderAdapter extends RecyclerView.Adapter<AdminOrderAdapter.ViewHolder> {

    private final List<AdminOrderDto> data;
    private final Consumer<AdminOrderDto> onChangeStatus;
    private final Consumer<AdminOrderDto> onDetail;

    public AdminOrderAdapter(
            List<AdminOrderDto> data,
            Consumer<AdminOrderDto> onChangeStatus,
            Consumer<AdminOrderDto> onDetail
    ) {
        this.data = data;
        this.onChangeStatus = onChangeStatus;
        this.onDetail = onDetail;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_admin_order, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder h, int position) {
        AdminOrderDto o = data.get(position);

        h.tvOrderId.setText("Đơn #" + o.id);
        h.tvCustomer.setText(o.customerName);
        h.tvTotal.setText("Tổng: " + o.totalAmount + "₫");
        h.tvCreatedAt.setText("Ngày: " + o.createdAt);
        h.tvStatus.setText(o.status);

        // Status color
        switch (o.status) {
            case "MOI":        h.tvStatus.setBackgroundResource(R.drawable.bg_chip_blue); break;
            case "DANG_XU_LY": h.tvStatus.setBackgroundResource(R.drawable.bg_chip_orange); break;
            case "DANG_GIAO":  h.tvStatus.setBackgroundResource(R.drawable.bg_chip_purple); break;
            case "HOAN_THANH": h.tvStatus.setBackgroundResource(R.drawable.bg_chip_green); break;
            case "DA_HUY":     h.tvStatus.setBackgroundResource(R.drawable.bg_chip_red); break;
        }

        h.btnChange.setOnClickListener(v -> onChangeStatus.accept(o));
        h.btnDetail.setOnClickListener(v -> onDetail.accept(o));
    }

    @Override
    public int getItemCount() { return data.size(); }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvOrderId, tvCustomer, tvTotal, tvCreatedAt, tvStatus;
        MaterialButton btnChange, btnDetail;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvOrderId   = itemView.findViewById(R.id.tvOrderId);
            tvCustomer  = itemView.findViewById(R.id.tvCustomerName);
            tvTotal     = itemView.findViewById(R.id.tvTotal);
            tvCreatedAt = itemView.findViewById(R.id.tvCreatedAt);
            tvStatus    = itemView.findViewById(R.id.tvOrderStatus);
            btnChange   = itemView.findViewById(R.id.btnChangeStatus);
            btnDetail   = itemView.findViewById(R.id.btnDetail);
        }
    }
}
