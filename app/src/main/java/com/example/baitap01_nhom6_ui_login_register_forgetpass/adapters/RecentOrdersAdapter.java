package com.example.baitap01_nhom6_ui_login_register_forgetpass.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.baitap01_nhom6_ui_login_register_forgetpass.R;
import com.example.baitap01_nhom6_ui_login_register_forgetpass.models.dto.AdminOrderDto;
import com.example.baitap01_nhom6_ui_login_register_forgetpass.util.PriceFormatter;

import java.util.List;

public class RecentOrdersAdapter extends RecyclerView.Adapter<RecentOrdersAdapter.OrderVH> {

    private final List<AdminOrderDto> data;

    public RecentOrdersAdapter(List<AdminOrderDto> data) {
        this.data = data;
    }

    @NonNull
    @Override
    public OrderVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // có thể dùng layout riêng item_recent_order;
        // nếu bạn không có thì dùng luôn item_admin_order cũng được
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_recent_order, parent, false);
        return new OrderVH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderVH h, int position) {
        AdminOrderDto o = data.get(position);
        h.tvCode.setText("Đơn #" + o.id);
        h.tvCustomer.setText("Khách: " + o.customerName);
        h.tvDate.setText("Ngày: " + o.createdAt);
        h.tvTotal.setText(PriceFormatter.vnd(o.totalAmount));
//        h.tvStatus.setText(o.status);
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    static class OrderVH extends RecyclerView.ViewHolder {
        TextView tvCode, tvCustomer, tvDate, tvTotal, tvStatus;

        OrderVH(@NonNull View itemView) {
            super(itemView);
            tvCode     = itemView.findViewById(R.id.tvCode);
            tvCustomer = itemView.findViewById(R.id.tvCustomer);
            tvDate     = itemView.findViewById(R.id.tvDate);
            tvTotal    = itemView.findViewById(R.id.tvTotal);
//            tvStatus   = itemView.findViewById(R.id.tvStatus);
        }
    }
}
