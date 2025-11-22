package com.example.baitap01_nhom6_ui_login_register_forgetpass.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.baitap01_nhom6_ui_login_register_forgetpass.R;
import com.example.baitap01_nhom6_ui_login_register_forgetpass.models.dto.AdminCustomerDto;
import com.google.android.material.button.MaterialButton;

import java.util.List;

public class AdminCustomerAdapter extends RecyclerView.Adapter<AdminCustomerAdapter.CustomerVH> {

    public interface OnCustomerActionListener {
        void onBlockClicked(AdminCustomerDto customer);
        void onDeleteClicked(AdminCustomerDto customer);
    }

    private final List<AdminCustomerDto> data;
    private final OnCustomerActionListener listener;

    public AdminCustomerAdapter(List<AdminCustomerDto> data,
                                OnCustomerActionListener listener) {
        this.data = data;
        this.listener = listener;
    }

    @NonNull
    @Override
    public CustomerVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_admin_customer, parent, false);
        return new CustomerVH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull CustomerVH h, int position) {
        AdminCustomerDto c = data.get(position);

        h.tvName.setText(c.fullName);
        h.tvEmail.setText(c.email);
        h.tvPhone.setText(c.phone != null ? c.phone : "—");


        // createdAt: c.createdAt (ISO) -> hiển thị luôn hoặc format nhẹ
        h.tvCreatedAt.setText("ĐK: " + (c.createdAt != null ? c.createdAt : "N/A"));

        if (c.blocked) {
            h.tvStatus.setText("BLOCKED");
            h.tvStatus.setBackgroundResource(R.drawable.bg_status_chip_blocked);
            h.btnBlock.setText("Unblock");
        } else {
            h.tvStatus.setText("ACTIVE");
            h.tvStatus.setBackgroundResource(R.drawable.bg_status_chip_active);
            h.btnBlock.setText("Block");
        }

        h.btnBlock.setOnClickListener(v -> {
            if (listener != null) listener.onBlockClicked(c);
        });

        h.btnDelete.setOnClickListener(v -> {
            if (listener != null) listener.onDeleteClicked(c);
        });
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    static class CustomerVH extends RecyclerView.ViewHolder {
        TextView tvName, tvEmail, tvPhone, tvCreatedAt, tvStatus;
        MaterialButton btnBlock, btnDelete;

        public CustomerVH(@NonNull View itemView) {
            super(itemView);
            tvName      = itemView.findViewById(R.id.tvCustomerName);
            tvEmail     = itemView.findViewById(R.id.tvCustomerEmail);
            tvPhone     = itemView.findViewById(R.id.tvCustomerPhone);
            tvCreatedAt = itemView.findViewById(R.id.tvCreatedAt);
            tvStatus    = itemView.findViewById(R.id.tvStatus);
            btnBlock    = itemView.findViewById(R.id.btnBlock);
            btnDelete   = itemView.findViewById(R.id.btnDelete);
        }
    }
}
