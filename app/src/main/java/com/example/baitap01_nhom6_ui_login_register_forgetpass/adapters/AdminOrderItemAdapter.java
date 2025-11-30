package com.example.baitap01_nhom6_ui_login_register_forgetpass.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.baitap01_nhom6_ui_login_register_forgetpass.R;
import com.example.baitap01_nhom6_ui_login_register_forgetpass.models.dto.OrderDetailItemDto;
import com.example.baitap01_nhom6_ui_login_register_forgetpass.util.PriceFormatter;

import java.util.ArrayList;
import java.util.List;

public class AdminOrderItemAdapter extends RecyclerView.Adapter<AdminOrderItemAdapter.ViewHolder> {

    private final List<OrderDetailItemDto> items = new ArrayList<>();

    public void setItems(List<OrderDetailItemDto> newItems) {
        items.clear();
        if (newItems != null) items.addAll(newItems);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_admin_order_product, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        OrderDetailItemDto item = items.get(position);

        holder.tvProductName.setText(item.getProductName());
        holder.tvQuantityPrice.setText(
                "x" + item.getSoLuong() + " · " + PriceFormatter.vnd(item.getDonGia())
        );
        holder.tvSubtotal.setText(PriceFormatter.vnd(item.getThanhTien()));

        Glide.with(holder.itemView.getContext())
                .load(item.getImageUrl())
                .placeholder(R.drawable.ic_launcher_background)
                .error(R.drawable.ic_launcher_background)
                .into(holder.imgProduct);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imgProduct;
        TextView tvProductName, tvQuantityPrice, tvSubtotal;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            imgProduct      = itemView.findViewById(R.id.imgProduct);
            tvProductName   = itemView.findViewById(R.id.tvProductName);
            tvQuantityPrice = itemView.findViewById(R.id.tvQuantityPrice);
            tvSubtotal      = itemView.findViewById(R.id.tvSubtotal);
        }
    }
}
